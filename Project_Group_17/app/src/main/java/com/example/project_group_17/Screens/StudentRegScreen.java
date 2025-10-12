package com.example.project_group_17.Screens;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.project_group_17.R;

public class StudentRegScreen extends AppCompatActivity {

    Button submitButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.studentreg);

        submitButton = findViewById(R.id.submitButton);

        // if all fields are filled go to user logged in page, if not show a toast
        submitButton.setOnClickListener(v -> {
            if (validate()) {
                Intent intent = new Intent(StudentRegScreen.this, UserScreen.class);
                startActivity(intent);
            } else {
                Toast.makeText(this, "Please fill in all required fields", Toast.LENGTH_LONG).show();
            }
        });
    }

    // checks if the required fields are not empty
    public boolean validate() {
        EditText firstNametxt = (EditText) findViewById(R.id.firstName);
        EditText lastNametxt = (EditText) findViewById(R.id.lastName);
        EditText emailtxt = (EditText) findViewById(R.id.email);
        EditText passwordtxt = (EditText) findViewById(R.id.password);
        EditText phoneNumbertxt = (EditText) findViewById(R.id.phoneNumber);
        EditText programOfStudytxt = (EditText) findViewById(R.id.programOfStudy);
        String firstName = firstNametxt.getText().toString().trim();
        String lastName = lastNametxt.getText().toString().trim();
        String email = emailtxt.getText().toString().trim();
        String password = passwordtxt.getText().toString().trim();
        String phoneNumber = phoneNumbertxt.getText().toString().trim();
        String programOfStudy = programOfStudytxt.getText().toString().trim();
        String[] fields = {firstName, lastName, email, password, phoneNumber, programOfStudy};
        for (String s : fields) {
            if (s.isEmpty()) {
                return false;
            }
        }
        return true;
    }
}
