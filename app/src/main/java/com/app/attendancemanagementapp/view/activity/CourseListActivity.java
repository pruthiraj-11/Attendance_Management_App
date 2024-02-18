package com.app.attendancemanagementapp.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.app.attendancemanagementapp.R;
import com.app.attendancemanagementapp.adapter.CourseListAdapter;
import com.app.attendancemanagementapp.model.Course;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class CourseListActivity extends AppCompatActivity {
   private FloatingActionButton addCourseButton;
   private RecyclerView courseRv;
   private String intented_dept,intented_Shift;
   private List<Course>  courseList;
   private DatabaseReference courseRef;
   private CourseListAdapter courseListAdapter;
   private LottieAnimationView lottieAnimationView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_list);

        Toolbar toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        final Intent intent=getIntent();
        intented_dept=intent.getStringExtra("CDEPT");
        intented_Shift=intent.getStringExtra("CSHIFT");
        lottieAnimationView=findViewById(R.id.course_loader);
        TextView noCourseMsg=findViewById(R.id.noCourseMsg);
        addCourseButton=findViewById(R.id.addCBtn);
        courseRv=findViewById(R.id.CourseListRV);
        courseList=new ArrayList<>();
        courseListAdapter=new CourseListAdapter(getApplicationContext(),courseList);
        courseRv.setLayoutManager(new LinearLayoutManager(CourseListActivity.this));
        courseRv.setAdapter(courseListAdapter);

        courseRef= FirebaseDatabase.getInstance().getReference().child("Department").child(intented_dept).child("Course").child(intented_Shift);
        courseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                courseList.clear();
                if(dataSnapshot.exists()){
                    for(DataSnapshot dataSnapshot1:dataSnapshot.getChildren()){
                        if(dataSnapshot1.hasChildren()){
                            Course course=dataSnapshot1.getValue(Course.class);
                            courseList.add(course);
                        }
                    }
                    if (!courseList.isEmpty()) {
                        courseListAdapter.notifyDataSetChanged();
                        lottieAnimationView.setVisibility(View.GONE);
                        courseRv.setVisibility(View.VISIBLE);
                    } else {
                        lottieAnimationView.setVisibility(View.GONE);
                        noCourseMsg.setVisibility(View.VISIBLE);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        addCourseButton.setOnClickListener(v -> {
            Intent intent1=new Intent(CourseListActivity.this,AddCourseActivity.class);
            intent1.putExtra("CDEPT",intented_dept);
            intent1.putExtra("CSHIFT",intented_Shift);
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
}
