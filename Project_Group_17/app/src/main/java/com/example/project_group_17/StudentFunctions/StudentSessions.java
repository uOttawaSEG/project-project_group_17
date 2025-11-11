package com.example.project_group_17.StudentFunctions;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.project_group_17.R;
import com.example.project_group_17.Screens.UserScreen;
import com.example.project_group_17.TutorFunctions.ListDisplays.UpcomingSessions;
import com.example.project_group_17.TutorFunctions.TimeSlot;
import com.example.project_group_17.UserHierarchy.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class StudentSessions extends AppCompatActivity {
    DatabaseReference databaseSchedules;
    private Button goBack;
    List<TimeSlot> availableSlots = new ArrayList<TimeSlot>();
    User u;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.student_sessions);

        Serializable se = getIntent().getSerializableExtra("userInfo");
        u = (User) se;

        goBack = findViewById(R.id.gobackBtn);

        goBack.setOnClickListener(v ->{
            Intent intent = new Intent(StudentSessions.this, UserScreen.class);
            startActivity(intent);
            finish();
        });

        databaseSchedules = FirebaseDatabase.getInstance().getReference("Schedules");
        loadAvailableSessions();
    }

    private void loadAvailableSessions() {
        ListView listView = findViewById(R.id.sessions_list);

        ArrayAdapter<TimeSlot> adapter = new ArrayAdapter<TimeSlot>(this, android.R.layout.simple_list_item_1, availableSlots);
        listView.setAdapter(adapter);

        // get all free time slots
        databaseSchedules.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                availableSlots.clear();
                if (snapshot.exists()) {
                    for (DataSnapshot scheduleSnapshot : snapshot.getChildren()) {
                        GenericTypeIndicator<List<TimeSlot>> t = new GenericTypeIndicator<List<TimeSlot>>() {};
                        List<TimeSlot> allSlots = scheduleSnapshot.child("timeSlots").getValue(t);
                        if(allSlots !=null) {
                            for (int i = 0; i < Objects.requireNonNull(allSlots).size(); i++) {
                                TimeSlot slot = allSlots.get(i);
                                if (slot.getStatus() == TimeSlot.Status.FREE) {
                                    availableSlots.add(slot);
                                }
                            }
                        }
                    }
                    adapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(StudentSessions.this, "No available sessions found", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(StudentSessions.this, "Database Error", Toast.LENGTH_LONG).show();
                return;
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TimeSlot selectedSession = availableSlots.get(position);
                requestSession(selectedSession, availableSlots, adapter);
            }
        });
    }

    // alert dialog for confirming session request
    private void requestSession(TimeSlot selectedSession, List<TimeSlot> availableSlots, ArrayAdapter<TimeSlot> adapter) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Request a session?");

        builder.setMessage(selectedSession.toString());
        builder.setNegativeButton("Request Session", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                sessionRequested(selectedSession);
                availableSlots.remove(selectedSession);
                adapter.notifyDataSetChanged();
                Toast.makeText(StudentSessions.this, "Session Requested", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNeutralButton("Exit", null);
        builder.show();
    }

    // updating session status to pending and filling in student id
    private void sessionRequested(@NonNull TimeSlot slot) {
        databaseSchedules.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot scheduleSnapshot : snapshot.getChildren()) {
                    for (DataSnapshot slotSnapshot : scheduleSnapshot.child("timeSlots").getChildren()) {
                        TimeSlot dbSlot = slotSnapshot.getValue(TimeSlot.class);
                        if (dbSlot != null && dbSlot.equals(slot)) {
                            slotSnapshot.getRef().child("status").setValue("PENDING")
                                    .addOnFailureListener(e ->
                                            Toast.makeText(StudentSessions.this, "Error requesting: " + e.getMessage(), Toast.LENGTH_LONG).show()
                                    );
                            slotSnapshot.getRef().child("studentID").setValue(u.getId())
                                    .addOnFailureListener(e ->
                                            Toast.makeText(StudentSessions.this, "Error requesting: " + e.getMessage(), Toast.LENGTH_LONG).show()
                                    );
                            return;
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(StudentSessions.this, "Error requesting session", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
