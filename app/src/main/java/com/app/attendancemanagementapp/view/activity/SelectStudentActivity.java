package com.app.attendancemanagementapp.view.activity;

import android.content.Intent;

import android.os.Bundle;

import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.app.attendancemanagementapp.R;
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

public class SelectStudentActivity extends AppCompatActivity {
    private Spinner deptSP,batchSP,shiftSp;
    private final List<String> deptList=new ArrayList<>();
    private final List<String> batchList=new ArrayList<>();
    private String selectedDept= "";
    private String selectedBatch,selectedShift;
    private ArrayAdapter<String> deptAdapter,batchAdapter,shiftAdapter;
    private DatabaseReference deptRef,batchRef;
    private String[] shift, dept, batch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_student);

        Toolbar toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        deptSP=findViewById(R.id.deptSp);
        batchSP=findViewById(R.id.batchSp);
        Button nextBtn = findViewById(R.id.selectNextBtn);
        shiftSp=findViewById(R.id.shiftSp);
        shift=getResources().getStringArray(R.array.shift);
        dept=getResources().getStringArray(R.array.department);
        batch=getResources().getStringArray(R.array.batch);

        deptAdapter=new ArrayAdapter<>(SelectStudentActivity.this,android.R.layout.simple_list_item_1,dept);
        deptSP.setAdapter(deptAdapter);

        batchAdapter=new ArrayAdapter<>(SelectStudentActivity.this,android.R.layout.simple_list_item_1,batch);
        batchSP.setAdapter(batchAdapter);

        deptSP.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedDept=parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                SweetToast.defaultShort(getApplicationContext(),"To continue, select a department.");
            }
        });

        batchSP.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedBatch=parent.getItemAtPosition(position).toString();
                shiftSp.setVisibility(View.VISIBLE);
                shiftAdapter=new ArrayAdapter<>(SelectStudentActivity.this,android.R.layout.simple_list_item_1,shift);
                shiftSp.setAdapter(shiftAdapter);
                shiftSp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        selectedShift=parent.getItemAtPosition(position).toString();
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                        SweetToast.defaultShort(getApplicationContext(),"To continue, select a shift");
                    }
                });
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                SweetToast.defaultShort(getApplicationContext(),"To continue, select a batch.");
            }
        });



//        deptRef= FirebaseDatabase.getInstance().getReference().child("Department");
//        deptRef.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                deptList.clear();
//                deptList.add("Select department");
//                if (dataSnapshot.exists()){
//                  for(DataSnapshot dataSnapshot1:dataSnapshot.getChildren()){
//                      if(dataSnapshot1.hasChildren()){
//                          String key=dataSnapshot1.getKey();
//                          deptList.add(key);
//                      }
//                  }
//
//                  deptAdapter=new ArrayAdapter<>(SelectStudentActivity.this,android.R.layout.simple_list_item_1,deptList);
//                  deptSP.setAdapter(deptAdapter);
//
//                  deptSP.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//                      @Override
//                      public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                          selectedDept=parent.getItemAtPosition(position).toString();
//
//                          if (selectedDept!=null ){
//                              batchRef=deptRef.child(selectedDept).child("Student");
//                              batchRef.addValueEventListener(new ValueEventListener() {
//                                  @Override
//                                  public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                                          batchList.clear();
//                                          batchList.add("Select batch");
//                                      if(dataSnapshot.exists()){
//                                          for(DataSnapshot dataSnapshot2:dataSnapshot.getChildren()){
//                                              if(dataSnapshot2.hasChildren()){
//                                                  String batch=dataSnapshot2.getKey();
//                                                  batchList.add(batch);
//                                              }
//                                          }
//
//                                          batchAdapter=new ArrayAdapter<>(SelectStudentActivity.this,android.R.layout.simple_list_item_1,batchList);
//                                          batchSP.setAdapter(batchAdapter);
//                                          batchSP.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//                                              @Override
//                                              public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                                                  selectedBatch=parent.getItemAtPosition(position).toString();
//
//                                                  if(selectedBatch!=null){
//                                                      shiftAdapter=new ArrayAdapter<>(SelectStudentActivity.this,android.R.layout.simple_list_item_1,shift);
//                                                      shiftSp.setAdapter(shiftAdapter);
//                                                      shiftSp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//                                                          @Override
//                                                          public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                                                              selectedShift=parent.getItemAtPosition(position).toString();
//                                                          }
//
//                                                          @Override
//                                                          public void onNothingSelected(AdapterView<?> parent) {
//
//                                                          }
//                                                      });
//                                                  }
//                                              }
//
//                                              @Override
//                                              public void onNothingSelected(AdapterView<?> parent) {
//
//                                              }
//                                          });
//                                      }
//                                  }
//                                  @Override
//                                  public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                                  }
//                              });
//                          }
//                      }
//                      @Override
//                      public void onNothingSelected(AdapterView<?> parent) {
//
//                      }
//                  });
//              }
//            }
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });

        nextBtn.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(selectedDept!=null && !selectedDept.equals("Select department") && selectedBatch!=null && !selectedBatch.equals("Select batch") && selectedShift!=null && !selectedShift.equals("Select shift")){
                Intent intent=new Intent(SelectStudentActivity.this,StudentListActivity.class) ;
                intent.putExtra("DEPT",selectedDept);
                intent.putExtra("BATCH",selectedBatch);
                intent.putExtra("SHIFT",selectedShift);
                startActivity(intent);
            }
        }
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
