package com.example.project_group_17.Screens;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.project_group_17.AdminFunctions.AdminInbox;
import com.example.project_group_17.R;
import com.example.project_group_17.TutorFunctions.Schedule;
import com.example.project_group_17.TutorFunctions.TimeSlot;
import com.example.project_group_17.UserHierarchy.User;

import java.io.Serializable;
import java.sql.Time;
import java.util.ArrayList;
import android.widget.Toast;
import java.util.Collections;
import java.util.List;
import android.util.Log;
import java.util.HashSet;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class TutorCreatingSlots extends AppCompatActivity {
    private static final String TAG = "TutorCreatingSlots";
    private DatabaseReference databaseSchedules;
    private EditText date;
    private EditText startTime;
    private EditText endTime;
    private CheckBox isAutoApporved;
    private String tutorID;
    private Schedule schedule;
    private Button create;
    private String id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.schedule);

        databaseSchedules = FirebaseDatabase.getInstance().getReference("Schedules");

        date = findViewById(R.id.etDate);
        startTime = findViewById(R.id.etStartTime);
        endTime = findViewById(R.id.etEndTime);
        isAutoApporved = findViewById(R.id.cbAutoApprove);
        create = findViewById(R.id.btnCreate);
        Serializable se = getIntent().getSerializableExtra("userInfo");
        User u = (User) se;
        tutorID = u.getId();

        createSchedule();
        create.setOnClickListener(v -> createTimeSlot());

    }

    public void createSchedule() {
        databaseSchedules.orderByChild("userID").equalTo(tutorID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot scheduleSnapshot : snapshot.getChildren()) {
                        id = scheduleSnapshot.getKey();
                        schedule = scheduleSnapshot.getValue(Schedule.class);
                    }
                } else {
                    schedule = new Schedule(tutorID);
                    id = databaseSchedules.push().getKey();
                    databaseSchedules.child(id).setValue(schedule);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(TutorCreatingSlots.this, "Database error", Toast.LENGTH_LONG).show();
                return;
            }
        });
    }

    public void createTimeSlot() {

        String d = date.getText().toString().trim();
        String start = startTime.getText().toString().trim();
        String end = endTime.getText().toString().trim();
        boolean auto = isAutoApporved.isChecked();

        if (d.isEmpty() || start.isEmpty() || end.isEmpty()) {
            Toast.makeText(this, "Invalid Format: Please fill out all fields", Toast.LENGTH_SHORT).show();
        } else if (TimeSlot.isValidDateFormat(d)) {
            Toast.makeText(this, "Invalid Date Format: Should be YYYY-MM-DD", Toast.LENGTH_SHORT).show();
        } else if (TimeSlot.isPast(d)) {
            Toast.makeText(this, "Invalid Date: Timeslot cannot be in the past", Toast.LENGTH_SHORT).show();
        } else if (TimeSlot.isValidTime(start) || TimeSlot.isValidTime(end)) {
            Toast.makeText(this, "Invalid Time: Must be within a 24 hour format", Toast.LENGTH_SHORT).show();
        } else if (TimeSlot.is30Apart(start) || TimeSlot.is30Apart(end)) {
            Toast.makeText(this, "Invalid Time Format: Times must be in increments of 30 minutes", Toast.LENGTH_SHORT).show();
        } else if (TimeSlot.compareStartEnd(start, end)) {
            Toast.makeText(this, "Invalid Time Format: Start time must be before end time", Toast.LENGTH_SHORT).show();
        } else if (schedule.overlapChecking(d, start, end)) {
            Toast.makeText(this, "Conflicting Slot: A timeslot is already registered within that period", Toast.LENGTH_SHORT).show();
        } else {
            TimeSlot timeSlot = new TimeSlot(d, start, end, auto, tutorID);
            schedule.add(timeSlot);
            databaseSchedules.child(id).setValue(schedule);
            Toast.makeText(this, "Successfully created timeslot.", Toast.LENGTH_SHORT).show();
        }
    }

    public void goBack(View view) {
        Intent intent = new Intent(TutorCreatingSlots.this, UserScreen.class);
        Serializable se = getIntent().getSerializableExtra("userInfo");
        User u = (User) se;
        intent.putExtra("userInfo", u);
        startActivity(intent);
        finish();
    }

}

