package com.softdesign.devintensive.ui.activities;

import android.graphics.Color;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.softdesign.devintensive.R;
import com.softdesign.devintensive.utils.ConstantManager;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = ConstantManager.TAG_PREFIX + "MainActivity";

    protected EditText mEditText;
    protected Button mRedButton, mGreenButton;
    protected int mColorMode;

    /**
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(TAG, "onCreate");

        mRedButton = (Button) findViewById(R.id.red_btn);
        mGreenButton = (Button) findViewById(R.id.grn_btn);
        mEditText = (EditText) findViewById(R.id.textView);

        mRedButton.setOnClickListener(this);
        mGreenButton.setOnClickListener(this);

        if (savedInstanceState != null) {
            mColorMode = savedInstanceState.getInt(ConstantManager.COLOR_MODE_KEY);
            if (mColorMode == 1) {
                mEditText.setBackgroundColor(Color.RED);
            } else if (mColorMode == 2) {
                mEditText.setBackgroundColor(Color.GREEN);
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "onStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d(TAG, "onRestart");
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.red_btn:
                mEditText.setBackgroundColor(Color.RED);
                mColorMode = 1;
                break;
            case R.id.grn_btn:
                mEditText.setBackgroundColor(Color.GREEN);
                mColorMode = 2;
                break;
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        Log.d(TAG, "onSaveInstanceState");
        outState.putInt(ConstantManager.COLOR_MODE_KEY, mColorMode);
    }
}
