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
        ListIterator<TimeSlot> iterator = timeSlots.listIterator(); // WOOHOO I LOVE ITERATORS CONSTANT O(1) TIME WOOHOO

        while (iterator.hasNext()) {
            TimeSlot curr = iterator.next();

            if ((timeSlot.getDate() + timeSlot.getStart()).compareTo(curr.getDate() + curr.getStart()) < 0) {
                iterator.previous();
                iterator.add(timeSlot);
                return;
            }
        }

        iterator.add(timeSlot);
    }

    public boolean delete(TimeSlot timeSlot, String deleterId) { // Hi Lucas, deleterId is the ID of the tutor attempting to remove the timeslot. It will automatically compare the deleterId to the tutorId of the timeslot
        if (deleterId.equals(timeSlot.getTutorID())) {
            timeSlots.remove(timeSlot);
            return true;
        } else {
            return false;
        }
    }

    public boolean overlapChecking(String d, String s, String e) {
        return false;
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
