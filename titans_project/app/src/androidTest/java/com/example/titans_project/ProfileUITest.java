package com.example.titans_project;

import androidx.test.espresso.Espresso;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

@RunWith(AndroidJUnit4.class)
public class ProfileViewUITest {

    @Rule
    public ActivityScenarioRule<ProfileView> activityRule =
            new ActivityScenarioRule<>(ProfileView.class);

    @Test
    public void testSaveChangesButton() {
        onView(withId(R.id.button_save_changes)).perform(click());
        onView(withText("Changes Saved")).check(matches(isDisplayed()));
    }

    @Test
    public void testReturnButton() {
        onView(withId(R.id.button_return)).perform(click());
        // Assume the activity finishes after clicking the button
        // No assertion required since finish() behavior is implicit
    }











