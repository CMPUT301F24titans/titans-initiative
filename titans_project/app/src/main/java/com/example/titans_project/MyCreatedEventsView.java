package com.example.titans_project;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

/**
 * This activity allows the user to view all events they have created.
 * The user can also create new events, view event details, and manage attendees or waitlists.
 */
public class MyCreatedEventsView extends AppCompatActivity {

    private ListView eventList;                     // ListView to display the list of events created by the user
    private ArrayList<Event> eventsdataList;        // List of events to display in the ListView
    private EventsArrayAdapter eventsArrayAdapter;  // Adapter to bind the event list to the ListView
    private Button create_event_button, return_button; // Buttons for creating events and returning to previous screen
    private Intent event_detail = new Intent();     // Intent to navigate to EventDetailView
    private Intent create_event = new Intent();     // Intent to navigate to CreateEventView
    private FirebaseFirestore db;                   // Firebase Firestore instance for accessing data
    private CollectionReference eventRef, userRef; // References to the Firestore collections
    private static final String TAG = "AnonymousAuthActivity"; // Tag for logging
    private FirebaseAuth mAuth;                     // FirebaseAuth instance for authentication
    private Integer default_applicant_limit = 10000; // Default applicant limit for events

    /**
     * Called when the activity is created. Initializes Firebase, sets up UI components,
     * listens for changes to the events collection, and handles user interactions.
     *
     * @param savedInstanceState The saved instance state, if any.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_created_events);

        // Initialize Firebase
        FirebaseApp.initializeApp(this);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Set up Firestore references
        eventRef = db.collection("events");
        userRef = db.collection("user");

        // Initialize UI components
        eventList = findViewById(R.id.listview_events);
        create_event_button = findViewById(R.id.create_event_button);
        return_button = findViewById(R.id.button_return);

        // Initialize the event data list and adapter
        eventsdataList = new ArrayList<>();
        eventsArrayAdapter = new EventsArrayAdapter(this, eventsdataList);
        eventList.setAdapter(eventsArrayAdapter);

        // Listen for real-time updates to the events collection
        eventRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
            /**
             * This method is called every time the events collection is updated.
             * It checks if the current user is the organizer of each event and updates the event list.
             *
             * @param querySnapshots The query snapshot containing event data.
             * @param error The error, if any, that occurred while fetching data.
             */
            @Override
            public void onEvent(@Nullable QuerySnapshot querySnapshots, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    Log.e(TAG, "Error fetching events: " + error.getMessage());
                    return;
                }
                if (querySnapshots != null) {
                    eventsdataList.clear(); // Clear the existing event list

                    // Iterate over each document in the snapshot
                    for (QueryDocumentSnapshot doc : querySnapshots) {
                        String user_id = mAuth.getCurrentUser().getUid();  // Get the current user's ID
                        String organizer_id = doc.getString("organizerID"); // Get the event organizer's ID

                        // Check if the current user is the organizer of the event
                        if (user_id.equals(organizer_id)) {
                            // Retrieve event details from Firestore
                            String event_id = doc.getString("eventID");
                            String event_name = doc.getString("name");
                            String facility_name = doc.getString("facilityName");
                            String created_date = doc.getString("createdDate");
                            String event_date = doc.getString("eventDate");
                            String description = doc.getString("description");
                            String picture = doc.getString("picture");

                            // Set the applicant limit, or use the default if not specified
                            Integer applicant_limit = default_applicant_limit;
                            Object applicantLimitObj = doc.get("applicantLimit");
                            if (applicantLimitObj != null) {
                                applicant_limit = ((Long) applicantLimitObj).intValue(); // Cast to Integer
                                Log.w(TAG, "applicantLimit: " + applicant_limit);
                            } else {
                                Log.w(TAG, "applicantLimit is missing or null");
                            }

                            // Add the event to the data list
                            Log.d(TAG, String.format("Event(%s, %s) fetched", event_name, event_date));
                            eventsdataList.add(new Event(event_id, event_name, facility_name, created_date, event_date, description, organizer_id, picture, applicant_limit, null));
                        }
                    }

                    // Notify the adapter that the data has been updated
                    eventsArrayAdapter.notifyDataSetChanged();
                }
            }
        });

        // Set up the "Create Event" button
        create_event_button.setOnClickListener(new View.OnClickListener() {
            /**
             * When the user clicks the "Create Event" button, they are navigated to the Create Event screen.
             *
             * @param view The view that was clicked.
             */
            @Override
            public void onClick(View view) {
                create_event.setClass(MyCreatedEventsView.this, CreateEventView.class);
                startActivity(create_event);
            }
        });

        // Set up the "Return" button
        return_button.setOnClickListener(new View.OnClickListener() {
            /**
             * When the user clicks the "Return" button, the activity finishes and returns to the previous screen.
             *
             * @param view The view that was clicked.
             */
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        // Set up item click listener for the events list
        eventList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            /**
             * When the user clicks on an event, they are navigated to the Event Detail screen.
             *
             * @param adapterView The AdapterView where the click occurred.
             * @param view The view that was clicked.
             * @param position The position of the clicked item.
             * @param l The row ID of the clicked item.
             */
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Event clickedEvent = eventsdataList.get(position);

                // Set up the EventDetailView intent with event data
                event_detail.setClass(MyCreatedEventsView.this, EventDetailView.class);
                event_detail.putExtra("event ID", clickedEvent.getEventID());
                event_detail.putExtra("event name", clickedEvent.getName());
                event_detail.putExtra("event facility", clickedEvent.getFacilityName());
                event_detail.putExtra("event create date", clickedEvent.getCreatedDate());
                event_detail.putExtra("event date", clickedEvent.getEventDate());
                event_detail.putExtra("event description", clickedEvent.getDescription());
                event_detail.putExtra("event organizer", clickedEvent.getOrganizerID());
                event_detail.putExtra("event image", clickedEvent.getPicture());
                event_detail.putExtra("event limit", clickedEvent.getApplicantLimit());
                event_detail.putExtra("viewer", "organizer");

                Log.w(TAG, "applicantLimit (when clicked on from MainActivity): " + eventsdataList.get(position).getApplicantLimit());
                startActivity(event_detail);
            }
        });

        // Set up long click listener for event items
        eventList.setOnItemLongClickListener((adapterView, view, position, l) -> {
            // Show options for Waitlist or Attendees
            Event selectedEvent = eventsdataList.get(position);

            // Navigate to WaitlistActivity with event data
            Intent waitlistIntent = new Intent(MyCreatedEventsView.this, WaitlistActivity.class);
            waitlistIntent.putExtra("eventID", selectedEvent.getEventID());
            startActivity(waitlistIntent);
            return true;
        });
    }
}
