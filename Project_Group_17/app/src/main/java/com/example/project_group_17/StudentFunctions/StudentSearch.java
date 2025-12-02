package com.example.project_group_17.StudentFunctions;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.project_group_17.R;
import com.example.project_group_17.Screens.LoginScreen;
import com.example.project_group_17.Screens.RegistrationScreen;
import com.example.project_group_17.Screens.UserScreen;
import com.example.project_group_17.UserHierarchy.Student;

import java.io.Serializable;

public class StudentSearch extends AppCompatActivity {
    Student u;
    String enteredCourse;
    Button enterButton;
    EditText searchBar;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.student_search);
        Serializable se = getIntent().getSerializableExtra("userInfo");
        if(se instanceof Student){
            u = (Student) se;
        } else{
            Toast.makeText(StudentSearch.this, "Not a Student User", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(StudentSearch.this, UserScreen.class);
            intent.putExtra("userInfo", se);
            startActivity(intent);
            finish();
        }

        searchBar = findViewById(R.id.searchBarId);
        enteredCourse = searchBar.getText().toString();

        enterButton = findViewById(R.id.enterButton);
        enterButton.setOnClickListener(v -> {
            enteredCourse();
        });

    }

    public void enteredCourse(){
        Intent intent = new Intent(StudentSearch.this, StudentSessions.class);
        intent.putExtra("userInfo", u);
        intent.putExtra("courseInfo", enteredCourse);
        startActivity(intent);
        finish();
    }
}
