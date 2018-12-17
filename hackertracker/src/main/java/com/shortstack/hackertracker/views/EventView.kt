package com.shortstack.hackertracker.views

import android.animation.ObjectAnimator
import android.content.Context
import android.graphics.Color
import android.graphics.PorterDuff
import android.os.Build
import androidx.core.content.ContextCompat
import android.util.AttributeSet
import android.util.DisplayMetrics
import android.view.View
import android.view.animation.DecelerateInterpolator
import android.widget.FrameLayout
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.orhanobut.logger.Logger
import com.shortstack.hackertracker.App
import com.shortstack.hackertracker.R
import com.shortstack.hackertracker.database.DatabaseManager
import com.shortstack.hackertracker.models.EventViewModel
import com.shortstack.hackertracker.models.FirebaseEvent
import com.shortstack.hackertracker.models.FirebaseLocation
import com.shortstack.hackertracker.models.FirebaseType
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
    var content: EventViewModel? = null
        private set

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
        if (content?.hasAnimatedProgress == false) {
            content?.hasAnimatedProgress = true
            progress.progress = 0
            updateProgressBar()
        } else {
            setProgressBar()
        }

        setOnClickListener {
            //            (context as? MainActivity)?.navigate(content?.event)
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

        val text = content?.event?.location?.keys?.firstOrNull() ?: "Unknown"

        FirebaseDatabase.getInstance().getReference("conferences/DC26/locations/$text").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                Logger.e("cancelled")
            }

            override fun onDataChange(p0: DataSnapshot) {
                location.text = p0.getValue(FirebaseLocation::class.java)?.name
            }
        })

        if (displayMode != DISPLAY_MODE_MIN) {
            time.text = content?.getFullTimeStamp(context)
        }
    }

    private fun renderCategoryColour() {

        val id = content?.event?.type?.keys?.firstOrNull()

        FirebaseDatabase.getInstance().getReference("conferences/DC26/types/$id").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                Logger.e("cancelled")
            }

            override fun onDataChange(p0: DataSnapshot) {
                Logger.d("Data changed!")

                val type = p0.getValue(FirebaseType::class.java) ?: return


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
        })
    }

    private fun renderBookmark(color: Int) {
        val isBookmarked = false
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

    }

    companion object {

        const val DISPLAY_MODE_MIN = 0
        const val DISPLAY_MODE_FULL = 1
        const val PROGRESS_UPDATE_DURATION_PER_PERCENT = 50

    }


}
