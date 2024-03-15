package com.app.attendancemanagementapp.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.app.attendancemanagementapp.R;
import com.app.attendancemanagementapp.adapter.StudentListAdapter;
import com.app.attendancemanagementapp.model.Student;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import xyz.hasnat.sweettoast.SweetToast;

public class StudentListActivity extends AppCompatActivity {
    private final List<Student> studentList=new ArrayList<>();
    private RecyclerView studentListRV;
    private String intentDept;
    private String intentBatch;
    private String intentShift;
    private StudentListAdapter studentListAdapter;
    private LottieAnimationView lottieAnimationView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_list);

        Toolbar studentListToolbar = findViewById(R.id.studentListToolbar);
        FloatingActionButton addStudentBtn = findViewById(R.id.addStudentBtn);
        TextView noStudentIndi=findViewById(R.id.noStudentMsg);
        studentListRV=findViewById(R.id.StudentListRV);
        lottieAnimationView=findViewById(R.id.animation_view);
        Intent intent=getIntent();
        intentDept= intent.getStringExtra("DEPT");
        intentBatch=intent.getStringExtra("BATCH");
        intentShift=intent.getStringExtra("SHIFT");

        setSupportActionBar(studentListToolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        studentListAdapter=new StudentListAdapter(StudentListActivity.this,studentList);
        studentListRV.setLayoutManager(new LinearLayoutManager(StudentListActivity.this));
        studentListRV.setAdapter(studentListAdapter);

        DatabaseReference studentListRef = FirebaseDatabase.getInstance().getReference().child("Department").child(intentDept).child("Student").child(intentBatch).child("allstudent").child(intentShift);
        studentListRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                studentList.clear();
                if(dataSnapshot.exists()) {
                    for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                        if (dataSnapshot1.hasChildren()) {
                            Student student = dataSnapshot1.getValue(Student.class);
                            studentList.add(student);
                        }
                    }
                    if (!studentList.isEmpty()) {
                        lottieAnimationView.setVisibility(View.GONE);
                        studentListAdapter.notifyDataSetChanged();
                        studentListRV.setVisibility(View.VISIBLE);
                    } else {
                        lottieAnimationView.setVisibility(View.GONE);
                        noStudentIndi.setVisibility(View.VISIBLE);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        addStudentBtn.setOnClickListener(v -> {
            Intent intent1=new Intent(StudentListActivity.this,AddStudentActivity.class);
            intent1.putExtra("DEPT",intentDept);
            intent1.putExtra("BATCH",intentBatch);
            intent1.putExtra("SHIFT",intentShift);
            startActivity(intent1);
        });
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==android.R.id.home){
            this.finish();
        }
        return super.onOptionsItemSelected(item);
    }

    public boolean onContextItemSelected(@NonNull MenuItem item) {
        if (Objects.equals(item.getTitle(), "Delete")) {
            new MaterialAlertDialogBuilder(StudentListActivity.this)
                    .setMessage("Are you sure to remove this student?")
                    .setPositiveButton("Yes", (dialogInterface, i) -> {
                        studentListAdapter.removeItem(item.getGroupId());
                        SweetToast.success(StudentListActivity.this,"Student Removed.");
                    }).setNegativeButton("No", (dialogInterface, i) -> dialogInterface.dismiss()).setCancelable(false).show();
        }
        return super.onContextItemSelected(item);
    }
}
