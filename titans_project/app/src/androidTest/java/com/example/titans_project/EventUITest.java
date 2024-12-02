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
public class EventUITest {

    @Rule
    public ActivityScenarioRule<EventDetailView> activityRule =
            new ActivityScenarioRule<>(EventDetailView.class);

    @Test
    public void testEventDetailsDisplay() {
        // Assuming EventDetailView displays event name and event date
        onView(withId(R.id.event_name)).check(matches(withText("Swimming Class")));
        onView(withId(R.id.event_date)).check(matches(withText("2025-02-01")));
    }

    @Test
    public void testApplyForEventButton() {
        // Assuming "Apply" button is for general user
        onView(withId(R.id.button_apply)).perform(click());
        onView(withText("Successfully applied")).check(matches(isDisplayed()));
    }

    @Test
    public void testAdminDeleteEventButton() {
        // Assuming admin sees the "Delete Event" button
        onView(withId(R.id.button_apply)).perform(click());
        onView(withText("Event Deleted")).check(matches(isDisplayed()));
    }

    @Test
    public void testReturnButton() {
        onView(withId(R.id.button_return)).perform(click());
        // Ensure the activity finishes after pressing the return button
        assertTrue(activityRule.getScenario().getResult().isFinished());
    }

    @Test
    public void testEventPictureDisplay() {
        // Assuming an event image is loaded with a non-null picture
        onView(withId(R.id.profile_pic)).check(matches(isDisplayed()));
    }