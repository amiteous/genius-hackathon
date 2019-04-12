package com.mtahack.genius;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

public class FallNotificator implements SensorEventListener {
    private static FallNotificator m_instance;
    private static double[] pastAcceleration;
    private static double peak = 2;
    private static int peak_count = 0;
    private static long last_event = 0;
    private static boolean isFreeFalling = false;
    private static Runnable fall_listener;

    public static FallNotificator getInstance(){
        if (m_instance == null){
            m_instance = new FallNotificator();
            m_instance.pastAcceleration = new double[3];
        }
        return m_instance;
    }
    public static void setFallListener(Runnable listener){
        fall_listener = listener;
    }

    public boolean startMeasuring(Context ctx){
        SensorManager sensorManager = (SensorManager) ctx.getSystemService(Context.SENSOR_SERVICE);
        if (sensorManager == null || sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) == null) {
            return false;
        }
        sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_FASTEST);
        return true;
    }
    public void stopMeasuring(Context ctx){
        SensorManager sensorManager = (SensorManager) ctx.getSystemService(Context.SENSOR_SERVICE);
        if (sensorManager == null || sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) == null) {
            return;
        }
        sensorManager.unregisterListener(this);

    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() != Sensor.TYPE_ACCELEROMETER) {
            return;
        }
        double ax = event.values[0];
        double ay = event.values[1];
        double az = event.values[2];
        double total_acc = Math.sqrt(ax * ax + ay* ay  + az* az);
        if (pastAcceleration[0] == 0){
            pastAcceleration[0] = total_acc;
        }

        if (System.currentTimeMillis() - last_event < 200){
            return;
        }
        last_event = System.currentTimeMillis();

        double diff = Math.abs(total_acc - pastAcceleration[0]);
        //Log.d("Genius", "Acceleration is: "+ String.valueOf(total_acc));
        if (total_acc < 3){
            isFreeFalling = true;
        }else {
            if (total_acc > 10 && isFreeFalling) {
                Log.d("genius", "youfell");
                if (fall_listener != null) {
                    fall_listener.run();
//            }
                }
                isFreeFalling = false;
            }
//
//        if (diff > peak){
//            //Log.d("genius", "peak_count: " + peak_count);
//            peak_count += 1;
//        }else{
//            peak_count = 0;
//        }
//        if (peak_count >= 4){
//            //Log.d("Genius", "You fell! " + total_acc + " " + pastAcceleration[0]);
//            if (fall_listener != null){
//                fall_listener.run();
//            }
//            peak_count = 0;
//        }
//        pastAcceleration[0] = total_acc;
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }
}
