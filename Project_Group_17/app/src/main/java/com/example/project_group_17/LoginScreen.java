package com.example.project_group_17;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class LoginScreen extends AppCompatActivity {

    Button btnGoRegister;

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
}