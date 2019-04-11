package com.mtahack.genius;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.telephony.SmsManager;
import android.view.View;

public class SmsSender {
    private Context ctx;
    public SmsSender(Context ctx){
        this.ctx = ctx;
    }
    public void sendSms(String phones[], String message){
        SmsManager mgr = (SmsManager) SmsManager.getDefault();
        for (String phone: phones) {
            mgr.sendTextMessage(phone, null, message, null, null);
        }
    }
    public void requestPermission(Activity activity, int requestCode){
        if (!isNeedPermission()){
            return;
        }
        ActivityCompat.requestPermissions(activity,
                new String[]{Manifest.permission.SEND_SMS},
                requestCode);
    }

    public boolean isNeedPermission(){
        if (ContextCompat.checkSelfPermission(ctx, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED){
            return true;
        }
        return false;
    }
}
