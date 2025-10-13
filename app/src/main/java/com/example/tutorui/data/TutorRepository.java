package com.example.tutorui.data;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.tutorui.models.Tutor;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.LinkedList;
import java.util.List;

public class TutorRepository {

    private static TutorRepository INSTANCE;
    private final SharedPreferences preferences;
    private final Gson g = new Gson();
    private static final String PREFERENCE = "tutor_prefs";
    private static final String KEY_TUTORS = "tutors_json";
    private LinkedList<Tutor> tutors = new LinkedList<>();

    private TutorRepository(Context context) {
        preferences = context.getSharedPreferences(PREFERENCE, Context.MODE_PRIVATE);
        load();
    }

    public static synchronized TutorRepository getInstance(Context context) {
        if(INSTANCE == null) {
            INSTANCE = new TutorRepository(context.getApplicationContext());
        }
        return INSTANCE;
    }

    public LinkedList<Tutor> systemReport() {
        return tutors;
    }

    public void addTutor(Tutor tutor) {
        tutors.add(tutor);
    }

    public void save() {
        preferences.edit().putString(KEY_TUTORS,g.toJson(tutors)).apply();
    }

    private void load() {
        String json = preferences.getString(KEY_TUTORS, null);
        if (json!=null) {
            Type type = new TypeToken<LinkedList<Tutor>>(){}.getType();
            List<Tutor> loaded = g.fromJson(json, type);
            if (loaded != null) {
                tutors = new LinkedList<>(loaded);
            }

        }
    }

}