package com.mtahack.genius;


import android.Manifest;
import android.app.Notification;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private Button btnStart;
    private Button btnLocation;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnStart = (Button) findViewById(R.id.btn_start);
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
        });

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
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                              int[] grantResults){
        if (grantResults[0] !=PackageManager.PERMISSION_GRANTED || grantResults[1] !=PackageManager.PERMISSION_GRANTED){
            Log.d("genius", "permission not granted");
            finish();
        }
    }

}
