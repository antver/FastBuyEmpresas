package com.fastbuy.fastbuyempresas.Config;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class BootBroadcast extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        context.startService(new Intent(context, NotificationService.class));
        //Toast.makeText(context, "Action: " + intent.getAction(), Toast.LENGTH_LONG).show();
    }
}
