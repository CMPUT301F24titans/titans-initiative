package com.example.titans_project;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

/**
 * Activity for viewing details of an event.
 * Provides options to edit event details or view attendees.
 */
public class EventDetailsView extends AppCompatActivity {

    private TextView eventTitle, eventOrganizer, eventDetails, eventDate;
    private Button editButton, viewAttendeesButton;
    private ImageView backButton;
    private int eventIndex = -1;

    /**
     * Called when the activity is created.
     * Initializes the views and retrieves event data from the Intent passed by the previous activity.
     *
     * @param savedInstanceState The saved instance state of the activity.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_view_event_details); // Layout for viewing event details

        // Initialize views
        eventTitle = findViewById(R.id.eventTitle);
        eventOrganizer = findViewById(R.id.eventOrganizer);
        eventDetails = findViewById(R.id.eventDetails);
        eventDate = findViewById(R.id.eventDate);
        editButton = findViewById(R.id.editButton);
        viewAttendeesButton = findViewById(R.id.viewAttendeesButton);
        backButton = findViewById(R.id.backButton);

        // Retrieve event data from the Intent passed by CreatedEventsView
        Intent intent = getIntent();
        String name = intent.getStringExtra("eventName");
        String organizer = intent.getStringExtra("organizerName");
        String details = intent.getStringExtra("eventDetails");
        String date = intent.getStringExtra("eventDate");
        String time = intent.getStringExtra("eventTime");
        eventIndex = intent.getIntExtra("eventIndex", -1);

        // Set the retrieved data into the views
        eventTitle.setText(name);
        eventOrganizer.setText("Organized by " + organizer);
        eventDetails.setText(details);
        eventDate.setText(date + " at " + time);

        // Set up the back button to return to the previous screen
        backButton.setOnClickListener(v -> onBackPressed());

        // Set up the edit button to navigate to the EditEventView for editing event details
        editButton.setOnClickListener(v -> {
            // Prepare the intent to pass event details to EditEventView
            Intent editIntent = new Intent(EventDetailsView.this, EditEventView.class);
            editIntent.putExtra("eventName", name);
            editIntent.putExtra("organizerName", organizer);
            editIntent.putExtra("eventDetails", details);
            editIntent.putExtra("eventDate", date);
            editIntent.putExtra("eventTime", time);
            editIntent.putExtra("eventIndex", eventIndex);
            // Start EditEventView and wait for the result
            startActivityForResult(editIntent, 1); // Handle result from EditEventView
        });

        // Set up the view attendees button (functionality to be implemented if needed)
        viewAttendeesButton.setOnClickListener(v -> {
            // Handle the action when the "View Attendees" button is clicked
            // This could navigate to a screen showing the list of attendees
            Toast.makeText(this, "View Attendees feature not yet implemented.", Toast.LENGTH_SHORT).show();
        });
    }

    /**
     * Called when the result is returned from another activity.
     * This handles the updated event data returned after editing an event.
     *
     * @param requestCode The request code used to start the activity.
     * @param resultCode The result code indicating whether the operation was successful.
     * @param data The intent containing the result data.
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Check if the result is OK and data is not null
        if (resultCode == RESULT_OK && data != null) {
            // Retrieve updated event details from the result Intent
            String eventName = data.getStringExtra("eventName");
            String organizerName = data.getStringExtra("organizerName");
            String eventDetailsText = data.getStringExtra("eventDetails");
            String eventDateText = data.getStringExtra("eventDate");
            String eventTime = data.getStringExtra("eventTime");

            // Update the views with the updated event details
            eventTitle.setText(eventName);
            eventOrganizer.setText("Organized by " + organizerName);
            eventDetails.setText(eventDetailsText);
            eventDate.setText(eventDateText + " at " + eventTime);

            // Send updated data back to CreatedEventsView (or any other activity as needed)
            Intent resultIntent = new Intent();
            resultIntent.putExtra("eventName", eventName);
            resultIntent.putExtra("organizerName", organizerName);
            resultIntent.putExtra("eventDetails", eventDetailsText);
            resultIntent.putExtra("eventDate", eventDateText);
            resultIntent.putExtra("eventTime", eventTime);
            resultIntent.putExtra("eventIndex", eventIndex);
            setResult(RESULT_OK, resultIntent);

            // Notify the user that the event was updated successfully
            Toast.makeText(this, "Event updated successfully", Toast.LENGTH_SHORT).show();
        }
    }
}
