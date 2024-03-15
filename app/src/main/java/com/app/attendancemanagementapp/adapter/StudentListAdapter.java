package com.app.attendancemanagementapp.adapter;

import android.content.Context;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.attendancemanagementapp.R;
import com.app.attendancemanagementapp.model.Student;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;
import java.util.Objects;

public class StudentListAdapter extends RecyclerView.Adapter<StudentListAdapter.StudentListViewHolder> {

    private List<Student> studentList;
    private Context context;

    public StudentListAdapter(Context context,List<Student> studentList) {

        this.context = context;
        this.studentList = studentList;
    }

    public  StudentListAdapter(){

    }

    @NonNull
    @Override
    public StudentListAdapter.StudentListViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater layoutInflater=LayoutInflater.from(context);
        View view=layoutInflater.inflate(R.layout.single_student_layout,viewGroup,false);
        return new StudentListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StudentListAdapter.StudentListViewHolder studentListViewHolder, int i) {
        studentListViewHolder.Student_name.setText(studentList.get(studentListViewHolder.getAdapterPosition()).getName());
        studentListViewHolder.course_code.setText(studentList.get(studentListViewHolder.getAdapterPosition()).getCourse_code());
        studentListViewHolder.id.setText(studentList.get(studentListViewHolder.getAdapterPosition()).getId());
    }

    @Override
    public int getItemCount() {
        return studentList.size();
    }

    public static class StudentListViewHolder extends  RecyclerView.ViewHolder implements View.OnCreateContextMenuListener{
        TextView Student_name;
        TextView course_code;
        TextView id;
        LinearLayout constraintLayout;
        public StudentListViewHolder(@NonNull View itemView) {
            super(itemView);
            Student_name=itemView.findViewById(R.id.studentNameTV);
            course_code=itemView.findViewById(R.id.studentCourseTv);
            id=itemView.findViewById(R.id.studentIDv);
            constraintLayout=itemView.findViewById(R.id.root_layout_student);
            constraintLayout.setOnCreateContextMenuListener(this);
        }

        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
            menu.add(getAdapterPosition(),101,0,"Delete");
        }
    }

    public void removeItem(int position) {
        String batch= studentList.get(position).getBatch();
        String shift=studentList.get(position).getShift();
        String dept=studentList.get(position).getDepartment();
        String key=studentList.get(position).getKey();
        String ID=studentList.get(position).getId();
        studentList.remove(position);
        notifyItemRemoved(position);
        DatabaseReference databaseReference=FirebaseDatabase.getInstance().getReference().child("Department").child(dept).child("Student").child(batch).child("allstudent").child(shift);
        databaseReference.child(Objects.requireNonNull(key)).setValue(null);
        FirebaseDatabase.getInstance().getReference().child("AllStudentCredentials").child(ID).setValue(null);
    }

    public void updateCollection(List<Student> studentList){
        this.studentList=studentList;
        notifyDataSetChanged();
    }
}
