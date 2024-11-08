package com.example.titans_project;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class CreateEventView extends AppCompatActivity {

    private EditText eventNameEditText, organizerNameEditText, eventDetailsEditText, eventDateEditText, eventTimeEditText;
    private CheckBox termsCheckBox;
    private Button submitButton;
    private int eventIndex = -1;  // Used to identify an existing event for editing

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_create_event);

        // Initialize Views
        eventNameEditText = findViewById(R.id.editTextEventName);
        organizerNameEditText = findViewById(R.id.editTextOrganizerName);
        eventDetailsEditText = findViewById(R.id.editTextEventDetails);
        eventDateEditText = findViewById(R.id.editTextEventDate);
        eventTimeEditText = findViewById(R.id.editTextEventTime);
        termsCheckBox = findViewById(R.id.checkBoxAcceptTerms);
        submitButton = findViewById(R.id.button_submit);

        // Set up back button to navigate back to previous screen
        ImageView backIcon = findViewById(R.id.back_icon);
        backIcon.setOnClickListener(v -> onBackPressed());

        // Check if the activity is launched for editing an existing event
        Intent intent = getIntent();
        if (intent.hasExtra("eventIndex")) {
            eventIndex = intent.getIntExtra("eventIndex", -1);
            if (eventIndex != -1) {
                // Prefill the fields with event details for editing
                eventNameEditText.setText(intent.getStringExtra("eventName"));
                organizerNameEditText.setText(intent.getStringExtra("organizerName"));
                eventDetailsEditText.setText(intent.getStringExtra("eventDetails"));
                eventDateEditText.setText(intent.getStringExtra("eventDate"));
                eventTimeEditText.setText(intent.getStringExtra("eventTime"));
            }
        }

        // Set the onClickListener for the Submit button
        submitButton.setOnClickListener(v -> {
            // Validate and submit the event data
            validateAndSubmitEvent();
        });
    }

    // Method to validate input and submit event data
    private void validateAndSubmitEvent() {
        String eventName = eventNameEditText.getText().toString().trim();
        String organizerName = organizerNameEditText.getText().toString().trim();
        String eventDetails = eventDetailsEditText.getText().toString().trim();
        String eventDate = eventDateEditText.getText().toString().trim();
        String eventTime = eventTimeEditText.getText().toString().trim();
        boolean termsAccepted = termsCheckBox.isChecked();

        // Perform input validation
        if (eventName.isEmpty()) {
            showToast("Event name is required");
        } else if (organizerName.isEmpty()) {
            showToast("Organizer name is required");
        } else if (eventDetails.isEmpty()) {
            showToast("Event details are required");
        } else if (eventDate.isEmpty()) {
            showToast("Event date is required");
        } else if (eventTime.isEmpty()) {
            showToast("Event time is required");
        } else if (!termsAccepted) {
            showToast("You must accept the terms and conditions");
        } else {
            // All validations passed, send back the data
            sendEventDataBack(eventName, organizerName, eventDetails, eventDate, eventTime);
        }
    }

    // Method to show toast messages
    private void showToast(String message) {
        Toast.makeText(CreateEventView.this, message, Toast.LENGTH_SHORT).show();
    }

    // Method to send the event data back to the calling activity (EventDetailsView or any other)
    private void sendEventDataBack(String eventName, String organizerName, String eventDetails, String eventDate, String eventTime) {
        Intent resultIntent = new Intent();
        resultIntent.putExtra("eventName", eventName);
        resultIntent.putExtra("organizerName", organizerName);
        resultIntent.putExtra("eventDetails", eventDetails);
        resultIntent.putExtra("eventDate", eventDate);
        resultIntent.putExtra("eventTime", eventTime);

        if (eventIndex != -1) {
            // Include event index if editing an existing event
            resultIntent.putExtra("eventIndex", eventIndex);
        }

        setResult(RESULT_OK, resultIntent);
        finish();  // Close CreateEventView and return to the calling activity
    }
}
