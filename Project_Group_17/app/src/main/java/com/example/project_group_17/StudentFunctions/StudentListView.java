package com.example.project_group_17.StudentFunctions;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.example.project_group_17.AdminFunctions.AdminInbox;
import com.example.project_group_17.AdminFunctions.PendingRequests;
import com.example.project_group_17.AdminFunctions.RejectedRequests;
import com.example.project_group_17.R;
import com.example.project_group_17.Screens.LoginScreen;
import com.example.project_group_17.Screens.UserScreen;
import com.example.project_group_17.StudentFunctions.PastSessions;
import com.example.project_group_17.StudentFunctions.StudentSessions;
import com.example.project_group_17.StudentFunctions.StudentUpcomingSessions;
import com.example.project_group_17.TutorFunctions.ListDisplays.PendingSessions;
import com.example.project_group_17.TutorFunctions.ListDisplays.PreviousSessions;
import com.example.project_group_17.TutorFunctions.ListDisplays.UpcomingSessions;
import com.example.project_group_17.UserHierarchy.User;

import java.io.Serializable;

public class StudentListView extends AppCompatActivity {
    User u;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.student_list_view);

        Serializable se = getIntent().getSerializableExtra("userInfo");
        u = (User) se;
    }
    public void loadUpcoming(View view){
        Intent intent = new Intent(StudentListView.this, UpcomingSessions.class);
        intent.putExtra("userInfo", u);
        startActivity(intent);
        finish();
    }
    public void loadPrevious(View view){
        Intent intent = new Intent(StudentListView.this, PastSessions.class);
        intent.putExtra("userInfo", u);
        startActivity(intent);
        finish();
    }
    public void loadRequests(View view){
        Intent intent = new Intent(StudentListView.this, StudentRequests.class);
        intent.putExtra("userInfo", u);
        startActivity(intent);
        finish();
    }

    public void loadAvailable(View view){
        Intent intent = new Intent(StudentListView.this, StudentSessions.class);
        intent.putExtra("userInfo", u);
        startActivity(intent);
        finish();
    }

    public void goBack(View view) {
        Intent intent = new Intent(StudentListView.this, UserScreen.class);
        intent.putExtra("userInfo", u);
        startActivity(intent);
        finish();
    }
}
