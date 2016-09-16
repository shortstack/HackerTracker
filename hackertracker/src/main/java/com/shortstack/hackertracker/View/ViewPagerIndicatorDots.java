package com.shortstack.hackertracker.View;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.PorterDuff;
import android.support.annotation.ColorRes;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.shortstack.hackertracker.R;

public class ViewPagerIndicatorDots extends LinearLayout implements ViewPager.OnPageChangeListener {

    public static final int COLOUR_FADE_DURATION = 250;
    public static final int COLOR_ACTIVE = R.color.colorPrimary;
    public static final int COLOR_INACTIVE = R.color.white;
    public static final int SCALE_DURATION = 250;
    public static final float SIZE_MAX = 1.5f;
    public static final float SIZE_MIN = 1;
    private ViewPager mPager;
    private ImageView mSelectedView;
    private PagerAdapter mAdapter;

    public ViewPagerIndicatorDots(Context context) {
        super(context);
    }

    public ViewPagerIndicatorDots(Context context, AttributeSet attrs) {
        super(context, attrs);
    }


    public void setViewPager(ViewPager pager) {
        mPager = pager;
        mAdapter = pager.getAdapter();
        init();
    }

    private void init() {
        int childCount = mAdapter.getCount();
        int currentItem = mPager.getCurrentItem();

        for (int i = 0; i < childCount; i++) {
            View view = inflate(getContext(), R.layout.view_pager_dot_unselected, null);
            addView(view);
            if (i == currentItem) {
                selectDot(view);
            }
        }
        mPager.addOnPageChangeListener(this);
    }

    private void selectDot(View view) {
        deselectPreviousDot();

        mSelectedView = (ImageView) view;
        animationColour(mSelectedView, R.color.white, R.color.colorPrimary);
        scaleDrawable(mSelectedView, SIZE_MIN, SIZE_MAX);
    }

    private void scaleDrawable(ImageView target, float from, float to) {
        Animation scale = new ScaleAnimation(from, to, from, to, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        scale.setDuration(SCALE_DURATION);
        scale.setFillAfter(true);

        target.startAnimation(scale);
    }

    private void animationColour(final ImageView target, @ColorRes int from, @ColorRes int to) {
        int colorFrom = ContextCompat.getColor(getContext(), from);
        int colorTo = ContextCompat.getColor(getContext(), to);
        ValueAnimator colorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), colorFrom, colorTo);
        colorAnimation.setDuration(COLOUR_FADE_DURATION);
        colorAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(ValueAnimator animator) {
                target.getDrawable().mutate().setColorFilter((int) animator.getAnimatedValue(), PorterDuff.Mode.SRC_ATOP);
            }

        });
        colorAnimation.start();
    }

    private void deselectPreviousDot() {
        if (mSelectedView != null) {
            animationColour(mSelectedView, COLOR_ACTIVE, COLOR_INACTIVE);
            scaleDrawable(mSelectedView, SIZE_MAX, SIZE_MIN);
        }
    }


    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        selectDot(getChildAt(position));
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }
}
