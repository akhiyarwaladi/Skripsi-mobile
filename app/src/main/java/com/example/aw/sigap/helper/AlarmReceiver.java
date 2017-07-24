package com.example.aw.sigap.helper;

/**
 * Created by AW on 7/24/2017.
 */

import android.app.*;
import android.content.*;
import android.os.*;

public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent background = new Intent(context, BackgroundService.class);
        context.startService(background);
    }

}