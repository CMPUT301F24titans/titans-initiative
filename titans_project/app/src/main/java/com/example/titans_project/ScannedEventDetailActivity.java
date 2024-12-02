package com.example.titans_project;

import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This activity shows the details of a scanned event and allows the user to apply to the event's waitlist.
 * The user can also check whether geolocation is required for the event and allow location data to be submitted if necessary.
 */
public class ScannedEventDetailActivity extends AppCompatActivity {

    private TextView eventNameTextView, eventDateTextView, eventDescriptionTextView;
    private CheckBox geolocationCheckbox;  // Checkbox to indicate whether the user needs to enable geolocation
    private String eventID;  // The event ID passed from the previous activity
    private FirebaseAuth mAuth;  // Firebase authentication instance for user authentication
    private FirebaseFirestore db;  // Firestore database instance to interact with event and user data
    private Button return_button, applyButton;  // Buttons for navigating back and applying to the event
    private LocationManager locationManager;  // Manages location services
    private Boolean geolocationRequired;  // Boolean flag indicating if geolocation is required for this event

    /**
     * Called when the activity is created. Initializes UI components, fetches event details from Firestore,
     * and checks if geolocation is required.
     *
     * @param savedInstanceState The saved instance state of the activity if it was previously destroyed.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_scanned_event_detail);

        // Initialize UI components
        eventNameTextView = findViewById(R.id.eventNameTextView);
        eventDateTextView = findViewById(R.id.eventDateTextView);
        eventDescriptionTextView = findViewById(R.id.eventDescriptionTextView);
        applyButton = findViewById(R.id.applyButton);
        return_button = findViewById(R.id.returnButton);
        geolocationCheckbox = findViewById(R.id.geolocationCheckBox);

        // Retrieve event ID from the intent
        eventID = getIntent().getStringExtra("eventID");

        // Initialize FirebaseAuth and Firestore instances
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Initialize LocationManager for geolocation
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        // Load event details from Firestore if the eventID is valid
        if (eventID != null) {
            loadEventDetails(eventID);
        } else {
            Toast.makeText(this, "Invalid event ID", Toast.LENGTH_SHORT).show();
            finish();  // Close activity if event ID is invalid
        }

        // Check if geolocation is required for the event
        db.collection("events").document(eventID).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        geolocationRequired = documentSnapshot.getBoolean("geolocation");
                        if (geolocationRequired == null || !geolocationRequired) {
                            geolocationCheckbox.setVisibility(View.GONE);  // Hide checkbox if geolocation is not required
                        }
                    }
                });

        // Set up click listener for the apply button
        applyButton.setOnClickListener(v -> {

            if (geolocationRequired) {
                // If geolocation is required, ensure the checkbox is checked
                if (!geolocationCheckbox.isChecked()) {
                    geolocationCheckbox.setError("Geolocation Required");
                    return;
                }
            }

            // Fetch event details and check if the user can apply to the event
            db.collection("events").document(eventID).get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            Long applicantLimitLong = documentSnapshot.getLong("applicantLimit");
                            Integer applicantLimit = applicantLimitLong != null ? applicantLimitLong.intValue() : 0;
                            ArrayList<HashMap<String, String>> applications = (ArrayList<HashMap<String, String>>) documentSnapshot.get("waitlist");

                            if (applications != null) {
                                // Check if there's room in the waitlist
                                if (applications.size() < applicantLimit) {
                                    applyToEvent();
                                    // Check if geolocation is enabled for the event
                                    if (geolocationRequired != null && geolocationRequired) {
                                        getOneTimeLocation();  // Fetch and submit the user's location
                                    }
                                } else {
                                    Toast.makeText(ScannedEventDetailActivity.this, "Event Full: Unable to join event", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Toast.makeText(ScannedEventDetailActivity.this, "Waitlist Error: Unable to join event", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

        });

        // Set up click listener for the return button
        return_button.setOnClickListener(view -> finish());  // Finish the activity and return to the previous screen
    }

    /**
     * Loads the event details (name, date, description) from Firestore based on the event ID.
     *
     * @param eventID The event ID used to fetch event details.
     */
    private void loadEventDetails(String eventID) {
        db.collection("events").document(eventID).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        eventNameTextView.setText(documentSnapshot.getString("name"));
                        eventDateTextView.setText(documentSnapshot.getString("eventDate"));
                        eventDescriptionTextView.setText(documentSnapshot.getString("description"));
                    } else {
                        Toast.makeText(this, "Event not found", Toast.LENGTH_SHORT).show();
                        finish();  // Close the activity if the event is not found
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Failed to load event details", Toast.LENGTH_SHORT).show());
    }

    /**
     * Applies the current user to the event's waitlist in Firestore.
     * This method checks if the user is already on the waitlist and adds them if not.
     */
    private void applyToEvent() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String userID = currentUser.getUid();
            DocumentReference userRef = db.collection("user").document(userID);

            // Fetch the current user's details
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

                                    // Add the event to the user's applications list
                                    userRef.update("applications", FieldValue.arrayUnion(eventID));
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

    /**
     * Fetches the user's current location and updates the event's locations field in Firestore.
     * This method is used if geolocation is required for the event.
     */
    private void getOneTimeLocation() {
        try {
            // Request a one-time location update using the GPS provider
            locationManager.requestSingleUpdate(LocationManager.GPS_PROVIDER, new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    // Store the location data (latitude and longitude) in a HashMap
                    HashMap<String, Double> locationCoordinates = new HashMap<>();
                    locationCoordinates.put("latitude", location.getLatitude());
                    locationCoordinates.put("longitude", location.getLongitude());

                    // Update Firestore with the new location data
                    db.collection("events").document(eventID).update("locations", FieldValue.arrayUnion(locationCoordinates));
                }

                @Override
                public void onStatusChanged(String provider, int status, Bundle extras) {
                    // Handle status change if needed
                }

                @Override
                public void onProviderEnabled(String provider) {
                    // Handle provider enabled if needed
                }

                @Override
                public void onProviderDisabled(String provider) {
                    // Handle provider disabled if needed
                }
            }, null);
        } catch (SecurityException e) {
            // Handle permission exception
            Toast.makeText(this, "Permission issue: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}
