package com.example.project_group_17.Screens;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.project_group_17.R;
import com.example.project_group_17.UserHierarchy.Admin;
import com.example.project_group_17.UserRecordingDatastructure.LinkedList;
import com.example.project_group_17.UserRecordingDatastructure.Node;

public class LoginScreen extends AppCompatActivity {

    Button btnGoRegister;
    LinkedList<Node> list = new LinkedList<>();
    Admin admin = new Admin();

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
        EditText emailView =
                findViewById(R.id.emailId);
        EditText passwordView =
                findViewById(R.id.passwordId);
        String enteredEmail = emailView.getText().toString();
        String enteredPassword = passwordView.getText().toString();
        //Checks to see if the user entered the admin login information
        if(enteredEmail.equals(admin.getUsername())&&enteredPassword.equals(admin.getPassword())){
            Intent intent = new Intent(LoginScreen.this,
                    UserScreen.class);
            intent.putExtra("isAdmin", admin);
            startActivity(intent);

        }else{
            Node currentUser = list.userExists(enteredEmail, enteredPassword);
            String[] fields = {enteredEmail, enteredPassword};
            for (String s : fields) {
                if (s.isEmpty()) {
                    Toast.makeText(this, "Please enter both email and password  ", Toast.LENGTH_LONG).show();
                }
            }
            if (currentUser == null){
                Toast.makeText(this, "Incorrect email or password please try again ", Toast.LENGTH_LONG).show();
            } else{
                Intent intent = new Intent(LoginScreen.this,
                        UserScreen.class);
                intent.putExtra("userInfo", currentUser);
                startActivity(intent);
            }
        }
    }
}