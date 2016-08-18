package com.softdesign.devintensive.utils;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.util.Log;
import android.util.TypedValue;

/**
 * Created by Ageev Evgeny on 09.07.2016.
 */
public class UIHelper {
    private static final Context mContext = DevIntensiveApplication.getContext();

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

    public static void openRepo(Context ctx, String repoAddr) {
        int pos = repoAddr.indexOf("http://");
        if (pos == -1) {
            repoAddr = "http://" + repoAddr;
        } else if (pos != 0) {
            Log.d("UTILS", "Incorrect address - " + repoAddr + "!");
            return;
        }
        Log.d("UTILS", "ADDR - " + repoAddr);
        Uri address = Uri.parse("http://" + repoAddr);
        Intent intent = new Intent(Intent.ACTION_VIEW, address);
        if (intent.resolveActivity(ctx.getPackageManager()) != null) {
            ctx.startActivity(intent);
        }
    }
}
