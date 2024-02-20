package com.app.attendancemanagementapp.view.activity;

import android.content.ActivityNotFoundException;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.app.attendancemanagementapp.R;
import com.app.attendancemanagementapp.databinding.ActivityAdminSignUpBinding;
import com.app.attendancemanagementapp.model.Admin;
import com.app.attendancemanagementapp.storage.SaveUser;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.Objects;

import xyz.hasnat.sweettoast.SweetToast;

public class AdminSignUpActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private String uId;
    private DatabaseReference adminRef;
    private FirebaseStorage firebaseStorage;
    private BottomSheetDialog bottomSheetDialog;
    private ActivityResultLauncher<Intent> launcher;
    private ActivityResultLauncher<String> activityResultLauncher;
    private String adminProfileURL;
    private ActivityAdminSignUpBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityAdminSignUpBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mAuth=FirebaseAuth.getInstance();
        firebaseStorage=FirebaseStorage.getInstance();

        activityResultLauncher=registerForActivityResult(new ActivityResultContracts.GetContent(), o -> {
            if(o!=null){
                binding.profileImage.setImageURI(o);
                Bitmap bitmap = null;
                ContentResolver contentResolver = getApplicationContext().getContentResolver();
                try {
                    if(Build.VERSION.SDK_INT < 28) {
                        bitmap = MediaStore.Images.Media.getBitmap(contentResolver, o);
                    } else {
                        ImageDecoder.Source source = ImageDecoder.createSource(contentResolver, o);
                        bitmap = ImageDecoder.decodeBitmap(source);
                    }
                    uploadImage(bitmap);
                } catch (Exception e) {
                    Log.d("tag", Objects.requireNonNull(e.getMessage()));
                }
            }
        });

        launcher=registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), o -> {
            if (o.getResultCode()== RESULT_OK && o.getData()!=null){
                Bundle bundle=o.getData().getExtras();
                Bitmap bitmap= null;
                if (bundle != null) {
                    bitmap = (Bitmap) bundle.get("data");
                }
                if (bitmap != null) {
                    binding.profileImage.setImageBitmap(bitmap);
                    uploadImage(bitmap);
                }
            }
        });

        binding.signUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               signUp();
            }
        });
        binding.profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showBottomSheetDialog();
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
            binding.adminSignUPPB.setVisibility(View.VISIBLE);
            adminRef= FirebaseDatabase.getInstance().getReference().child("Admin").child("AdminCredentials");
            Admin admin=new Admin(
                    name,
                    email,
                    "",
                    "",
                    "",
                    "",
                    "",
                    "",
                    "",
                    adminProfileURL,
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
                        binding.adminSignUPPB.setVisibility(View.GONE);
                        SweetToast.success(getApplicationContext(),"New Admin Added successfully",Toast.LENGTH_SHORT);
                        SaveUser saveUser=new SaveUser();
                        saveUser.admin_saveData(AdminSignUpActivity.this,false);
                        Intent intent=new Intent(AdminSignUpActivity.this,AdminLoginActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        binding.adminSignUPPB.setVisibility(View.GONE);
                        SweetToast.error(getApplicationContext(),"Try again later.",Toast.LENGTH_SHORT);
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void uploadImage(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        byte[] data = byteArrayOutputStream.toByteArray();
        final StorageReference storageReference= firebaseStorage.getReference().child("profile_pic").child("admin_profile_pic").child("admin");
        UploadTask uploadTask = storageReference.putBytes(data);
        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                storageReference.getDownloadUrl().addOnSuccessListener(uri -> {
                    adminProfileURL =uri.toString();
                    SweetToast.success(getApplicationContext(), "Profile photo updated.", Toast.LENGTH_SHORT);
                });
            }
        }).addOnFailureListener(e -> SweetToast.error(getApplicationContext(), "Failed to upload image.", Toast.LENGTH_SHORT));
    }

    private void showBottomSheetDialog() {
        bottomSheetDialog=new BottomSheetDialog(this);
        bottomSheetDialog.setContentView(R.layout.media_pick_bottom_sheet_dialog);
        bottomSheetDialog.findViewById(R.id.camera_pick).setOnClickListener(view13 -> {
            bottomSheetDialog.dismiss();
            Intent intent=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            try {
                launcher.launch(intent);
            } catch (ActivityNotFoundException e){
                Toast.makeText(this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        bottomSheetDialog.findViewById(R.id.gallery_pick).setOnClickListener(view12 -> {
            bottomSheetDialog.dismiss();
            activityResultLauncher.launch("image/*");
        });
        bottomSheetDialog.show();
        bottomSheetDialog.setOnDismissListener(dialog -> bottomSheetDialog=null);
    }
}
