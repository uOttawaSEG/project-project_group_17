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

public class RejectedRequests extends AppCompatActivity {

    DatabaseReference databaseUsers;

    private Button seePending;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rejected_requests);

        seePending = findViewById(R.id.seependingBtn);

        seePending.setOnClickListener(v ->{
            Intent intent = new Intent(RejectedRequests.this, PendingRequests.class);
            startActivity(intent);
            finish();
        });

        databaseUsers = FirebaseDatabase.getInstance().getReference("Users");
        loadRejectedRequests();
    }

    private void loadRejectedRequests(){
        ListView listView = findViewById(R.id.listView);

        List<User> rejectedRequests = new ArrayList<User>();

        ArrayAdapter<User> adapter = new ArrayAdapter<User>(this, android.R.layout.simple_list_item_1, rejectedRequests);
        listView.setAdapter(adapter);
        databaseUsers.orderByChild("registrationStatus").equalTo("rejected").addListenerForSingleValueEvent(new ValueEventListener() {
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

                        rejectedRequests.add(user);
                    }
                    adapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(RejectedRequests.this, "No rejected requests found", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(RejectedRequests.this, "Database error", Toast.LENGTH_LONG).show();
                return;
            }
        });
        //when an item in the list is clicked we create a new alert dialog to approve or reject
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                User selectedUser = rejectedRequests.get(position);
                approveOrReject(selectedUser, rejectedRequests, adapter);
            }
        });
    }
    private void approveOrReject(User selectedUser, List<User> rejectedRequests, ArrayAdapter<User> adapter){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Approve or Reject the request.");
        builder.setMessage(selectedUser.toString());

        builder.setPositiveButton("Approve", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                approveRequest(selectedUser);
                rejectedRequests.remove(selectedUser);
                adapter.notifyDataSetChanged();
                Toast.makeText(RejectedRequests.this, "Request Approved", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNeutralButton("Cancel", null);
        builder.show();

    }
    private void approveRequest(@NonNull User user){
        user.setRegistrationStatus("approved");
        databaseUsers.child(user.getId()).setValue(user);
    }
}
