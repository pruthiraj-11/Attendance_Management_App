package com.app.attendancemanagementapp.service;

import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

public class ServiceCommunicator extends Service {
    private ReadSendSMSService mSMSreceiver;
    private IntentFilter mIntentFilter;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("servicestart","service started");
        mSMSreceiver = new ReadSendSMSService();
        mIntentFilter = new IntentFilter();
        mIntentFilter.addAction("android.provider.Telephony.SMS_RECEIVED");
        registerReceiver(mSMSreceiver, mIntentFilter);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mSMSreceiver);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
