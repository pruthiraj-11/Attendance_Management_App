package com.app.attendancemanagementapp.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;

import com.app.attendancemanagementapp.R;
import com.app.attendancemanagementapp.storage.SaveUser;
import com.app.attendancemanagementapp.view.fragment.AdminHomeFragment;
import com.bumptech.glide.Glide;
import com.google.android.material.navigation.NavigationView;

import de.hdodenhof.circleimageview.CircleImageView;


public class AdminActivity extends AppCompatActivity {

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

        View headerView = admin_nav_view.getHeaderView(0);
        TextView userName= (TextView) headerView.findViewById(R.id.headerName);
        TextView userEmail= (TextView) headerView.findViewById(R.id.emailText_id);
        CircleImageView userProfile= (CircleImageView) headerView.findViewById(R.id.adminProfile);
        userName.setText(username);
        userEmail.setText(usermail);
        Glide.with(getApplicationContext()).load(profile_image).placeholder(R.drawable.placeholder_profile).into(userProfile);

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
                    finish();
                }
                return true;
            }
        });
    }
}
