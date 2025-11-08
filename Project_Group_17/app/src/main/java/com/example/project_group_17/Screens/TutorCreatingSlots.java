package com.example.project_group_17.Screens;

import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import com.example.project_group_17.R;
import com.example.project_group_17.TutorFunctions.Schedule;
import com.example.project_group_17.TutorFunctions.TimeSlot;

import java.util.ArrayList;
import android.widget.Toast;
import java.util.Collections;
import java.util.List;
import android.util.Log;
import java.util.HashSet;


public class TutorCreatingSlots extends AppCompatActivity {
    private static final String TAG = "TutorCreatingSlots";

    private EditText date;
    private EditText startTime;
    private EditText endTime;
    private CheckBox isAutoApporved;
    private Button create;

    private final List<TimeSlot> accumlatedSlots = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.schedule);

        date = findViewById(R.id.etDate);
        startTime = findViewById(R.id.etStartTime);
        endTime = findViewById(R.id.etEndTime);
        isAutoApporved = findViewById(R.id.cbAutoApprove);
        create = findViewById(R.id.btnCreate);

        create.setOnClickListener(v -> generateSchedule());

    }

    private void generateSchedule() {

        String d = date.getText().toString().trim();
        String start = startTime.getText().toString().trim();
        String end = endTime.getText().toString().trim();
        boolean auto = isAutoApporved.isChecked();

    try{

        List<TimeSlot> slots = Schedule.incrSlots(start, end);

        int added = merge(accumlatedSlots, slots);
        Collections.sort(accumlatedSlots);

        if (added > 0) {
            Toast.makeText(this, "Added " + added + " slot(s). Total: " + accumlatedSlots.size(), Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "No new slots (duplicates/overlap). Total: " + accumlatedSlots.size(), Toast.LENGTH_SHORT).show();
        }

        Schedule schedule = new Schedule("FETCH FROM FIREBASE", d, auto, accumlatedSlots);

        //NEED TO FIND A WAY TO STORE SCHEDULE IN FIREBASE.
        Log.d(TAG, "Schedule: tutor=" + schedule.getTutorID()
                + " date=" + schedule.getDate()
                + " auto=" + schedule.isAuto()
                + " slots=" + schedule.getTimeSlots().size());

        for (TimeSlot s : schedule.getTimeSlots()) {
            Log.d(TAG, "  slot " + s.getStart() + "-" + s.getEnd() + " status=" + s.getStatus());
        }


    } catch (IllegalArgumentException ex) {
        Toast.makeText(this, ex.getMessage(), Toast.LENGTH_LONG).show();
        Log.e(TAG, "Validation error: " + ex.getMessage());
    } catch (Exception ex) {
        Toast.makeText(this, "Unexpected error while creating availability.", Toast.LENGTH_LONG).show();
        Log.e(TAG, "Unexpected error", ex);
    }

    }

    private int merge(List<TimeSlot> a, List<TimeSlot> b) {

        HashSet<String> set = new HashSet<>();

        for (TimeSlot time : a) {
            set.add(time.getKey());
        }

        int added = 0;
        for (TimeSlot time : b) {
            if(!set.contains(time.getKey())) {
                a.add(time);
                set.add(time.getKey());
                added++;
            }
        }

        return added;





    }

}

