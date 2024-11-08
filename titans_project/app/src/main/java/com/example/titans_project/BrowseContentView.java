package com.example.titans_project;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Switch;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.FirebaseApp;
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
    private CollectionReference userRef;
    private ListView contentList;
    private ArrayList<Event> eventDataList = new ArrayList<>();
    private ArrayList<User> profileDataList = new ArrayList<>();
    private EventsArrayAdapter eventArrayAdapter;
    private ProfilesArrayAdapter profileArrayAdapter;
    private Switch back_user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.browse_events);

        // Initialize Firebase
        FirebaseApp.initializeApp(this);
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        eventRef = db.collection("events");
        userRef = db.collection("user");

        // Initialize buttons and listview
        Button browse_events = findViewById(R.id.eventsButton);
        Button browse_profiles = findViewById(R.id.profilesButton);
        contentList = findViewById(R.id.browse_content_listview);  // Ensure this ID matches the one in your XML
        back_user = findViewById(R.id.back_user);

        // Array adapters
        profileArrayAdapter = new ProfilesArrayAdapter(this, profileDataList);
        eventArrayAdapter = new EventsArrayAdapter(this, eventDataList);

        browse_events.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Display ALL events when Event button is clicked
                contentList.setAdapter(eventArrayAdapter);

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
            }
        });

        back_user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        browse_profiles.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Display ALL profiles when Profile button is clicked
                contentList.setAdapter(profileArrayAdapter);

                // Get Firebase event data and populate the listview
                userRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot querySnapshots, @Nullable FirebaseFirestoreException error) {
                        if (error != null) {
                            Log.e(TAG, "Error fetching events: " + error.getMessage());
                            return;
                        }
                        if (querySnapshots != null) {
                            profileDataList.clear();
                            for (QueryDocumentSnapshot doc: querySnapshots) {
                                String full_name = doc.getString("full_name");
                                String email = doc.getString("email");
                                String phone_number = doc.getString("phone_number");
                                String facility = doc.getString("facility");
                                Boolean notifications = doc.getBoolean("notifications");

                                Log.d(TAG, String.format("User(%s, %s) fetched", full_name, email));
                                profileDataList.add(new User(full_name, email, phone_number, facility, notifications));
                            }
                            profileArrayAdapter.notifyDataSetChanged();
                        }
                    }
                });
            }
        });
    }
}


