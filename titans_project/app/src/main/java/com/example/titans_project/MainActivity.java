package com.example.titans_project;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * This is a class that defines the main activity of the app
 */
public class MainActivity extends AppCompatActivity {
    private ListView eventList;
    private ArrayList<Event> eventsdataList;
    private ArrayList<User> usersdataList;
    private EventsArrayAdapter eventsArrayAdapter;
    private Button profile_button, application_button, created_events_button, scan_button;
    private ImageButton notifications_button;
    private TextView notification_counter;
    private Switch admin_switch;
    Intent profile = new Intent();
    Intent my_applications = new Intent();
    Intent event_detail = new Intent();
    Intent admin = new Intent();
    Intent created_event = new Intent();
    Intent notifications = new Intent();
    Intent scan =  new Intent();
    private FirebaseFirestore db;
    private CollectionReference eventRef, userRef;
    private static final String TAG = "AnonymousAuthActivity";
    private FirebaseAuth mAuth;
    private Boolean adminChecked;
    Integer default_applicant_limit = 10000;

    /**
     * This is called as soon as the activity begins
     */
    @Override
    protected void onStart() {
        super.onStart();
        // Get the current user
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            // Check for notifications only if the user is signed in
            checkForNotifications(db.collection("user").document(currentUser.getUid()));
        }
    }

    /**
     * This is called after the activity is resumed (after being in background/another activity finishes)
     */
    @Override
    protected void onResume() {
        super.onResume();
        // Get the current user
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            // Check for notifications only if the user is signed in
            checkForNotifications(db.collection("user").document(currentUser.getUid()));
        }
    }

    /**
     * This runs after the onStart function above
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_enrolled_events);

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


        notifications_button = findViewById(R.id.notifications_button);
        created_events_button = findViewById(R.id.created_events_button);
        application_button = findViewById(R.id.application_button);
        profile_button = findViewById(R.id.profile_button);
        scan_button = findViewById(R.id.scan_button);
        notification_counter = findViewById(R.id.notifications_counter);
        eventList = findViewById(R.id.listview_events);
        admin_switch = findViewById(R.id.admin_mode);

        eventsdataList = new ArrayList<>();
        eventsArrayAdapter = new EventsArrayAdapter(this, eventsdataList);
        eventList.setAdapter(eventsArrayAdapter);
        usersdataList =  new ArrayList<>();
        eventRef.addSnapshotListener(new EventListener<QuerySnapshot>() {

            /**
             * Display all of user's enrolled events
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
                        String event_id = doc.getString("eventID");
                        String event_name = doc.getString("name");
                        String facility_name = doc.getString("facilityName");
                        String created_date = doc.getString("createdDate");
                        String event_date = doc.getString("eventDate");
                        String description = doc.getString("description");
                        String organizer_id = doc.getString("organizerID");
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
                        eventsdataList.add(new Event(event_id, event_name, facility_name, created_date, event_date, description, organizer_id, picture, applicant_limit));
                    }
                    eventsArrayAdapter.notifyDataSetChanged();
                }
            }
        });

        notifications_button.setOnClickListener(new View.OnClickListener() {
            /**
             * User clicks on the Notifications button, bring user to NotificationView
             * @param view
             */
            @Override
            public void onClick(View view) {
                notifications.setClass(MainActivity.this, NotificationView.class);
                startActivity(notifications);
            }
        });

        application_button.setOnClickListener(new View.OnClickListener() {
            /**
             * User clicks on the Applications button, bring user to MyApplicationsView
             * @param view
             */
            @Override
            public void onClick(View view) {
                my_applications.setClass(MainActivity.this, MyApplicationsView.class);
                startActivity(my_applications);
            }
        });

        profile_button.setOnClickListener(new View.OnClickListener() {
            /**
             * User clicks on the Profile button, bring user to ProfileView
             * @param view
             */
            @Override
            public void onClick(View view) {
                profile.setClass(MainActivity.this, ProfileView.class);
                startActivity(profile);
            }
        });

        scan_button.setOnClickListener(new View.OnClickListener() {
            /**
             * User clicks on the Scan button, bring user to Scan page
             * @param view
             */
            @Override
            public void onClick(View view) {
                scan.setClass(MainActivity.this, QRScannerActivity.class);
                startActivity(scan);
            }
        });

        /**
         * User clicks on the Admin mode, switch the app to admin mode
         */
        admin_switch.setOnCheckedChangeListener((admin_switch, adminChecked) -> {
            if (adminChecked){
                admin.setClass(MainActivity.this, BrowseContentView.class);
                startActivity(admin);
                admin_switch.setChecked(false);
            }
        });

        created_events_button.setOnClickListener(new View.OnClickListener() {
            /**
             * User clicks on Create Event button, bring user to CreatedEventsView
             * @param view
             */
            @Override
            public void onClick(View view) {
                created_event.setClass(MainActivity.this, MyCreatedEventsView.class);
                startActivity(created_event);
            }
        });

        eventList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            /**
             * User clicks on the event in events list, bring user to EventDetailView
             * @param adapterView
             * @param view
             * @param position
             * @param l
             */
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Event clickedEvent = eventsdataList.get(position);
                event_detail.setClass(MainActivity.this, EventDetailView.class);
                event_detail.putExtra("event ID", clickedEvent.getEventID());
                event_detail.putExtra("event name", clickedEvent.getName());
                event_detail.putExtra("event facility", clickedEvent.getFacilityName());
                event_detail.putExtra("event create date", clickedEvent.getCreatedDate());
                event_detail.putExtra("event date", clickedEvent.getEventDate());
                event_detail.putExtra("event description", clickedEvent.getDescription());
                event_detail.putExtra("event organizer", clickedEvent.getOrganizerID());
                event_detail.putExtra("event image", clickedEvent.getPicture());
                event_detail.putExtra("event limit", clickedEvent.getApplicantLimit());
                Log.w(TAG, "applicantLimit (when clicked on from MainActivity): " + eventsdataList.get(position).getApplicantLimit());
                event_detail.putExtra("viewer", "enrolled");
                startActivity(event_detail);
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
            // check if user exists in Firebase
            db.collection("user").document(currentUser.getUid())
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            if (!task.getResult().exists()) {
                                // User document does not exist, create profile
                                createProfile(currentUser);
                            }
                        } else {
                            Log.e(TAG, "Error checking user existence", task.getException());
                        }
                    });
            // User does not exist in Firebase, create new user profile for them
            db.collection("user").document(currentUser.getUid()).update("user_id", currentUser.getUid());
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
        userData.put("user_id", user.getUid());
        userData.put("profile_pic", "");
        // Store ArrayList for user's notifications
        userData.put("notification_list", new ArrayList<>());
        // Create nested HashMap to store user's events
        userData.put("applications", new ArrayList<>());
        userData.put("accepted", new ArrayList<>());
        userData.put("enrolled", new ArrayList<>());
        // Store in Firebase
        userRef.document(user.getUid()).set(userData);
    }


    /**
     * Checks if the user has any notifications
     * @param userRef
     */
    private void checkForNotifications(DocumentReference userRef) {
        userRef.get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        // Retrieve the notification_list as an ArrayList
                        ArrayList<Notification> notificationList = (ArrayList<Notification>) documentSnapshot.get("notification_list");
                        // The list is not empty
                        if (notificationList != null && !notificationList.isEmpty()) {
                            notification_counter.setText("Notifications: " + notificationList.size());
                            notification_counter.setTextColor(getResources().getColor(R.color.light_blue));
                        }
                        else {
                            // The list is either null or empty
                            notification_counter.setText("Notifications: 0");
                            notification_counter.setTextColor(getResources().getColor(R.color.light_gray));
                            Log.d(TAG, "Notification list is empty.");
                        }
                    } else {
                        // Document doesn't exist
                        Log.d(TAG, "Document does not exist.");
                    }
                })
                .addOnFailureListener(e -> {
                    // Handle error
                    Log.w(TAG, "Failed to retrieve notification list", e);
                });
    }

}