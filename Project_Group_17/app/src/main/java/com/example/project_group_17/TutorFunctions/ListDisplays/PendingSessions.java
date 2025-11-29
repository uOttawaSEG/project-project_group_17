package com.example.project_group_17.TutorFunctions.ListDisplays;

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

import com.example.project_group_17.AdminFunctions.PendingRequests;
import com.example.project_group_17.R;
import com.example.project_group_17.StudentFunctions.StudentSessions;
import com.example.project_group_17.TutorFunctions.Schedule;
import com.example.project_group_17.TutorFunctions.TimeSlot;
import com.example.project_group_17.TutorFunctions.TutorListView;
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

public class PendingSessions extends AppCompatActivity {
    DatabaseReference databaseSchedules;
    private Button goBack;
    DatabaseReference scheduleReference;
    Schedule schedule;
    List<TimeSlot> pendingSlots = new ArrayList<TimeSlot>();
    User u;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pending_sessions);

        Serializable se = getIntent().getSerializableExtra("userInfo");
        u = (User) se;

        goBack = findViewById(R.id.seerejectedBtn);

        goBack.setOnClickListener(v ->{
            Intent intent = new Intent(com.example.project_group_17.TutorFunctions.ListDisplays.PendingSessions.this, TutorListView.class);
            intent.putExtra("userInfo", u);
            startActivity(intent);
            finish();
        });

        databaseSchedules = FirebaseDatabase.getInstance().getReference("Schedules");
        loadRequests();
    }

    //Will implement once the students can make requests
    private void loadRequests(){
        ListView listView = findViewById(R.id.listView);

        ArrayAdapter<TimeSlot> adapter = new ArrayAdapter<TimeSlot>(this, android.R.layout.simple_list_item_1, pendingSlots);
        listView.setAdapter(adapter);
        //IN the schedules database see if there is one with the same userid as the tutor that opened this class
        databaseSchedules.orderByChild("userID").equalTo(u.getId()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                pendingSlots.clear();
                if (snapshot.exists()) {
                    for (DataSnapshot scheduleSnapshot : snapshot.getChildren()) {
                        String scheduleId = scheduleSnapshot.getKey();
                        schedule = scheduleSnapshot.getValue(Schedule.class);
                        scheduleReference = databaseSchedules.child(scheduleId);

                        GenericTypeIndicator<List<TimeSlot>> t = new GenericTypeIndicator<List<TimeSlot>>() {};
                        List<TimeSlot> allSlots = scheduleSnapshot.child("timeSlots").getValue(t);
                        if(allSlots !=null) {
                            for (int i = 0; i < Objects.requireNonNull(allSlots).size(); i++) {
                                TimeSlot slot = allSlots.get(i);
                                if (slot.isPending() && !slot.getPast()) {
                                    pendingSlots.add(slot);
                                }
                            }
                        }
                        //Get the schedule that that is related to the tutor that opened this class
                        //Retrieve the list of timeslots and check if any of the timeslots are pending and not already past
                        //if not add them to the pendingSlots list
                    }
                    adapter.notifyDataSetChanged();
                }
                else {
                    Toast.makeText(com.example.project_group_17.TutorFunctions.ListDisplays.PendingSessions.this, "No upcoming sessions found", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(com.example.project_group_17.TutorFunctions.ListDisplays.PendingSessions.this, "Database error", Toast.LENGTH_LONG).show();
                return;
            }
        });
        //when an item in the list is clicked we create a new alert dialog to approve or reject
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TimeSlot selectedSession = pendingSlots.get(position);
                approveOrReject(selectedSession, pendingSlots, adapter);
            }
        });
    }

    //Creates a new alert dialog with the users information where the admin can either approve or reject the request
    private void approveOrReject(TimeSlot selectedSlot, List<TimeSlot> pendingRequests, ArrayAdapter<TimeSlot> adapter){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Approve or Reject the request.");

        builder.setPositiveButton("Approve", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                approveRequest(selectedSlot, adapter);
                pendingRequests.remove(selectedSlot);
                adapter.notifyDataSetChanged();
                Toast.makeText(PendingSessions.this, "Request Approved", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("Reject", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                rejectRequest(selectedSlot, adapter);
                pendingRequests.remove(selectedSlot);
                adapter.notifyDataSetChanged();
                Toast.makeText(PendingSessions.this, "Request Rejected", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNeutralButton("Cancel", null);

        if (selectedSlot.isPending()) {
            DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("Users");
            usersRef.child(selectedSlot.getStudentID()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    String msg;
                    if (snapshot.exists()) {
                        String firstName = snapshot.child("firstName").getValue(String.class);
                        String lastName = snapshot.child("lastName").getValue(String.class);
                        String email = snapshot.child("email").getValue(String.class);
                        String phoneNumber = snapshot.child("phoneNumber").getValue(String.class);

                        msg = "Requested by: " + firstName + " " + lastName +
                                "\nEmail: " + email +
                                "\nPhone: " + phoneNumber +
                                "\n\nSession Time: " + selectedSlot.toString();
                    } else {
                        msg = "Booked by unknown student\n\nSession Time: " + selectedSlot.toString();
                    }
                    builder.setMessage(msg);
                    builder.show();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    builder.setMessage("Error loading student info");
                    builder.show();
                }
            });
        } else {
            builder.setMessage(selectedSlot.toString());
            builder.show();
        }
    }

    private void approveRequest(@NonNull TimeSlot slot, ArrayAdapter<TimeSlot> adapter){
        //Books locally
        slot.book();
        //slot.setStudentID(u.getId());


        //Cancels in the database
        // Get the tutor schedule
        databaseSchedules.orderByChild("userID").equalTo(u.getId()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot scheduleSnapshot : snapshot.getChildren()) {
                    for (DataSnapshot slotSnapshot : scheduleSnapshot.child("timeSlots").getChildren()) {
                        TimeSlot dbSlot = slotSnapshot.getValue(TimeSlot.class);
                        if (dbSlot != null && Objects.equals(dbSlot.getSlotID(), slot.getSlotID())) {
                            // Update status field in Firebase

                            slotSnapshot.getRef().child("status").setValue("BOOKED")
                                    .addOnFailureListener(e ->
                                            Toast.makeText(PendingSessions.this, "Error updating session: " + e.getMessage(), Toast.LENGTH_LONG).show()
                                    );
                            //This doesn't make much sense at all and I have no idea what I was thinking when I added it
                            /*slotSnapshot.getRef().child("studentID").setValue(u.getId())
                                    .addOnSuccessListener(aVoid ->
                                            Toast.makeText(PendingSessions.this, "Session cancelled successfully", Toast.LENGTH_SHORT).show()
                                    )
                                    .addOnFailureListener(e ->
                                            Toast.makeText(PendingSessions.this, "Error updating session: " + e.getMessage(), Toast.LENGTH_LONG).show()
                                    );*/
                            pendingSlots.remove(slot);
                            adapter.notifyDataSetChanged();
                            Toast.makeText(PendingSessions.this, "Session Booked", Toast.LENGTH_LONG).show();
                            return;
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(PendingSessions.this, "Error updating session status", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void rejectRequest(@NonNull TimeSlot slot, ArrayAdapter<TimeSlot> adapter){
        //Cancels locally
        slot.cancel();
        slot.setStudentID("Available");


        //Cancels in the database
        // Get the tutor schedule
        databaseSchedules.orderByChild("userID").equalTo(u.getId()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot scheduleSnapshot : snapshot.getChildren()) {
                    for (DataSnapshot slotSnapshot : scheduleSnapshot.child("timeSlots").getChildren()) {
                        TimeSlot dbSlot = slotSnapshot.getValue(TimeSlot.class);
                        if (dbSlot != null && Objects.equals(dbSlot.getSlotID(), slot.getSlotID())) {
                            // Update status field in Firebase

                            //need to add functionality for when a request is rejected
                            slotSnapshot.getRef().child("status").setValue("PENDING")
                                    .addOnFailureListener(e ->
                                            Toast.makeText(PendingSessions.this, "Error updating session: " + e.getMessage(), Toast.LENGTH_LONG).show()
                                    );
                            slotSnapshot.getRef().child("studentID").setValue("Available")
                                    .addOnSuccessListener(aVoid ->
                                            Toast.makeText(PendingSessions.this, "Session cancelled successfully", Toast.LENGTH_SHORT).show()
                                    )
                                    .addOnFailureListener(e ->
                                            Toast.makeText(PendingSessions.this, "Error updating session: " + e.getMessage(), Toast.LENGTH_LONG).show()
                                    );
                            pendingSlots.remove(slot);
                            adapter.notifyDataSetChanged();
                            Toast.makeText(PendingSessions.this, "Session rejected", Toast.LENGTH_LONG).show();
                            return;
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(PendingSessions.this, "Error updating session status", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
