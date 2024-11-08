package com.example.titans_project;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class CreateEventActivity extends AppCompatActivity {

    private EditText eventTitleEdit, organizerEdit, eventDetailsEdit, eventDateEdit;
    private Button submitButton;

    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_event);

        // Initialize Firebase Firestore
        db = FirebaseFirestore.getInstance();

        // Initialize UI components
        eventTitleEdit = findViewById(R.id.eventTitleEdit);
        organizerEdit = findViewById(R.id.organizerEdit);
        eventDetailsEdit = findViewById(R.id.eventDetailsEdit);
        eventDateEdit = findViewById(R.id.eventDateEdit);
        submitButton = findViewById(R.id.submitButton);

        // Set submit button listener
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createEventInFirestore();
            }
        });
    }

    private void createEventInFirestore() {
        String eventName = eventTitleEdit.getText().toString();
        String organizer = organizerEdit.getText().toString();
        String eventDetails = eventDetailsEdit.getText().toString();
        String eventDate = eventDateEdit.getText().toString();

        if (eventName.isEmpty() || organizer.isEmpty() || eventDetails.isEmpty() || eventDate.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        String createdDate = new SimpleDateFormat("yyyy/MM/dd", Locale.getDefault()).format(new Date());
        String eventId = eventName + organizer + eventDate;

        Map<String, Object> event = new HashMap<>();
        event.put("Event Name", eventName);
        event.put("Organizer", organizer);
        event.put("Description", eventDetails);
        event.put("Event Date", eventDate);
        event.put("Event Created Date", createdDate);
        event.put("Event ID", eventId);

        db.collection("events").document(eventId)
                .set(event)
                .addOnSuccessListener(aVoid -> Toast.makeText(CreateEventActivity.this, "Event created successfully", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(CreateEventActivity.this, "Failed to create event", Toast.LENGTH_SHORT).show());
    }
}
