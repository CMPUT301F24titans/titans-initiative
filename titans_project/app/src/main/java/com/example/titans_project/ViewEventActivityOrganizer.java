package com.example.titans_project;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class ViewEventActivityOrganizer extends AppCompatActivity {

    private Button qrCodeButton, deleteButton, attendeesButton, editButton;
    private TextView eventTitle, organizerInfo, eventDetails, eventDate;
    private ImageView eventImage;

    private FirebaseFirestore db;
    private DocumentReference eventRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_event_organizer);

        String eventId = "fakeEventTitle";

        db = FirebaseFirestore.getInstance();
        eventRef = db.collection("events").document(eventId);

        eventTitle = findViewById(R.id.eventTitle);
        organizerInfo = findViewById(R.id.organizerInfo);
        eventImage = findViewById(R.id.eventImage);
        eventDetails = findViewById(R.id.eventDetails);
        eventDate = findViewById(R.id.eventDate);

        qrCodeButton = findViewById(R.id.qrCodeButton);
        deleteButton = findViewById(R.id.deleteButton);
        attendeesButton = findViewById(R.id.attendeesButton);
        editButton = findViewById(R.id.editButton);

        loadEventData();

        setupButtonListeners();
    }

    private void loadEventData() {
        eventRef.get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        eventTitle.setText(documentSnapshot.getString("Event Name"));
                        organizerInfo.setText("Organized by " + documentSnapshot.getString("Organizer"));
                        eventDetails.setText(documentSnapshot.getString("Description"));
                        eventDate.setText("Event Date: " + documentSnapshot.getString("Event Date"));
                        // Glide.with(this).load(documentSnapshot.getString("Picture")).into(eventImage);
                    } else {
                        Toast.makeText(this, "Event not found", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Failed to load event data", Toast.LENGTH_SHORT).show());
    }

    private void setupButtonListeners() {
        qrCodeButton.setOnClickListener(v -> {
            Intent intent = new Intent(ViewEventActivityOrganizer.this, QRCodeActivity.class);
            intent.putExtra("eventId", "fakeEventTitle"); // 传递 Event ID
            startActivity(intent);
        });

        deleteButton.setOnClickListener(v -> {
            Intent intent = new Intent(ViewEventActivityOrganizer.this, DeleteEventActivity.class);
            intent.putExtra("eventId", "fakeEventTitle"); // 传递 Event ID
            startActivity(intent);
        });

        attendeesButton.setOnClickListener(v -> {
            Intent intent = new Intent(ViewEventActivityOrganizer.this, AttendeesActivity.class);
            intent.putExtra("eventId", "fakeEventTitle"); // 传递 Event ID
            startActivity(intent);
        });

        editButton.setOnClickListener(v -> {
            Intent intent = new Intent(ViewEventActivityOrganizer.this, EditEventActivity.class);
            intent.putExtra("eventId", "fakeEventTitle"); // 传递 Event ID
            startActivity(intent);
        });
    }
}
