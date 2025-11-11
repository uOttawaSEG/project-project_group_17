package com.example.project_group_17.TutorFunctions;

import android.widget.Toast;

import java.io.Serializable;

import java.util.*;

import android.widget.Toast;

import android.text.TextUtils;

import com.example.project_group_17.UserHierarchy.Student;
import com.example.project_group_17.UserHierarchy.User;


public class TimeSlot implements Serializable, Comparable<TimeSlot> {

    public enum Status {

        FREE,
        PENDING,
        BOOKED,
        CANCELLED

    }

    private String date;
    private String start;
    private String end;
    private Status status;
    private boolean auto;
    private String studentID;
    private String tutorID;
    private Student student;


    public TimeSlot() {}

    public TimeSlot(String date, String start, String end, boolean auto, String tutorID) {

        this.date = date;
        this.start = start;
        this.end = end;
        this.status = Status.FREE;
        this.auto = auto;
        this.studentID = "Not added yet";
        this.tutorID = tutorID;

    }

    public String getDate() {
        return date;
    }

    public String getStart() {
        return start;
    }

    public String getEnd() {
        return end;
    }

    public Status getStatus() {
        return status;
    }

    public boolean getAuto() {
        return auto;
    }

    public String getStudentID() {
        return studentID;
    }
    public void setStudentID(String id){
        this.studentID=id;
    }

    public String getTutorID() {
        return tutorID;
    }

    public static boolean isValidTime(String time) {
        return !time.matches("^([01]\\d|2[0-3]):[0-5]\\d$");
    }
    public static boolean compareStartEnd(String s, String e) {
        return s.compareTo(e) >= 0;
    }

    public static boolean isValidDateFormat(String date) {
        return (date == null || !date.matches("^\\d{4}-\\d{2}-\\d{2}$"));
    }

    public static boolean isPast(String date) {
        String today = new java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault()).format((new Date()));

        return (date.compareTo(today) < 0);
    }

    public static boolean is30Apart(String time) {
        return (time == null || !time.matches("^([01]\\d|2[0-3]):(00|30)$"));
    }

    @Override
    public int compareTo(TimeSlot time) {

        return this.start.compareTo((time.start));
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        else if (!(obj instanceof TimeSlot)) {
            return false;
        }

        TimeSlot other = (TimeSlot) obj;
        return Objects.equals(this.start,other.start) && Objects.equals(this.end, other.end);

    }

    @Override
    public int hashCode() {
        return Objects.hash(start,end);
    }

    @Override
    public String toString() {
        return "TimeSlot{" + getDate() + " " + getStart() + "-" + getEnd() + ", status=" + getStatus() + ", created by: " + getTutorID() + "}";
    }

    public boolean getPast() {
        String today = new java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault()).format((new Date()));

        return (this.getDate().compareTo(today) < 0);
    }

    public void cancel(){
        this.status = Status.CANCELLED;
    }
    public void book(User u){
        this.status = Status.BOOKED;
        setStudentID(u.getId());
        setStudent((Student)u);
    }
    public boolean isPending(){
        return getStatus() == Status.PENDING;
    }
    public boolean isBooked(){
        return getStatus() == Status.BOOKED;
    }
    public boolean isCancelled(){
        return getStatus() == Status.CANCELLED;
    }

    public Student getStudent(){
        return this.student;
    }
    public void setStudent(Student student){
        this.student=student;
    }

    public void setPending(){
        this.status=Status.PENDING;
    }
}
