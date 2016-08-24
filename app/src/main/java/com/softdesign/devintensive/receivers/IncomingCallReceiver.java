package com.softdesign.devintensive.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.TelephonyManager;
import android.widget.Toast;

/**
 * Created by Ageev Evegeny on 23.08.2016.
 */
public class IncomingCallReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context ctx, Intent i) {
        String phoneState = i.getStringExtra(TelephonyManager.EXTRA_STATE);
        if (phoneState.equals(TelephonyManager.EXTRA_STATE_RINGING)) {
            String phoneNumber = i.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);
            Toast.makeText(ctx, "Incoming Call From: " + phoneNumber, Toast.LENGTH_LONG).show();
        }
    }
}
