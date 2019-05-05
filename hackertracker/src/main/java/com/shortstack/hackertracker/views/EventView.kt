package com.shortstack.hackertracker.views

import android.animation.ObjectAnimator
import android.content.Context
import android.graphics.Color
import android.graphics.PorterDuff
import android.os.Build
import android.util.AttributeSet
import android.view.View
import android.view.animation.DecelerateInterpolator
import android.widget.FrameLayout
import androidx.core.content.ContextCompat
import com.shortstack.hackertracker.R
import com.shortstack.hackertracker.analytics.AnalyticsController
import com.shortstack.hackertracker.database.DatabaseManager
import com.shortstack.hackertracker.models.local.Event
import com.shortstack.hackertracker.ui.activities.MainActivity
import com.shortstack.hackertracker.utils.TickTimer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.row_event.view.*
import org.koin.standalone.KoinComponent
import org.koin.standalone.inject

class EventView : FrameLayout, KoinComponent {

    companion object {
        const val DISPLAY_MODE_MIN = 0
        const val DISPLAY_MODE_FULL = 1
        private const val PROGRESS_UPDATE_DURATION_PER_PERCENT = 50
    }

    private val timer: TickTimer by inject()
    private val database: DatabaseManager by inject()
    private val analytics: AnalyticsController by inject()


    private var disposable: Disposable? = null
    private var displayMode: Int = DISPLAY_MODE_MIN
    private var animation: ObjectAnimator? = null

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
    }

    constructor(context: Context, event: Event, display: Int = DISPLAY_MODE_FULL) : super(context) {
        displayMode = display
        init()
        setContent(event)
    }

    private fun init() {
        inflate(context, R.layout.row_event, this)
        setDisplayMode()
    }

    fun setContent(event: Event) {
        render(event)
    }

    override fun onDetachedFromWindow() {
        disposable?.dispose()
        disposable = null
        finishAnimation()
        super.onDetachedFromWindow()
    }

    private fun finishAnimation() {
        animation?.cancel()
        animation = null
    }

    private fun setDisplayMode() {
        when (displayMode) {
            DISPLAY_MODE_MIN -> {
                time.visibility = View.GONE
            }
            DISPLAY_MODE_FULL -> {
                time.visibility = View.VISIBLE
            }
        }
    }

    private fun render(event: Event) {
        title.text = event.title
        location.text = event.location.name

        if (displayMode != DISPLAY_MODE_MIN) {
            time.text = event.getFullTimeStamp(context)
        }

        renderCategoryColour(event)
        updateBookmark(event)
        setProgressBar(event)


        setOnClickListener {
            (context as? MainActivity)?.navigate(event)
        }

        star_bar.setOnClickListener {
            onBookmarkClick(event)
        }

        // TODO: Check that this works as intended instead of from within onAttachedToWindow().
        disposable?.dispose()
        disposable = timer.observable.observeOn(AndroidSchedulers.mainThread())
                .subscribe { updateProgressBar(event) }
    }

    private fun setProgressBar(event: Event) {
        progress.progress = getProgress(event)
    }

    private fun updateProgressBar(event: Event) {
        val progress = getProgress(event)

        if (progress < this.progress.progress) {
            setProgressBar(event)
            return
        }

        finishAnimation()

        val duration = PROGRESS_UPDATE_DURATION_PER_PERCENT * (progress - this.progress.progress)

        animation = ObjectAnimator.ofInt(this.progress, "progress", progress)
                .also {
                    it.duration = duration.toLong()
                    it.interpolator = DecelerateInterpolator()
                    it.start()
                }
    }

    private fun getProgress(event: Event): Int {
        return (event.progress * 100).toInt()
    }

    private fun renderCategoryColour(event: Event) {
        val type = event.type

        category_text.text = type.name

        val color = Color.parseColor(type.color)
        category.setBackgroundColor(color)
        progress.progressDrawable.setColorFilter(color, PorterDuff.Mode.SRC_IN)


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val drawable = ContextCompat.getDrawable(context, R.drawable.chip_background)?.mutate()
            drawable?.setTint(color)
            category_text.background = drawable
        }
    }


    private fun updateBookmark(event: Event) {
        val type = event.type
        val color = Color.parseColor(type.color)
        renderBookmark(event, color)
    }

    private fun renderBookmark(event: Event,color: Int) {
        val isBookmarked = event.isBookmarked

        val drawable = if (isBookmarked) {
            R.drawable.ic_star_accent_24dp
        } else {
            R.drawable.ic_star_border_white_24dp
        }

        val image = ContextCompat.getDrawable(context, drawable)?.mutate()

        if (isBookmarked && Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            image?.setTint(color)
        }

        star_bar.setImageDrawable(image)
    }

    private fun onBookmarkClick(event: Event) {
        event.isBookmarked = !event.isBookmarked
        database.updateBookmark(event)

        val action = if (event.isBookmarked) AnalyticsController.EVENT_BOOKMARK else AnalyticsController.EVENT_UNBOOKMARK
        analytics.onEventAction(action, event)

        updateBookmark(event)
    }
}
