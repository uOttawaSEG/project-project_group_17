package com.example.tutorui;

import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class RegisteredSuccessfully extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_registered_successfully);

        String name = getIntent().getStringExtra("firstname");
        TextView msg = findViewById(R.id.welcomePrompt);
        if (name == null) name = "";
        msg.setText("Registration Complete, Thank you "+name+"!");

    }
}