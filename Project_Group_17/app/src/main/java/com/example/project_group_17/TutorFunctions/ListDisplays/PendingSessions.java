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
import com.example.project_group_17.TutorFunctions.Schedule;
import com.example.project_group_17.TutorFunctions.TimeSlot;
import com.example.project_group_17.TutorFunctions.TutorListView;
import com.example.project_group_17.UserHierarchy.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class PendingSessions extends AppCompatActivity {
    DatabaseReference databaseUsers;
    private Button goBack;
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

        databaseUsers = FirebaseDatabase.getInstance().getReference("Schedules");
        //loadRequests();
    }

    //Will implement once the students can make requests
   /* private void loadRequests(){
        ListView listView = findViewById(R.id.listView);

        List<TimeSlot> upComingSlots = new ArrayList<TimeSlot>();

        ArrayAdapter<TimeSlot> adapter = new ArrayAdapter<TimeSlot>(this, android.R.layout.simple_list_item_1, upComingSlots);
        listView.setAdapter(adapter);
        //IN the schedules database see if there is one with the same userid as the tutor that opened this class
        databaseUsers.orderByChild("userID").equalTo(u.getId()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                        Schedule schedule;
                        //Get the schedule that that is related to the tutor that opened this class
                        //Retrieve the list of timeslots and check if they are in the past or not
                        //if not add them to the upcomingslots list
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
                TimeSlot selectedSession = upComingSlots.get(position);
                approveOrReject(selectedSession, upComingSlots, adapter);
            }
        });
    }*/

    //Creates a new alert dialog with the users information where the admin can either approve or reject the request
    private void approveOrReject(User selectedUser, List<User> pendingRequests, ArrayAdapter<User> adapter){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Approve or Reject the request.");
        builder.setMessage(selectedUser.toString());

        builder.setPositiveButton("Approve", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                approveRequest(selectedUser);
                pendingRequests.remove(selectedUser);
                adapter.notifyDataSetChanged();
                Toast.makeText(PendingSessions.this, "Request Approved", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("Reject", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                rejectRequest(selectedUser);
                pendingRequests.remove(selectedUser);
                adapter.notifyDataSetChanged();
                Toast.makeText(PendingSessions.this, "Request Rejected", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNeutralButton("Cancel", null);
        builder.show();

    }

    private void approveRequest(@NonNull User user){

    }
    private void rejectRequest(@NonNull User user){

    }
}
