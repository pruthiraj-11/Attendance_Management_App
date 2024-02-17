package com.app.attendancemanagementapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.app.attendancemanagementapp.R;
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

public class ViewAttendanceAdapter extends RecyclerView.Adapter<ViewAttendanceAdapter.ViewAttendanceViewHolder> {

    private List<Student> studentList=new ArrayList<>();
    private List<String> presentList=new ArrayList<>();
    private List<String> absentList=new ArrayList<>();
    private Context context;
    private DatabaseReference presentRef,absentRef;

    public ViewAttendanceAdapter(Context context, List<Student> studentList) {
        this.context = context;
        this.studentList = studentList;
    }

    public ViewAttendanceAdapter(){
    }

    @NonNull
    @Override
    public ViewAttendanceViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater layoutInflater=LayoutInflater.from(context);
        View view=layoutInflater.inflate(R.layout.single_student_layout,viewGroup,false);
        return new ViewAttendanceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewAttendanceViewHolder holder, int position) {
        holder.Student_name.setText(studentList.get(holder.getAdapterPosition()).getName());
        holder.course_code.setText(studentList.get(holder.getAdapterPosition()).getCourse_code());
        holder.id.setText(studentList.get(holder.getAdapterPosition()).getId());

        holder.itemView.setOnClickListener(v -> {
            presentRef= FirebaseDatabase.getInstance().getReference().child("Department").child(studentList.get(holder.getAdapterPosition()).getDepartment())
                    .child("Attendance").child(new SaveUser().getTeacher(context).getShift()).child(new SaveUser().teacher_CourseLoadData(context));

            presentRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    presentList.clear();
                    absentList.clear();
                    if(dataSnapshot.exists()){
                        for (DataSnapshot dataSnapshot1:dataSnapshot.getChildren()){
                            for(DataSnapshot dataSnapshot2:dataSnapshot1.child("Present").getChildren()){
                                String key=dataSnapshot2.getKey();
                                if(holder.getAdapterPosition()!=-1){
                                    if(Objects.requireNonNull(key).equals(studentList.get(holder.getAdapterPosition()).getId())){
                                        presentList.add(key);
                                    }
                                }
                            }
                            for(DataSnapshot dataSnapshot2:dataSnapshot1.child("Absent").getChildren()){
                                String key= Objects.requireNonNull(dataSnapshot2.getValue()).toString();
                                if(key.equals(studentList.get(holder.getAdapterPosition()).getId())){
                                    absentList.add(key);
                                }
                            }
                        }
                        final AlertDialog dialoga =new AlertDialog.Builder(context).create();
                        View view=LayoutInflater.from(context).inflate(R.layout.viewatedance,null);

                        TextView presentTV,absentTV,name,id;
                        Button button;

                        presentTV=view.findViewById(R.id.presentStudentTV1);
                        absentTV=view.findViewById(R.id.absentStudentTV1);
                        name=view.findViewById(R.id.vName);
                        id=view.findViewById(R.id.vID);
                        name.setText(studentList.get(holder.getAdapterPosition()).getName());
                        id.setText(studentList.get(holder.getAdapterPosition()).getId());
                        button=view.findViewById(R.id.Okbtn);
                        presentTV.setText(Integer.toString(presentList.size()));
                        absentTV.setText(Integer.toString(absentList.size()));
                        button.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialoga.dismiss();
                            }
                        });
                        dialoga.setView(view);
                        dialoga.setCancelable(true);
                        dialoga.show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        });
    }

    @Override
    public int getItemCount() {
        return studentList.size();
    }

    public static class ViewAttendanceViewHolder extends  RecyclerView.ViewHolder {
        TextView Student_name;
        TextView course_code;
        TextView id;
        public ViewAttendanceViewHolder(@NonNull View itemView) {
            super(itemView);
            Student_name=itemView.findViewById(R.id.studentNameTV);
            course_code=itemView.findViewById(R.id.studentCourseTv);
            id=itemView.findViewById(R.id.studentIDv);
        }
    }

    public void updateCollection(List<Student> studentList){
        this.studentList=studentList;
        notifyDataSetChanged();
    }
}
