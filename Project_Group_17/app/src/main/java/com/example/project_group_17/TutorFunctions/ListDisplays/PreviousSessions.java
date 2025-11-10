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
import java.util.List;
import java.util.Objects;

public class PreviousSessions extends AppCompatActivity {
    DatabaseReference databaseSchedule;
    private Button goBack;
    User u;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.past_sessions);

        Serializable se = getIntent().getSerializableExtra("userInfo");
        u = (User) se;

        databaseSchedule = FirebaseDatabase.getInstance().getReference("Schedules");
        loadUpcomingSessions();

        goBack = findViewById(R.id.seerejectedBtn);

        goBack.setOnClickListener(v ->{
            Intent intent = new Intent(com.example.project_group_17.TutorFunctions.ListDisplays.PreviousSessions.this, TutorListView.class);
            intent.putExtra("userInfo", u);
            startActivity(intent);
            finish();
        });
    }

    //Load requests loops through all of the users active in the database and finds the ones
    //whose registration status is pending and adds them to the pendingRequests list
    //this list is then displayed using the list view
    private void loadUpcomingSessions(){
        ListView listView = findViewById(R.id.listView);

        List<TimeSlot> upComingSlots = new ArrayList<TimeSlot>();

        ArrayAdapter<TimeSlot> adapter = new ArrayAdapter<TimeSlot>(this, android.R.layout.simple_list_item_1, upComingSlots);
        listView.setAdapter(adapter);
        //IN the schedules database see if there is one with the same userid as the tutor that opened this class
        databaseSchedule.orderByChild("userID").equalTo(u.getId()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot scheduleSnapshot : snapshot.getChildren()) {
                        GenericTypeIndicator<List<TimeSlot>> t = new GenericTypeIndicator<List<TimeSlot>>() {};
                        List<TimeSlot> allSlots = scheduleSnapshot.child("timeSlots").getValue(t);
                        for(int i = 0; i< Objects.requireNonNull(allSlots).size(); i++){
                            TimeSlot slot = allSlots.get(i);
                            if(slot.getPast()){
                                upComingSlots.add(slot);
                            }
                        }
                        //Get the schedule that that is related to the tutor that opened this class
                        //Retrieve the list of timeslots and check if they are in the past or not
                        //if they are in the past add them to the previous sessions list
                    }
                    adapter.notifyDataSetChanged();
                }
                else {
                    Toast.makeText(com.example.project_group_17.TutorFunctions.ListDisplays.PreviousSessions.this, "No upcoming sessions found", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(com.example.project_group_17.TutorFunctions.ListDisplays.PreviousSessions.this, "Database error", Toast.LENGTH_LONG).show();
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
    private void approveOrReject(TimeSlot selectedUser, List<TimeSlot> pendingRequests, ArrayAdapter<TimeSlot> adapter){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Previous Session");
        //Change so that we get the student that created the timeslot and display their information
        builder.setMessage(selectedUser.toString());
        builder.setNeutralButton("Exit", null);
        builder.show();
    }
}
