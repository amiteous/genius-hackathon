package com.mtahack.genius;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class InitReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent inte) {
        Intent intent = new Intent();
        intent.setClass(context, FallObserverService.class);
        context.startService(intent);
    }
}
