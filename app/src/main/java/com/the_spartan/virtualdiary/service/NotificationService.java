package com.the_spartan.virtualdiary.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.preference.PreferenceManager;
import android.util.Log;

/**
 * Created by Spartan on 3/26/2018.
 */

public class NotificationService extends Service {

    static AlarmManager alarmManager;
    static PendingIntent pendingIntent;


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        boolean isNotificationTurnedOn = preferences.getBoolean("notification_switch_value", false);
//        String intervalString = preferences.getString("notification", "1");
        long interval = Long.parseLong(preferences.getString("notification", "1"));
//        Toast.makeText(this, " " + interval, Toast.LENGTH_SHORT).show();

        interval = interval * 24 * 3600 * 1000;

        Log.d("Notification Service", " " + isNotificationTurnedOn);

        if (isNotificationTurnedOn){
            Intent notificationBroadcastIntent = new Intent(this, NotificationBroadcastReceiver.class);
            pendingIntent = PendingIntent.getBroadcast(this, 101, notificationBroadcastIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            alarmManager = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + interval, interval, pendingIntent);

            Log.d("Notification Service", "Alarm manager set");
            Log.d("Notification delay", "" + interval);
        }

        return START_STICKY;
    }

    @Override
    public void onDestroy() {

        alarmManager.cancel(pendingIntent);

        super.onDestroy();
    }
}
