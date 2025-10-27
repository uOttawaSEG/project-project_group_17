package com.example.project_group_17.UserHierarchy;

import androidx.annotation.NonNull;

public class Student extends User{
    private String programOfStudy;

    public Student() { // hey guys it's Victor please don't remove this I know it looks useless but Firebase will decapitate itself if there isn't a no arg constructor
        super();
    }

    public Student(String id, String first, String last, String email, String pass, String pn, String programOfStudy) {
        super("Student", id, first, last, email, pass, pn);
        this.programOfStudy = programOfStudy;
    }

    public String getProgramOfStudy() {
        return this.programOfStudy;
    }

    @Override @NonNull
    public String toString(){
        return "User Type: " + this.getUserType() + "\nName: " + this.getFirstName() + " " + this.getLastName() + "\nEmail: " + this.getEmail() + "\nPhone Number: " + this.getPhoneNumber() + "\nProgram of Study: " + this.getProgramOfStudy();
    }
}
