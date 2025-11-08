package com.example.project_group_17.Screens;

import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import com.example.project_group_17.R;
import com.example.project_group_17.TutorFunctions.Schedule;
import com.example.project_group_17.TutorFunctions.TimeSlot;

import java.util.List;
import android.util.Log;

public class TutorCreatingSlots extends AppCompatActivity {
    private static final String TAG = "TutorCreatingSlots";

    private EditText date;
    private EditText startTime;
    private EditText endTime;
    private CheckBox isAutoApporved;
    private Button create;

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

        List<TimeSlot> slots = Schedule.incrSlots(start, end);

        Schedule schedule = new Schedule("FETCH FROM FIREBASE",d,auto,slots);

        //NEED TO FIND A WAY TO STORE SCHEDULE IN FIREBASE.
        Log.d(TAG, "Schedule: tutor=" + schedule.getTutorID()
                + " date=" + schedule.getDate()
                + " auto=" + schedule.isAuto()
                + " slots=" + schedule.getTimeSlots().size());

        for (TimeSlot s : schedule.getTimeSlots()) {
            Log.d(TAG, "  slot " + s.getStart() + "-" + s.getEnd() + " status=" + s.getStatus());
        }




    }

}

