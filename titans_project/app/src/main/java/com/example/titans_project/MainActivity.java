package com.example.titans_project;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class MainActivity extends AppCompatActivity {

    private Button profile_button, application_button, created_events_button;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_my_events); // Set the layout for My Events page

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Find buttons
        profile_button = findViewById(R.id.profile_button);
        application_button = findViewById(R.id.application_button);
        created_events_button = findViewById(R.id.created_events_button); // Find the Created Events button

        // Handle authentication status
        if (mAuth.getCurrentUser() == null) {
            // Show a message or provide the user with options to log in
            Toast.makeText(this, "You are not logged in. Please log in to access the content.", Toast.LENGTH_LONG).show();
        } else {
            // The user is logged in
            // Optionally fetch and display user data from Firestore if needed
            fetchUserData();
        }

        // User clicks on the Profile button
        profile_button.setOnClickListener(view -> {
            Intent profile = new Intent(MainActivity.this, ProfileView.class);
            startActivity(profile);
        });

        // User clicks on the My Applications button
        application_button.setOnClickListener(view -> {
            Intent myApplications = new Intent(MainActivity.this, MyApplicationsView.class);
            startActivity(myApplications);
        });

        // User clicks on the Created Events button
        created_events_button.setOnClickListener(view -> {
            // Open the Created Events Activity when the button is clicked
            Intent createdEvents = new Intent(MainActivity.this, CreatedEventsView.class);
            startActivity(createdEvents);
        });
    }

    /**
     * Optionally fetch user data from Firestore and display it.
     */
    private void fetchUserData() {
        String userId = mAuth.getCurrentUser().getUid();

        db.collection("users").document(userId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        // Example: Show the user’s name (assuming the field is "username")
                        String username = documentSnapshot.getString("username");
                        Toast.makeText(MainActivity.this, "Welcome back, " + username, Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(MainActivity.this, "No user data found.", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(MainActivity.this, "Failed to fetch user data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    @Override
    protected void onStart() {
        super.onStart();

        // Check if user is logged in when the activity starts
        if (mAuth.getCurrentUser() == null) {
            // If not logged in, show a message or disable buttons
            Toast.makeText(this, "You are not logged in. Please log in to access the content.", Toast.LENGTH_LONG).show();
        }
    }
}
