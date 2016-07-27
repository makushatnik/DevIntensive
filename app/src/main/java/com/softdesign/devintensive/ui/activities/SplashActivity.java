package com.softdesign.devintensive.ui.activities;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.RelativeLayout;

import com.redmadrobot.chronos.ChronosOperation;
import com.redmadrobot.chronos.ChronosOperationResult;
import com.redmadrobot.chronos.gui.activity.ChronosActivity;
import com.softdesign.devintensive.R;
import com.softdesign.devintensive.utils.AppConfig;

public class SplashActivity extends ChronosActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        //RelativeLayout rl = (RelativeLayout) findViewById(R.id.main_layout);

        runOperation(new SplashOperation());
    }

    private class SplashOperation extends ChronosOperation<String> {



        @Nullable
        @Override
        public String run() {
            long startTime = System.currentTimeMillis();
            long time = 0;
            while (time < AppConfig.SPLASH_DELAY) {
                time = System.currentTimeMillis() - startTime;
                Log.d("SPLASH", " TIME = " + time);
                try {
                    Thread.sleep(100);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            Intent authIntent = new Intent(SplashActivity.this, AuthActivity.class);
            startActivity(authIntent);
            finish();
            return "ok";
        }

        @NonNull
        @Override
        public Class<? extends ChronosOperationResult<String>> getResultClass() {
            return Result.class;
        }
    }

    public final static class Result extends ChronosOperationResult<String> {}
}
