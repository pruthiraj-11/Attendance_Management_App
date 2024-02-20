package com.app.attendancemanagementapp.view.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.app.attendancemanagementapp.R;
import com.app.attendancemanagementapp.databinding.ActivityForgotPasswordBinding;
import com.app.attendancemanagementapp.model.Admin;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

import xyz.hasnat.sweettoast.SweetToast;

public class ForgotPasswordActivity extends AppCompatActivity {

    private ActivityForgotPasswordBinding binding;
    private String oldPassword;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityForgotPasswordBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        DatabaseReference adminLogRef=FirebaseDatabase.getInstance().getReference().child("Admin");
        adminLogRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {
                    for(DataSnapshot dataSnapshot1:dataSnapshot.getChildren()) {
                        Admin admin= dataSnapshot1.getValue(Admin.class);
                        oldPassword= Objects.requireNonNull(admin).getPassword();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        binding.passwordresetbtn.setOnClickListener(v -> {
            if (Objects.requireNonNull(binding.oldpasswordreset.getText()).toString().isEmpty()) {
                binding.oldpasswordreset.setError("Can't be empty.");
            } else if (Objects.requireNonNull(binding.newPassword.getText()).toString().isEmpty()) {
                binding.newPassword.setError("Can't be empty.");
            } else if (binding.oldpasswordreset.getText().toString().equals(binding.newPassword.getText().toString())) {
                SweetToast.error(getApplicationContext(),"New password can't be same as old password.");
            } else if (!binding.oldpasswordreset.getText().toString().equals(oldPassword)){
                binding.oldpasswordreset.setError("Wrong old Password.");
            } else {
                FirebaseDatabase.getInstance().getReference().child("Admin").child("AdminCredentials").child("password").setValue(binding.newPassword.getText().toString()).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        SweetToast.success(getApplicationContext(),"Password Changed successfully.");
                        binding.oldpasswordreset.setText("");
                        binding.newPassword.setText("");
                    }
                });
            }
        });

        binding.backbtn.setOnClickListener(v -> {
            startActivity(new Intent(ForgotPasswordActivity.this, AdminActivity.class));
            finish();
        });

    }
}