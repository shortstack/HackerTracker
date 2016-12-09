package com.shortstack.hackertracker.View;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.support.v7.widget.CardView;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.shortstack.hackertracker.Model.Default;
import com.shortstack.hackertracker.R;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ItemView extends CardView {

    public static final int DISPLAY_MODE_MIN = 0;
    public static final int DISPLAY_MODE_FULL = 1;


    private int mDisplayMode = DISPLAY_MODE_FULL;
    private boolean mRoundCorners = true;
    private Default mItem;


    @Bind(R.id.title)
    TextView title;

    @Bind(R.id.time)
    TextView time;

    @Bind(R.id.location)
    TextView location;

    @Bind(R.id.category_text)
    TextView categoryText;

    @Bind(R.id.demo)
    View demo;

    @Bind(R.id.exploit)
    View exploit;

    @Bind(R.id.tool)
    View tool;

    @Bind(R.id.category)
    View category;


    public ItemView(Context context) {
        super(context);
        init();
        inflate();
    }

    private void init() {
        setCardBackgroundColor(getResources().getColor(R.color.card_background));

        if( mRoundCorners ) {
            float radius = convertDpToPixel(2, getContext());
            setRadius(radius);
        }
    }

    public ItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        getStyle(context, attrs);
        init();
        inflate();
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
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService( Context.LAYOUT_INFLATER_SERVICE );

        View view = inflater.inflate( R.layout.row_item, null );
        ButterKnife.bind(this, view);

        addView(view);
    }

    public void setItem( Default item ) {
        mItem = item;
        renderItem();
    }

    public Default getContent() {
        return mItem;
    }

    private void renderItem() {
        renderText();
        renderIcon();
        renderCategoryColour();
        renderBookmark();
    }

    private void renderText() {
        title.setText(getContent().getDisplayTitle());
        location.setText(getContent().getLocation());

        if( mDisplayMode == DISPLAY_MODE_MIN ) {
            time.setVisibility(GONE);
            return;
        }

        time.setText(getContent().getFullTimeStamp(getContext()));
    }

    private void renderIcon() {
        tool.setVisibility(getContent().isTool() ? View.VISIBLE : View.GONE);
        exploit.setVisibility(getContent().isExploit() ? View.VISIBLE : View.GONE);
        demo.setVisibility(getContent().isDemo() ? View.VISIBLE : View.GONE);
    }

    private void renderCategoryColour() {
        int count = getContent().getCategoryColorPosition();

        int[] allColors = getResources().getIntArray(R.array.colors);
        String[] allLabels = getResources().getStringArray(R.array.filter_types);

        int position = count % allColors.length;

        category.setBackgroundColor(allColors[position]);

        if( mDisplayMode == DISPLAY_MODE_MIN ) {
            categoryText.setVisibility(GONE);
            return;
        }

        categoryText.setText(allLabels[position]);
    }

    private void renderBookmark() {
        // TODO: Add bookmark to the layout, and handle the status.
    }

    public void updateBookmark() {
        renderBookmark();
    }

    private float convertDpToPixel(float dp, Context context){
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        return dp * ((float)metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
    }
}
