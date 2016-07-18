package com.softdesign.devintensive.ui.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.softdesign.devintensive.R;
import com.softdesign.devintensive.utils.DevIntensiveApplication;

/**
 * Created by Ageev Evgeny on 13.07.2016.
 */
public class AspectRatioImageView extends ImageView {
    private static final float DEFAULT_ASPECT_RATIO = 1.73f;
    private final float mAspectRatio;

    public AspectRatioImageView(Context ctx, AttributeSet attrs) {
        super(ctx, attrs);

        TypedArray a = ctx.obtainStyledAttributes(attrs, R.styleable.AspectRatioImageView);
        mAspectRatio = a.getFloat(R.styleable.AspectRatioImageView_aspect_ratio, DEFAULT_ASPECT_RATIO);
        a.recycle();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int newWidth = getMeasuredWidth();
        int newHeight = (int)(newWidth / mAspectRatio);

        setMeasuredDimension(newWidth, newHeight);
    }
}
