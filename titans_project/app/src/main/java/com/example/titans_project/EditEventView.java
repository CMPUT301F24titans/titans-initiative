package com.example.titans_project;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

/**
 * Activity for editing an existing event.
 * Allows users to modify event details and save the updated information.
 */
public class EditEventView extends AppCompatActivity {

    private EditText eventName, organizerName, eventDetails, eventDate, eventTime;
    private Button saveButton;
    private ImageView backButton;
    private int eventIndex = -1;

    /**
     * Called when the activity is created.
     * Initializes the views, retrieves event data from the Intent, and sets up listeners.
     *
     * @param savedInstanceState The saved instance state of the activity.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_edit_event);

        // Initialize views
        eventName = findViewById(R.id.editTextEventName);
        organizerName = findViewById(R.id.editTextOrganizerName);
        eventDetails = findViewById(R.id.editTextEventDetails);
        eventDate = findViewById(R.id.editTextEventDate);
        eventTime = findViewById(R.id.editTextEventTime);
        saveButton = findViewById(R.id.button_save);
        backButton = findViewById(R.id.back_icon);

        // Retrieve data from the Intent that launched this activity
        Intent intent = getIntent();
        String name = intent.getStringExtra("eventName");
        String organizer = intent.getStringExtra("organizerName");
        String details = intent.getStringExtra("eventDetails");
        String date = intent.getStringExtra("eventDate");
        String time = intent.getStringExtra("eventTime");
        eventIndex = intent.getIntExtra("eventIndex", -1);

        // Set the text fields with the data retrieved from the Intent
        eventName.setText(name);
        organizerName.setText(organizer);
        eventDetails.setText(details);
        eventDate.setText(date);
        eventTime.setText(time);

        // Set up the back button to navigate back to the previous screen
        backButton.setOnClickListener(v -> onBackPressed());

        // Set up the save button to validate and save the edited event data
        saveButton.setOnClickListener(v -> {
            // Get updated values from the input fields
            String updatedName = eventName.getText().toString();
            String updatedOrganizer = organizerName.getText().toString();
            String updatedDetails = eventDetails.getText().toString();
            String updatedDate = eventDate.getText().toString();
            String updatedTime = eventTime.getText().toString();

            // Validate that all fields have been filled in
            if (updatedName.isEmpty() || updatedOrganizer.isEmpty() || updatedDetails.isEmpty() || updatedDate.isEmpty() || updatedTime.isEmpty()) {
                Toast.makeText(EditEventView.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            // Create an Intent to send the updated event data back to the calling activity
            Intent resultIntent = new Intent();
            resultIntent.putExtra("eventName", updatedName);
            resultIntent.putExtra("organizerName", updatedOrganizer);
            resultIntent.putExtra("eventDetails", updatedDetails);
            resultIntent.putExtra("eventDate", updatedDate);
            resultIntent.putExtra("eventTime", updatedTime);
            resultIntent.putExtra("eventIndex", eventIndex);

            setResult(RESULT_OK, resultIntent);

            // Show a toast message to confirm that the event has been saved
            Toast.makeText(this, "Event saved successfully", Toast.LENGTH_SHORT).show();
            finish(); // Close the activity and return to the previous screen
        });
    }
}
