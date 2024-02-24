package com.app.attendancemanagementapp.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.app.attendancemanagementapp.R;
import com.app.attendancemanagementapp.adapter.TakeAttendanceRVAdapter;
import com.app.attendancemanagementapp.model.Student;
import com.app.attendancemanagementapp.storage.SaveUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import xyz.hasnat.sweettoast.SweetToast;

public class StudentLoginActivity extends AppCompatActivity {
    private EditText studentIDET, studentPassET;
    private DatabaseReference studentRef;
    private List<Student> studentList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_login);

        studentIDET = findViewById(R.id.studentLoginID);
        studentPassET = findViewById(R.id.studentLoginpass);
        Button studentLoginBtn = findViewById(R.id.studentLoginBtn);
        TextView forgot_password_view=findViewById(R.id.forgot_password_view);
        studentLoginBtn.setOnClickListener(v -> studentLogIn());

        Button cancelBtn, confirmBtn;
        EditText studentmail;
        final AlertDialog dialog = new AlertDialog.Builder(StudentLoginActivity.this).create();
        View view = LayoutInflater.from(StudentLoginActivity.this).inflate(R.layout.student_password_forgot_layout, null);
        cancelBtn = view.findViewById(R.id.cancelBtn);
        confirmBtn = view.findViewById(R.id.confirmBtn);
        studentmail = view.findViewById(R.id.studentmail);
        dialog.setCancelable(false);
        dialog.setView(view);
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String studentMail=studentmail.getText().toString().trim();

            }
        });
        forgot_password_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.show();
            }
        });
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                startActivity(new Intent(StudentLoginActivity.this, MainActivity.class));
                finish();
            }
        });
    }

    private void studentLogIn() {
        final String id = studentIDET.getText().toString();
        final String pass = studentPassET.getText().toString();
        if (id.isEmpty()) {
            studentIDET.setError("Enter your registration number");
            studentIDET.requestFocus();
        } else if (pass.isEmpty()) {
            studentPassET.setError("Enter your password");
            studentIDET.requestFocus();
        } else {
            studentRef = FirebaseDatabase.getInstance().getReference().child("Department");
            studentRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    studentList.clear();
                    if (dataSnapshot.exists()) {
                        for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                            String dept = dataSnapshot1.getKey();
                            DatabaseReference deptRef = studentRef.child(Objects.requireNonNull(dept)).child("Student");
                            deptRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    for (DataSnapshot ds1 : dataSnapshot.getChildren()) {
                                        for (DataSnapshot ds2 : ds1.getChildren()) {
                                            for (DataSnapshot ds3 : ds2.getChildren()) {
                                                for (DataSnapshot ds4 : ds3.getChildren()) {
                                                    if (ds4.hasChildren()) {
                                                        Student student = ds4.getValue(Student.class);
                                                        if (Objects.requireNonNull(student).getId().equals(id)) {
                                                            new SaveUser().saveStudent(getApplicationContext(), student);
                                                            studentList.add(student);
                                                            if (studentList.get(0).getId().equals(id) && studentList.get(0).getPassword().equals(pass)) {
                                                                SweetToast.success(getApplicationContext(), "Login Successfully");
                                                                new SaveUser().Student_saveData(getApplicationContext(), true);
                                                                Intent intent = new Intent(StudentLoginActivity.this, StudentActivity.class);
                                                                startActivity(intent);
                                                                finish();
                                                            } else {
                                                                SweetToast.error(getApplicationContext(), "You Entered wrong Id or password");
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }
}
