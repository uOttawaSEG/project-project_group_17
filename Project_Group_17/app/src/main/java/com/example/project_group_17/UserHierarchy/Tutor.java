package com.example.project_group_17.UserHierarchy;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

public class Tutor extends User {

    private String degree;
    private List<String> courses;

    private double averageRating;

    private int numOfRatings;

    private List<String> ratedBy;

    public Tutor() { // hey guys it's Victor please don't remove this I know it looks useless but Firebase will decapitate itself if there isn't a no arg constructor
        super();
    }

    public Tutor(String id, String firstName, String lastName, String email, String password, String phone, String degree, List<String> courses) {
        super("Tutor", id, firstName, lastName, email, password, phone);
        this.degree = degree;
        this.courses = courses;
        this.averageRating = 0;
        this.numOfRatings = 0;
    }
    public String getDegree() {
        return degree;
    }

    public List<String> getCourses() {
        return courses;
    }

    public double getAvgRating() { return this.averageRating; }

    public List<String> getRatedBy() {return this.ratedBy;}

    public void setRatedBy(List<String> ratings) {
        this.ratedBy = ratings;
    }

    public void addRating(double rating) {
        this.numOfRatings++;
        this.averageRating = this.averageRating + (rating - this.averageRating) / this.numOfRatings;
    }

    public void addRatedBy(String id) {
        this.ratedBy.add(id);
    }

    @Override @NonNull
    public String toString(){
        return "User Type: " + this.getUserType()+"\nName: " + this.getFirstName() + " " + this.getLastName() + "\nEmail: " + this.getEmail() + "\nPhone Number: " + this.getPhoneNumber() + "\nDegree: " + this.getDegree() + "\nCourses: " + this.getCourses();
    }


}