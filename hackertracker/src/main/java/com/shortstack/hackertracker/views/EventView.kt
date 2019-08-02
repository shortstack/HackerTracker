package com.shortstack.hackertracker.views

import android.animation.ObjectAnimator
import android.content.Context
import android.graphics.Color
import android.os.Build
import android.util.AttributeSet
import android.util.DisplayMetrics
import android.view.View
import android.widget.FrameLayout
import androidx.core.content.ContextCompat
import com.shortstack.hackertracker.R
import com.shortstack.hackertracker.utilities.Analytics
import com.shortstack.hackertracker.database.DatabaseManager
import com.shortstack.hackertracker.models.local.Event
import com.shortstack.hackertracker.ui.activities.MainActivity
import com.shortstack.hackertracker.utilities.TickTimer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.row_event.view.*
import org.koin.standalone.KoinComponent
import org.koin.standalone.inject

class EventView : FrameLayout, KoinComponent {

    companion object {
        const val DISPLAY_MODE_MIN = 0
        const val DISPLAY_MODE_FULL = 1
    }

    private val timer: TickTimer by inject()
    private val database: DatabaseManager by inject()
    private val analytics: Analytics by inject()


    private var disposable: Disposable? = null
    var displayMode: Int = DISPLAY_MODE_MIN
    private var animation: ObjectAnimator? = null


    constructor(context: Context, attrs: AttributeSet? = null, display: Int = DISPLAY_MODE_MIN) : super(context, attrs) {
        displayMode = display
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
                guideline.setGuidelineBegin(convertDpToPixel(16.0f, context).toInt())
                category_dot.visibility = View.GONE
                category_text.visibility = View.GONE
            }
            DISPLAY_MODE_FULL -> {
                val width = context.resources.getDimension(R.dimen.time_width).toInt()
                guideline.setGuidelineBegin(width)
                category_dot.visibility = View.VISIBLE
                category_text.visibility = View.VISIBLE
            }
        }
    }

    fun convertDpToPixel(dp: Float, context: Context): Float {
        return dp * (context.resources.displayMetrics.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT)
    }


    private fun render(event: Event) {
        title.text = event.title

        // Stage 2
        if (displayMode == DISPLAY_MODE_FULL) {
            location.text = event.location.name
        } else {
            location.text = event.getFullTimeStamp(context) + " / " + event.location.name
        }


        renderCategoryColour(event)
        updateBookmark(event)

        setOnClickListener {
            (context as? MainActivity)?.navigate(event)
        }

        star_bar.setOnClickListener {
            onBookmarkClick(event)
        }
    }

    private fun renderCategoryColour(event: Event) {
        val type = event.type

        category_text.text = type.name

        val color = Color.parseColor(type.color)
        category.setBackgroundColor(color)


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val drawable = ContextCompat.getDrawable(context, R.drawable.chip_background)?.mutate()
            drawable?.setTint(color)
            category_dot.background = drawable
        }
    }


    private fun updateBookmark(event: Event) {
        val type = event.type
        val color = Color.parseColor(type.color)
        renderBookmark(event, color)
    }

    private fun renderBookmark(event: Event, color: Int) {
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

        val action = if (event.isBookmarked) Analytics.EVENT_BOOKMARK else Analytics.EVENT_UNBOOKMARK
        analytics.onEventAction(action, event)

        updateBookmark(event)
    }
}
