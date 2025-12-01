package com.example.project_group_17.StudentFunctions.RequestClasses;

import java.io.Serializable;
import java.util.*;
public class StudentSchedule implements Serializable {
    private String userID;
    private List<Request> requests;
    public StudentSchedule() {
        requests = new LinkedList<>();
    }
    public StudentSchedule(String userID) {
        this.userID = userID;
        requests = new LinkedList<>();
    }

    public String getUserID() {
        return userID;
    }

    public List<Request> getRequests() {
        return requests;
    }

    public void add(Request request) {
        ListIterator<Request> iterator = requests.listIterator(); // WOOHOO I LOVE ITERATORS CONSTANT O(1) TIME WOOHOO

        while (iterator.hasNext()) {
            Request curr = iterator.next();

            if ((request.getDate() + request.getStart()).compareTo(curr.getDate() + curr.getStart()) < 0) {
                iterator.previous();
                iterator.add(request);
                return;
            }
        }

        iterator.add(request);
    }

    public void delete(Request request) {
        requests.remove(request);
    }

    public boolean overlapChecking(String d, String s, String e) {
        if(requests==null){
            return false;
        }
        for (Request slot : requests) {

            if (d.equals(slot.getDate())) {

                if ((parseTime(s) < parseTime(slot.getEnd())) && (parseTime(e) > parseTime(slot.getStart()))) {

                    return true;
                }
            }
        }

        return false;
    }

    public int parseTime(String time) {

        int hour = Integer.parseInt(time.substring(0,2));
        int min = Integer.parseInt(time.substring(3,5));

        return (hour*60)+min;

    }

    @Override
    public String toString() {
        return "Schedule{userID=" + userID +
                ", slots=" + requests + "}";
    }
}
