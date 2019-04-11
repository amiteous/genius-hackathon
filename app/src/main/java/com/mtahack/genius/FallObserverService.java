package com.mtahack.genius;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.PowerManager;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

public class FallObserverService extends BackgroundService {

    private PowerManager.WakeLock wakeLock;
    private long lastFall;
    public void onCreate(){
        super.onCreate();
        PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);
        wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "FallObserver:WakeLock");
        wakeLock.acquire();
        String channel_id = "channel_id";
        String channel_name = "Channel name";

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createChannelID(channel_id, channel_name);
        }
        Notification notification = new NotificationCompat.Builder(this, channel_id)
                .setContentTitle("Guardian Angel")
                .setContentText("Guarding you")
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .build();
        startForeground(1, notification);
        FallNotificator.getInstance().setFallListener(new Runnable() {
            @Override
            public void run() {
                if (System.currentTimeMillis() - lastFall < 1000){
                    return;
                }
                lastFall = System.currentTimeMillis();
                Log.d("genius", "fall detected");
                notification("Ouch!","fall detected!",FallObserverService.this);
                Intent intent = new Intent();
                intent.setClass(FallObserverService.this, PanicActivity.class);
                intent.setFlags(FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });
        FallNotificator.getInstance().startMeasuring(this);
    }
    public void onDestroy() {
        super.onDestroy();
        Log.d("genius", "service is getting killed");
        wakeLock.release();
        FallNotificator.getInstance().startMeasuring(this);
    }
    public FallObserverService(){
        super("FallObserverService");
    }
    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d("genius", "onHandleIntent");
    }

    public void notification(String title, String message, Context context) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        int notificationId = 0;
        String channelId = "channel-id";
        String channelName = "Channel Name";
        int importance = NotificationManager.IMPORTANCE_HIGH;

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel mChannel = new NotificationChannel(
                    channelId, channelName, importance);
            notificationManager.createNotificationChannel(mChannel);
        }

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context, channelId)
                .setSmallIcon(R.drawable.ic_launcher_foreground)//R.mipmap.ic_launcher
                .setContentTitle(title)
                .setContentText(message)
                .setVibrate(new long[]{100, 250})
                .setLights(Color.YELLOW, 500, 5000)
                .setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_SOUND)
                .setColor(ContextCompat.getColor(context, R.color.colorPrimary));

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addNextIntent(new Intent(context, MainActivity.class));
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0,PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(resultPendingIntent);
        notificationManager.notify(notificationId, mBuilder.build());
    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    public String createChannelID(String channelid, String channelname){
        NotificationChannel chan = new NotificationChannel(channelid, channelname, NotificationManager.IMPORTANCE_HIGH);
        NotificationManager mgr = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        chan.setLightColor(Color.BLUE);
        chan.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
        mgr.createNotificationChannel(chan);
        return channelid;
    }
}
