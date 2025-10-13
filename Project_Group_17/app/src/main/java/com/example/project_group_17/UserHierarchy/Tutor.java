package com.example.project_group_17.UserHierarchy;

import java.util.List;

public class Tutor extends User {

    private String degree;
    private List<String> courses;


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


}