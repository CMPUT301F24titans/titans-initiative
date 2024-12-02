package com.example.titans_project;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

/**
 * Activity to display a list of the current user's event applications.
 * This screen retrieves events that the user has applied for from Firebase Firestore and displays them in a ListView.
 * The user can also navigate to a screen displaying accepted events.
 */
public class MyApplicationsView extends AppCompatActivity {
    private Button return_button, view_accepted_events_button; // Buttons for navigation
    private FirebaseAuth mAuth;                               // Firebase Authentication instance
    private FirebaseFirestore db;                              // Firestore instance to interact with the database
    private ListView eventList;                               // ListView to display the events
    private ArrayList<Event> eventsdataList;                  // List of events the user has applied for
    private ApplicationsArrayAdapter applicationsArrayAdapter; // Adapter for the ListView
    private Integer default_applicant_limit = 10000;          // Default applicant limit if none is provided in Firestore

    /**
     * Called when the activity is created. Initializes Firebase services, sets up the UI, and retrieves the user's applications.
     *
     * @param savedInstanceState The saved instance state, if any.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this); // Enables Edge-to-Edge UI (no system UI around the activity)
        setContentView(R.layout.fragment_my_applications);

        // Initialize Firebase
        FirebaseApp.initializeApp(this);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Initialize UI elements
        return_button = findViewById(R.id.button_return);
        view_accepted_events_button = findViewById(R.id.view_accepted_events_button);
        eventList = findViewById(R.id.listview_events);

        // Initialize the event list and adapter
        eventsdataList = new ArrayList<>();
        applicationsArrayAdapter = new ApplicationsArrayAdapter(this, eventsdataList);
        eventList.setAdapter(applicationsArrayAdapter);

        // Retrieve applications for the current user
        retrieveApplications(applications -> {
            if (applications != null) {
                Log.d("retrieveApplications", "Applications: " + applications);
                // Fetch events by their IDs if applications are found
                retrieveEventsById(applications);
                applicationsArrayAdapter.notifyDataSetChanged();
            } else {
                Log.d("retrieveApplications", "No applications retrieved.");
            }
        });

        // Set up button listeners
        view_accepted_events_button.setOnClickListener(view -> {
            Intent accepted_events = new Intent(MyApplicationsView.this, AcceptedEventsActivity.class);
            startActivity(accepted_events);
        });

        return_button.setOnClickListener(view -> finish()); // Close the activity when return button is clicked
    }

    /**
     * Fetches the current user's applications (list of event IDs the user has applied for).
     *
     * @param listener Callback listener for the applications retrieval result.
     */
    private void retrieveApplications(OnApplicationsRetrievedListener listener) {
        FirebaseUser currentUser = mAuth.getCurrentUser(); // Get the current logged-in user

        // Check if the user is logged in
        if (currentUser != null) {
            db.collection("user").document(currentUser.getUid()) // Access the user's document in Firestore
                    .get()
                    .addOnSuccessListener(document -> {
                        if (document.exists()) {
                            ArrayList<String> applications = (ArrayList<String>) document.get("applications");
                            listener.onApplicationsRetrieved(applications); // Pass applications to the listener
                        } else {
                            Toast.makeText(MyApplicationsView.this, "No applications found in the database", Toast.LENGTH_SHORT).show();
                            listener.onApplicationsRetrieved(null); // No applications found
                        }
                    })
                    .addOnFailureListener(exception -> {
                        Toast.makeText(MyApplicationsView.this, "Error retrieving applications", Toast.LENGTH_SHORT).show();
                        listener.onApplicationsRetrieved(null); // Error retrieving applications
                    });
        } else {
            Toast.makeText(MyApplicationsView.this, "User not logged in", Toast.LENGTH_SHORT).show();
            listener.onApplicationsRetrieved(null); // User is not logged in
        }
    }

    /**
     * Listener interface for retrieving applications.
     *
     * Used to pass the retrieved list of applications back to the caller.
     */
    public interface OnApplicationsRetrievedListener {
        void onApplicationsRetrieved(ArrayList<String> applications);
    }

    /**
     * Retrieves event details from Firestore using a list of event IDs and populates the event list.
     *
     * @param documentIds List of event IDs the user has applied for.
     */
    private void retrieveEventsById(List<String> documentIds) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        int[] completedCount = {0}; // Counter to track the number of completed fetches

        // Iterate through each event ID to retrieve the corresponding event data
        for (String docId : documentIds) {
            db.collection("events").document(docId)
                    .get()
                    .addOnSuccessListener(document -> {
                        if (document.exists()) {
                            // Retrieve event details
                            String event_id = document.getString("eventID");
                            String event_name = document.getString("name");
                            String facility_name = document.getString("facilityName");
                            String created_date = document.getString("createdDate");
                            String event_date = document.getString("eventDate");
                            String description = document.getString("description");
                            String organizer_id = document.getString("organizerID");
                            String picture = document.getString("picture");

                            // Retrieve the applicant limit if available
                            Integer applicant_limit = default_applicant_limit;
                            Object applicantLimitObj = document.get("applicantLimit");
                            if (applicantLimitObj != null) {
                                applicant_limit = ((Long) applicantLimitObj).intValue(); // Cast to Integer
                            }

                            // Add event data to the list
                            eventsdataList.add(new Event(event_id, event_name, facility_name, created_date, event_date, description, organizer_id, picture, applicant_limit, null));
                        }

                        completedCount[0]++; // Increment the counter
                        if (completedCount[0] == documentIds.size()) {
                            // Notify the adapter to refresh the UI when all events are fetched
                            runOnUiThread(() -> applicationsArrayAdapter.notifyDataSetChanged());
                        }
                    })
                    .addOnFailureListener(e -> {
                        completedCount[0]++; // Increment the counter in case of failure
                        Log.e("MyApplicationsView", "Error fetching document with ID " + docId, e);
                        if (completedCount[0] == documentIds.size()) {
                            // Notify the adapter to refresh the UI even if some fetches failed
                            runOnUiThread(() -> applicationsArrayAdapter.notifyDataSetChanged());
                        }
                    });
        }
    }
}
