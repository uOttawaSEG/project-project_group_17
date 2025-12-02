package com.example.project_group_17.Screens;

import com.example.project_group_17.TutorFunctions.TimeSlot;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.Test;


public class TimeSlotTest {

    @Test
    public void checkIsValidTime() {

        TimeSlot slot = new TimeSlot();
        assertTrue(slot.isValidTime("11"));

    }
}
