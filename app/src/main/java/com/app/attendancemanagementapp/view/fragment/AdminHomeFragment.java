package com.app.attendancemanagementapp.view.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.app.attendancemanagementapp.R;
import com.app.attendancemanagementapp.view.activity.CourseDeptctivity;
import com.app.attendancemanagementapp.view.activity.DeptActivity;
import com.app.attendancemanagementapp.view.activity.SelectStudentActivity;

public class AdminHomeFragment extends Fragment {
    private CardView studentListCV,teacherListCV,courseListCV,attendanceListCV;

    public AdminHomeFragment() {

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.fragment_admin_home, container, false);
        studentListCV=view.findViewById(R.id.studentListCV);
        teacherListCV=view.findViewById(R.id.teacherListCV);
        courseListCV=view.findViewById(R.id.courseListCV);

        studentListCV.setOnClickListener(v -> startActivity(new Intent(getContext(), SelectStudentActivity.class)));
        teacherListCV.setOnClickListener(v -> startActivity(new Intent(getContext(), DeptActivity.class)));
        courseListCV.setOnClickListener(v -> startActivity(new Intent(getContext(), CourseDeptctivity.class)));
        return  view;
    }
}
