package com.example.project_group_17.TutorFunctions;

import android.util.Log;
import android.widget.Toast;

import com.example.project_group_17.Screens.TutorCreatingSlots;
import com.example.project_group_17.UserHierarchy.Tutor;

import java.io.Serializable;
import java.sql.Time;
import java.util.*;
public class Schedule implements Serializable {
    private String userID;
    private List<TimeSlot> timeSlots;
    public Schedule() {}
    public Schedule(String userID) {
        this.userID = userID;
        timeSlots = new LinkedList<>();
    }

    public String getUserID() {
        return userID;
    }

    public List<TimeSlot> getTimeSlots() {
        return timeSlots;
    }

    public void add(TimeSlot timeSlot) {
        timeSlots.add(timeSlot);
    }

    public void clear() {
        timeSlots.clear();
    }

    @Override
    public String toString() {
        return "Schedule{userID=" + userID +
                ", slots=" + timeSlots + "}";
    }



}
