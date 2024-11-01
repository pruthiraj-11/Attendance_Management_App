package com.app.attendancemanagementapp.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
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
import com.app.attendancemanagementapp.adapter.CheckableSpinnerAdapter;
import com.app.attendancemanagementapp.model.Course;
import com.app.attendancemanagementapp.model.SpinnerObject;
import com.app.attendancemanagementapp.model.Student;
import com.app.attendancemanagementapp.model.StudentCredential;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import xyz.hasnat.sweettoast.SweetToast;

public class AddStudentActivity extends AppCompatActivity {

    private Spinner studentDeptSP;
    private Spinner studentCourseSp;
    private EditText addStudentName,addStudentEmail,addStudentID,addStudentPhone,addStudentPassword;
    private String[] dept;
    private String[] semester;
    private String[] year;
    private ArrayList<String> courseNamelist=new ArrayList<>();
    private ArrayList<String> course_codeList=new ArrayList<>();
    private ArrayAdapter<String> deptAdapter;
    private ArrayAdapter<String> courseAdapter;
    private String SelectedDept;
    private String SelectedYear;
    private String SelectedSemister;
    private String intentDept;
    private String intentBatch;
    private String intentShift;
    private DatabaseReference studentRef;
    private DatabaseReference attendanceRef;
    private final List<CheckableSpinnerAdapter.SpinnerItem<SpinnerObject>> course_spinner_items = new ArrayList<>();
    private final List<CheckableSpinnerAdapter.SpinnerCode<SpinnerObject>> course_spinner_code = new ArrayList<>();
    private final Set<SpinnerObject> course_selected_items = new HashSet<>();
    private final Set<SpinnerObject> selected_course=new HashSet<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_student);
        //chck duplicacy
        Toolbar addStudentToolbar = findViewById(R.id.addStudentToolbar);
        setSupportActionBar(addStudentToolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        Intent intent=getIntent();

        intentDept= intent.getStringExtra("DEPT");
        intentBatch=intent.getStringExtra("BATCH");
        intentShift=intent.getStringExtra("SHIFT");

        //studentDeptSP=findViewById(R.id.addStudentDept);
        Spinner studentSemesterSp = findViewById(R.id.addStudentSemester);
        Spinner studentYearSp = findViewById(R.id.addStudentYear);
        studentCourseSp=findViewById(R.id.addStudentCourse);
        Button addStudentButton = findViewById(R.id.addStudentBtn);
        addStudentName=findViewById(R.id.addStudentName);
        addStudentEmail=findViewById(R.id.addStudentEmail);
        addStudentID=findViewById(R.id.addStudentID);
        addStudentPhone=findViewById(R.id.addStudentPhone);
        addStudentPassword=findViewById(R.id.addStudentPassword);

        studentRef= FirebaseDatabase.getInstance().getReference().child("Department").child(intentDept).child("Student").child(intentBatch).child("allstudent").child(intentShift);
        //attendanceRef=FirebaseDatabase.getInstance().getReference().child("Department").child(intentDept).child("Student").child(intentBatch).child("attendance");

        dept=getResources().getStringArray(R.array.department);
        semester=getResources().getStringArray(R.array.semester);
        year=getResources().getStringArray(R.array.year);

        DatabaseReference coureRef = FirebaseDatabase.getInstance().getReference().child("Department").child(intentDept).child("Course").child(intentShift);
        coureRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                courseNamelist.clear();
                course_codeList.clear();
                if(dataSnapshot.exists()){
                    for(DataSnapshot dataSnapshot1:dataSnapshot.getChildren()) {
                        if(dataSnapshot1.hasChildren()) {
                            Course course=dataSnapshot1.getValue(Course.class);
                            String name= Objects.requireNonNull(course).getCourse_name();
                            String code=course.getCourse_code();
                            courseNamelist.add(name);
                            course_codeList.add(code);
                        }
                    }
                    List<SpinnerObject> all_objects = new ArrayList<>();
                    for (int i = 0; i < courseNamelist.size(); i++) {
                        SpinnerObject myObject=new SpinnerObject();
                        myObject.setmName(courseNamelist.get(i));
                        myObject.setC_code(course_codeList.get(i));
                        all_objects.add(myObject);
                    }
                    for (SpinnerObject o : all_objects) {
                        course_spinner_items.add(new CheckableSpinnerAdapter.SpinnerItem<>(o,o.getmName()));
                        course_spinner_code.add(new CheckableSpinnerAdapter.SpinnerCode<>(o,o.getC_code()));
                    }
                    String headerText = "Select Courses";
                    CheckableSpinnerAdapter<SpinnerObject> cadapter = new CheckableSpinnerAdapter<>(AddStudentActivity.this,headerText,course_spinner_items,course_spinner_code,course_selected_items,selected_course);
                    studentCourseSp.setAdapter(cadapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        ArrayAdapter<String> semesterAdapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_list_item_1, semester);
        ArrayAdapter<String> yearAdapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_list_item_1, year);
       // courseAdapter=new ArrayAdapter<>(getApplicationContext(),android.R.layout.simple_list_item_1,course);
        studentSemesterSp.setAdapter(semesterAdapter);
        studentYearSp.setAdapter(yearAdapter);
        studentSemesterSp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                SelectedSemister=parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
       studentYearSp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
           @Override
           public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
               SelectedYear=parent.getItemAtPosition(position).toString();
           }

           @Override
           public void onNothingSelected(AdapterView<?> parent) {

           }
       });
        addStudentButton.setOnClickListener(v -> addStudent());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==android.R.id.home){
            this.finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private void addStudent(){
        StringBuilder stringBuildert=new StringBuilder();
        for(SpinnerObject so:course_selected_items){
            stringBuildert.append(so.getmName().concat(","));
        } StringBuilder stringBuilderc=new StringBuilder();
        for(SpinnerObject so:selected_course){
            stringBuilderc.append(so.getC_code().concat(","));
        }
        String name=addStudentName.getText().toString().trim();
        String email=addStudentEmail.getText().toString().trim();
        String ID=addStudentID.getText().toString().trim();
        String phone=addStudentPhone.getText().toString().trim();
        String password=addStudentPassword.getText().toString().trim();

       if(name.isEmpty()){
           addStudentName.setError("Enter student name");
           addStudentName.requestFocus();
       } else if(email.isEmpty()){
           addStudentEmail.setError("Enter student email");
           addStudentEmail.requestFocus();
        }else if(phone.isEmpty()){
           addStudentPhone.setError("Enter Phone Number");
           addStudentPhone.requestFocus();
       } else if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
           addStudentEmail.setError("Enter valid email");
           addStudentEmail.requestFocus();
       } else if (password.isEmpty()) {
           addStudentPassword.setError("Enter Password");
           addStudentPassword.requestFocus();
       } else if(SelectedSemister.equals("Select semester")){
           SweetToast.warning(getApplicationContext(),"Select semester");
       }else if(SelectedYear.equals("Select year")){
           SweetToast.warning(getApplicationContext(),"Select year");
       }else if(ID.isEmpty()){
           addStudentID.setError("Enter Student Registration Number");
           addStudentID.requestFocus();
       }else if(stringBuildert.toString().isEmpty()){
           SweetToast.warning(getApplicationContext(),"Select courses");
       }else {
           DatabaseReference databaseReference=FirebaseDatabase.getInstance().getReference().child("AllStudentCredentials");
           final boolean[] isEmailRegistered = {false};
           final boolean[] isRegdNumRegistered = {false};
           databaseReference.addValueEventListener(new ValueEventListener() {
               @Override
               public void onDataChange(@NonNull DataSnapshot snapshot) {
                   if (snapshot.exists()) {
                       for (DataSnapshot dataSnapshot: snapshot.getChildren()) {
                           StudentCredential studentCredential=dataSnapshot.getValue(StudentCredential.class);
                           if (Objects.requireNonNull(studentCredential).getStudentEmail().equals(email)) {
                               isEmailRegistered[0] =true;
                           }
                           if (studentCredential.getStudentRegdNum().equals(ID)) {
                               isRegdNumRegistered[0] =true;
                           }
                       }
                       if (isEmailRegistered[0] && isRegdNumRegistered[0]) {
                           SweetToast.error(getApplicationContext(),"Student already registered.");
                       } else if (isEmailRegistered[0]) {
                           SweetToast.error(getApplicationContext(),"Email already registered.");
                       } else if (isRegdNumRegistered[0]) {
                           SweetToast.error(getApplicationContext(),"Registration number already registered.");
                       } else {
                           String key=studentRef.push().getKey();
                           Student student=new Student(name,ID,SelectedYear,SelectedSemister,intentDept,intentBatch,"",email,phone,"",stringBuildert.toString(),stringBuilderc.toString(),intentShift,password, key);
                           studentRef.child(Objects.requireNonNull(key)).setValue(student).addOnCompleteListener(task -> {
                               if(task.isSuccessful()){
                                   StudentCredential studentCredential=new StudentCredential(email,password,ID);
                                   FirebaseDatabase.getInstance().getReference().child("AllStudentCredentials").child(ID).setValue(studentCredential);
                                   SweetToast.success(getApplicationContext(),"Student Data added Successfully");
                               }
                           });
                       }
                   }
               }

               @Override
               public void onCancelled(@NonNull DatabaseError error) {

               }
           });
       }
    }
}
