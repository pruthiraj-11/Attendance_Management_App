package com.app.attendancemanagementapp.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.app.attendancemanagementapp.R;
import com.app.attendancemanagementapp.model.Course;
import com.app.attendancemanagementapp.model.Teacher;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import xyz.hasnat.sweettoast.SweetToast;

public class AddCourseActivity extends AppCompatActivity {
    private Spinner courseTeacherSp;
    private EditText courseCodeET;
    private DatabaseReference batchListRef;
    private DatabaseReference courseRef;
    private DatabaseReference courseTitleRef;
    private DatabaseReference courseCodeRef;
    private List<String> teacherList;
    private List<String> teacherIDList;
    private List<String> courseCodeList;
    private String selected_batch,selected_teacher,selected_teacherID,selected_course_title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_course);

        Toolbar toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        Spinner courseBatchSp = findViewById(R.id.courseBatchSp);
        courseTeacherSp=findViewById(R.id.courseTeacherSp);
        Spinner courseTitleSP = findViewById(R.id.courseTitleSp);
        courseCodeET=findViewById(R.id.courseCode);
        Button addCourseBtn = findViewById(R.id.addCourseBtn);

        Intent intent=getIntent();
        String intentedDep = intent.getStringExtra("CDEPT");
        String intentedShift = intent.getStringExtra("CSHIFT");
        teacherList=new ArrayList<>();
        String [] batchList1=getResources().getStringArray(R.array.batch);
        List<String> batchList = new ArrayList<>(Arrays.asList(batchList1));
        teacherIDList=new ArrayList<>();
        courseCodeList=new ArrayList<>();

        String [] courseTitle=getResources().getStringArray(R.array.course);
        String [] courseTitleCode=getResources().getStringArray(R.array.course_code);
        List<String> courseTitleList = new ArrayList<>(Arrays.asList(courseTitle));
        courseTitleList.add(0,"Select course");
        courseCodeList.addAll(Arrays.asList(courseTitleCode));
        courseCodeList.add(0,"Course Code");

        ArrayAdapter<String> arrayAdapter=new ArrayAdapter<>(AddCourseActivity.this,android.R.layout.simple_list_item_1, courseTitleList);
        courseTitleSP.setAdapter(arrayAdapter);
        courseTitleSP.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selected_course_title=parent.getItemAtPosition(position).toString();
                courseCodeET.setText(courseCodeList.get(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        courseRef=FirebaseDatabase.getInstance().getReference().child("Department").child(Objects.requireNonNull(intentedDep)).child("Course").child(Objects.requireNonNull(intentedShift));
//        courseTitleRef=FirebaseDatabase.getInstance().getReference().child("Department").child(intentedDep).child("Courselist");
//        courseTitleRef.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                CourseTitleList.clear();
//                CourseTitleList.add(0,"Select course");
//                courseCodeList.add(0,"Course Code");
//                if (dataSnapshot.exists()){
//                    for (DataSnapshot ds1:dataSnapshot.getChildren()){
//                        String key=ds1.getKey();
//                        String key1= Objects.requireNonNull(ds1.getValue()).toString();
//                        CourseTitleList.add(key);
//                        courseCodeList.add(key1);
//                    }
//                    ArrayAdapter<String> arrayAdapter=new ArrayAdapter<>(AddCourseActivity.this,android.R.layout.simple_list_item_1,CourseTitleList);
//                    courseTitleSP.setAdapter(arrayAdapter);
//                    courseTitleSP.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//                        @Override
//                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                            selected_course_title=parent.getItemAtPosition(position).toString();
//                            courseCodeET.setText(courseCodeList.get(position));
//                        }
//
//                        @Override
//                        public void onNothingSelected(AdapterView<?> parent) {
//
//                        }
//                    });
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });

        DatabaseReference teacherListRef = FirebaseDatabase.getInstance().getReference().child("Department").child(intentedDep).child("Teacher").child(intentedShift);
        teacherListRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                teacherList.clear();
                teacherIDList.clear();
                teacherList.add(0,"Select teacher");
                teacherIDList.add(0,"id");
                if(dataSnapshot.exists()){
                        for(DataSnapshot dataSnapshot2:dataSnapshot.getChildren()){
                            if(dataSnapshot2.hasChildren()){
                                Teacher teacher=dataSnapshot2.getValue(Teacher.class);
                                String name= Objects.requireNonNull(teacher).getName();
                                String id=teacher.getId();
                                teacherList.add(name);
                                teacherIDList.add(id);
                            }
                        }
                    ArrayAdapter<String> arrayAdapter=new ArrayAdapter<>(AddCourseActivity.this,android.R.layout.simple_list_item_1,teacherList);
                    courseTeacherSp.setAdapter(arrayAdapter);
                    courseTeacherSp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            selected_teacher=parent.getItemAtPosition(position).toString();
                            selected_teacherID=teacherIDList.get(position);
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {

                        }
                    });
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        batchList.add(0,"Select batch");
        ArrayAdapter<String> arrayAdapter1=new ArrayAdapter<>(AddCourseActivity.this,android.R.layout.simple_list_item_1, batchList);
        courseBatchSp.setAdapter(arrayAdapter1);
        courseBatchSp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selected_batch=parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
//        batchListRef=FirebaseDatabase.getInstance().getReference().child("Department").child(intentedDep).child("Student");
//        batchListRef.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                batchList.clear();
//                batchList.add("Select batch");
//                if(dataSnapshot.exists()){
//                    for(DataSnapshot dataSnapshot1:dataSnapshot.getChildren()){
//                        if(dataSnapshot1.hasChildren()){
//                             String key=dataSnapshot1.getKey();
//                            batchList.add(key);
//                        }
//                    }
//                    ArrayAdapter<String> arrayAdapter=new ArrayAdapter<>(AddCourseActivity.this,android.R.layout.simple_list_item_1,batchList);
//                    courseBatchSp.setAdapter(arrayAdapter);
//                    courseBatchSp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//                        @Override
//                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                            selected_batch=parent.getItemAtPosition(position).toString();
//                        }
//
//                        @Override
//                        public void onNothingSelected(AdapterView<?> parent) {
//
//                        }
//                    });
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });
        addCourseBtn.setOnClickListener(v -> addCourse());
    }

    private void addCourse(){
        String course1=courseCodeET.getText().toString();
        if(selected_course_title.equals("Select course")){
            SweetToast.error(getApplicationContext(),"Select course");
        }else if(course1.isEmpty()){
            courseCodeET.setError("Give course code");
        }else if(selected_batch.equals("Select batch")){
            SweetToast.warning(getApplicationContext(),"Select batch");
        }else if(selected_teacher.equals("Select teacher")) {
            SweetToast.warning(getApplicationContext(), "Select teacher");
        } else {
            String key=courseRef.push().getKey();
            Course course=new Course("",selected_course_title,course1,selected_teacher,selected_teacherID,selected_batch);
            courseRef.child(Objects.requireNonNull(key)).setValue(course).addOnCompleteListener(task -> {
                if(task.isSuccessful()){
                    SweetToast.success(getApplicationContext(),"Course added successfully");
                }
            });
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==android.R.id.home){
            this.finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
