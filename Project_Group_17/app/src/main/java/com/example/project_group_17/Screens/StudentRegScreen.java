package com.example.project_group_17.Screens;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.project_group_17.R;
import com.example.project_group_17.UserHierarchy.Student;

import com.example.project_group_17.UserHierarchy.Student;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class StudentRegScreen extends AppCompatActivity {
    DatabaseReference databaseStudents;
    EditText firstNametxt;
    EditText lastNametxt;
    EditText emailtxt;
    EditText passwordtxt;
    EditText phoneNumbertxt;
    EditText programOfStudytxt;
    String firstName;
    String lastName;
    String email;
    String password;
    String phoneNumber;
    String programOfStudy;
    Button submitButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.studentreg);

        databaseStudents = FirebaseDatabase.getInstance().getReference("students");

        firstNametxt = (EditText) findViewById(R.id.firstName);
        lastNametxt = (EditText) findViewById(R.id.lastName);
        emailtxt = (EditText) findViewById(R.id.email);
        passwordtxt = (EditText) findViewById(R.id.password);
        phoneNumbertxt = (EditText) findViewById(R.id.phoneNumber);
        programOfStudytxt = (EditText) findViewById(R.id.programOfStudy);

        submitButton = findViewById(R.id.submitButton);

        // if all fields are filled go to user logged in page, if not show a toast
        submitButton.setOnClickListener(v -> {
            firstName = firstNametxt.getText().toString().trim();
            lastName = lastNametxt.getText().toString().trim();
            email = emailtxt.getText().toString().trim();
            password = passwordtxt.getText().toString().trim();
            phoneNumber = phoneNumbertxt.getText().toString().trim();
            programOfStudy = programOfStudytxt.getText().toString().trim();

            if (validate()) {
                registerStudent();
                Intent intent = new Intent(StudentRegScreen.this, UserScreen.class);
                startActivity(intent);
            } else {
                Toast.makeText(this, "Please fill in all required fields", Toast.LENGTH_LONG).show();
            }
        });
    }

    // checks if the required fields are not empty
    public boolean validate() {
        String[] fields = {firstName, lastName, email, password, phoneNumber, programOfStudy};
        for (String s : fields) {
            if (s.isEmpty()) {
                return false;
            }
        }
        return true;
    }

    public void registerStudent() {
        String id = databaseStudents.push().getKey();
        Student student = new Student(id, firstName, lastName, email, password, phoneNumber, programOfStudy);

        databaseStudents.child(id).setValue(student);
        Toast.makeText(this, "Registered Successfully", Toast.LENGTH_LONG).show();
        firstNametxt.setText("");
        lastNametxt.setText("");
        emailtxt.setText("");
        passwordtxt.setText("");
        phoneNumbertxt.setText("");
        programOfStudytxt.setText("");
    }
}