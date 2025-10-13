package com.example.tutorui;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.widget.EditText;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.List;

import com.example.tutorui.data.TutorRepository;
import com.google.android.material.button.MaterialButton;
import com.example.tutorui.models.Tutor;
import com.example.tutorui.RegisteredSuccessfully;

public class MainActivity extends AppCompatActivity {

    private EditText etUsername, etEmail, etPassword, etRepassword;
    private MaterialButton btnRegister;

    private String first, last, phone, degree, coursesCsv;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        first = getIntent().getStringExtra("firstName");
        last = getIntent().getStringExtra("lastname");
        phone = getIntent().getStringExtra("phone");
        degree = getIntent().getStringExtra("degree");
        coursesCsv = getIntent().getStringExtra("coursesCsv");

        etUsername = findViewById(R.id.username);
        etEmail = findViewById(R.id.email);
        etPassword = findViewById(R.id.password);
        etRepassword = findViewById(R.id.repassword);
        btnRegister = findViewById(R.id.signupbtn);

        btnRegister.setOnClickListener(v -> register());


    }

    private void register() {
        String username = textOf(etUsername);
        String email = textOf(etEmail);
        String pass = textOf(etPassword);
        String repass = textOf(etRepassword);

        if (username.isEmpty()) {
            errorToast("Username Cannot be Left Blank");
            return;
        }
        if (!validEmail(email)) {
            errorToast("Please Enter a Valid Email");
            return;
        }

        if (!pass.equals(repass)) {
            errorToast("Passwords do not Match");
            return;
        }

        List<String> courses = convert(coursesCsv);

        Tutor tutor = new Tutor(first, last, phone, degree, courses, username, email, pass);
        TutorRepository repo = TutorRepository.getInstance(this);
        repo.addTutor(tutor);
        repo.save();

        startActivity(new Intent(this, RegisteredSuccessfully.class));
        Intent intent = new Intent(this, RegisteredSuccessfully.class);
        intent.putExtra("firstName", first);
        startActivity(intent);
        finish();
    }

    private String textOf(EditText txt) {
        CharSequence seq = txt.getText();
        return seq == null ? "" : seq.toString().trim();
    }

    private boolean validEmail(String e) {
        return !TextUtils.isEmpty(e) && Patterns.EMAIL_ADDRESS.matcher(e).matches();
    }


    private void errorToast(String m) {
        Toast.makeText(this, m, Toast.LENGTH_SHORT).show();
    }

    private List<String> convert(String text) {

        List<String> courses = new ArrayList<>();

        if (text == null || text.trim().isEmpty()) {
            return courses;
        }

        String[] items = text.split(",");

        for (String item : items) {
            String course = item.trim().toUpperCase();
            if (!course.isEmpty()) {
                courses.add(course);
            }
        }

        return courses;

    }


}
