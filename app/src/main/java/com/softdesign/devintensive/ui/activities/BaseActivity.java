package com.softdesign.devintensive.ui.activities;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.softdesign.devintensive.R;

/**
 * Created by Ageev Evgeny on 25.06.2016.
 */
public class BaseActivity extends AppCompatActivity {
    protected ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    protected void showProgress() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this, R.style.progress_dialog);
            mProgressDialog.setCancelable(false);
            mProgressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            mProgressDialog.show();
            mProgressDialog.setContentView(R.layout.progress_dialog);
        } else {
            mProgressDialog.show();
            mProgressDialog.setContentView(R.layout.progress_dialog);
        }
    }

    protected void hideProgress() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.hide();
        }
    }

    protected void showError(String tag, String message) {
        showToast(message);
        Log.e(tag, message);
    }

    protected void showError(String tag, String message, Exception error) {
        showToast(message);
        Log.e(tag, message, error);
    }

    protected void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    protected void showSnackbar(CoordinatorLayout coord, String message) {
        Snackbar.make(coord, message, Snackbar.LENGTH_LONG).show();
    }
}
