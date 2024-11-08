package com.example.titans_project;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.longClick;
import static androidx.test.espresso.assertion.ViewAssertions.doesNotExist;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import static org.hamcrest.CoreMatchers.anything;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.action.ViewActions;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * This is the test start from my event page
 */
@RunWith(AndroidJUnit4.class)
@LargeTest
public class AdminActivityTest {
    @Rule
    public ActivityScenarioRule<BrowseContentView> scenario = new
            ActivityScenarioRule<BrowseContentView>(BrowseContentView.class);

    /**
     * This is the test of switch to user
     */
    @Test
    public void testSwitchMode(){
        // Click on admin mode switch
        onView(withId(R.id.admin_mode)).perform(click());
        // Check is switch to the user mode
        onView(withText("My Events")).check(matches(isDisplayed()));
    }

    /**
     * This is the test of events button
     */
    @Test
    public void testEventList(){
        // Click on Events button
        onView(withId(R.id.eventsButton)).perform(click());
        // Check there is event list
        onView(withText("Event Name")).check(matches(isDisplayed()));
    }

    /**
     * This is the test of profile button
     */
    @Test
    public void testUserList(){
        // Click on Profiles button
        onView(withId(R.id.profilesButton)).perform(click());
        // Check there is user list
        onView(withText("Event Name")).check(matches(isDisplayed()));
    }

}
