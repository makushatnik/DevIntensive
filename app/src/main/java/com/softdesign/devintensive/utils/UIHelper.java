package com.softdesign.devintensive.utils;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;
import android.util.TypedValue;

import com.softdesign.devintensive.data.storage.models.User;

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

    public static String filePathFromUri(@NonNull Uri uri) {
        String filePath = null;
        if ("content".equals(uri.getScheme())) {
            Cursor cursor = mContext.getContentResolver().query(uri,
                    new String[]{android.provider.MediaStore.Images.ImageColumns.DATA}, null, null, null);
            if (cursor != null) {
                cursor.moveToFirst();
                filePath = cursor.getString(0);
                cursor.close();
            }
        } else {
            filePath = uri.getPath();
        }
        return filePath;
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

    public static boolean isFirstLike(User user) {
//        String myId = DataManager.getInstance().getPreferencesManager().getUserId();
//        List<Like> userLikes = user.getLikes();
//        for (Like like : userLikes) {
//            if (like.getId() == myId) return true;
//        }
        return true;
    }
}
