package com.shortstack.hackertracker.View;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.PorterDuff;
import android.support.v7.widget.CardView;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.shortstack.hackertracker.Application.App;
import com.shortstack.hackertracker.Database.DatabaseController;
import com.shortstack.hackertracker.Event.FavoriteEvent;
import com.shortstack.hackertracker.Event.RefreshTimerEvent;
import com.shortstack.hackertracker.Model.Item;
import com.shortstack.hackertracker.Model.ItemViewModel;
import com.shortstack.hackertracker.Model.Types;
import com.shortstack.hackertracker.R;
import com.squareup.otto.Subscribe;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ItemView extends CardView {

    public static final int DISPLAY_MODE_MIN = 0;
    public static final int DISPLAY_MODE_FULL = 1;
    public static final int PROGRESS_UPDATE_DURATION_PER_PERCENT = 50;


    private int mDisplayMode = DISPLAY_MODE_FULL;
    private boolean mRoundCorners = true;
    private ItemViewModel mItem;

    @BindView(R.id.updated)
    View updated;


    @BindView(R.id.title)
    TextView title;

    @BindView(R.id.time)
    TextView time;

    @BindView(R.id.location)
    TextView location;

    @BindView(R.id.category_text)
    TextView categoryText;

    @BindView(R.id.demo)
    View demo;

    @BindView(R.id.exploit)
    View exploit;

    @BindView(R.id.tool)
    View tool;

    @BindView(R.id.category)
    View category;

    @BindView(R.id.star_bar)
    View star;

    @BindView(R.id.progress)
    ProgressBar progress;
    private ObjectAnimator mAnimation;

    public ItemView(Context context) {
        super(context);
        init();
        inflate();
    }

    private void init() {
        setCardBackgroundColor(getResources().getColor(R.color.card_background));

        if (mRoundCorners) {
            float radius = convertDpToPixel(2, getContext());
            setRadius(radius);
        }
    }

    public ItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        getStyle(context, attrs);
        init();
        inflate();
        setDisplayMode();
    }


    private void getStyle(Context context, AttributeSet attrs) {
        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.ItemView,
                0, 0);
        try {
            mDisplayMode = a.getInteger(R.styleable.ItemView_displayMode, DISPLAY_MODE_FULL);
            mRoundCorners = a.getBoolean(R.styleable.ItemView_roundCorners, true);
        } finally {
            a.recycle();
        }
    }

    private void inflate() {
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View view = inflater.inflate(R.layout.row_item, null);
        ButterKnife.bind(this, view);

        addView(view);
    }

    public void setItem(Item item) {
        mItem = new ItemViewModel(item);
        renderItem();
    }

    public ItemViewModel getContent() {
        return mItem;
    }


    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (!isInEditMode())
            App.Companion.getApplication().registerBusListener(this);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (!isInEditMode())
            App.Companion.getApplication().unregisterBusListener(this);

        finishAnimation();
        setProgressBar();
    }

    private void finishAnimation() {
        if (mAnimation != null) {
            mAnimation.cancel();
            mAnimation = null;
        }
    }

    @Subscribe
    public void onRefreshTimeEvent(RefreshTimerEvent event) {
//        long time = System.currentTimeMillis();
        updateProgressBar();
//        Logger.d("Refreshed in " + ( System.currentTimeMillis() - time));
    }

    @Subscribe
    public void onFavoriteEvent(FavoriteEvent event) {
        if (event.getItem() == mItem.getId()) {
            updateBookmark();
        }
    }

    private void setDisplayMode() {
        switch (mDisplayMode) {
            case DISPLAY_MODE_FULL:

                break;

            case DISPLAY_MODE_MIN:
                time.setVisibility(GONE);
                categoryText.setVisibility(GONE);
                break;
        }
    }

    private void renderItem() {
        //updated.setVisibility( new Random().nextBoolean() ? VISIBLE : GONE);

        renderText();
        renderIcon();
        renderCategoryColour();
        renderBookmark();
        setProgressBar();
    }

    private void setProgressBar() {
        progress.setProgress(getProgress());
    }

    public void updateProgressBar() {
        int progress = getProgress();

        if (progress < this.progress.getProgress()) {
            setProgressBar();
            return;
        }

        finishAnimation();

        int duration = PROGRESS_UPDATE_DURATION_PER_PERCENT * (progress - this.progress.getProgress());

        mAnimation = ObjectAnimator.ofInt(this.progress, "progress", progress);
        mAnimation.setDuration(duration);
        mAnimation.setInterpolator(new DecelerateInterpolator());
        mAnimation.start();
    }

    private int getProgress() {
        return (int) ((getContent().getProgress()) * 100);
    }

    private void renderText() {
        title.setText(getContent().getDisplayTitle());
        location.setText(getContent().getLocation());

        if (mDisplayMode == DISPLAY_MODE_FULL) {
            time.setText(getContent().getFullTimeStamp(getContext()));
        }
    }

    @SuppressWarnings("ResourceType")
    private void renderIcon() {
        tool.setVisibility(getContent().getToolsVisibility());
        exploit.setVisibility(getContent().getExploitVisibility());
        demo.setVisibility(getContent().getDemoVisibility());
    }

    private void renderCategoryColour() {
        int count = getContent().getCategoryColorPosition();

        int[] allColors = getResources().getIntArray(R.array.colors);

        List<Types.Type> types = App.application.getDatabaseController().getTypes();


        int position = count % allColors.length;

        category.setBackgroundColor(allColors[position]);
        progress.getProgressDrawable().setColorFilter(allColors[position], PorterDuff.Mode.SRC_IN);

        if (mDisplayMode == DISPLAY_MODE_FULL) {
            categoryText.setText(types.get(position).getType());
        }
    }

    @SuppressWarnings("ResourceType")
    private void renderBookmark() {
        star.setVisibility(getContent().getBookmarkVisibility());
    }

    public void updateBookmark() {
        renderBookmark();
    }

    private float convertDpToPixel(float dp, Context context) {
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        return dp * ((float) metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
    }

    public void onShareClick() {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_TEXT, getContent().getDetailsDescription(getContext()));
        intent.setType("text/plain");

        getContext().startActivity(intent);
    }

    public void onBookmarkClick() {
        DatabaseController databaseController = App.Companion.getApplication().getDatabaseController();
        databaseController.toggleBookmark(databaseController.getWritableDatabase(), getContent().getItem());
    }
}
