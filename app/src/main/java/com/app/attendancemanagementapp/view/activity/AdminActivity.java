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
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.app.attendancemanagementapp.R;
import com.app.attendancemanagementapp.storage.SaveUser;
import com.app.attendancemanagementapp.view.fragment.AdminHomeFragment;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;
import xyz.hasnat.sweettoast.SweetToast;


public class AdminActivity extends AppCompatActivity {

    private CircleImageView userProfile;
    private ActivityResultLauncher<String> activityResultLauncher;
    private ActivityResultLauncher<Intent> launcher;
    private String adminProfileURL;
    private FirebaseStorage firebaseStorage;
    private BottomSheetDialog bottomSheetDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        Toolbar admin_toolbar = findViewById(R.id.admin_toolbar_id);
        setSupportActionBar(admin_toolbar);

        DrawerLayout admin_nav_drawer = findViewById(R.id.admin_nav_drawer_id);
        NavigationView admin_nav_view = findViewById(R.id.admin_nav_view_id);

        Intent intent=getIntent();
        String username=intent.getStringExtra("name");
        String usermail=intent.getStringExtra("email");
        String profile_image=intent.getStringExtra("profile_image");

        firebaseStorage=FirebaseStorage.getInstance();

        View headerView = admin_nav_view.getHeaderView(0);
        TextView userName= (TextView) headerView.findViewById(R.id.headerName);
        TextView userEmail= (TextView) headerView.findViewById(R.id.emailText_id);
        userProfile= (CircleImageView) headerView.findViewById(R.id.adminProfile);
        userName.setText(username);
        userEmail.setText(usermail);
        Glide.with(getApplicationContext()).load(profile_image).placeholder(R.drawable.placeholder_profile).into(userProfile);

        userProfile.setOnClickListener(v -> {
            admin_nav_drawer.closeDrawer(GravityCompat.START);
            showBottomSheetDialog();
        });

        activityResultLauncher=registerForActivityResult(new ActivityResultContracts.GetContent(), o -> {
            if(o!=null){
                userProfile.setImageURI(o);
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
                    userProfile.setImageBitmap(bitmap);
                    uploadImage(bitmap);
                }
            }
        });

        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, admin_nav_drawer, admin_toolbar,R.string.nav_drawer_open, R.string.nav_drawer_close);
        actionBarDrawerToggle.getDrawerArrowDrawable().setColor(getColor(R.color.colorWhite));

        admin_nav_drawer.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();

        if(savedInstanceState==null){
            getSupportFragmentManager().beginTransaction().replace(R.id.admin_fragment_container,new AdminHomeFragment()).commit();
        }
        admin_nav_view.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                admin_nav_drawer.closeDrawer(GravityCompat.START);
                if (menuItem.getItemId()==R.id.menu_item_logout){
                    SaveUser saveUser=new SaveUser();
                    saveUser.admin_saveData(AdminActivity.this,false);
                    startActivity(new Intent(AdminActivity.this,MainActivity.class));
                    finish();
                } else if (menuItem.getItemId()==R.id.menu_add_new_admin) {
                    startActivity(new Intent(AdminActivity.this, AdminSignUpActivity.class));
                    finish();
                } else if (menuItem.getItemId()==R.id.menu_item_change_password) {
                    startActivity(new Intent(AdminActivity.this, ForgotPasswordActivity.class));
                }
                return true;
            }
        });

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (admin_nav_drawer.isDrawerOpen(GravityCompat.START)) {
                    admin_nav_drawer.closeDrawer(GravityCompat.START);
                } else {
                    finish();
                }
            }
        });
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
                    FirebaseDatabase.getInstance().getReference().child("Admin").child("AdminCredentials").child("profile_image").setValue(adminProfileURL).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            SweetToast.success(getApplicationContext(), "Profile photo updated.");
                        }
                    });
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
