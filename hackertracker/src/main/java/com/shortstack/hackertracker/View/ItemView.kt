package com.shortstack.hackertracker.View

import android.animation.ObjectAnimator
import android.content.Context
import android.content.Intent
import android.graphics.PorterDuff
import android.support.v7.widget.CardView
import android.util.AttributeSet
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.animation.DecelerateInterpolator
import butterknife.ButterKnife
import com.shortstack.hackertracker.Application.App
import com.shortstack.hackertracker.Event.FavoriteEvent
import com.shortstack.hackertracker.Event.RefreshTimerEvent
import com.shortstack.hackertracker.Model.Item
import com.shortstack.hackertracker.Model.ItemViewModel
import com.shortstack.hackertracker.R
import com.squareup.otto.Subscribe
import kotlinx.android.synthetic.main.row_item.view.*

class ItemView : CardView {


    private var mDisplayMode = DISPLAY_MODE_FULL
    private var mRoundCorners = true
    var content : ItemViewModel? = null
        private set

    private var mAnimation : ObjectAnimator? = null

    constructor(context : Context) : super(context) {
        init()
        inflate()
    }

    private fun init() {
        setCardBackgroundColor(resources.getColor(R.color.card_background))

        if (mRoundCorners) {
            val radius = convertDpToPixel(2f, context)
            setRadius(radius)
        } else {
            radius = 0f
        }
    }

    constructor(context : Context, attrs : AttributeSet) : super(context, attrs) {
        getStyle(context, attrs)
        init()
        inflate()
        setDisplayMode()
    }


    private fun getStyle(context : Context, attrs : AttributeSet) {
        val a = context.theme.obtainStyledAttributes(
                attrs,
                R.styleable.ItemView,
                0, 0)
        try {
            mDisplayMode = a.getInteger(R.styleable.ItemView_displayMode, DISPLAY_MODE_FULL)
            mRoundCorners = a.getBoolean(R.styleable.ItemView_roundCorners, true)
        } finally {
            a.recycle()
        }
    }

    private fun inflate() {
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

        val view = inflater.inflate(R.layout.row_item, null)
        ButterKnife.bind(this, view)

        addView(view)
    }

    fun setItem(item : Item) {
        content = ItemViewModel(item)
        renderItem()
    }


    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        if (!isInEditMode)
            App.application.registerBusListener(this)
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        if (!isInEditMode)
            App.application.unregisterBusListener(this)

        finishAnimation()
        setProgressBar()
    }

    private fun finishAnimation() {
        if (mAnimation != null) {
            mAnimation!!.cancel()
            mAnimation = null
        }
    }

    @Subscribe
    fun onRefreshTimeEvent(event : RefreshTimerEvent) {
        //        long time = System.currentTimeMillis();
        updateProgressBar()
        //        Logger.d("Refreshed in " + ( System.currentTimeMillis() - time));
    }

    @Subscribe
    fun onFavoriteEvent(event : FavoriteEvent) {
        if (event.item == content!!.id) {
            updateBookmark()
        }
    }

    private fun setDisplayMode() {
        when (mDisplayMode) {
            DISPLAY_MODE_FULL -> {
            }

            DISPLAY_MODE_MIN -> {
                time!!.visibility = View.GONE
                rootView.category_text!!.visibility = View.GONE
            }
        }
    }

    private fun renderItem() {
        //updated.setVisibility( new Random().nextBoolean() ? VISIBLE : GONE);

        renderText()
        renderCategoryColour()
        renderBookmark()
        setProgressBar()
    }

    private fun setProgressBar() {
        progress!!.progress = getProgress()
    }

    fun updateProgressBar() {
        val progress = getProgress()

        if (progress < this.progress!!.progress) {
            setProgressBar()
            return
        }

        finishAnimation()

        val duration = PROGRESS_UPDATE_DURATION_PER_PERCENT * (progress - this.progress!!.progress)

        mAnimation = ObjectAnimator.ofInt(this.progress, "progress", progress)
        mAnimation!!.duration = duration.toLong()
        mAnimation!!.interpolator = DecelerateInterpolator()
        mAnimation!!.start()
    }

    private fun getProgress() : Int {
        return (content!!.progress * 100).toInt()
    }

    private fun renderText() {
        title!!.text = content!!.displayTitle
        location!!.text = content!!.location

        if (mDisplayMode == DISPLAY_MODE_FULL) {
            time!!.text = content!!.getFullTimeStamp(context)
        }
    }

    private fun renderCategoryColour() {
        val count = content!!.getCategoryColorPosition()

        val allColors = resources.getIntArray(R.array.colors)


        val position = count % allColors.size

        category!!.setBackgroundColor(allColors[position])
        progress!!.progressDrawable.setColorFilter(allColors[position], PorterDuff.Mode.SRC_IN)

        if (mDisplayMode == DISPLAY_MODE_FULL) {
            val types = App.application.databaseController.types
            rootView.category_text!!.text = types[position].type
        }
    }

    private fun renderBookmark() {
        rootView.star_bar!!.visibility = content!!.bookmarkVisibility
    }

    fun updateBookmark() {
        renderBookmark()
    }

    private fun convertDpToPixel(dp : Float, context : Context) : Float {
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
        val databaseController = App.application.databaseController
        databaseController.toggleBookmark(databaseController.writableDatabase, content!!.item)
    }

    companion object {

        val DISPLAY_MODE_MIN = 0
        val DISPLAY_MODE_FULL = 1
        val PROGRESS_UPDATE_DURATION_PER_PERCENT = 50
    }
}
