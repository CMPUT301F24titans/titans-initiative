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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Switch;
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
    private ListView eventList;
    private ArrayList<Event> eventsdataList;
    private ArrayList<User> usersdataList;
    private EventsArrayAdapter eventsArrayAdapter;
    private Button profile_button, application_button;
    private Switch admin_switch;
    Intent profile = new Intent();
    Intent my_applications = new Intent();
    Intent event_detail = new Intent();
    Intent admin = new Intent();
    private Event testEvent, fakeEvent;
    private User testUser, fakeUser;
    private FirebaseFirestore db;
    private CollectionReference eventRef, userRef;
    private static final String TAG = "AnonymousAuthActivity";
    private FirebaseAuth mAuth;
    private Boolean adminChecked;


    /**
     * This runs after the onStart function above
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_my_events);

        // Initialize Firebase
        FirebaseApp.initializeApp(this);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // adminChecked false by default
        adminChecked = false;

        // Check and perform anonymous sign-in
        performAnonymousSignIn();

        eventRef = db.collection("events");
        userRef = db.collection("user");

        eventList = findViewById(R.id.listview_events);
        profile_button = findViewById(R.id.profile_button);
        application_button = findViewById(R.id.application_button);
        admin_switch = findViewById(R.id.admin_mode);

        eventsdataList = new ArrayList<>();
        eventsArrayAdapter = new EventsArrayAdapter(this, eventsdataList);
        eventList.setAdapter(eventsArrayAdapter);
        usersdataList =  new ArrayList<>();

        eventRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot querySnapshots, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    Log.e(TAG, "Error fetching events: " + error.getMessage());
                    return;
                }
                if (querySnapshots != null) {
                    eventsdataList.clear();
                    for (QueryDocumentSnapshot doc: querySnapshots) {
                        String event_name = doc.getString("event_name");
                        String organizer = doc.getString("event_organizer");
                        String created_date = doc.getString("created_date");
                        String event_date = doc.getString("event_date");
                        String description = doc.getString("description");
                        Log.d(TAG, String.format("Event(%s, %s) fetched", event_name, event_date));
                        eventsdataList.add(new Event(event_name, organizer, created_date, event_date, description));
                    }
                    eventsArrayAdapter.notifyDataSetChanged();
                }
            }
        });

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

        // User clicks on the Admin mode
        admin_switch.setOnCheckedChangeListener((admin_switch, adminChecked) -> {
            if (adminChecked){
                admin.setClass(MainActivity.this, BrowseContentView.class);
                startActivity(admin);
                admin_switch.setChecked(false);
            }
        });

        // User clicks on the event in events list
        eventList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                event_detail.setClass(MainActivity.this, EventDetailView.class);
                event_detail.putExtra("event name", eventsdataList.get(position).getName());
                event_detail.putExtra("event organizer", eventsdataList.get(position).getOrganizer());
                event_detail.putExtra("event description", eventsdataList.get(position).getDescription());
                event_detail.putExtra("event date", eventsdataList.get(position).getEventDate());
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
        eventdata.put("event_name", event.getName());
        eventdata.put("organizer", event.getOrganizer());
        eventdata.put("created_date", event.getCreatedDate());
        eventdata.put("event_date", event.getEventDate());
        eventdata.put("description", event.getDescription());
        eventdata.put("picture", event.getPicture());
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
    }
}