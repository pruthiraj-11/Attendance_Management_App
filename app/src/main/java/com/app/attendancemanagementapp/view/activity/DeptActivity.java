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
import java.util.List;
import java.util.Objects;

public class DeptActivity extends AppCompatActivity {
    private Spinner tDeptSp,tShiftSp;
    private Button tNextBtn;
    private DatabaseReference tDeptRef;
    private List<String> tDeptList=new ArrayList<>();
    private String tSelectedDept,tSelectedShift;
    private String[] tshiftList,tDeptList1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dept);

        Toolbar toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        tshiftList=getResources().getStringArray(R.array.shift);
        tDeptList1=getResources().getStringArray(R.array.department);

        tDeptSp=findViewById(R.id.TdeptSp);
        tShiftSp=findViewById(R.id.TShiftSp);
        tNextBtn=findViewById(R.id.tNextBtn);

        ArrayAdapter<String> arrayAdapter=new ArrayAdapter<>(DeptActivity.this,android.R.layout.simple_list_item_1,tDeptList1);
        tDeptSp.setAdapter(arrayAdapter);
        tDeptSp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                tSelectedDept=parent.getItemAtPosition(position).toString();
                if(!tSelectedDept.isEmpty()){
                    ArrayAdapter<String> arrayAdapter1=new ArrayAdapter<>(DeptActivity.this,android.R.layout.simple_list_item_1,tshiftList);
                    tShiftSp.setAdapter(arrayAdapter1);
                    tShiftSp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            tSelectedShift=parent.getItemAtPosition(position).toString();
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {

                        }
                    });
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

//        tDeptRef= FirebaseDatabase.getInstance().getReference().child("Department");
//        tDeptRef.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                tDeptList.clear();
//                tDeptList.add("Select Department");
//                if(dataSnapshot.exists()){
//                    for(DataSnapshot dataSnapshot1:dataSnapshot.getChildren()){
//                        if(dataSnapshot1.hasChildren()){
//                            String key=dataSnapshot1.getKey();
//                            tDeptList.add(key);
//                        }
//                    }
//                    ArrayAdapter<String> arrayAdapter=new ArrayAdapter<>(DeptActivity.this,android.R.layout.simple_list_item_1,tDeptList);
//                    tDeptSp.setAdapter(arrayAdapter);
//                    tDeptSp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//                        @Override
//                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                            tSelectedDept=parent.getItemAtPosition(position).toString();
//                            if(!tSelectedDept.isEmpty()){
//                                ArrayAdapter<String> arrayAdapter1=new ArrayAdapter<>(DeptActivity.this,android.R.layout.simple_list_item_1,tshiftList);
//                                tShiftSp.setAdapter(arrayAdapter1);
//                                tShiftSp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//                                    @Override
//                                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                                       tSelectedShift=parent.getItemAtPosition(position).toString();
//                                    }
//
//                                    @Override
//                                    public void onNothingSelected(AdapterView<?> parent) {
//
//                                    }
//                                });
//                            }
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

        tNextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(tSelectedDept!=null && !tSelectedDept.equals("Select Department") && tSelectedShift!=null && !tSelectedShift.equals("Select shift")){
                    Intent intent=new Intent(DeptActivity.this,TeacherListActivity.class);
                    intent.putExtra("TDEPT",tSelectedDept);
                    intent.putExtra("TSHIFT",tSelectedShift);
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
