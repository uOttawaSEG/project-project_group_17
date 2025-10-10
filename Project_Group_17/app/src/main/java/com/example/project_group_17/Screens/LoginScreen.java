package com.example.project_group_17.Screens;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.example.project_group_17.R;
import com.example.project_group_17.UserRecordingDatastructure.LinkedList;

public class LoginScreen extends AppCompatActivity {

    Button btnGoRegister;
    LinkedList list = new LinkedList();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnGoRegister= findViewById(R.id.btnGoRegister);



        // Start new Activity with explicit Intent
        btnGoRegister.setOnClickListener(v -> {
            Intent intent = new Intent(LoginScreen.this, RegistrationScreen.class);
            startActivity(intent);
        });


    }

    public void login(View view){
        //checks if email and password are valid and if so sends to the appropriate user page
        EditText emailView = (EditText)
                findViewById(R.id.emailId);
        EditText passwordView = (EditText)
                findViewById(R.id.passwordId);
        String enteredEmail = emailView.getText().toString();
        String enteredPassword = passwordView.getText().toString();
        //Team team = new Team(teamName, postalCode, drawableName);
        Intent intent = new Intent(LoginScreen.this,
                UserScreen.class);
        //intent.putExtra("teamInfo", team);
        startActivity(intent);
    }
}