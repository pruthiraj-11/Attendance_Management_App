package com.app.attendancemanagementapp;

import android.app.AlarmManager;
import android.app.Application;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;

import com.app.attendancemanagementapp.service.ServiceCommunicator;

public class MyApplication extends Application {
    private Thread.UncaughtExceptionHandler defaultUEH;
    private Thread.UncaughtExceptionHandler uncaughtExceptionHandler = new Thread.UncaughtExceptionHandler() {

        @Override
        public void uncaughtException(@NonNull Thread thread, Throwable ex) {
            Log.d("service restarted", "Uncaught exception start!");
            ex.printStackTrace();

            PendingIntent service = PendingIntent.getService(
                    getApplicationContext(),
                    1001,
                    new Intent(getApplicationContext(), ServiceCommunicator.class), PendingIntent.FLAG_ONE_SHOT | PendingIntent.FLAG_IMMUTABLE);

            AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, 1000, service);
            System.exit(2);
        }
    };

    public MyApplication() {
        defaultUEH = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(uncaughtExceptionHandler);
    }
}
