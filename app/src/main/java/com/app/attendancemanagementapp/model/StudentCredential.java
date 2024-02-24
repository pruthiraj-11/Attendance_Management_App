package com.app.attendancemanagementapp.model;

public class StudentCredential {
    String studentEmail, studentPassword;

    public StudentCredential(String studentEmail, String studentPassword) {
        this.studentEmail = studentEmail;
        this.studentPassword = studentPassword;
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
