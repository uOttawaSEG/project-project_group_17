package com.example.project_group_17.TutorFunctions;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.example.project_group_17.AdminFunctions.AdminInbox;
import com.example.project_group_17.AdminFunctions.PendingRequests;
import com.example.project_group_17.AdminFunctions.RejectedRequests;
import com.example.project_group_17.R;
import com.example.project_group_17.TutorFunctions.ListDisplays.PendingSessions;
import com.example.project_group_17.TutorFunctions.ListDisplays.PreviousSessions;
import com.example.project_group_17.TutorFunctions.ListDisplays.UpcomingSessions;
import com.example.project_group_17.UserHierarchy.User;

import java.io.Serializable;

public class TutorListView extends AppCompatActivity {
    User u;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tutor_list_view);

        Serializable se = getIntent().getSerializableExtra("userInfo");
        u = (User) se;
    }
    public void loadUpcoming(View view){
        Intent intent = new Intent(TutorListView.this, UpcomingSessions.class);
        intent.putExtra("userInfo", u);
        startActivity(intent);
        finish();
    }
    public void loadPrevious(View view){
        Intent intent = new Intent(TutorListView.this, PreviousSessions.class);
        intent.putExtra("userInfo", u);
        startActivity(intent);
        finish();
    }
    public void loadPending(View view){
        Intent intent = new Intent(TutorListView.this, PendingSessions.class);
        intent.putExtra("userInfo", u);
        startActivity(intent);
        finish();
    }
}
