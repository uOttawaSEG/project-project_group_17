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
import com.example.project_group_17.Screens.TutorCreatingSlots;
import com.example.project_group_17.Screens.UserScreen;
import com.example.project_group_17.StudentFunctions.RequestClasses.Request;
import com.example.project_group_17.StudentFunctions.RequestClasses.StudentSchedule;
import com.example.project_group_17.TutorFunctions.ListDisplays.UpcomingSessions;
import com.example.project_group_17.TutorFunctions.Schedule;
import com.example.project_group_17.TutorFunctions.TimeSlot;
import com.example.project_group_17.UserHierarchy.Student;
import com.example.project_group_17.UserHierarchy.Tutor;
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
    DatabaseReference databaseStudentSchedules;

    DatabaseReference databaseUsers;
    private Button goBack;
    private StudentSchedule schedule;
    private String id;
    List<TimeSlot> availableSlots = new ArrayList<TimeSlot>();
    List<String> tutorList = new ArrayList<>();
    Student u;
    String sCourse;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.student_sessions);

        Serializable se = getIntent().getSerializableExtra("userInfo");
        Serializable sc = getIntent().getSerializableExtra("courseInfo");
        if (sc instanceof String) {
            sCourse = (String) sc;
        }
        if(se instanceof Student){
            u = (Student) se;
        } else{
            Toast.makeText(StudentSessions.this, "Not a Student User", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(StudentSessions.this, UserScreen.class);
            intent.putExtra("userInfo", se);
            startActivity(intent);
            finish();
        }

        goBack = findViewById(R.id.gobackBtn);

        goBack.setOnClickListener(v ->{
            Intent intent = new Intent(StudentSessions.this, StudentSearch.class);
            intent.putExtra("userInfo", u);
            startActivity(intent);
            finish();
        });

        databaseSchedules = FirebaseDatabase.getInstance().getReference("Schedules");
        databaseStudentSchedules = FirebaseDatabase.getInstance().getReference("StudentSchedules");
        databaseUsers = FirebaseDatabase.getInstance().getReference("Users");
        loadTutors();
    }

    private void loadTutors() {
        databaseUsers.orderByChild("userType").equalTo("Tutor").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                tutorList.clear();
                if (snapshot.exists()) {
                    for (DataSnapshot tutorSnap : snapshot.getChildren()) {
                        Tutor tutor = tutorSnap.getValue(Tutor.class);
                        if (tutor != null && tutor.getCourses() != null) {
                            if (tutor.getCourses().contains(sCourse)) {
                                tutorList.add(tutor.getId());
                            }
                        }
                    }
                    loadAvailableSessions();
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
    }

    private void loadAvailableSessions() {

        ListView listView = findViewById(R.id.sessions_list);

        ArrayAdapter<TimeSlot> adapter = new ArrayAdapter<TimeSlot>(this, android.R.layout.simple_list_item_1, availableSlots);
        listView.setAdapter(adapter);
        availableSlots.clear();
        for (String id : tutorList) {
            databaseSchedules.orderByChild("userId").equalTo(id).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        for (DataSnapshot scheduleSnapshot : snapshot.getChildren()) {
                            GenericTypeIndicator<List<TimeSlot>> t = new GenericTypeIndicator<List<TimeSlot>>() {};
                            List<TimeSlot> allSlots = scheduleSnapshot.child("timeSlots").getValue(t);
                            if(allSlots !=null) {
                                for (int i = 0; i < Objects.requireNonNull(allSlots).size(); i++) {
                                    TimeSlot slot = allSlots.get(i);
                                    if (!slot.getPast() &&(slot.getStatus() == TimeSlot.Status.FREE||slot.getStatus()==TimeSlot.Status.PENDING)) {
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
        }


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
                createSchedule(selectedSession, adapter);
                availableSlots.remove(selectedSession);
                adapter.notifyDataSetChanged();
            }
        });
        builder.setNeutralButton("Exit", null);
        builder.show();
    }
    //Creates or loads the schedule
    public void createSchedule(TimeSlot slot, ArrayAdapter<TimeSlot> adapter) {
        databaseStudentSchedules.orderByChild("userID").equalTo(u.getId()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot scheduleSnapshot : snapshot.getChildren()) {
                        DataSnapshot child = snapshot.getChildren().iterator().next();
                        id = child.getKey();
                        schedule = child.getValue(StudentSchedule.class);
                    }
                } else {
                    id = databaseStudentSchedules.push().getKey();
                    schedule = new StudentSchedule(u.getId());
                    databaseStudentSchedules.child(id).setValue(schedule);
                }
                createRequest(slot, adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(StudentSessions.this, "Database error", Toast.LENGTH_LONG).show();
                return;
            }
        });
    }

    public void createRequest(TimeSlot slot, ArrayAdapter<TimeSlot> adapter) {

        String d = slot.getDate();
        String start = slot.getStart();
        String end = slot.getEnd();
        boolean auto = slot.getAuto();

        if (schedule.overlapChecking(d, start, end)) {
            Toast.makeText(this, "Conflicting Slot: You have already registered for a session in that perios", Toast.LENGTH_SHORT).show();
        } else {
            Request r = new Request(d, start,end,u.getId(),slot.getTutorID(),slot.getSlotID(), slot.getTutorName(), slot.getTutorRatingStar());
            TimeSlot.Status status;
            if(auto){
                status = TimeSlot.Status.BOOKED;
                r.approve();
            } else {
                status = TimeSlot.Status.PENDING;
            }
            schedule.add(r);
            databaseStudentSchedules.child(id).setValue(schedule);
            updateSlot(slot, adapter, status);
            Toast.makeText(this, "Successfully created request.", Toast.LENGTH_SHORT).show();
        }
    }
    // updating session status to pending and filling in student id
    private void updateSlot(@NonNull TimeSlot slot, ArrayAdapter<TimeSlot> adapter, TimeSlot.Status status) {

        //Updates the timeslot with the new student request and sets it to Pending
        databaseSchedules.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot scheduleSnapshot : snapshot.getChildren()) {
                    for (DataSnapshot slotSnapshot : scheduleSnapshot.child("timeSlots").getChildren()) {
                        TimeSlot dbSlot = slotSnapshot.getValue(TimeSlot.class);
                        if (dbSlot != null && Objects.equals(dbSlot.getSlotID(), slot.getSlotID())) {
                            if(slot.getAuto()){
                                slot.clearRequests();
                            }else{
                                slot.addRequest(u.getId());
                            }
                            slot.setStatus(status);
                            slotSnapshot.getRef().child("status").setValue(status)
                                    .addOnFailureListener(e ->
                                            Toast.makeText(StudentSessions.this, "Error requesting: " + e.getMessage(), Toast.LENGTH_LONG).show()
                                    );
                            slotSnapshot.getRef().child("studentsRequesting").setValue(slot.getStudentsRequesting())
                                    .addOnFailureListener(e ->
                                            Toast.makeText(StudentSessions.this, "Error requesting: " + e.getMessage(), Toast.LENGTH_LONG).show()
                                    );
                            Toast.makeText(StudentSessions.this, "Session Requested", Toast.LENGTH_LONG).show();
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
