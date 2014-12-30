package com.xoverto.activeaberdeen;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by andrew on 12/07/2014.
 */
public class DataAlarmReceiver extends BroadcastReceiver {

    public static final String ACTION_REFRESH_DATA_ALARM = "com.xoverto.matchthecity.ACTION_REFRESH_DATA";

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent startIntent = new Intent(context, DataUpdateService.class);
        context.startService(startIntent);
    }
}
