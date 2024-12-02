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
public class QRCodeUITest {

    @Rule
    public ActivityScenarioRule<QRCodeActivity> qrCodeActivityRule =
            new ActivityScenarioRule<>(QRCodeActivity.class);

    @Rule
    public ActivityScenarioRule<QRScannerActivity> qrScannerActivityRule =
            new ActivityScenarioRule<>(QRScannerActivity.class);

    @Test
    public void testQRCodeGeneratedDisplayed() {
        // Simulate passing an event ID to QRCodeActivity
        String eventID = "event123";
        Espresso.onView(withId(R.id.qrCodeImageView))
                .check(matches(withText("QR Code for: " + eventID)));
    }

    @Test
    public void testSaveQRCodeToGallery() {
        onView(withId(R.id.saveToLocalButton)).perform(click());
        onView(withText("QR Code saved to local gallery")).check(matches(isDisplayed()));
    }

    @Test
    public void testScanQRCodeButton() {
        onView(withId(R.id.scan_button)).perform(click());
        // Ensure the QR scanner initiates
        onView(withText("Scan a QR code")).check(matches(isDisplayed()));
    }










