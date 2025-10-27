package com.example.project_group_17.AdminFunctions;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.example.project_group_17.R;
import com.example.project_group_17.Screens.LoginScreen;
import com.example.project_group_17.Screens.UserScreen;



public class AdminInbox extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_inbox);

    }

    public void loadPending(View view){
        Intent intent = new Intent(AdminInbox.this, PendingRequests.class);
        startActivity(intent);
        finish();
    }
    public void loadRejected(View view){
        Intent intent = new Intent(AdminInbox.this, RejectedRequests.class);
        startActivity(intent);
        finish();
    }

    public void adminLogout(View view) {
        Intent intent = new Intent(AdminInbox.this, LoginScreen.class);
        startActivity(intent);
        finish();
    }


}
