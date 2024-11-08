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
    public void testSwitchMode(){
        // Click on admin mode switch
        onView(withId(R.id.admin_mode)).perform(click());
        // Check is switch to the admin mode
        onView(withText("Browse Content")).check(matches(isDisplayed()));
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


}
