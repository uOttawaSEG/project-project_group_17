package com.example.project_group_17.Screens;

import static com.example.project_group_17.R.*;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.project_group_17.R;

public class RegistrationScreen extends AppCompatActivity {
    Button btnGoStudent;
    Button btnGoTutor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.registration);

        btnGoStudent= findViewById(id.btnGoStudent);
        btnGoTutor= findViewById(R.id.btnGoTutor);



        // Start new Activity with explicit Intent
        btnGoStudent.setOnClickListener(v -> {
            Intent intent = new Intent(RegistrationScreen.this, StudentRegScreen.class);
            startActivity(intent);
        });
        // Start new Activity with explicit Intent
        btnGoTutor.setOnClickListener(v -> {
            Intent intent = new Intent(RegistrationScreen.this, TutorRegScreen.class);
            startActivity(intent);
        });
    }
}
