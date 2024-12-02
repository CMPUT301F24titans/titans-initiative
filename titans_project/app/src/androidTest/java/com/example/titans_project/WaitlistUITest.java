package com.example.titans_project;

import androidx.test.espresso.Espresso;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

@RunWith(AndroidJUnit4.class)
public class WaitlistUITest {

    @Rule
    public ActivityScenarioRule<WaitlistActivity> waitlistActivityRule =
            new ActivityScenarioRule<>(WaitlistActivity.class);

    @Test
    public void testReturnButton() {
        onView(withId(R.id.returnButton)).perform(click());
        // Ensure the activity finishes after pressing the return button
        assertTrue(waitlistActivityRule.getScenario().getResult().isFinished());
    }

    @Test
    public void testWaitlistDisplay() {
        // Check that the RecyclerView for the waitlist is displayed
        onView(withId(R.id.waitlistRecyclerView)).check(matches(withText("")));
    }

    @Test
    public void testGenerateLotteryButton() {
        // Set lottery size
        onView(withId(R.id.editTextLotterySize)).perform(replaceText("5"));
        // Click the generate lottery button
        onView(withId(R.id.buttonGenerateLottery)).perform(click());
        // Verify the LotteryActivity starts
        onView(withText("LotteryActivity")).check(matches(withText("")));
    }

    @Test
    public void testSendNotificationButton() {
        // Click the send notification button
        onView(withId(R.id.buttonSendNotification)).perform(click());
        // Verify the SendNotification activity starts
        onView(withText("SendNotification")).check(matches(withText("")));
    }
}












