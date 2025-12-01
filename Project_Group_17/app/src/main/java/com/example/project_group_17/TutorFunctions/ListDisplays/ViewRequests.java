package com.example.project_group_17.TutorFunctions.ListDisplays;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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
import com.example.project_group_17.Screens.LoginScreen;
import com.example.project_group_17.Screens.UserScreen;
import com.example.project_group_17.StudentFunctions.RequestClasses.Request;
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

public class ViewRequests extends AppCompatActivity {
    DatabaseReference databaseSchedules;
    DatabaseReference databaseStudentSchedules;
    DatabaseReference databaseUsers;
    private Button goBack;
    DatabaseReference scheduleReference;
    List<String> students = new ArrayList<String>();
    TimeSlot slot;
    User u;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_requests);

        Serializable se = getIntent().getSerializableExtra("selectedSlot");
        Serializable sr = getIntent().getSerializableExtra("userInfo");
        u = (User) sr;
        slot = (TimeSlot) se;

        goBack = findViewById(R.id.seerejectedBtn);

        goBack.setOnClickListener(v ->{
            Intent intent = new Intent(com.example.project_group_17.TutorFunctions.ListDisplays.ViewRequests.this, PendingSessions.class);
            intent.putExtra("userInfo", u);
            startActivity(intent);
            finish();
        });

        databaseSchedules = FirebaseDatabase.getInstance().getReference("Schedules");
        databaseUsers = FirebaseDatabase.getInstance().getReference("Users");
        databaseStudentSchedules=FirebaseDatabase.getInstance().getReference("StudentSchedules");
        loadRequests();
    }

    private void loadRequests(){
        ListView listView = findViewById(R.id.listView);

        students = slot.getStudentsRequesting();
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, students);
        listView.setAdapter(adapter);

        //when an item in the list is clicked we create a new alert dialog to approve or reject
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selectedRequest = students.get(position);
                approveOrReject(selectedRequest, students, adapter);
            }
        });
    }

    private void approveOrReject(String selectedRequest, List<String> pendingRequests, ArrayAdapter<String> adapter){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Approve or Reject Request");

        builder.setPositiveButton("Approve Request", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                approveRequest(selectedRequest, adapter);
                students.remove(selectedRequest);
                adapter.notifyDataSetChanged();
                Toast.makeText(ViewRequests.this, "Request Approved", Toast.LENGTH_SHORT).show();

            }
        });
        builder.setNegativeButton("Reject Request", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                rejectRequest(selectedRequest,adapter);
                students.remove(selectedRequest);
                adapter.notifyDataSetChanged();
                Toast.makeText(ViewRequests.this, "Request Rejected", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNeutralButton("Cancel", null);


            DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("Users");
            usersRef.child(selectedRequest).addListenerForSingleValueEvent(new ValueEventListener() {
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
                                "\n\nSession Time: " + slot.toString();
                    } else {
                        msg = "Requested by unknown student\n\nSession Time: " + slot.toString();
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

    }

    private void approveRequest(@NonNull String studentID, ArrayAdapter<String> adapter){
        //Books locally
        slot.book();
        //Clears the list of requesting students
        slot.clearRequests();
        slot.setStudentID(studentID);
        List<String> requestsToUpdate = slot.getStudentsRequesting();


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
                                            Toast.makeText(ViewRequests.this, "Error updating session: " + e.getMessage(), Toast.LENGTH_LONG).show()
                                    );
                            slotSnapshot.getRef().child("studentID").setValue(studentID)
                                    .addOnFailureListener(e ->
                                            Toast.makeText(ViewRequests.this, "Error updating session: " + e.getMessage(), Toast.LENGTH_LONG).show()
                                    );
                            slotSnapshot.getRef().child("studentsRequesting").setValue(slot.getStudentsRequesting())
                                    .addOnFailureListener(e ->
                                            Toast.makeText(ViewRequests.this, "Error requesting: " + e.getMessage(), Toast.LENGTH_LONG).show()
                                    );
                            students.remove(studentID);
                            adapter.notifyDataSetChanged();
                            Toast.makeText(ViewRequests.this, "Session Booked", Toast.LENGTH_LONG).show();
                            updateRequestApproved(studentID);
                            rejectAllOtherRequests(studentID);
                            return;
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ViewRequests.this, "Error updating session status", Toast.LENGTH_SHORT).show();
            }
        });

        //Since the request has been approved send back to the slot view
        Intent intent = new Intent(com.example.project_group_17.TutorFunctions.ListDisplays.ViewRequests.this, PendingSessions.class);
        intent.putExtra("userInfo", u);
        startActivity(intent);
        finish();
    }
    private void rejectRequest(@NonNull String studentID,ArrayAdapter<String> adapter){
        //Removes the request
        slot.removeRequest(studentID);

        databaseSchedules.orderByChild("userID").equalTo(u.getId()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot scheduleSnapshot : snapshot.getChildren()) {
                    for (DataSnapshot slotSnapshot : scheduleSnapshot.child("timeSlots").getChildren()) {
                        TimeSlot dbSlot = slotSnapshot.getValue(TimeSlot.class);
                        if (dbSlot != null && Objects.equals(dbSlot.getSlotID(), slot.getSlotID())) {
                            // Update status field in Firebase
                            //Add functionality for informing the students that there request was rejected
                            slotSnapshot.getRef().child("studentsRequesting").setValue(slot.getStudentsRequesting())
                                    .addOnFailureListener(e ->
                                            Toast.makeText(ViewRequests.this, "Error requesting: " + e.getMessage(), Toast.LENGTH_LONG).show()
                                    );
                            students.remove(studentID);
                            adapter.notifyDataSetChanged();
                            Toast.makeText(ViewRequests.this, "Request rejected", Toast.LENGTH_LONG).show();
                            updateRequestRejected(studentID);
                            return;
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ViewRequests.this, "Error updating session status", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateRequestRejected(String studentIdPicked){
        databaseStudentSchedules.orderByChild("userID").equalTo(studentIdPicked).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot scheduleSnapshot : snapshot.getChildren()) {
                    for (DataSnapshot slotSnapshot : scheduleSnapshot.child("requests").getChildren()) {
                        Request dbreq = slotSnapshot.getValue(Request.class);
                        if (dbreq != null && Objects.equals(dbreq.getSlotID(), slot.getSlotID())) {
                            // Update status field in Firebase
                            dbreq.reject();
                            slotSnapshot.getRef().child("status").setValue("REJECTED")
                                    .addOnFailureListener(e ->
                                            Toast.makeText(ViewRequests.this, "Error requesting: " + e.getMessage(), Toast.LENGTH_LONG).show()
                                    );
                            Toast.makeText(ViewRequests.this, "Request updated", Toast.LENGTH_LONG).show();
                            return;
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ViewRequests.this, "Error updating request status", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void updateRequestApproved(String studentIdPicked){
        databaseStudentSchedules.orderByChild("userID").equalTo(studentIdPicked).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot scheduleSnapshot : snapshot.getChildren()) {
                    for (DataSnapshot slotSnapshot : scheduleSnapshot.child("requests").getChildren()) {
                        Request dbreq = slotSnapshot.getValue(Request.class);
                        if (dbreq != null && Objects.equals(dbreq.getSlotID(), slot.getSlotID())) {
                            // Update status field in Firebase
                            dbreq.approve();
                            slotSnapshot.getRef().child("status").setValue("APPROVED")
                                    .addOnFailureListener(e ->
                                            Toast.makeText(ViewRequests.this, "Error requesting: " + e.getMessage(), Toast.LENGTH_LONG).show()
                                    );
                            Toast.makeText(ViewRequests.this, "Request updated", Toast.LENGTH_LONG).show();
                            return;
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ViewRequests.this, "Error updating request status", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void rejectAllOtherRequests(String notRejected){
        for(String student : students){
            if(!student.equals(notRejected)){
                updateRequestRejected(student);
            }
        }
    }
}
