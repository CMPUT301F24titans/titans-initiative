package com.example.titans_project;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class BrowseContentView extends AppCompatActivity {

    private static final String TAG = "BrowseContentView";
    private CollectionReference eventRef;
    private ListView eventList;
    private ArrayList<Event> eventDataList = new ArrayList<>();  // Initialize the eventDataList
    private EventsArrayAdapter eventArrayAdapter;  // Custom adapter for Event

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.browse_events);

        // Initialize Firebase
        FirebaseApp.initializeApp(this);
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        eventRef = db.collection("events");

        // Initialize buttons and listview
        Button browse_events = findViewById(R.id.eventsButton);
        Button browse_profiles = findViewById(R.id.profilesButton);
        eventList = findViewById(R.id.browse_content_listview);  // Ensure this ID matches the one in your XML

        // Initialize eventArrayAdapter with custom adapter
        eventArrayAdapter = new EventsArrayAdapter(this, eventDataList);
        eventList.setAdapter(eventArrayAdapter);

        // Get Firebase event data and populate the listview
        eventRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot querySnapshots, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    Log.e(TAG, "Error fetching events: " + error.getMessage());
                    return;
                }
                if (querySnapshots != null) {
                    eventDataList.clear();
                    for (QueryDocumentSnapshot doc: querySnapshots) {
                        String event_name = doc.getString("event_name");
                        String organizer = doc.getString("event_organizer");
                        String created_date = doc.getString("created_date");
                        String event_date = doc.getString("event_date");
                        String description = doc.getString("description");

                        Log.d(TAG, String.format("Event(%s, %s) fetched", event_name, event_date));
                        eventDataList.add(new Event(event_name, organizer, created_date, event_date, description));
                    }
                    eventArrayAdapter.notifyDataSetChanged();
                }
            }
        });

        browse_profiles.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // When Profiles button clicked display all profiles
            }
        });
    }
}


