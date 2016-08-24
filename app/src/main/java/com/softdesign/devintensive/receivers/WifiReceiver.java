package com.softdesign.devintensive.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by Ageev Evgeny on 23.08.2016.
 */
public class WifiReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context ctx, Intent i) {
        boolean isNetwork = i.getBooleanExtra("EXTRA_NO_CONNECTIVITY", true);
        if (!isNetwork) {
            ConnectivityManager conn = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetwork = conn.getActiveNetworkInfo();
            boolean isConnected = (activeNetwork != null && activeNetwork.isConnectedOrConnecting());
            if (isConnected) {
                boolean isWifi = activeNetwork.getType() == ConnectivityManager.TYPE_WIFI;
                Toast.makeText(ctx, "Hooray! Have a WiFi Network", Toast.LENGTH_LONG).show();
            } else {
                Log.e("WifiReceiver", "Error wit network");
            }
        }
    }
}
