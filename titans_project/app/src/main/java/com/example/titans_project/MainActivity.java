package com.example.titans_project;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * This is a class that defines the main activity of the app
 */
public class MainActivity extends AppCompatActivity {

    private Button profile_button, application_button, created_events_button;
    private FirebaseAuth mAuth;
    private ListView eventList;
    private ArrayList<Event> eventsdataList;
    private ArrayList<User> usersdataList;
    private EventsArrayAdapter eventsArrayAdapter;
    Intent profile = new Intent();
    Intent my_applications = new Intent();
    Intent event_detail = new Intent();
    private Event testEvent, fakeEvent;
    private User testUser, fakeUser;
    private FirebaseFirestore db;
    private CollectionReference eventRef, userRef;
    private static final String TAG = "AnonymousAuthActivity";

    /**
     * This runs after the onStart function above
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_my_events); // Set the layout for My Events page

        // Initialize Firebase
        FirebaseApp.initializeApp(this);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Check and perform anonymous sign-in
        performAnonymousSignIn();

        eventRef = db.collection("events");
        userRef = db.collection("user");

        eventList = findViewById(R.id.listview_events);
        profile_button = findViewById(R.id.profile_button);
        application_button = findViewById(R.id.application_button);
        created_events_button = findViewById(R.id.created_events_button); // Find the Created Events button

        testEvent = new Event("testEventTitle", "use1", "2024/11/5", "2024/11/8", "nothing1", "picture1");
        fakeEvent = new Event("fakeEventTitle", "use2", "2055/11/5", "2055/11/8", "nothing2", "picture2");
        testUser = new User("testBot1", "12345678@ualberta.ca");
        fakeUser = new User("fakeBot1", "87654321@ualberta.ca");

        eventsdataList = new ArrayList<>();
        eventsArrayAdapter = new EventsArrayAdapter(this, eventsdataList);
        eventList.setAdapter(eventsArrayAdapter);
        addEvent(testEvent);
        addEvent(fakeEvent);

        usersdataList =  new ArrayList<>();

        // User clicks on the Profile button
        profile_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                profile.setClass(MainActivity.this, ProfileView.class);
                startActivity(profile);
            }
        });

        // User clicks on the Applications button
        application_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                my_applications.setClass(MainActivity.this, MyApplicationsView.class);
                startActivity(my_applications);
            }
        });

        // User clicks on the event in events list
        eventList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                event_detail.setClass(MainActivity.this, EventDetailsView.class);
                event_detail.putExtra("event name", eventsdataList.get(position).getName());
                event_detail.putExtra("event organizer", eventsdataList.get(position).getOrganizer());
                event_detail.putExtra("event description", eventsdataList.get(position).getDescription());
                event_detail.putExtra("event date", eventsdataList.get(position).getEvent_date());
                startActivity(event_detail);
            }
        });
    }

    /**
     * This used for add event into events list and also add into firebase
     * @param event
     *      The event want to add to event list
     */
    private void addEvent(Event event){
        eventsdataList.add(event);
        eventsArrayAdapter.notifyDataSetChanged();
        HashMap<String, String> eventdata = new HashMap<>();
        eventdata.put("Event Name", event.getName());
        eventdata.put("Organizer", event.getOrganizer());
        eventdata.put("Event Created Date", event.getCreated_date());
        eventdata.put("Event Date", event.getEvent_date());
        eventdata.put("Description", event.getDescription());
        eventdata.put("Picture", event.getPicture());
        eventdata.put("Event ID", event.getName()+event.getOrganizer()+event.getEvent_date());
        eventRef.document(event.getName()).set(eventdata);
    }

    /**
     * This performs an anonymous sign-in for new users and detects returning users
     */
    private void performAnonymousSignIn() {
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser == null) {
            // No current user, attempt to sign in anonymously
            mAuth.signInAnonymously()
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            // successful anonymous first time sign in
                            if (task.isSuccessful()) {
                                Log.d(TAG, "signInAnonymously:success");
                                FirebaseUser user = mAuth.getCurrentUser();
                                createProfile(user);  // store user in Firebase
                            }
                            // unsuccessful anonymous sign in
                            else {
                                Log.w(TAG, "signInAnonymously:failure", task.getException());
                                Toast.makeText(MainActivity.this, "Authentication failed.",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        } else {
            // Existing user detected
            Log.d(TAG, "User already signed in");
            Toast.makeText(MainActivity.this, "Successfully Signed In, User UID: " + currentUser.getUid(),
                    Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * This creates a user profile in Firebase
     */
    public void createProfile(FirebaseUser user){
        userRef = db.collection("user");
        // Create HashMap to store user's information
        HashMap<String, Object> userData = new HashMap<>();
        userData.put("full_name","");
        userData.put("email","");
        userData.put("phone_number","");
        userData.put("facility","");
        userData.put("notifications", Boolean.FALSE);
        // Create nested HashMap to store user's events
        HashMap<String, Object> eventData = new HashMap<>();  // empty initially
        userData.put("Events", eventData);
        // Store in Firebase
        userRef.document(user.getUid()).set(userData);
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