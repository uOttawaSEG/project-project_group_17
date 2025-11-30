package com.example.project_group_17.StudentFunctions;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.example.project_group_17.R;
import com.example.project_group_17.Screens.UserScreen;
import com.example.project_group_17.StudentFunctions.PastSessions;
import com.example.project_group_17.StudentFunctions.StudentSessions;
import com.example.project_group_17.StudentFunctions.UpcomingSessions;
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
