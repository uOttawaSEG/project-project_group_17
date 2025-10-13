package com.example.project_group_17.Screens;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.project_group_17.R;
import com.example.project_group_17.UserHierarchy.Admin;
import com.example.project_group_17.UserHierarchy.User;

import java.io.Serializable;

public class UserScreen extends AppCompatActivity {
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.userscreen_main);
        Serializable admin = getIntent().getSerializableExtra("isAdmin");
        if(admin != null){
            Admin a = (Admin) admin;
            TextView userType = findViewById(R.id.userTypeTextViewId);
            userType.setText( a.getUserType());
        } else{
            Serializable s = getIntent().getSerializableExtra("userInfo");
            User u = (User) s;
            TextView userType =  findViewById(R.id.userTypeTextViewId);
            assert u != null;
            userType.setText(u.getUserType());
        }

    }

    public void logout(View view){
        Intent intent = new Intent(UserScreen.this,
                LoginScreen.class);
        startActivity(intent);
    }
}
