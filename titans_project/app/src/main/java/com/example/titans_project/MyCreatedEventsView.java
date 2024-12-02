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
 * This is a class that defines the My Created Events activity of the app
 */
public class MyCreatedEventsView extends AppCompatActivity {
    private ListView eventList;
    private ArrayList<Event> eventsdataList;
    private EventsArrayAdapter eventsArrayAdapter;
    private Button create_event_button, return_button;
    Intent event_detail = new Intent();
    Intent create_event = new Intent();
    private FirebaseFirestore db;
    private CollectionReference eventRef, userRef;
    private static final String TAG = "AnonymousAuthActivity";
    private FirebaseAuth mAuth;
    Integer default_applicant_limit = 10000;


    /**
     * This runs after the onStart function above
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_created_events);

        // Initialize Firebase
        FirebaseApp.initializeApp(this);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        eventRef = db.collection("events");
        userRef = db.collection("user");

        eventList = findViewById(R.id.listview_events);
        create_event_button = findViewById(R.id.create_event_button);
        return_button = findViewById(R.id.button_return);

        eventsdataList = new ArrayList<>();
        eventsArrayAdapter = new EventsArrayAdapter(this, eventsdataList);
        eventList.setAdapter(eventsArrayAdapter);


        eventRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
            /**
             * Displays the events in Firebase in app for user to view all user created events
             * @param querySnapshots The value of the event. {@code null} if there was an error.
             * @param error The error if there was error. {@code null} otherwise.
             */
            @Override
            public void onEvent(@Nullable QuerySnapshot querySnapshots, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    Log.e(TAG, "Error fetching events: " + error.getMessage());
                    return;
                }
                if (querySnapshots != null) {
                    eventsdataList.clear();
                    for (QueryDocumentSnapshot doc: querySnapshots) {
                        String user_id = mAuth.getCurrentUser().getUid();
                        String organizer_id = doc.getString("organizerID");
                        // Check if user is owner of event
                        if (user_id.equals(organizer_id)) {
                            String event_id = doc.getString("eventID");
                            String event_name = doc.getString("name");
                            String facility_name = doc.getString("facilityName");
                            String created_date = doc.getString("createdDate");
                            String event_date = doc.getString("eventDate");
                            String description = doc.getString("description");
                            String picture = doc.getString("picture");
                            Integer applicant_limit = default_applicant_limit;
                            Object applicantLimitObj = doc.get("applicantLimit");
                            if (applicantLimitObj != null) {
                                applicant_limit = ((Long) applicantLimitObj).intValue(); // Cast to Integer
                                Log.w(TAG, "applicantLimit: " + applicant_limit);
                            } else {
                                Log.w(TAG, "applicantLimit is missing or null");
                            }
                            Log.d(TAG, String.format("Event(%s, %s) fetched", event_name, event_date));
                            eventsdataList.add(new Event(event_id, event_name, facility_name, created_date, event_date, description, organizer_id, picture, applicant_limit, null));
                        }
                    }
                    eventsArrayAdapter.notifyDataSetChanged();
                }
            }
        });

        create_event_button.setOnClickListener(new View.OnClickListener() {
            /**
             * User clicks on Create Event button, send user to Create Event activity
             * @param view
             */
            @Override
            public void onClick(View view) {
                create_event.setClass(MyCreatedEventsView.this, CreateEventView.class);
                startActivity(create_event);
            }
        });

        return_button.setOnClickListener(new View.OnClickListener() {
            /**
             * User clicks on Return button, return to previous activity
             * @param view
             */
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        eventList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            /**
             * User clicks on an Event in the events list, send user to Event Detail activity
             */
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Event clickedEvent = eventsdataList.get(position);
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

        eventList.setOnItemLongClickListener((adapterView, view, position, l) -> {
            // Show options for Waitlist or Attendees
            Event selectedEvent = eventsdataList.get(position);

            // Navigate to WaitlistActivity
            Intent waitlistIntent = new Intent(MyCreatedEventsView.this, WaitlistActivity.class);
            waitlistIntent.putExtra("eventID", selectedEvent.getEventID());
            startActivity(waitlistIntent);
            return true;
        });
    }
}