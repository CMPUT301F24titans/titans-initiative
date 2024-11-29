package com.example.titans_project;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ScannedEventDetailActivity extends AppCompatActivity {

    private TextView eventNameTextView, eventDateTextView, eventDescriptionTextView;
    private String eventID;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private String userId;
    private String fullName;


    /**
     * Called when activity starts, create all activity objects here
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanned_event_detail);

        eventNameTextView = findViewById(R.id.eventNameTextView);
        eventDateTextView = findViewById(R.id.eventDateTextView);
        eventDescriptionTextView = findViewById(R.id.eventDescriptionTextView);
        Button applyButton = findViewById(R.id.applyButton);
        Button viewAttendeesButton = findViewById(R.id.viewAttendeesButton);

        eventID = getIntent().getStringExtra("eventID");
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        userId = getIntent().getStringExtra("userId");
        fullName = getIntent().getStringExtra("fullName");

        if (eventID != null) {
            loadEventDetails(eventID);
        } else {
            Toast.makeText(this, "Invalid event ID", Toast.LENGTH_SHORT).show();
            finish();
        }

        applyButton.setOnClickListener(v -> {
            applyToEvent();
            //addNotificationToUser(userId, fullName);
        });

        viewAttendeesButton.setOnClickListener(v -> {
            Intent intent = new Intent(ScannedEventDetailActivity.this, AttendeesActivity.class);
            intent.putExtra("eventID", eventID);
            startActivity(intent);
        });
    }

    private void loadEventDetails(String eventID) {
        db.collection("events").document(eventID).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        eventNameTextView.setText(documentSnapshot.getString("name"));
                        eventDateTextView.setText(documentSnapshot.getString("eventDate"));
                        eventDescriptionTextView.setText(documentSnapshot.getString("description"));
                    } else {
                        Toast.makeText(this, "Event not found", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Failed to load event details", Toast.LENGTH_SHORT).show());
    }

    private void applyToEvent() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String userID = currentUser.getUid();
            DocumentReference userRef = db.collection("user").document(userID);

            userRef.get().addOnSuccessListener(documentSnapshot -> {
                if (documentSnapshot.exists()) {
                    String fullName = documentSnapshot.getString("full_name");

                    // Check if the user is already in the waitlist
                    db.collection("events").document(eventID).get()
                            .addOnSuccessListener(eventSnapshot -> {
                                if (eventSnapshot.exists()) {
                                    List<Map<String, String>> waitlist = (List<Map<String, String>>) eventSnapshot.get("waitlist");
                                    if (waitlist != null) {
                                        for (Map<String, String> entry : waitlist) {
                                            if (userID.equals(entry.get("user_id"))) {
                                                Toast.makeText(this, "You are already in the waitlist for this event.", Toast.LENGTH_SHORT).show();
                                                return;
                                            }
                                        }
                                    }

                                    // Add the user to the waitlist
                                    Map<String, String> newEntry = new HashMap<>();
                                    newEntry.put("user_id", userID);
                                    newEntry.put("full_name", fullName);

                                    db.collection("events").document(eventID)
                                            .update("waitlist", FieldValue.arrayUnion(newEntry))
                                            .addOnSuccessListener(aVoid -> {
                                                Toast.makeText(this, "Successfully added to the waitlist!", Toast.LENGTH_SHORT).show();
                                            })
                                            .addOnFailureListener(e -> {
                                                Toast.makeText(this, "Failed to join the waitlist.", Toast.LENGTH_SHORT).show();
                                            });
                                }
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(this, "Failed to fetch event details.", Toast.LENGTH_SHORT).show();
                            });
                } else {
                    Toast.makeText(this, "User details not found.", Toast.LENGTH_SHORT).show();
                }
            }).addOnFailureListener(e -> {
                Toast.makeText(this, "Failed to fetch user details.", Toast.LENGTH_SHORT).show();
            });
        } else {
            Toast.makeText(this, "You need to log in to apply.", Toast.LENGTH_SHORT).show();
        }
    }

    private void addNotificationToUser(String userId, String fullName) {
        // Create notification content
        Map<String, Object> notification = new HashMap<>();
        notification.put("title", "Waitlist Application Successful");
        notification.put("content", "Hi " + fullName + ", you have successfully applied to the event!");
        notification.put("timestamp", String.valueOf(System.currentTimeMillis()));

        // Update user's notificationList in the database
        DocumentReference userRef = db.collection("user").document(userId);

        userRef.update("notificationList", FieldValue.arrayUnion(notification))
                .addOnSuccessListener(aVoid -> {
                    Log.d("Notification", "Notification added to user: " + fullName);
                })
                .addOnFailureListener(e -> {
                    Log.e("Notification", "Failed to add notification: ", e);
                });
    }

}
