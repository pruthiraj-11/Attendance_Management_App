package com.app.attendancemanagementapp.view.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;

import com.app.attendancemanagementapp.R;
import com.app.attendancemanagementapp.adapter.IntroAdapter;
import com.app.attendancemanagementapp.model.IntroContent;
import com.app.attendancemanagementapp.storage.SaveUser;
import com.google.android.material.tabs.TabLayout;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.util.ArrayList;
import java.util.List;

public class IntroActivity extends AppCompatActivity {

    private ViewPager intro_viewpager;
    private TabLayout intro_tab_indecator;
    private Button intro_start_btn;
    private TextView intro_next_textview;
    int position;
    private IntroAdapter introAdapter;
    Animation into_start_btn_anim;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);

        intro_viewpager = findViewById(R.id.intro_viewpager_id);
        intro_tab_indecator = findViewById(R.id.into_tab_indecator_id);
        intro_next_textview = findViewById(R.id.intro_next_textview_id);
        intro_start_btn = findViewById(R.id.intro_start_btn_id);
        into_start_btn_anim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.button_animation);

        final List<IntroContent> intro_content_list = new ArrayList<>();
        String intro_details = getResources().getString(R.string.intro_details);
        String intro_contacts = getResources().getString(R.string.intro_contacts);
        String intro_blood_donation = getResources().getString(R.string.intro_blood_donation);
        String intro_find_location = getResources().getString(R.string.intro_find_location);

        intro_content_list.add(new IntroContent(intro_contacts, intro_details, R.drawable.resume));
        intro_content_list.add(new IntroContent(intro_blood_donation, intro_details, R.drawable.course_allo));
        intro_content_list.add((new IntroContent(intro_find_location, intro_details, R.drawable.attendance_icon)));

        introAdapter = new IntroAdapter(this, intro_content_list);
        intro_viewpager.setAdapter(introAdapter);
        intro_tab_indecator.setupWithViewPager(intro_viewpager);

        requestPermissions();

        intro_next_textview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                position = intro_viewpager.getCurrentItem();
                if (position < intro_content_list.size()) {
                    position++;
                    intro_viewpager.setCurrentItem(position);
                }
                if (position == intro_content_list.size() - 1) {
                    intro_load_last_Screen();
                }
            }
        });
        intro_tab_indecator.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getPosition() == intro_content_list.size() - 1) {
                    intro_load_last_Screen();
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }

        });
        intro_start_btn.setOnClickListener(v -> {
            new SaveUser().introSave(getApplicationContext(),true);
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
            finish();
        });
    }

    private void intro_load_last_Screen() {
        intro_next_textview.setVisibility(View.INVISIBLE);
        intro_start_btn.setVisibility(View.VISIBLE);
        intro_tab_indecator.setVisibility(View.INVISIBLE);
        intro_start_btn.setAnimation(into_start_btn_anim);
    }

    private void requestPermissions() {
        Dexter.withContext(this)
                .withPermissions(Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.RECEIVE_SMS,
                        Manifest.permission.SEND_SMS,
                        Manifest.permission.SEND_SMS)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport multiplePermissionsReport) {
                        if (multiplePermissionsReport.areAllPermissionsGranted()) {
                            Toast.makeText(IntroActivity.this, "All the permissions are granted..", Toast.LENGTH_SHORT).show();
                        }
                        if (multiplePermissionsReport.isAnyPermissionPermanentlyDenied()) {
                            showSettingsDialog();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> list, PermissionToken permissionToken) {
                        permissionToken.continuePermissionRequest();
                    }
                }).withErrorListener(error -> {
                    Toast.makeText(getApplicationContext(), "Error occurred! ", Toast.LENGTH_SHORT).show();
                }).onSameThread().check();
    }

    private void showSettingsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(IntroActivity.this);
        builder.setTitle("Need Permissions");
        builder.setMessage("This app needs permission to use this feature. You can grant them in app settings.");
        builder.setPositiveButton("Goto Settings", (dialog, which) -> {
            dialog.cancel();
            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            Uri uri = Uri.fromParts("package", getPackageName(), null);
            intent.setData(uri);
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> {
            dialog.cancel();
        });
        builder.setCancelable(false);
        builder.show();
    }
}
