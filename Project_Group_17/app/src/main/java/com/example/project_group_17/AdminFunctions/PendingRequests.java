package com.example.project_group_17.AdminFunctions;

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
import com.example.project_group_17.UserHierarchy.Student;
import com.example.project_group_17.UserHierarchy.Tutor;
import com.example.project_group_17.UserHierarchy.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class PendingRequests extends AppCompatActivity {
    DatabaseReference databaseUsers;
    private Button seeRejected;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pending_requests);

        seeRejected = findViewById(R.id.seerejectedBtn);

        seeRejected.setOnClickListener(v ->{
            Intent intent = new Intent(PendingRequests.this, RejectedRequests.class);
            startActivity(intent);
            finish();
        });

        databaseUsers = FirebaseDatabase.getInstance().getReference("Users");
        loadPendingRequests();
    }

    //Load requests loops through all of the users active in the database and finds the ones
    //whose registration status is pending and adds them to the pendingRequests list
    //this list is then displayed using the list view
    private void loadPendingRequests(){
        ListView listView = findViewById(R.id.listView);

        List<User> pendingRequests = new ArrayList<User>();

        ArrayAdapter<User> adapter = new ArrayAdapter<User>(this, android.R.layout.simple_list_item_1, pendingRequests);
        listView.setAdapter(adapter);
        databaseUsers.orderByChild("registrationStatus").equalTo("pending").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                        User user;
                        String userType = userSnapshot.child("userType").getValue(String.class);

                        if ("Student".equals(userType)) {
                            user = userSnapshot.getValue(Student.class);
                        } else {
                            user = userSnapshot.getValue(Tutor.class);
                        }

                        pendingRequests.add(user);
                    }
                    adapter.notifyDataSetChanged();
                }
                else {
                    Toast.makeText(PendingRequests.this, "No pending requests found", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(PendingRequests.this, "Database error", Toast.LENGTH_LONG).show();
                return;
            }
        });
        //when an item in the list is clicked we create a new alert dialog to approve or reject
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                User selectedUser = pendingRequests.get(position);
                approveOrReject(selectedUser, pendingRequests, adapter);
            }
        });
    }

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
                Toast.makeText(PendingRequests.this, "Request Approved", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("Reject", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                rejectRequest(selectedUser);
                pendingRequests.remove(selectedUser);
                adapter.notifyDataSetChanged();
                Toast.makeText(PendingRequests.this, "Request Rejected", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNeutralButton("Cancel", null);
        builder.show();

    }

    private void approveRequest(@NonNull User user){
        user.setRegistrationStatus("approved");
        databaseUsers.child(user.getId()).setValue(user);
    }
    private void rejectRequest(@NonNull User user){
        user.setRegistrationStatus("rejected");
        databaseUsers.child(user.getId()).setValue(user);
    }
}
