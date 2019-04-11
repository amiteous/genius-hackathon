package com.mtahack.genius;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

public class LocationFetcher {
    private Context ctx;
    public LocationFetcher(Context ctx){
        this.ctx = ctx;
    }
    public boolean isNeedPermission(){
        if (ActivityCompat.checkSelfPermission(ctx, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(ctx, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        return false;
    }
    public void requestPermission(Activity activity, int requestCode){
        if (!isNeedPermission()){
            return;
        }
        ActivityCompat.requestPermissions(activity,
                new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION},
                requestCode);
    }
    @SuppressLint("MissingPermission")
    public String getLocationURL(){
        if (isNeedPermission()){
            return "";
        }
        LocationManager mgr = (LocationManager)ctx.getSystemService(ctx.LOCATION_SERVICE);
        Location loc  = mgr.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        if (loc == null){
            loc = mgr.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        }
        if (loc == null){
            return "";
        }
        String geoUri = "http://maps.google.com/maps?q=loc:" + loc.getLatitude() + "," + loc.getLongitude();
        return geoUri;
    }
}
