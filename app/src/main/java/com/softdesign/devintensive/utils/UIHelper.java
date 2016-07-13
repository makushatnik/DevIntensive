package com.softdesign.devintensive.utils;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.util.TypedValue;

/**
 * Created by Ageev Evgeny on 09.07.2016.
 */
public class UIHelper {
    private static Context mContext = DevIntensiveApplication.getContext();

    public static int getStatusBarHeight() {
        int result = 0;
        Resources contRes = mContext.getResources();
        int resourceId = contRes.getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = contRes.getDimensionPixelSize(resourceId);
        }
        return result;
    }

    public static int getActionBarHeight() {
        int result = 0;
        TypedValue tv = new TypedValue();
        if (mContext.getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true)) {
            result = TypedValue.complexToDimensionPixelSize(tv.data, mContext.getResources().getDisplayMetrics());
        }
        return result;
    }

    public static int lerp(int start, int end, float friction) {
        return (int)(start + (end - start) * friction);
    }

    public static float currentFriction(int start, int end, int currentValue) {
        return (float)(currentValue - start) / (end - start);
    }
}
