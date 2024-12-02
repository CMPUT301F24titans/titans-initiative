package com.example.titans_project;

import androidx.test.espresso.Espresso;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.*;

@RunWith(AndroidJUnit4.class)
public class LotteryUITest {

    @Rule
    public ActivityScenarioRule<LotteryActivity> activityRule =
            new ActivityScenarioRule<>(LotteryActivity.class);

    @Test
    public void testStartLotteryButton() {
        // Click the start lottery button
        Espresso.onView(withId(R.id.startLotteryButton)).perform(click());

        // Verify the success message is displayed
        Espresso.onView(withText("Lottery completed successfully!"))
                .check(matches(isDisplayed()));
    }
    @Test
    public void testValidateEventIdNotNullOrEmpty() {
        lotteryActivity.eventID = null;
        assertFalse("Lottery should not start with null event ID", lotteryActivity.validateEventId());

        lotteryActivity.eventID = "";
        assertFalse("Lottery should not start with empty event ID", lotteryActivity.validateEventId());

        lotteryActivity.eventID = "event123";
        assertTrue("Lottery should start with valid event ID", lotteryActivity.validateEventId());
    }





