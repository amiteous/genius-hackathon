package com.mtahack.genius;

import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

public class PanicActivity extends AppCompatActivity {
    private Button ok;
    private Button bad;
    private Runnable notify;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d("genius", "panic started!");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_panic);
        getSupportActionBar().hide();
        final Window win = getWindow();
        win.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
        win.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        Settings.init(PanicActivity.this);
        Settings settings = null;
        try{
            settings = Settings.getInstance();
        } catch (Exception e) {}

        final MediaPlayer mp = MediaPlayer.create(this, R.raw.alarm);
        notify = new Runnable() {
            @Override
            public void run() {
                SmsSender sender = new SmsSender(PanicActivity.this);
                Settings settings = null;
                try{
                    settings = Settings.getInstance();
                } catch (Exception e) {}
                String url = "";
                if (settings.getSendLoaction()){
                    url = new LocationFetcher(PanicActivity.this).getLocationURL();
                }
                // sender.sendSms(settings.getContactsToText(), "Emergeny SMS: " + url);
                Log.d("genius", "send sms!!");
                mp.stop();
            }
        };
        final Handler handler = new Handler();
        handler.postDelayed(notify, settings.getAlarmTimeInSeconds() * 1000);
        //handler.postDelayed(notify, 5 * 1000);
        ok = (Button) findViewById(R.id.btnYes);
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handler.removeCallbacks(notify);
                mp.stop();
                finish();
            }
        });
        bad = (Button) findViewById(R.id.btnNO);
        bad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                notify.run();
                mp.stop();
                finish();
            }
        });
        handler.post(new Runnable() {
            @Override
            public void run() {
                mp.start();
            }
        });
    }
}
