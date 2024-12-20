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
public class MainActivityTest {
    @Rule
    public ActivityScenarioRule<MainActivity> scenario = new
            ActivityScenarioRule<MainActivity>(MainActivity.class);

    /**
     * This is the test of switch to admin mode
     */
    @Test
    public void testSwitchAdminMode(){
        // Click on admin mode switch
        onView(withId(R.id.admin_mode)).perform(click());
        // Check is switch to the admin mode
        onView(withText("Browse Content")).check(matches(isDisplayed()));
    }

    /**
     * This is the test of switch to user
     */
    @Test
    public void testSwitchUserMode(){
        // Click on admin mode switch
        onView(withId(R.id.admin_mode)).perform(click());
        // Check is switch to the admin mode
        onView(withText("Browse Content")).check(matches(isDisplayed()));
        // Click on admin mode switch again
        onView(withId(R.id.back_user)).perform(click());
        // Check is switch to the user mode
        onView(withText("My Events")).check(matches(isDisplayed()));
    }

    /**
     * This is the test of switch to profile page
     */
    @Test
    public void testProfilePage(){
        // Click the profile button
        onView(withId(R.id.profile_button)).perform(click());
        // Check is switch to the profile page
        onView(withText("Profile")).check(matches(isDisplayed()));
    }

    /**
     * This is the test of switch to my application page
     */
    @Test
    public void testMyApplication(){
        // Click on the my_application button
        onView(withId(R.id.application_button)).perform(click());
        // Check is switch to the my application page
        onView(withText("Applications")).check(matches(isDisplayed()));
    }

    /**
     * This is the test of profile save the user information
     */
    @Test
    public void testProfileSave(){
        // Click the profile button
        onView(withId(R.id.profile_button)).perform(click());
        // Write name
        onView(withId(R.id.edit_text_full_name)).perform(ViewActions.typeText("Sho H"));
        // Click save change button
        onView(withId(R.id.button_save_changes)).perform(click());
        // Click the profile button again
        onView(withId(R.id.profile_button)).perform(click());
        // Check the user name has been stored
        onView(withText("Sho H")).check(matches(isDisplayed()));
    }
}
