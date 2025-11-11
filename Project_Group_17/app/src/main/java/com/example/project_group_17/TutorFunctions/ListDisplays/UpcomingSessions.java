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

import com.example.project_group_17.AdminFunctions.RejectedRequests;
import com.example.project_group_17.R;
import com.example.project_group_17.TutorFunctions.Schedule;
import com.example.project_group_17.TutorFunctions.TimeSlot;
import com.example.project_group_17.TutorFunctions.TutorListView;
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
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class UpcomingSessions extends AppCompatActivity {
    DatabaseReference databaseSchedules;
    private Button goBack;
    List<TimeSlot> upComingSlots = new ArrayList<TimeSlot>();
    User u;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.upcoming_sessions);

        Serializable se = getIntent().getSerializableExtra("userInfo");
        u = (User) se;

        goBack = findViewById(R.id.seerejectedBtn);

        goBack.setOnClickListener(v ->{
            Intent intent = new Intent(com.example.project_group_17.TutorFunctions.ListDisplays.UpcomingSessions.this, TutorListView.class);
            intent.putExtra("userInfo", u);
            startActivity(intent);
            finish();
        });

        databaseSchedules = FirebaseDatabase.getInstance().getReference("Schedules");
        loadUpcomingSessions();
    }

    //Load requests loops through all of the users active in the database and finds the ones
    //whose registration status is pending and adds them to the pendingRequests list
    //this list is then displayed using the list view
    private void loadUpcomingSessions(){
        ListView listView = findViewById(R.id.listView);

        ArrayAdapter<TimeSlot> adapter = new ArrayAdapter<TimeSlot>(this, android.R.layout.simple_list_item_1, upComingSlots);
        listView.setAdapter(adapter);
        //IN the schedules database see if there is one with the same userid as the tutor that opened this class
        databaseSchedules.orderByChild("userID").equalTo(u.getId()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                upComingSlots.clear();
                if (snapshot.exists()) {
                    for (DataSnapshot scheduleSnapshot : snapshot.getChildren()) {
                        GenericTypeIndicator<List<TimeSlot>> t = new GenericTypeIndicator<List<TimeSlot>>() {};
                        List<TimeSlot> allSlots = scheduleSnapshot.child("timeSlots").getValue(t);
                        if(allSlots !=null){
                            for(int i = 0; i< Objects.requireNonNull(allSlots).size(); i++){
                                TimeSlot slot = allSlots.get(i);
                                if(!slot.getPast()&&!slot.isCancelled()){
                                    upComingSlots.add(slot);
                                }
                            }
                        }

                        //Get the schedule that that is related to the tutor that opened this class
                        //Retrieve the list of timeslots and check if they are in the past or not
                        //if not add them to the upcomingslots list
                    }
                    adapter.notifyDataSetChanged();
                }
                else {
                    Toast.makeText(com.example.project_group_17.TutorFunctions.ListDisplays.UpcomingSessions.this, "No upcoming sessions found", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(com.example.project_group_17.TutorFunctions.ListDisplays.UpcomingSessions.this, "Database error", Toast.LENGTH_LONG).show();
                return;
            }
        });
        //when an item in the list is clicked we create a new alert dialog to approve or reject
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TimeSlot selectedSession = upComingSlots.get(position);
                approveOrReject(selectedSession, upComingSlots, adapter);
            }
        });
    }

    //Creates a new alert dialog with the users information where the admin can either approve or reject the request
    private void approveOrReject(TimeSlot selectedSession, List<TimeSlot> upComingSlots, ArrayAdapter<TimeSlot> adapter){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Cancel The Session?");
        //Should get the student attached to the timeslots information
        builder.setMessage(selectedSession.toString());

        builder.setNegativeButton("Cancel Session", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                cancelSession(selectedSession);
                upComingSlots.remove(selectedSession);
                adapter.notifyDataSetChanged();
                Toast.makeText(com.example.project_group_17.TutorFunctions.ListDisplays.UpcomingSessions.this, "Session Cancelled", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNeutralButton("Exit", null);
        builder.show();

    }

    private void cancelSession(@NonNull TimeSlot slot) {
        //Cancels locally
        slot.cancel();

        //Cancels in the database
        // Get the tutor schedule
        databaseSchedules.orderByChild("userID").equalTo(u.getId()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot scheduleSnapshot : snapshot.getChildren()) {
                    for (DataSnapshot slotSnapshot : scheduleSnapshot.child("timeSlots").getChildren()) {
                        TimeSlot dbSlot = slotSnapshot.getValue(TimeSlot.class);
                        if (dbSlot != null && dbSlot.equals(slot)) {
                            // Update status field in Firebase

                            slotSnapshot.getRef().child("status").setValue("CANCELLED")
                                    .addOnSuccessListener(aVoid ->
                                            Toast.makeText(UpcomingSessions.this, "Session cancelled successfully", Toast.LENGTH_SHORT).show()
                                    )
                                    .addOnFailureListener(e ->
                                            Toast.makeText(UpcomingSessions.this, "Error updating session: " + e.getMessage(), Toast.LENGTH_LONG).show()
                                    );
                            return;
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(UpcomingSessions.this, "Error updating session status", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
