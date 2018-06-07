package com.shortstack.hackertracker.views

import android.animation.ObjectAnimator
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.PorterDuff
import android.support.v4.content.ContextCompat
import android.support.v7.widget.CardView
import android.util.AttributeSet
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.animation.DecelerateInterpolator
import com.orhanobut.logger.Logger
import com.shortstack.hackertracker.App
import com.shortstack.hackertracker.R
import com.shortstack.hackertracker.database.DatabaseManager
import com.shortstack.hackertracker.events.BusProvider
import com.shortstack.hackertracker.events.RefreshTimerEvent
import com.shortstack.hackertracker.models.DatabaseEvent
import com.shortstack.hackertracker.models.Event
import com.shortstack.hackertracker.models.EventViewModel
import com.squareup.otto.Subscribe
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.row_event.view.*
import javax.inject.Inject

class EventView(context: Context, attrs: AttributeSet) : CardView(context, attrs) {

    @Inject
    lateinit var database: DatabaseManager

    private var mDisplayMode = DISPLAY_MODE_FULL
    private var mRoundCorners = true
    var content: EventViewModel? = null
        private set

    private var mAnimation: ObjectAnimator? = null

    init {
        App.application.component.inject(this)

        getStyle(context, attrs)

        setCardBackgroundColor(ContextCompat.getColor(context, R.color.card_background))

        inflate()
        setDisplayMode()
    }


    private fun getStyle(context: Context, attrs: AttributeSet) {
        val a = context.theme.obtainStyledAttributes(attrs,
                R.styleable.EventView, 0, 0)
        try {
            mDisplayMode = a.getInteger(R.styleable.EventView_displayMode, DISPLAY_MODE_FULL)
            mRoundCorners = a.getBoolean(R.styleable.EventView_roundCorners, true)
        } finally {
            a.recycle()
        }

        radius = if (mRoundCorners) convertDpToPixel(2f, context) else 0f
    }

    private fun inflate() {
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

        val view = inflater.inflate(R.layout.row_event, null)

        addView(view)
    }

    fun setEvent(event: DatabaseEvent) {
        content = EventViewModel(event)
        renderItem()
    }


    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        if (!isInEditMode)
            BusProvider.bus.register(this)
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        if (!isInEditMode)
            BusProvider.bus.unregister(this)

        finishAnimation()
    }

    private fun finishAnimation() {
        if (mAnimation != null) {
            mAnimation?.cancel()
            mAnimation = null
        }
    }

    fun onRefreshTimeEvent() {
        Logger.d("Updated ${content?.title}")
        updateProgressBar()
    }

    private fun setDisplayMode() {
        when (mDisplayMode) {
            DISPLAY_MODE_FULL -> {
            }

            DISPLAY_MODE_MIN -> {
                time.visibility = View.GONE
                category_text.visibility = View.GONE
            }
        }
    }

    fun setDisplayMode(mode: Int) {
        mDisplayMode = mode
        setDisplayMode()
    }

    private fun renderItem() {
        renderText()
        renderCategoryColour()
        renderBookmark()
        setProgressBar()
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

        mAnimation = ObjectAnimator.ofInt(this.progress, "progress", progress)
                .also {
                    it.duration = duration.toLong()
                    it.interpolator = DecelerateInterpolator()
                    it.start()
                }
    }

    private fun getProgress(): Int {
        return (content!!.progress * 100).toInt()
    }

    private fun renderText() {
        title.text = content?.title
        location.text = content?.location

        if (mDisplayMode == DISPLAY_MODE_FULL) {
            time.text = content?.getFullTimeStamp(context)
        }
    }

    private fun renderCategoryColour() {
        val type = content?.event?.type?.firstOrNull() ?: return


        category_text.text = type.type
        val color = Color.parseColor(type.colour)

        category.setBackgroundColor(color)
        progress.progressDrawable.setColorFilter(color, PorterDuff.Mode.SRC_IN)


    }

    private fun renderBookmark() {
        star_bar.visibility = content?.bookmarkVisibility ?: View.INVISIBLE
    }

    private fun updateBookmark() {
        renderBookmark()
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
