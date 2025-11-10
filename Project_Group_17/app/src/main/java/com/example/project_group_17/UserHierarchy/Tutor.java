package com.example.project_group_17.UserHierarchy;

import androidx.annotation.NonNull;

import java.util.List;

public class Tutor extends User {

    private String degree;
    private List<String> courses;

    public Tutor() { // hey guys it's Victor please don't remove this I know it looks useless but Firebase will decapitate itself if there isn't a no arg constructor
        super();
    }

    public Tutor(String id, String firstName, String lastName, String email, String password, String phone, String degree, List<String> courses) {
        super("Tutor", id, firstName, lastName, email, password, phone);
        this.degree = degree;
        this.courses = courses;
    }
    public String getDegree() {
        return degree;
    }

    public List<String> getCourses() {
        return courses;
    }

    @Override @NonNull
    public String toString(){
        return "User Type: " + this.getUserType()+"\nName: " + this.getFirstName() + " " + this.getLastName() + "\nEmail: " + this.getEmail() + "\nPhone Number: " + this.getPhoneNumber() + "\nDegree: " + this.getDegree() + "\nCourses: " + this.getCourses();
    }


}