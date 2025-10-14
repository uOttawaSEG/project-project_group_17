package com.example.project_group_17.Screens;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.project_group_17.R;
import com.example.project_group_17.UserHierarchy.Admin;
import com.example.project_group_17.UserHierarchy.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginScreen extends AppCompatActivity {
    DatabaseReference databaseUsers;
    Button btnGoRegister;
    Admin admin = new Admin();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        databaseUsers = FirebaseDatabase.getInstance().getReference("Users");

        btnGoRegister= findViewById(R.id.btnGoRegister);

        // Start new Activity with explicit Intent
        btnGoRegister.setOnClickListener(v -> {
            Intent intent = new Intent(LoginScreen.this, RegistrationScreen.class);
            startActivity(intent);
        });


    }

    public void login(View view){
        //checks if email and password are valid and if so sends to the appropriate user page
        EditText emailView = findViewById(R.id.emailId);
        EditText passwordView = findViewById(R.id.passwordId);
        String enteredEmail = emailView.getText().toString();
        String enteredPassword = passwordView.getText().toString();
        //Checks to see if the user entered the admin login information
        if(enteredEmail.equals(admin.getUsername())&&enteredPassword.equals(admin.getPassword())){
            Intent intent = new Intent(LoginScreen.this, UserScreen.class);
            intent.putExtra("isAdmin", admin);
            startActivity(intent);
            finish();
        }else{
            //Node currentUser = list.userExists(enteredEmail, enteredPassword);
            String[] fields = {enteredEmail, enteredPassword};
            for (String s : fields) {
                if (s.isEmpty()) {
                    Toast.makeText(this, "Please enter both email and password  ", Toast.LENGTH_LONG).show();
                    return;
                }
            }
            databaseUsers.orderByChild("email").equalTo(enteredEmail).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                            User user = userSnapshot.getValue(User.class);
                            if (user.getPassword().equals(enteredPassword)) {
                                Toast.makeText(LoginScreen.this, "Login successful", Toast.LENGTH_LONG).show();
                                Intent intent = new Intent(LoginScreen.this, UserScreen.class);
                                intent.putExtra("userInfo", user);
                                startActivity(intent);
                                finish();
                            } else {
                                Toast.makeText(LoginScreen.this, "Incorrect password", Toast.LENGTH_LONG).show();
                            }
                        }
                    } else {
                        Toast.makeText(LoginScreen.this, "Incorrect username", Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(LoginScreen.this, "Database error", Toast.LENGTH_LONG).show();
                    return;
                }
            });

        }
    }
}