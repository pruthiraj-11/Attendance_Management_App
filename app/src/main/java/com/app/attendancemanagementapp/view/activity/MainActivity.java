package com.app.attendancemanagementapp.view.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.app.attendancemanagementapp.R;
import com.app.attendancemanagementapp.model.Admin;
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
}
