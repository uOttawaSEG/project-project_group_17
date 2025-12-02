package com.example.project_group_17;
import androidx.annotation.UiThread;
import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import org.junit.*;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.*;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.*;
import static org.junit.jupiter.api.Assertions.*;
import android.text.TextUtils;
import android.util.Log;
import android.widget.TextView;

import com.example.project_group_17.Screens.LoginScreen;

public class MainActivityTest {

    @Rule
    public ActivityScenarioRule<LoginScreen> mActivityTestRule = new ActivityScenarioRule<LoginScreen>(LoginScreen.class);


    @Test
    public void checkEmail() throws Exception {

        onView(withId(R.id.emailId)).perform(typeText("email@example.com"), closeSoftKeyboard());
        onView(withId(R.id.emailId)).check(matches(withText("email@example.com")));

    }

    @Test
    public void checkPassword() throws Exception {

        onView(withId(R.id.passwordId)).perform(typeText("1234"), closeSoftKeyboard());

    }
    @Test
    public void checkLogin() throws Exception {

        onView(withId(R.id.login)).perform(click());

    }







}
