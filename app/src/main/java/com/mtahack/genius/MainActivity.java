package com.mtahack.genius;


import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import java.util.Set;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

public class MainActivity extends AppCompatActivity {

    public static final int SMS_REQ = 1;
    public static final int LOCATION_REQ = 2;
    private Button btnStart;
    private Button btnLocation;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();
        final SmsSender sms = new SmsSender(MainActivity.this);
        final ContactsSettingsActivity contacts = new ContactsSettingsActivity();
        ContactsSettingsActivity.isNeedPermission(MainActivity.this);
        if (sms.isNeedPermission()) {
            sms.requestPermission(MainActivity.this, SMS_REQ);
            //return;
        }

        final LocationFetcher locate = new LocationFetcher(MainActivity.this);
        if (locate.isNeedPermission()){
            locate.requestPermission(MainActivity.this, LOCATION_REQ);
        }

        Intent intent = new Intent();
        intent.setClass(MainActivity.this, FallObserverService.class);
        startService(intent);

        Settings.init(MainActivity.this);
        final ImageButton emerButton = findViewById(R.id.imageButton3);
        emerButton.setOnClickListener(new View.OnClickListener() {
                                          @Override
                                          public void onClick(View v) {
                                              if (sms.isNeedPermission()){
                                                  sms.requestPermission(MainActivity.this, SMS_REQ);
                                                  return;
                                              }
                                              if (locate.isNeedPermission()){
                                                  locate.requestPermission(MainActivity.this,LOCATION_REQ);
                                              }
                                              Log.d("genius", locate.getLocationURL());
                                              Settings settings = null;
                                              try{
                                                  settings = Settings.getInstance();
                                              } catch (Exception e){}
                                              sendEmergencyCall(settings.getContactsToText(),"Emergency. GPS: "+locate.getLocationURL());
                                          }
                                      });

        final ImageButton settingsButton = findViewById(R.id.imageButton);
        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               Intent intent = new Intent();
               intent.setClass(MainActivity.this, SettingsActivity.class);
               startActivity(intent);
            }
        });
        /*btnStart = (Button) findViewById(R.id.btn_start);
        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, FallObserverService.class);
                startService(intent);

            }
        });

        btnLocation = (Button) findViewById(R.id.btn_location);
        btnLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Location loc = getLastKnownLocation(MainActivity.this);
                if (loc == null){
                    return;
                }

                String geoUri = "http://maps.google.com/maps?q=loc:" + loc.getLatitude() + "," + loc.getLongitude() + " (" + "my title" + ")";
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(geoUri));
                MainActivity.this.startActivity(intent);
            }
        });*/

    }

    public void onSMSgranted(){
        Log.d("genius", "onSMSGranted");
        SmsSender smsSender = new SmsSender(MainActivity.this);
        //smsSender.sendSms(new String[]{"0542368516","0548306906"},"Granted");
        //Settings.getInstance().getContactsToText()
    }

    public void sendEmergencyCall(Pair<String,String>[] contacts, String message){
        SmsSender smsSend = new SmsSender(MainActivity.this);
        smsSend.sendSms(contacts, message);
    }

    public Location getLastKnownLocation(Context ctx){
        LocationManager mgr = (LocationManager)ctx.getSystemService(LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        String provider = mgr.getBestProvider(criteria,false);
        if (ActivityCompat.checkSelfPermission(ctx, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(ctx, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            String[] permissions = new String[2];
            permissions[0] = android.Manifest.permission.ACCESS_FINE_LOCATION;
            permissions[1] = android.Manifest.permission.ACCESS_COARSE_LOCATION;
            ActivityCompat.requestPermissions(this, permissions,0);
            return null;
        }
        return mgr.getLastKnownLocation(provider);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                              int[] grantResults){
        for (int result: grantResults)
        {
            if (result != PackageManager.PERMISSION_GRANTED){
                Log.d("genius", "permission denied");
                finish();
            }
        }
        if (requestCode == SMS_REQ){
            final LocationFetcher locate = new LocationFetcher(MainActivity.this);
            if (locate.isNeedPermission()){
                locate.requestPermission(MainActivity.this, LOCATION_REQ);
            }
            onSMSgranted();
        }
        if (requestCode == LOCATION_REQ){

        }
    }

}
