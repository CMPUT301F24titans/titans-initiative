package com.example.titans_project;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

public class ViewAllEventsOrganizer extends AppCompatActivity {
    private ListView eventList;
    private ArrayList<Event> dataList;
    private EventsArrayAdapter eventsArrayAdapter;

    private FirebaseFirestore db;
    private CollectionReference eventRef;

    private Button myEventsButton, createEventsButton, profileButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_all_events_organizer);

        // Initialize Firebase
        db = FirebaseFirestore.getInstance();
        eventRef = db.collection("events");

        // Initialize UI components
        eventList = findViewById(R.id.listview_events);
        dataList = new ArrayList<>();
        eventsArrayAdapter = new EventsArrayAdapter(this, dataList);
        eventList.setAdapter(eventsArrayAdapter);

        // Load events from Firebase
        loadEventsFromFirebase();

        // Initialize bottom navigation buttons (no click actions for now)
        myEventsButton = findViewById(R.id.my_events_button);
        createEventsButton = findViewById(R.id.create_events_button);
        profileButton = findViewById(R.id.profile_button);
    }

    /**
     * Load events from Firebase Firestore and update the ListView.
     */
    private void loadEventsFromFirebase() {
        eventRef.get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    dataList.clear();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        Event event = document.toObject(Event.class);
                        dataList.add(event);
                    }
                    eventsArrayAdapter.notifyDataSetChanged(); // Update the ListView
                })
                .addOnFailureListener(e -> {
                    // Handle error if needed
                });
    }
}
