package com.example.titans_project;

import androidx.annotation.NonNull;
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
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private Button profile_button, application_button, created_events_button;
    private FirebaseAuth mAuth;
    private ListView eventList;
    private ArrayList<Event> eventsdataList;
    private EventsArrayAdapter eventsArrayAdapter;
    private Intent profile = new Intent();
    private Intent my_applications = new Intent();
    private Intent event_detail = new Intent();
    private Intent admin = new Intent();
    private FirebaseFirestore db;
    private CollectionReference eventRef, userRef;
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_my_events); // Ensure this layout exists

        // Initialize Firebase
        FirebaseApp.initializeApp(this);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Check and perform anonymous sign-in
        performAnonymousSignIn();

        eventRef = db.collection("events");
        userRef = db.collection("user");

        // Initialize UI components
        eventList = findViewById(R.id.listview_events);
        profile_button = findViewById(R.id.profile_button);
        application_button = findViewById(R.id.application_button);
        created_events_button = findViewById(R.id.created_events_button); // Ensure this ID exists

        // Initialize events list and adapter
        eventsdataList = new ArrayList<>();
        eventsArrayAdapter = new EventsArrayAdapter(this, eventsdataList);
        eventList.setAdapter(eventsArrayAdapter);

        // Set OnClickListeners for buttons
        profile_button.setOnClickListener(view -> {
            profile.setClass(MainActivity.this, ProfileView.class);
            startActivity(profile);
        });

        application_button.setOnClickListener(view -> {
            my_applications.setClass(MainActivity.this, MyApplicationsView.class);
            startActivity(my_applications);
        });

        created_events_button.setOnClickListener(view -> {
            Intent createdEvents = new Intent(MainActivity.this, CreatedEventsView.class);
            startActivity(createdEvents);
        });

        // Set OnItemClickListener for event list
        eventList.setOnItemClickListener((adapterView, view, position, l) -> {
            event_detail.setClass(MainActivity.this, EventDetailsView.class);
            event_detail.putExtra("event name", eventsdataList.get(position).getName());
            event_detail.putExtra("event organizer", eventsdataList.get(position).getOrganizer());
            event_detail.putExtra("event description", eventsdataList.get(position).getDescription());
            event_detail.putExtra("event date", eventsdataList.get(position).getEvent_date());
            startActivity(event_detail);
        });

        // Load events from Firestore
        loadEventsFromFirestore();
    }

    // Load events from Firestore
    private void loadEventsFromFirestore() {
        eventRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                QuerySnapshot querySnapshot = task.getResult();
                if (querySnapshot != null && !querySnapshot.isEmpty()) {
                    for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                        String eventName = document.getString("Event Name");
                        String organizer = document.getString("Organizer");
                        String createdDate = document.getString("Event Created Date");
                        String eventDate = document.getString("Event Date");
                        String description = document.getString("Description");
                        String picture = document.getString("Picture");

                        Event event = new Event(eventName, organizer, createdDate, eventDate, description, picture);
                        addEvent(event);
                    }
                } else {
                    Toast.makeText(MainActivity.this, "No events available.", Toast.LENGTH_SHORT).show();
                }
            } else {
                Log.e(TAG, "Error getting events: ", task.getException());
            }
        });
    }

    // Add event to list and Firestore
    private void addEvent(Event event) {
        if (event == null || event.getName() == null || event.getOrganizer() == null) {
            Log.e(TAG, "Invalid event data");
            return;
        }

        eventsdataList.add(event);
        eventsArrayAdapter.notifyDataSetChanged();

        // Prepare data for Firestore
        HashMap<String, String> eventdata = new HashMap<>();
        eventdata.put("Event Name", event.getName());
        eventdata.put("Organizer", event.getOrganizer());
        eventdata.put("Event Created Date", event.getCreated_date());
        eventdata.put("Event Date", event.getEvent_date());
        eventdata.put("Description", event.getDescription());
        eventdata.put("Picture", event.getPicture());
        eventdata.put("Event ID", event.getName() + event.getOrganizer() + event.getEvent_date());

        // Save to Firestore
        eventRef.document(event.getName()).set(eventdata)
                .addOnSuccessListener(aVoid -> Log.d(TAG, "Event successfully written to Firestore"))
                .addOnFailureListener(e -> Log.e(TAG, "Error writing event to Firestore", e));
    }

    // Perform anonymous sign-in for new users
    private void performAnonymousSignIn() {
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser == null) {
            // No current user, attempt to sign in anonymously
            mAuth.signInAnonymously()
                    .addOnCompleteListener(this, task -> {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "signInAnonymously:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            createProfile(user);  // Store user in Firebase
                        } else {
                            Log.w(TAG, "signInAnonymously:failure", task.getException());
                            Toast.makeText(MainActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            // Existing user detected
            Log.d(TAG, "User already signed in");
            Toast.makeText(MainActivity.this, "Successfully Signed In, User UID: " + currentUser.getUid(), Toast.LENGTH_SHORT).show();
        }
    }

    // Create a user profile in Firestore
    public void createProfile(FirebaseUser user) {
        userRef = db.collection("user");
        HashMap<String, Object> userData = new HashMap<>();
        userData.put("full_name", "");
        userData.put("email", "");
        userData.put("phone_number", "");
        userData.put("facility", "");
        userData.put("notifications", Boolean.FALSE);

        HashMap<String, Object> eventData = new HashMap<>();  // Empty initially
        userData.put("Events", eventData);

        // Store in Firebase
        userRef.document(user.getUid()).set(userData)
                .addOnSuccessListener(aVoid -> Log.d(TAG, "User profile created in Firestore"))
                .addOnFailureListener(e -> Log.e(TAG, "Error creating user profile in Firestore", e));
    }

    @Override
    protected void onStart() {
        super.onStart();

        // Check if user is logged in when the activity starts
        if (mAuth.getCurrentUser() == null) {
            // If not logged in, show a message or disable buttons
            Toast.makeText(this, "You are not logged in. Please log in to access the content.", Toast.LENGTH_LONG).show();
            profile_button.setEnabled(false);
            application_button.setEnabled(false);
            created_events_button.setEnabled(false);
        } else {
            // Enable buttons if logged in
            profile_button.setEnabled(true);
            application_button.setEnabled(true);
            created_events_button.setEnabled(true);
        }
    }
}
