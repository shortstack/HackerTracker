package com.shortstack.hackertracker.views

import android.animation.ObjectAnimator
import android.content.Context
import android.graphics.Color
import android.graphics.PorterDuff
import android.os.Build
import android.util.AttributeSet
import android.util.DisplayMetrics
import android.view.View
import android.view.animation.DecelerateInterpolator
import android.widget.FrameLayout
import androidx.core.content.ContextCompat
import com.shortstack.hackertracker.App
import com.shortstack.hackertracker.R
import com.shortstack.hackertracker.database.DatabaseManager
import com.shortstack.hackertracker.models.EventViewModel
import com.shortstack.hackertracker.models.FirebaseEvent
import com.shortstack.hackertracker.ui.activities.MainActivity
import com.shortstack.hackertracker.utils.TickTimer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.row_event.view.*
import javax.inject.Inject

class EventView : FrameLayout {

    @Inject
    lateinit var database: DatabaseManager

    @Inject
    lateinit var timer: TickTimer

    private var disposable: Disposable? = null

    private var displayMode: Int = DISPLAY_MODE_MIN

    private val model: EventViewModel = EventViewModel(null)

    private var animation: ObjectAnimator? = null

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
    }

    constructor(context: Context, event: FirebaseEvent, display: Int = DISPLAY_MODE_FULL) : super(context) {
        displayMode = display
        init()
        setContent(event)
    }

    private fun init() {
        inflate(context, R.layout.row_event, this)
        App.application.component.inject(this)
        setDisplayMode()
    }

    fun setContent(event: FirebaseEvent) {
        model.event = event
        render()
    }


    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        disposable = timer.observable.observeOn(AndroidSchedulers.mainThread())
                .subscribe { updateProgressBar() }
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

    fun setDisplayMode(mode: Int) {
        displayMode = mode
        setDisplayMode()
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

    private fun render() {
        renderText()
        renderCategoryColour()
        if (!model.hasAnimatedProgress) {
            model.hasAnimatedProgress = true
            progress.progress = 0
            updateProgressBar()
        } else {
            setProgressBar()
        }

        setOnClickListener {
            (context as? MainActivity)?.navigate(model.event)
        }

        star_bar.setOnClickListener {
            onBookmarkClick()
        }

    }

    private fun setProgressBar() {
        progress.progress = getProgress()
    }

    private fun updateProgressBar() {
        val progress = getProgress()

        if (progress < this.progress.progress) {
            setProgressBar()
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

    private fun getProgress(): Int {
        return (model.progress * 100).toInt()
    }

    private fun renderText() {
        title.text = model.title
        location.text = model.location

        if (displayMode != DISPLAY_MODE_MIN) {
            time.text = model.getFullTimeStamp(context)
        }
    }

    private fun renderCategoryColour() {
        val type = model.type

        category_text.text = type.name


        val color = Color.parseColor(type.color)
        category.setBackgroundColor(color)
        progress.progressDrawable.setColorFilter(color, PorterDuff.Mode.SRC_IN)


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val drawable = ContextCompat.getDrawable(context, R.drawable.chip_background)?.mutate()
            drawable?.setTint(color)
            category_text.background = drawable
        }

        renderBookmark(color)
    }

    private fun renderBookmark(color: Int) {
        val isBookmarked = model.isBookmarked

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

    private fun onBookmarkClick() {
        model.event?.let {
            it.isBookmarked = !it.isBookmarked
            database.updateBookmark(it)
        }
    }

    companion object {

        const val DISPLAY_MODE_MIN = 0
        const val DISPLAY_MODE_FULL = 1
        const val PROGRESS_UPDATE_DURATION_PER_PERCENT = 50

    }


}
