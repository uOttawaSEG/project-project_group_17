package com.example.project_group_17.Screens;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.content.Intent;
import android.widget.EditText;
import android.text.TextUtils;
import android.widget.Toast;
import com.example.project_group_17.R;


public class TutorRegScreen extends AppCompatActivity {

    private EditText etFirstName, etLastName, etPhone, etDegree, etCourses;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //EdgeToEdge.enable(this);
        setContentView(R.layout.tutorreg);

        etFirstName = findViewById(R.id.firstname);
        etLastName = findViewById(R.id.lastname);
        etPhone = findViewById(R.id.phonenumber);
        etDegree = findViewById(R.id.degree);
        etCourses = findViewById(R.id.courses);

        Button cont;
        cont=findViewById(R.id.continuebtn);
        cont.setOnClickListener(v -> {

            String first = textOf(etFirstName);
            String last = textOf(etLastName);
            String phone = textOf(etPhone);
            String degree = textOf(etDegree);
            String coursesCsv = textOf(etCourses);

            if (TextUtils.isEmpty(first) || TextUtils.isEmpty(last)) {
                errorToast("Please Fill in all Required Fields (First & Last Names");
                return;
            }

            if (!validPhone(phone)) {
                errorToast("Please Enter a Valid Phone Number");
                return;
            }
            Intent intent = new Intent(TutorRegScreen.this, TutorRegScreen2.class);
            intent.putExtra("firstName",first);
            intent.putExtra("lastName",last);
            intent.putExtra("phone",phone);
            intent.putExtra("degree",degree);
            intent.putExtra("coursesCsv",coursesCsv);
            startActivity(intent);
            finish();


        });

    }

    private String textOf(EditText txt) {
        CharSequence seq = txt.getText();
        return seq == null ? "":seq.toString().trim();
    }

    private boolean validPhone(String num) {
        String nums = num.replaceAll("\\D","");
        return nums.length() >= 10 && nums.length() <=15;
    }

    private void errorToast(String str) {
        Toast.makeText(this, str, Toast.LENGTH_SHORT).show();
    }
}
