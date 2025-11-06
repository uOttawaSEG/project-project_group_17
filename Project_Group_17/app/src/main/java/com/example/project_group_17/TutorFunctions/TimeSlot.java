package com.example.project_group_17.TutorFunctions;

import java.io.Serializable;
import java.sql.Time;
import java.util.*;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class TimeSlot implements Serializable {

    public enum Status {

        FREE,
        PENDING,
        BOOKED

    }

    protected final String start;
    protected final String end;
    protected final Status status;

    protected final String studentID;


    public TimeSlot(String start, String end) {

        this.start = start;
        this.end = end;
        this.status = Status.FREE;
        this.studentID = null;

    }

    public TimeSlot(String start, String end, Status status, String studentID) {

        isValid(start);
        isValid(end);
        is30Apart(start);
        is30Apart(end);
        isLogic(start,end);

        this.start = start;
        this.end = end;

        if (status == null) {
            this.status = Status.FREE;

        }else {

            this.status = status;
        }

        this.studentID = studentID;

    }

    private static void isValid(String time) {

        if (time == null || !time.matches("^([01]\\d|2[0-3]):[0-5]\\d$")) {

            throw new IllegalArgumentException("Invalid Format: HH:mm");

        }
    }

    private static void is30Apart(String time) {

        int min = Integer.parseInt(time.substring(3,5));

        if (min != 0 && min != 30) {

            throw new IllegalArgumentException("Time Must be in 30-min Increments");

        }
    }

    private static void isLogic(String start, String end) {

        if (start.compareTo(end) >= 0) {

            throw new IllegalArgumentException("ERROR, Start time must be before end time");

        }
    }

    public String getKey() {

        return start+" - "+end;
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





}
