package com.example.project_group_17.UserHierarchy;

public class Student extends User{
    private String programOfStudy;

    public Student(String id, String first, String last, String email, String pass, String pn, String programOfStudy) {
        super("Student", id, first, last, email, pass, pn);
        this.programOfStudy = programOfStudy;
    }

    public String getProgramOfStudy() {
        return this.programOfStudy;
    }

    public String toString(){
        return "User Type "+this.getUserType()+" Name: "+ this.getFirstName() + " " +this.getLastName() +" Email: "+this.getEmail()+" Phone Number: "+this.getPhoneNumber()+" Progrem of Study: "+this.getProgramOfStudy();
    }
}
