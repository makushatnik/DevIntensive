package com.softdesign.devintensive.utils;

import android.net.Uri;

import java.io.File;

/**
 * Created by Ageev Evgeny on 13.07.2016.
 */
public class FileUtils {
    public static File getFile(Uri fileUri) {
        return new File(fileUri.getPath());
    }
}
