package com.example.titans_project;

import androidx.annotation.NonNull;
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
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * This is a class that defines the main activity of the app
 */
public class MainActivity extends AppCompatActivity {
    private ListView eventList;
    private ArrayList<Event> dataList;
    private EventsArrayAdapter eventsArrayAdapter;
    private Button profile_button, application_button;
    Intent profile = new Intent();
    Intent my_applications = new Intent();
    Intent create_profile = new Intent();

    private FirebaseFirestore db;
    private CollectionReference eventRef;
    private CollectionReference userRef;
    private static final String TAG = "AnonymousAuthActivity";
    private FirebaseAuth mAuth;


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

        // Check and perform anonymous sign-in
        performAnonymousSignIn();

        eventRef = db.collection("events");

        eventList = findViewById(R.id.listview_events);
        profile_button = findViewById(R.id.profile_button);
        application_button = findViewById(R.id.application_button);

        String[] events = {"Edmonton", "Vancouver", "Toronto"};
        String[] date = {"2024/11/5", "2024/12/5", "2025/11/5"};
        dataList = new ArrayList<>();
        for (int i = 0; i < events.length; i++) {
            dataList.add(new Event(events[i], "organizer", "created date", date[i], "description", "picture"));
        }
        eventsArrayAdapter = new EventsArrayAdapter(this, dataList);
        eventList.setAdapter(eventsArrayAdapter);

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
    private void createProfile(FirebaseUser user){
        userRef = db.collection("user");
        // Create HashMap to store user's information
        HashMap<String, Object> userData = new HashMap<>();
        userData.put("full_name","");
        userData.put("email","");
        userData.put("phone_number","");
        userData.put("facility","");
        // Create nested HashMap to store user's events
        HashMap<String, Object> eventData = new HashMap<>();  // empty initially
        userData.put("Events", eventData);
        // Store in Firebase
        userRef.document(user.getUid()).set(userData);
    }
}