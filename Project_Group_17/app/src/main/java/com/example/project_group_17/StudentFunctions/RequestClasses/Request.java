package com.example.project_group_17.StudentFunctions.RequestClasses;

import android.widget.Toast;

import java.io.Serializable;

import java.util.*;

import android.widget.Toast;

import android.text.TextUtils;

import com.example.project_group_17.TutorFunctions.TimeSlot;
import com.example.project_group_17.UserHierarchy.Student;
import com.example.project_group_17.UserHierarchy.User;


public class Request implements Serializable, Comparable<com.example.project_group_17.StudentFunctions.RequestClasses.Request> {

    public enum Status {
        APPROVED,
        REJECTED,
        PENDING,
        CANCELLED
    }

    private String date;
    private String start;
    private String end;
    private Status status;
    private String studentID;
    private String tutorID;
    private String tutorFirstName;
    private String tutorLastName;
    //Eventually add Rating and class being offered

    private String slotID;

    public Request() {}

    public Request(String tutorFirstName, String tutorLastName, String date, String start, String end, String studentID,String tutorID, String slotID) {
        this.tutorFirstName=tutorFirstName;
        this.tutorLastName=tutorLastName;
        this.date = date;
        this.start = start;
        this.end = end;
        this.status = Request.Status.PENDING;
        this.studentID = studentID;
        this.tutorID = tutorID;
        this.slotID = slotID;
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

    public String getStudentID() {
        return studentID;
    }
    public void setStudentID(String id){
        this.studentID=id;
    }
    public String getTutorFirstName() {
        return tutorFirstName;
    }
    public void setTutorFirstName(String firstName){
        this.tutorFirstName=firstName;
    }
    public String getTutorLastName() {
        return tutorLastName;
    }
    public void setTutorLastName(String lastName){
        this.tutorLastName=lastName;
    }

    public String getSlotID() {
        return slotID;
    }
    public void setSlotID(String slotID){
        this.slotID=slotID;
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
    public int compareTo(com.example.project_group_17.StudentFunctions.RequestClasses.Request time) {

        return this.start.compareTo((time.start));
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        else if (!(obj instanceof Request)) {
            return false;
        }

        Request other = (Request) obj;
        return Objects.equals(this.slotID, other.slotID) && Objects.equals(this.studentID, other.studentID);

    }

    @Override
    public int hashCode() {
        return Objects.hash(start,end);
    }

    @Override
    public String toString() {
        return "Session with " + tutorFirstName + " " + tutorLastName +
                " on " + date + " " + start + "-" + end +
                " (" + status + ")";
    }

    public boolean getPast() {
        String today = new java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault()).format((new Date()));

        return (this.getDate().compareTo(today) < 0);
    }

    public void reject(){
        this.status = com.example.project_group_17.StudentFunctions.RequestClasses.Request.Status.REJECTED;
    }

    public void approve(){
        this.status = com.example.project_group_17.StudentFunctions.RequestClasses.Request.Status.APPROVED;
    }
    public void cancel(){
        this.status = Status.CANCELLED;
    }
    public boolean isCancelled(){
        return getStatus() == Status.CANCELLED;
    }
    public boolean isPending(){
        return getStatus() == com.example.project_group_17.StudentFunctions.RequestClasses.Request.Status.PENDING;
    }
    public boolean isApproved(){
        return getStatus() == com.example.project_group_17.StudentFunctions.RequestClasses.Request.Status.APPROVED;
    }
    public boolean isRejected(){
        return getStatus() == com.example.project_group_17.StudentFunctions.RequestClasses.Request.Status.REJECTED;
    }
    public void setPending(){
        this.status = com.example.project_group_17.StudentFunctions.RequestClasses.Request.Status.PENDING;
    }

    public void setDate(String date) { this.date = date; }
    public void setStart(String start) { this.start = start; }
    public void setEnd(String end) { this.end = end; }
    public void setStatus(com.example.project_group_17.StudentFunctions.RequestClasses.Request.Status status) { this.status = status; }
    public void setTutorID(String tutorID) { this.tutorID = tutorID; }
}
