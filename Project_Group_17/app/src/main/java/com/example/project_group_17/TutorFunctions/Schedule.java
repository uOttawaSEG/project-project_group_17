package com.example.project_group_17.TutorFunctions;

import java.io.Serializable;
import java.util.*;
public class Schedule implements Serializable {

    protected final String tutorID;
    protected final String date;
    protected final boolean auto;
    protected final List<TimeSlot> slots;

    public Schedule(String tutorID, String date, boolean auto, List<TimeSlot> slots) {

        isValid(date);
        isPast(date);
        isOverlap(slots);

        this.tutorID = tutorID;
        this.date = date;
        this.auto = auto;

        List<TimeSlot> lst = new ArrayList<>(slots);
        Collections.sort(lst);
        this.slots = Collections.unmodifiableList(lst);

    }

    public String getTutorID() {
        return tutorID;
    }

    public String getDate() {
        return date;
    }

    public boolean isAuto() {
        return auto;
    }

    public List<TimeSlot> getTimeSlots() {
        return slots;
    }

    public static List<TimeSlot> incrSlots(String start, String end) {

        is30Apart(start);
        is30Apart(end);

        if (start.compareTo(end) >= 0) {
            throw new IllegalArgumentException("Error: Start Time Must be Before End Time");
        }

        List<TimeSlot> saved = new ArrayList<>();
        String st = start;

        while (st.compareTo(end) < 0) {

            String incr = add30(st);

            if (incr.compareTo(end) > 0) {
                break;
            }

            saved.add(new TimeSlot(st,incr));
            st = incr;
        }
        if (saved.isEmpty()) {
            throw new IllegalArgumentException("Empty");

        }
        return saved;
    }

    public void catchTimeConflicts (Set<String> times) {

        if (times == null || times.isEmpty()) {
            return;
        }

        for (TimeSlot time : slots) {
            if (times.contains((time.getKey()))) {
                throw new IllegalArgumentException("Conflict Detected: "+time.getKey());
            }
        }
    }

    public Map<String, Object> store() {

        Map<String, Object> map = new LinkedHashMap<>();
        map.put("TutorID", tutorID);
        map.put("Date", date);
        map.put("Auto Approval Enabled", auto);

        List<Map<String,Object>> lst = new ArrayList<>();

        for (TimeSlot time : slots) {
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("Key",time.getKey());
            m.put("Start Time", time.getStart());
            m.put("End Time", time.getEnd());
            m.put("Status", time.getStatus().name());

            if (time.getStudentID() != null) {

                m.put("Student ID", time.getStudentID());
            }

            lst.add(m);

        }

        map.put("Time Slots", lst);
        return map;
    }


    private static void isValid(String date) {
        if (date == null || !date.matches("^\\d{4}-\\d{2}-\\d{2}$")) {
            throw new IllegalArgumentException("Invalid Date Format: yyyy-mm-dd");
        }
    }

    private static void isPast(String date) {
        String today = new java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault()).format((new Date()));

        if (date.compareTo(today) < 0) {
            throw new IllegalArgumentException("Past Date: "+date);
        }
    }

    private static void isOverlap(List<TimeSlot> slots) {
        if (slots == null || slots.isEmpty()) {
            throw new IllegalArgumentException("Time cannot be Empty");

        }
        Set<String> keys = new HashSet<>();

        for (TimeSlot s : slots) {

            if (!keys.add(s.getKey())) {
                throw new IllegalArgumentException("Overlapping Detected: "+s.getKey());

            }

        }
    }

    private static void is30Apart(String time) {

        if (time == null || !time.matches("^([01]\\d|2[0-3]):(00|30)$")) {

            throw new IllegalArgumentException("Invalid Format: HH:mm");
        }


    }

    private static String add30(String time) {
        int hour = Integer.parseInt(time.substring(0,2));
        int min = Integer.parseInt(time.substring(3,5));

        min += 30;
        if (min == 60) {
            min = 0;
            hour = (hour+1) % 24;

        }

        return String.format(java.util.Locale.getDefault(), "%02d:%02d",hour,min);
    }

    @Override
    public String toString() {
        return "Schedule{tutorID=" + tutorID + ", date=" + date + ", auto=" + auto +
                ", slots=" + slots + "}";
    }



}
