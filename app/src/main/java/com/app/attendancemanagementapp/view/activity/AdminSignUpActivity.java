package com.app.attendancemanagementapp.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.app.attendancemanagementapp.databinding.ActivityAdminSignUpBinding;
import com.app.attendancemanagementapp.model.Admin;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;

public class AdminSignUpActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private String uId;
    private DatabaseReference adminRef;
    private ActivityAdminSignUpBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityAdminSignUpBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mAuth=FirebaseAuth.getInstance();

        binding.signUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               signUp();
            }
        });
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                startActivity(new Intent(AdminSignUpActivity.this, AdminActivity.class));
                finish();
            }
        });
    }

    private void signUp() {
        final String name= Objects.requireNonNull(binding.adminSignUpNameET.getText()).toString().trim();
        final String email= Objects.requireNonNull(binding.adminSignUpEmailET.getText()).toString().trim();
        String password= Objects.requireNonNull(binding.adminSignUpPasswordET.getText()).toString().trim();
        String confirmPassword= Objects.requireNonNull(binding.adminSignUPConfirmPassET.getText()).toString().trim();

        if(name.isEmpty()){
            binding.adminSignUpNameET.setError("Enter admin name");
            binding.adminSignUpNameET.requestFocus();
        } else if(email.isEmpty()){
            binding.adminSignUpEmailET.setError("Enter admin email");
            binding.adminSignUpEmailET.requestFocus();
        }
        else if(password.isEmpty()){
            binding.adminSignUpPasswordET.setError("Enter password");
            binding.adminSignUpPasswordET.requestFocus();
        }
        else if(confirmPassword.isEmpty()){
            binding.adminSignUPConfirmPassET.setError("Confirm your password");
            binding.adminSignUPConfirmPassET.requestFocus();
        }  else if(!confirmPassword.equals(password)){
            binding.adminSignUPConfirmPassET.setError("Password does not match");
            binding.adminSignUPConfirmPassET.requestFocus();
        }else {
          mAuth.createUserWithEmailAndPassword(email,password)
                  .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                      @Override
                      public void onComplete(@NonNull Task<AuthResult> task) {
                          if(task.isSuccessful()){
                             uId= Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
                             adminRef= FirebaseDatabase.getInstance().getReference().child("Admin").child("AdminCredentials");
                              Admin admin=new Admin(
                                      name,
                                      email,
                                      "",
                                      "",
                                      "",
                                      uId,
                                      "",
                                      "",
                                      "",
                                      "",
                                      0,
                                      "",
                                      "",
                                      0,
                                      "",
                                      password);
                              adminRef.setValue(admin).addOnCompleteListener(new OnCompleteListener<Void>() {
                                          @Override
                                          public void onComplete(@NonNull Task<Void> task) {
                                              if(task.isSuccessful()){
                                                  mAuth.signOut();
                                                  Intent intent=new Intent(AdminSignUpActivity.this,AdminLoginActivity.class);
                                                  startActivity(intent);
                                                  finish();
                                                  Toast.makeText(getApplicationContext(),"Sign up successfully",Toast.LENGTH_SHORT).show();
                                              } else {
                                               mAuth.signOut();
                                              }
                                          }
                                      }).addOnFailureListener(new OnFailureListener() {
                                  @Override
                                  public void onFailure(@NonNull Exception e) {
                                      Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_SHORT).show();
                                  }
                              });
                          }else {
                              binding.addSignUpLL.setVisibility(View.VISIBLE);
                              binding.adminSignUPPB.setVisibility(View.GONE);
                              Toast.makeText(getApplicationContext(), Objects.requireNonNull(task.getException()).getMessage(),Toast.LENGTH_SHORT).show();
                          }
                      }
                  });
          binding.addSignUpLL.setVisibility(View.GONE);
          binding.adminSignUPPB.setVisibility(View.VISIBLE);
        }
    }
}