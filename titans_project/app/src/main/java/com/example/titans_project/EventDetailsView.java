package com.example.titans_project;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class EventDetailsView extends AppCompatActivity {

    private TextView eventTitle, eventOrganizer, eventDetails, eventDate;
    private Button editButton, viewAttendeesButton;
    private ImageView backButton;
    private int eventIndex = -1;

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

        // Retrieve data from the Intent passed by CreatedEventsView
        Intent intent = getIntent();
        String name = intent.getStringExtra("eventName");
        String organizer = intent.getStringExtra("organizerName");
        String details = intent.getStringExtra("eventDetails");
        String date = intent.getStringExtra("eventDate");
        String time = intent.getStringExtra("eventTime");
        eventIndex = intent.getIntExtra("eventIndex", -1);

        // Set initial text in the views
        eventTitle.setText(name);
        eventOrganizer.setText("Organized by " + organizer);
        eventDetails.setText(details);
        eventDate.setText(date + " at " + time);

        // Back button to return to previous screen
        backButton.setOnClickListener(v -> onBackPressed());

        // Edit event details
        editButton.setOnClickListener(v -> {
            Intent editIntent = new Intent(EventDetailsView.this, EditEventView.class);
            editIntent.putExtra("eventName", name);
            editIntent.putExtra("organizerName", organizer);
            editIntent.putExtra("eventDetails", details);
            editIntent.putExtra("eventDate", date);
            editIntent.putExtra("eventTime", time);
            editIntent.putExtra("eventIndex", eventIndex);
            startActivityForResult(editIntent, 1); // Handle result from EditEventView
        });

        // View attendees functionality (if needed)
        viewAttendeesButton.setOnClickListener(v -> {
            // Handle the action when viewing attendees
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && data != null) {
            String eventName = data.getStringExtra("eventName");
            String organizerName = data.getStringExtra("organizerName");
            String eventDetailsText = data.getStringExtra("eventDetails");
            String eventDateText = data.getStringExtra("eventDate");
            String eventTime = data.getStringExtra("eventTime");

            // Update the event details in the view
            eventTitle.setText(eventName);
            eventOrganizer.setText("Organized by " + organizerName);
            eventDetails.setText(eventDetailsText);
            eventDate.setText(eventDateText + " at " + eventTime);

            // Send updated data back to CreatedEventsView if necessary
            Intent resultIntent = new Intent();
            resultIntent.putExtra("eventName", eventName);
            resultIntent.putExtra("organizerName", organizerName);
            resultIntent.putExtra("eventDetails", eventDetailsText);
            resultIntent.putExtra("eventDate", eventDateText);
            resultIntent.putExtra("eventTime", eventTime);
            resultIntent.putExtra("eventIndex", eventIndex);
            setResult(RESULT_OK, resultIntent);

            // Notify user that the event was updated
            Toast.makeText(this, "Event updated successfully", Toast.LENGTH_SHORT).show();
        }
    }
}
