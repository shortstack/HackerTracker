package com.shortstack.hackertracker.views

import android.animation.ObjectAnimator
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.PorterDuff
import android.os.Build
import android.support.v4.content.ContextCompat
import android.support.v7.widget.CardView
import android.util.AttributeSet
import android.util.DisplayMetrics
import android.view.View
import android.view.animation.DecelerateInterpolator
import com.shortstack.hackertracker.App
import com.shortstack.hackertracker.BuildConfig
import com.shortstack.hackertracker.R
import com.shortstack.hackertracker.database.DatabaseManager
import com.shortstack.hackertracker.models.DatabaseEvent
import com.shortstack.hackertracker.models.EventViewModel
import com.shortstack.hackertracker.utils.TickTimer
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.row_event.view.*
import java.util.*
import javax.inject.Inject

class EventView(context: Context, attrs: AttributeSet) : CardView(context, attrs) {

    @Inject
    lateinit var database: DatabaseManager

    @Inject
    lateinit var timer: TickTimer

    private var disposable: Disposable? = null

    private var displayMode = DISPLAY_MODE_FULL
    private var mRoundCorners = true
    var content: EventViewModel? = null
        private set

    private var animation: ObjectAnimator? = null

    init {
        inflate(context, R.layout.row_event, this)

        App.application.component.inject(this)

        getStyle(context, attrs)

        setCardBackgroundColor(ContextCompat.getColor(context, android.R.color.transparent))


        setDisplayMode()
    }


    private fun getStyle(context: Context, attrs: AttributeSet) {
        val a = context.theme.obtainStyledAttributes(attrs,
                R.styleable.EventView, 0, 0)
        try {
            displayMode = a.getInteger(R.styleable.EventView_displayMode, DISPLAY_MODE_FULL)
//            mRoundCorners = a.getBoolean(R.styleable.EventView_roundCorners, true)
        } finally {
            a.recycle()
        }

        radius = if (mRoundCorners) convertDpToPixel(2f, context) else 0f
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
//        val visibility = if (displayMode == DISPLAY_MODE_FULL) View.VISIBLE else View.GONE
//        time.visibility = visibility
//        category_text.visibility = visibility
    }

    fun setDisplayMode(mode: Int) {
        displayMode = mode
        setDisplayMode()
    }

    private fun render() {
        renderText()
        renderCategoryColour()
//        renderBookmark(color)
        if (content?.hasAnimatedProgress == false) {
            content?.hasAnimatedProgress = true
            progress.progress = 0
            updateProgressBar()
        } else {

            setProgressBar()
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
        location.text = content?.location +  " | " + pair?.first + " - " + pair?.second
    }

    private fun renderCategoryColour() {
        val type = content?.event?.type?.firstOrNull() ?: return

        category_text.text = type.type
        val color =
//                if (BuildConfig.DEBUG) {
//            val colours = context.resources.getStringArray(R.array.colors)
//            Color.parseColor(colours[Random().nextInt(colours.size)])
//        } else {
            Color.parseColor(type.colour)
//        }

        category.setBackgroundColor(color)
        progress.progressDrawable.setColorFilter(color, PorterDuff.Mode.SRC_IN)

        val drawable = context.getDrawable(R.drawable.chip_background).mutate()
        drawable.setTint(color)

        category_text.setBackgroundDrawable(drawable)


        renderBookmark(color)
    }

    private fun renderBookmark(color: Int) {
        star_bar.visibility = View.VISIBLE

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

    fun onShareClick() {
        val intent = Intent(Intent.ACTION_SEND)
        intent.putExtra(Intent.EXTRA_TEXT, content?.getDetailsDescription(context))
        intent.type = "text/plain"

        context.startActivity(intent)
    }

    fun onBookmarkClick() {
        val event = content?.event?.event ?: return

        event.isBookmarked = !event.isBookmarked

        Single.fromCallable {
            database.updateEvent(event)
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
