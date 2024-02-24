package com.app.attendancemanagementapp.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
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

public class TakeAttendanceActivity extends AppCompatActivity {

    private String intentded_course, intentDate;
    private DatabaseReference studentRef, deptref, batchRef, attendanceRef, presentRef, absentRef;
    private String dept, batch;
    private List<Student> studentList = new ArrayList<>();
    private ListView listView;
    private RecyclerView taRV;
    private TakeAttendanceRVAdapter takeAttendanceAdapter;
    private Button submitBtn;
    private LottieAnimationView lottieAnimationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_take_attendance);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        Intent intent = getIntent();
        intentded_course = intent.getStringExtra("SC");
        intentDate = intent.getStringExtra("DATE");
        //  SweetToast.success(getApplicationContext(),intentDate);
        lottieAnimationView=findViewById(R.id.take_attendance_loader);
        submitBtn = findViewById(R.id.submitbtn);
        taRV = findViewById(R.id.tARv);
        TextView noStudentMsgAttendance=findViewById(R.id.noStudentMsgAttendance);
        studentRef = FirebaseDatabase.getInstance().getReference().child("Department");

        attendanceRef = FirebaseDatabase.getInstance().getReference().child("Department").child(new SaveUser().teacher_DeptLoadData(getApplicationContext())).child("Attendance").child(new SaveUser().teacher_ShiftLoadData(getApplicationContext())).child(intentded_course).child(intentDate);

        presentRef = attendanceRef.child("Present");
        absentRef = attendanceRef.child("Absent");
        studentRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                        dept = dataSnapshot1.getKey();
                        deptref = studentRef.child(new SaveUser().getTeacher(getApplicationContext()).getDepartment()).child("Student");
                        deptref.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                studentList.clear();
                                if (dataSnapshot.exists()) {
                                    for (DataSnapshot ds1 : dataSnapshot.getChildren()) {
                                        for (DataSnapshot ds2 : ds1.child("allstudent").child(new SaveUser().teacher_ShiftLoadData(getApplicationContext())).getChildren()) {
                                            if (ds2.hasChildren()) {
                                                Student student = ds2.getValue(Student.class);
                                                if (Objects.requireNonNull(student).getCourse().contains(intentded_course)) {
                                                    studentList.add(student);
                                                }
                                            }

                                        }
                                    }
                                    if (!studentList.isEmpty()) {
                                        takeAttendanceAdapter = new TakeAttendanceRVAdapter(getApplicationContext(), studentList);
                                        taRV.setLayoutManager(new LinearLayoutManager(TakeAttendanceActivity.this));
                                        takeAttendanceAdapter.notifyDataSetChanged();
                                        taRV.setAdapter(takeAttendanceAdapter);
                                        lottieAnimationView.setVisibility(View.GONE);
                                        taRV.setVisibility(View.VISIBLE);
                                    } else {
                                        lottieAnimationView.setVisibility(View.GONE);
                                        noStudentMsgAttendance.setVisibility(View.VISIBLE);
                                        submitBtn.setText("Go Back");
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
        TakeAttendanceRVAdapter.presentList.clear();
        TakeAttendanceRVAdapter.absentList.clear();
        submitBtn.setOnClickListener(v -> {
            if (submitBtn.getText().equals("Go Back")) {
                finish();
            } else {
                final AlertDialog dialog = new AlertDialog.Builder(TakeAttendanceActivity.this).create();
                View view = LayoutInflater.from(TakeAttendanceActivity.this).inflate(R.layout.attendancepopup, null);
                TextView total, present, absent;
                Button cancelBtn, confirmBtn;
                total = view.findViewById(R.id.TotalStudentTV);
                present = view.findViewById(R.id.presentStudentTV);
                absent = view.findViewById(R.id.absentStudentTV);
                cancelBtn = view.findViewById(R.id.canclebtn);
                confirmBtn = view.findViewById(R.id.confirmbtn);
                total.setText(Integer.toString(studentList.size()));
                present.setText(Integer.toString(TakeAttendanceRVAdapter.presentList.size()));
                absent.setText(Integer.toString(TakeAttendanceRVAdapter.absentList.size()));
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
                        String presentstudentID = "";
                        for (int i = 0; i < TakeAttendanceRVAdapter.presentList.size(); i++) {
                            presentstudentID = TakeAttendanceRVAdapter.presentList.get(i);
                            presentRef.child(presentstudentID).setValue(presentstudentID);
                        }

                        String absentstudentID = "";
                        for (int i = 0; i < TakeAttendanceRVAdapter.absentList.size(); i++) {
                            absentstudentID = TakeAttendanceRVAdapter.absentList.get(i);
                            absentRef.child(absentstudentID).setValue(absentstudentID);
                        }
                        // studentList.clear();
                        TakeAttendanceRVAdapter.presentList.clear();
                        TakeAttendanceRVAdapter.absentList.clear();
                        dialog.cancel();
                        SweetToast.success(getApplicationContext(), "Attendance data added successfully.");
                    }
                });
                dialog.show();
            }
        });
    }

    private void popup() {

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            this.finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
