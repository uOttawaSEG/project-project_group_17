package com.example.project_group_17.StudentFunctions;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.project_group_17.R;
import com.example.project_group_17.Screens.UserScreen;
import com.example.project_group_17.TutorFunctions.TimeSlot;
import com.example.project_group_17.UserHierarchy.Student;
import com.example.project_group_17.UserHierarchy.Tutor;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class PastSessions extends AppCompatActivity {
    DatabaseReference databaseSchedules;

    DatabaseReference databaseUsers;
    private Button goBack;
    List<TimeSlot> pastSlots = new ArrayList<TimeSlot>();
    Student u;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.student_past_sessions);

        Serializable se = getIntent().getSerializableExtra("userInfo");
        if(se instanceof Student){
            u = (Student) se;
        } else{
            Toast.makeText(PastSessions.this, "Not a Student User", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(PastSessions.this, StudentListView.class);
            intent.putExtra("userInfo", se);
            startActivity(intent);
            finish();
        }

        goBack = findViewById(R.id.seerejectedBtn);

        goBack.setOnClickListener(v ->{
            Intent intent = new Intent(PastSessions.this, UserScreen.class);
            intent.putExtra("userInfo", u);
            startActivity(intent);
            finish();
        });

        databaseSchedules = FirebaseDatabase.getInstance().getReference("Schedules");
        databaseUsers = FirebaseDatabase.getInstance().getReference("Users");
        loadPastSessions();
    }

    private void loadPastSessions() {
        ListView listView = findViewById(R.id.listView);

        ArrayAdapter<TimeSlot> adapter = new ArrayAdapter<TimeSlot>(this, android.R.layout.simple_list_item_1, pastSlots);
        listView.setAdapter(adapter);

        // get all past time slots
        databaseSchedules.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                pastSlots.clear();
                if (snapshot.exists()) {
                    for (DataSnapshot scheduleSnapshot : snapshot.getChildren()) {
                        GenericTypeIndicator<List<TimeSlot>> t = new GenericTypeIndicator<List<TimeSlot>>() {};
                        List<TimeSlot> allSlots = scheduleSnapshot.child("timeSlots").getValue(t);
                        if(allSlots !=null) {
                            for (int i = 0; i < Objects.requireNonNull(allSlots).size(); i++) {
                                TimeSlot slot = allSlots.get(i);
                                if (slot.getPast() &&(slot.getStudentID() == u.getId())) {
                                    pastSlots.add(slot);
                                }
                            }
                        }
                    }
                    adapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(PastSessions.this, "No past sessions found", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(PastSessions.this, "Database Error", Toast.LENGTH_LONG).show();
                return;
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TimeSlot selectedSession = pastSlots.get(position);
                rateTutor(selectedSession, adapter);
            }
        });
    }

    private void rateTutor(TimeSlot selectedSession, ArrayAdapter<TimeSlot> adapter) {
        databaseUsers.child(selectedSession.getTutorID()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(PastSessions.this);
                    builder.setTitle("Rate this tutor?");
                    Tutor selectedTutor = snapshot.getValue(Tutor.class);
                    if (selectedTutor != null) {
                        if (!selectedTutor.getRatedBy().contains(u.getId())) {
                            builder.setMessage("Rate "+selectedTutor.getFirstName()+" "+selectedTutor.getLastName()+" from 1 to 5");
                            final EditText input = new EditText(PastSessions.this);
                            input.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
                            input.setHint("Rate from 1-5");
                            builder.setView(input);
                            builder.setPositiveButton("Rate", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    String userInput = input.getText().toString().trim();
                                    if (!userInput.isEmpty()) {
                                        try {
                                            double rating = Double.parseDouble(userInput);
                                            if (rating < 1 || rating > 5) {
                                                Toast.makeText(PastSessions.this, "Invalid input, must be between 1 and 5", Toast.LENGTH_LONG).show();
                                            } else {
                                                selectedTutor.addRating(rating);
                                                selectedTutor.addRatedBy(u.getId());
                                                databaseUsers.child(selectedTutor.getId()).setValue(selectedTutor)
                                                        .addOnSuccessListener(aVoid -> Toast.makeText(PastSessions.this, "Successfully rated tutor!", Toast.LENGTH_SHORT).show())
                                                        .addOnFailureListener(e -> Toast.makeText(PastSessions.this, "Failed to rate tutor", Toast.LENGTH_SHORT).show());
                                                updateTimeSlots(adapter, selectedTutor, selectedTutor.getAvgRating());
                                            }
                                        } catch(NumberFormatException e) {
                                            Toast.makeText(PastSessions.this, "Invalid input", Toast.LENGTH_LONG).show();
                                        }
                                    }
                                }
                            });
                        } else {
                            builder.setMessage("You have already rated this tutor.");
                        }
                        builder.setNeutralButton("Exit", null);
                        builder.show();
                    } else {
                        Toast.makeText(PastSessions.this, "Couldn't access tutor", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(PastSessions.this, "Couldn't find tutor", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(PastSessions.this, "Database Error", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void updateTimeSlots(ArrayAdapter<TimeSlot> adapter, Tutor tu, double rating) {
        databaseSchedules.orderByChild("userID").equalTo(tu.getId()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot scheduleSnapshot : snapshot.getChildren()) {
                        GenericTypeIndicator<List<TimeSlot>> t = new GenericTypeIndicator<List<TimeSlot>>() {};
                        List<TimeSlot> allSlots = scheduleSnapshot.child("timeSlots").getValue(t);
                        if(allSlots !=null) {
                            for (int i = 0; i < Objects.requireNonNull(allSlots).size(); i++) {
                                TimeSlot slot = allSlots.get(i);
                                slot.setTutorRating(rating);
                                scheduleSnapshot.child("timeSlots").child(slot.getSlotID()).getRef().setValue(slot)
                                        .addOnFailureListener(e -> Toast.makeText(PastSessions.this, "Failed to update time slots.", Toast.LENGTH_SHORT).show());
                            }
                        }
                    }
                    adapter.notifyDataSetChanged();
                }
                else {
                    Toast.makeText(PastSessions.this, "Couldn't update timeslots", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(PastSessions.this, "Database error", Toast.LENGTH_LONG).show();
                return;
            }
        });
    }
}

