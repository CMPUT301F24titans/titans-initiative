package com.example.titans_project;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class EditEventView extends AppCompatActivity {

    private EditText eventName, organizerName, eventDetails, eventDate, eventTime;
    private Button saveButton;
    private ImageView backButton;
    private int eventIndex = -1;

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

        // Retrieve data from the Intent
        Intent intent = getIntent();
        String name = intent.getStringExtra("eventName");
        String organizer = intent.getStringExtra("organizerName");
        String details = intent.getStringExtra("eventDetails");
        String date = intent.getStringExtra("eventDate");
        String time = intent.getStringExtra("eventTime");
        eventIndex = intent.getIntExtra("eventIndex", -1);

        // Set the text fields with retrieved intent data
        eventName.setText(name);
        organizerName.setText(organizer);
        eventDetails.setText(details);
        eventDate.setText(date);
        eventTime.setText(time);

        // Set up back button click listener
        backButton.setOnClickListener(v -> onBackPressed());

        // Set up save button click listener
        saveButton.setOnClickListener(v -> {
            String updatedName = eventName.getText().toString();
            String updatedOrganizer = organizerName.getText().toString();
            String updatedDetails = eventDetails.getText().toString();
            String updatedDate = eventDate.getText().toString();
            String updatedTime = eventTime.getText().toString();

            // Validate inputs before saving
            if (updatedName.isEmpty() || updatedOrganizer.isEmpty() || updatedDetails.isEmpty() || updatedDate.isEmpty() || updatedTime.isEmpty()) {
                Toast.makeText(EditEventView.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            // Create an Intent to pass data back
            Intent resultIntent = new Intent();
            resultIntent.putExtra("eventName", updatedName);
            resultIntent.putExtra("organizerName", updatedOrganizer);
            resultIntent.putExtra("eventDetails", updatedDetails);
            resultIntent.putExtra("eventDate", updatedDate);
            resultIntent.putExtra("eventTime", updatedTime);
            resultIntent.putExtra("eventIndex", eventIndex);

            setResult(RESULT_OK, resultIntent);

            // Show a toast message indicating success
            Toast.makeText(this, "Event saved successfully", Toast.LENGTH_SHORT).show();
            finish(); // Close the activity and return to the previous screen
        });
    }
}
