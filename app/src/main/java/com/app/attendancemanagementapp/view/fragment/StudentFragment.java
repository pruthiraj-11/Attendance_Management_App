package com.app.attendancemanagementapp.view.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.app.attendancemanagementapp.R;
import com.app.attendancemanagementapp.storage.SaveUser;
import com.app.attendancemanagementapp.view.activity.ShowAttendanceActivity;

import java.util.ArrayList;
import java.util.List;

import xyz.hasnat.sweettoast.SweetToast;

public class StudentFragment extends Fragment {
    private TextView name,id,dept,shift,batch;
    private LinearLayout ViewAttendance;
    private String courses;
    private List<String> courseList=new ArrayList<>();

    public StudentFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
      View view=inflater.inflate(R.layout.fragment_student, container, false);

      name=view.findViewById(R.id.StudentName);
      id=view.findViewById(R.id.StudentId);
      dept=view.findViewById(R.id.StudentDept);
      shift=view.findViewById(R.id.StudentShift);
      batch=view.findViewById(R.id.StudentBatch);
      ViewAttendance=view.findViewById(R.id.ViewAttendance);

      SaveUser saveUser=new SaveUser();

      courses=saveUser.getStudent(requireContext()).getCourse();

      name.setText(saveUser.getStudent(requireContext()).getName());
      id.setText(saveUser.getStudent(requireContext()).getId());
      dept.setText(saveUser.getStudent(requireContext()).getDepartment());
      shift.setText(saveUser.getStudent(requireContext()).getShift());
      batch.setText(saveUser.getStudent(requireContext()).getBatch());

      ViewAttendance.setOnClickListener(v -> {
         Intent intent=new Intent(getContext(), ShowAttendanceActivity.class);
         startActivity(intent);
      });
      return view;
    }
}
