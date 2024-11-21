package com.example.titans_project;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
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
    private Boolean browsingEvents;
    Intent event_detail = new Intent();
    Intent profile_detail = new Intent();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.browse_content);

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

        // check if admin user clicked on browse events button
        browse_events.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                browsingEvents = true;
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
        // check if admin user clicked on browse profiles button
        browse_profiles.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                browsingEvents = false;
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
                                String user_id = doc.getString("user_id");

                                Log.d(TAG, String.format("User(%s, %s) fetched", full_name, email));
                                profileDataList.add(new User(full_name, email, phone_number, facility, notifications, user_id));
                            }
                            profileArrayAdapter.notifyDataSetChanged();
                        }
                    }
                });
            }
        });
        // check if admin user clicked on an item in the contentList
        contentList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                // user currently browsing events
                if (browsingEvents){
                    Event clickedEvent = eventDataList.get(position);
                    event_detail.setClass(BrowseContentView.this, EventDetailView.class);
                    event_detail.putExtra("event name", clickedEvent.getName());
                    event_detail.putExtra("event organizer", clickedEvent.getFacilityName());
                    event_detail.putExtra("event description", clickedEvent.getDescription());
                    event_detail.putExtra("event date", clickedEvent.getEventDate());
                    event_detail.putExtra("event_id", clickedEvent.getEventID());

                    event_detail.putExtra("viewer", "admin");
                    startActivity(event_detail);
                }
                // user currently browsing profiles
                else {
                    User clickedUser = profileDataList.get(position);

                    profile_detail.setClass(BrowseContentView.this, ProfileView.class);
                    profile_detail.putExtra("user_id", clickedUser.getUserID());
                    profile_detail.putExtra("viewer", "admin");
                    startActivity(profile_detail);
                }
            }
        });
    }
}


