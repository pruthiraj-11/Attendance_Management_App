package com.app.attendancemanagementapp.view.activity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;

import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;


import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.app.attendancemanagementapp.R;

import java.util.Objects;

import xyz.hasnat.sweettoast.SweetToast;

public class DatePickerActivity extends AppCompatActivity {
    private String intent_course;
    private Button nextBtn;
    private EditText dateET;
    private ImageButton dateIbtn;
    private String date;
    private DatePickerDialog datePickerDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_date_picker);

        Toolbar toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        dateET=findViewById(R.id.dateET);
        dateIbtn=findViewById(R.id.dateIbtn);
        nextBtn=findViewById(R.id.dateNxtBtn);
        intent_course=getIntent().getStringExtra("SC");

        dateIbtn.setOnClickListener(v -> {
            DatePicker datePicker=new DatePicker(DatePickerActivity.this);
            int currentDay=datePicker.getDayOfMonth();
            int currentMonth=datePicker.getMonth();
            int currentYear=datePicker.getYear();
           datePickerDialog =new DatePickerDialog(DatePickerActivity.this, new DatePickerDialog.OnDateSetListener() {
                       @Override
                       public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                           String stringBuilder = dayOfMonth + "-" +
                                   (month + 1) + "-" +
                                   year;
                           dateET.setText(stringBuilder);
                       }
                   },currentYear,currentMonth,currentDay
           );
           datePickerDialog.show();
        });

        nextBtn.setOnClickListener(v -> {
            date=dateET.getText().toString();
            if(!date.isEmpty()){
                Intent intent=new Intent(DatePickerActivity.this,TakeAttendanceActivity.class);
                intent.putExtra("SC",intent_course);
                intent.putExtra("DATE",date);
                startActivity(intent);
            }else {
                SweetToast.warning(getApplicationContext(),"Pick a date first");
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
