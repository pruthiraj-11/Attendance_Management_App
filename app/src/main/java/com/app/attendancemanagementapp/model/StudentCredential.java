package com.app.attendancemanagementapp.model;

public class StudentCredential {
    String studentEmail, studentPassword,studentRegdNum;

    public StudentCredential(String studentEmail, String studentPassword, String studentRegdNum) {
        this.studentEmail = studentEmail;
        this.studentPassword = studentPassword;
        this.studentRegdNum = studentRegdNum;
    }

    public String getStudentRegdNum() {
        return studentRegdNum;
    }

    public void setStudentRegdNum(String studentRegdNum) {
        this.studentRegdNum = studentRegdNum;
    }

    public StudentCredential() {
    }

    public String getStudentEmail() {
        return studentEmail;
    }

    public void setStudentEmail(String studentEmail) {
        this.studentEmail = studentEmail;
    }

    public String getStudentPassword() {
        return studentPassword;
    }

    public void setStudentPassword(String studentPassword) {
        this.studentPassword = studentPassword;
    }
}
