package com.shortstack.hackertracker.views

import android.animation.ObjectAnimator
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.PorterDuff
import android.os.Build
import androidx.core.content.ContextCompat
import android.util.AttributeSet
import android.util.DisplayMetrics
import android.view.View
import android.view.animation.DecelerateInterpolator
import android.widget.FrameLayout
import androidx.cardview.widget.CardView
import com.shortstack.hackertracker.App
import com.shortstack.hackertracker.R
import com.shortstack.hackertracker.database.DatabaseManager
import com.shortstack.hackertracker.models.DatabaseEvent
import com.shortstack.hackertracker.models.EventViewModel
import com.shortstack.hackertracker.ui.activities.MainActivity
import com.shortstack.hackertracker.utils.TickTimer
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.row_event.view.*
import javax.inject.Inject

class EventView : FrameLayout {

    @Inject
    lateinit var database: DatabaseManager

    @Inject
    lateinit var timer: TickTimer

    private var disposable: Disposable? = null

    private var displayMode: Int = DISPLAY_MODE_FULL
    var content: EventViewModel? = null
        private set

    private var animation: ObjectAnimator? = null

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
    }

    constructor(context: Context, event: DatabaseEvent, display: Int = DISPLAY_MODE_FULL) : super(context) {
        displayMode = display
        init()
        setContent(event)
    }

    private fun init() {
        inflate(context, R.layout.row_event, this)
        App.application.component.inject(this)
        setDisplayMode()
    }

    fun setContent(event: DatabaseEvent) {
        content = EventViewModel(event)
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

    private fun setDisplayMode() {
        val visibility = if (displayMode == DISPLAY_MODE_FULL) View.VISIBLE else View.GONE
//        star_bar.visibility = visibility
        category_text.visibility = visibility
//        progress.visibility = visibility
    }

    private fun render() {
        renderText()
        renderCategoryColour()
        if (content?.hasAnimatedProgress == false) {
            content?.hasAnimatedProgress = true
            progress.progress = 0
            updateProgressBar()
        } else {
            setProgressBar()
        }

        setOnClickListener {
            (context as? MainActivity)?.navigate(content?.event)
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
//        if (BuildConfig.DEBUG)
//            return Random().nextInt(100)

        return (content!!.progress * 100).toInt()
    }

    private fun renderText() {
        title.text = content?.title
        val pair = content?.getTimeStamp(context)
        location.text = content?.location + " | " + pair?.first + " - " + pair?.second
    }

    private fun renderCategoryColour() {
        val type = content?.event?.type?.firstOrNull() ?: return

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
        val isBookmarked = content?.event?.event?.isBookmarked == true
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

    private fun convertDpToPixel(dp: Float, context: Context): Float {
        val resources = context.resources
        val metrics = resources.displayMetrics
        return dp * (metrics.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT)
    }


    fun onBookmarkClick() {
        val event = content?.event?.event ?: return

        event.isBookmarked = !event.isBookmarked


        renderCategoryColour()

        Single.fromCallable {
            database.updateBookmark(event)
        }.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe()

    }

    companion object {

        const val DISPLAY_MODE_MIN = 0
        const val DISPLAY_MODE_FULL = 1
        const val PROGRESS_UPDATE_DURATION_PER_PERCENT = 50

    }


}
