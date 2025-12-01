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
import com.example.project_group_17.StudentFunctions.RequestClasses.Request;
import com.example.project_group_17.StudentFunctions.RequestClasses.StudentSchedule;
import com.example.project_group_17.TutorFunctions.ListDisplays.PendingSessions;
import com.example.project_group_17.TutorFunctions.Schedule;
import com.example.project_group_17.TutorFunctions.TimeSlot;
import com.example.project_group_17.TutorFunctions.TutorListView;
import com.example.project_group_17.UserHierarchy.Student;
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

public class StudentRequests extends AppCompatActivity {
    DatabaseReference databaseSchedules;
    private Button goBack;
    DatabaseReference databaseStudentSchedules;
    DatabaseReference studentSchedulesReference;
    List<Request> requests;
    Student u;
    StudentSchedule schedule;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_requests);

        Serializable sr = getIntent().getSerializableExtra("userInfo");
        u = (Student) sr;

        requests=new ArrayList<Request>();

        goBack = findViewById(R.id.seerejectedBtn);

        goBack.setOnClickListener(v ->{
            Intent intent = new Intent(StudentRequests.this, StudentListView.class);
            intent.putExtra("userInfo", u);
            startActivity(intent);
            finish();
        });

        databaseSchedules = FirebaseDatabase.getInstance().getReference("Schedules");
        databaseStudentSchedules= FirebaseDatabase.getInstance().getReference("StudentSchedules");
        loadRequests();
    }

    private void loadRequests(){
        ListView listView = findViewById(R.id.listView);

        ArrayAdapter<Request> adapter = new ArrayAdapter<Request>(this, android.R.layout.simple_list_item_1, requests);
        listView.setAdapter(adapter);
        //IN the schedules database see if there is one with the same userid as the tutor that opened this class
        databaseStudentSchedules.orderByChild("userID").equalTo(u.getId()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                requests.clear();
                if (snapshot.exists()) {
                    for (DataSnapshot scheduleSnapshot : snapshot.getChildren()) {
                        String scheduleId = scheduleSnapshot.getKey();
                        schedule = scheduleSnapshot.getValue(StudentSchedule.class);
                        studentSchedulesReference = databaseStudentSchedules.child(scheduleId);

                        GenericTypeIndicator<List<Request>> t = new GenericTypeIndicator<List<Request>>() {};
                        List<Request> allRequests = scheduleSnapshot.child("requests").getValue(t);
                        if (allRequests == null) {
                            allRequests = new ArrayList<>();
                        }

                        for (Request req : allRequests) {
                            if (!req.getPast()) {
                                requests.add(req);
                            }
                        }
                        //Get the schedule that that is related to the tutor that opened this class
                        //Retrieve the list of timeslots and check if any of the timeslots are pending and not already past
                        //if not add them to the pendingSlots list
                    }
                    adapter.notifyDataSetChanged();
                }
                else {
                    Toast.makeText(com.example.project_group_17.StudentFunctions.StudentRequests.this, "No upcoming sessions found", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(com.example.project_group_17.StudentFunctions.StudentRequests.this, "Database error", Toast.LENGTH_LONG).show();
                return;
            }
        });
        //when an item in the list is clicked we create a new alert dialog to approve or reject
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Request selectedRequest = requests.get(position);
                approveOrReject(selectedRequest, requests, adapter);
            }
        });
    }

    private void approveOrReject(Request selectedRequest, List<Request> pendingRequests, ArrayAdapter<Request> adapter){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("View Request");

        builder.setNegativeButton("Cancel Request", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                cancelRequest(selectedRequest,adapter);
                requests.remove(selectedRequest);
                adapter.notifyDataSetChanged();
                Toast.makeText(StudentRequests.this, "Request cancelled", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNeutralButton("Exit", null);


        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("Users");
        usersRef.child(u.getId()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String msg;
                if (snapshot.exists()) {
                    msg = "Requested slot offered by: "+selectedRequest.getTutorFirstName()+" "+ selectedRequest.getTutorLastName()+
                            "\n On: "+selectedRequest.getDate()+
                            "\n From: "+selectedRequest.getStart()+
                            "\n To: "+selectedRequest.getEnd();
                } else {
                    msg = "Unknown Request";
                }
                builder.setMessage(msg);
                builder.show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                builder.setMessage("Error loading request info");
                builder.show();
            }
        });

    }

    private void cancelRequest(@NonNull Request req,ArrayAdapter<Request> adapter){
        //Removes the request
        req.cancel();

        //Removes the request from the timeslot in Schedules
        databaseSchedules.orderByChild("userID").equalTo(req.getTutorID()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot scheduleSnapshot : snapshot.getChildren()) {
                    for (DataSnapshot slotSnapshot : scheduleSnapshot.child("timeSlots").getChildren()) {
                        TimeSlot dbSlot = slotSnapshot.getValue(TimeSlot.class);
                        if (dbSlot != null && Objects.equals(dbSlot.getSlotID(), req.getSlotID())) {
                            // Update status field in Firebase

                            //removes this request from the timeslots list of requesting students
                            dbSlot.removeRequest(req.getStudentID());
                            slotSnapshot.getRef().child("studentsRequesting").setValue(dbSlot.getStudentsRequesting())
                                    .addOnFailureListener(e ->
                                            Toast.makeText(StudentRequests.this, "Error requesting: " + e.getMessage(), Toast.LENGTH_LONG).show()
                                    );
                            Toast.makeText(StudentRequests.this, "Session rejected", Toast.LENGTH_LONG).show();
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(StudentRequests.this, "Error updating session status", Toast.LENGTH_SHORT).show();
            }
        });
        //Updates the student schedule so the request is now cancelled
        databaseStudentSchedules.orderByChild("userID").equalTo(req.getStudentID()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot scheduleSnapshot : snapshot.getChildren()) {
                    for (DataSnapshot slotSnapshot : scheduleSnapshot.child("requests").getChildren()) {
                        Request dbReq = slotSnapshot.getValue(Request.class);
                        if (dbReq != null && Objects.equals(dbReq.getSlotID(), req.getSlotID())) {
                            // Update status field in Firebase


                            slotSnapshot.getRef().child("status").setValue("CANCELLED")
                                    .addOnFailureListener(e ->
                                            Toast.makeText(StudentRequests.this, "Error updating session: " + e.getMessage(), Toast.LENGTH_LONG).show()
                                    );
                            requests.remove(req);
                            adapter.notifyDataSetChanged();
                            Toast.makeText(StudentRequests.this, "Session rejected", Toast.LENGTH_LONG).show();
                            return;
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(StudentRequests.this, "Error updating session status", Toast.LENGTH_SHORT).show();
            }
        });

    }
}
