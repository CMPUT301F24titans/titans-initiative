package com.example.titans_project;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.util.UUID;


public class EventDetailView extends AppCompatActivity {
    private Button return_button, apply_button, viewWaitlistButton, viewAttendeeButton, viewLotteryButton;
    private TextView name, organizer, description, date, application_limit;
    private CheckBox geolocation;
    private String user_type, picture_name;
    private FirebaseFirestore db;
    private static final String TAG = "eventDeletion";
    private Integer default_applicant_limit = 10000;
    Intent view_attendees = new Intent();
    Intent view_waitList = new Intent();
    Intent edit_event = new Intent();
    private ImageView picture;
    private StorageReference storageReference;
    private Event event = new Event(null, null, null, null, null, null, null, null, null);

    /**
     * Called when activity starts, create all activity objects here
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.fragment_event_details);

        // Initialize storage
        storageReference = FirebaseStorage.getInstance().getReference("event image");

        // Initialize Firebase
        FirebaseApp.initializeApp(this);
        db = FirebaseFirestore.getInstance();

        // Initialize Objects in layout
        name = findViewById(R.id.event_name);
        organizer = findViewById(R.id.organizer);
        picture = findViewById(R.id.profile_pic);
        date = findViewById(R.id.event_date);
        description = findViewById(R.id.event_description);
        application_limit = findViewById(R.id.event_applicant_limit);
        geolocation = findViewById(R.id.checkbox_geolocation);
        return_button = findViewById(R.id.button_return);
        apply_button = findViewById(R.id.button_apply);
        viewWaitlistButton = findViewById(R.id.viewWaitlistButton);
        viewAttendeeButton = findViewById(R.id.viewAttendeesButton);
        viewLotteryButton = findViewById(R.id.viewLotteryButton);

        // get the user type
        user_type = getIntent().getStringExtra("viewer");
        // admin user viewing
        if ("admin".equals(user_type)){
            apply_button.setText("Delete Event");  // apply button becomes delete button for admin
            geolocation.setVisibility(View.GONE);  // remove geolocation option for users already enrolled/applied
            viewWaitlistButton.setVisibility(View.GONE);
            viewAttendeeButton.setVisibility(View.GONE);
        }
        // already enrolled/applied entrant viewing
        else if ("enrolled".equals(user_type)) {
            apply_button.setVisibility(View.GONE);  // remove button for entrant users already enrolled/applied
            geolocation.setVisibility(View.GONE);  // remove geolocation option for users already enrolled/applied
            viewWaitlistButton.setVisibility(View.GONE);
            viewAttendeeButton.setVisibility(View.GONE);
        }

        // organizer viewing their own event
        else if ("organizer".equals(user_type)) {
            apply_button.setText("Edit Event");
            geolocation.setVisibility(View.GONE);
        }

        // get the event data
        get_event();
        // display all event data
        name.setText(event.getName());
        db.collection("user")
                .document(event.getOrganizerID())
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            organizer.setText(document.getString("full_name"));
                            Log.d("Firestore", "get organizer name");
                        } else {
                            Log.d("Firestore", "No such document");
                        }
                    } else {
                        Log.w("Firestore", "Error getting document.", task.getException());
                    }
                });
        picture_name = event.getPicture();
        if (picture_name != null){
            displayImage(picture_name);
        }
        else{
            Toast.makeText(EventDetailView.this, "no image found",
                    Toast.LENGTH_SHORT).show();
        }
        // Only display description if user set one
        if (!(event.getDescription().isEmpty())){
            description.setText(event.getDescription());
        }
        date.setText(event.getEventDate());
        // Display the limit event if user set one
        Object eventLimit = getIntent().getIntExtra("event limit", default_applicant_limit); // defaultLimit is a fallback value if "event limit" is not found
        Log.w(TAG, "applicantLimit (from EventDetailView): " + eventLimit);
        if (eventLimit != default_applicant_limit) {
            application_limit.setText("The limit of applicants is " + eventLimit);
        }

        viewLotteryButton.setOnClickListener(v -> {
            Intent intent = new Intent(EventDetailView.this, ViewLotteryActivity.class);
            intent.putExtra("eventID", event.getEventID());
            startActivity(intent);
        });

        viewWaitlistButton.setOnClickListener(new View.OnClickListener() {
            /**
             * User click on view wait list button, go to my wait list page
             * @param view
             */
            @Override
            public void onClick(View view) {
                view_waitList.setClass(EventDetailView.this, WaitlistActivity.class);
                view_waitList.putExtra("eventID", event.getEventID());
                startActivity(view_waitList);
            }
        });

        viewAttendeeButton.setOnClickListener(new View.OnClickListener() {
            /**
             * User click on view attendee button, go to view attendees page
             * @param view
             */
            @Override
            public void onClick(View view) {
                view_attendees.setClass(EventDetailView.this, AttendeesActivity.class);
                view_attendees.putExtra("eventID", event.getEventID());
                startActivity(view_attendees);
            }
        });

        apply_button.setOnClickListener(new View.OnClickListener() {
            /**
             * User clicks on the apply/delete/view attendees button
             * @param view
             */
            @Override
            public void onClick(View view) {
                // admin clicks delete event button
                if ("admin".equals(user_type)){
                    // delete event
                    deleteEvent(getIntent().getStringExtra("eventID"));
                }
                // organizer clicks view attendees button
                else if ("organizer".equals(user_type)){
                    edit_event.setClass(EventDetailView.this, CreateEventView.class);
                    editEvent();
                    startActivity(edit_event);
                }
                // entrant clicks enroll button
                else {
                    // enroll entrant into event
                }
            }
        });

        return_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    /**
     * set up the event with the data passed by previous page
     */
    private void get_event(){
        event.setEventID(getIntent().getStringExtra("event ID"));
        event.setName(getIntent().getStringExtra("event name"));
        event.setFacilityName(getIntent().getStringExtra("event facility"));
        event.setCreated_date(getIntent().getStringExtra("event create date"));
        event.setEvent_date(getIntent().getStringExtra("event date"));
        event.setDescription(getIntent().getStringExtra("event description"));
        event.setOrganizerID(getIntent().getStringExtra("event organizer"));
        event.setPicture(getIntent().getStringExtra("event image"));
    }

    /**
     * Retrieve image from firebase storage and display it
     * @param picture_name
     */
    private void displayImage(String picture_name){
        StorageReference imageRef = storageReference.child(picture_name);
        final File localFile = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), picture_name+".jpg");
        imageRef.getFile(localFile)
                .addOnSuccessListener(taskSnapshot -> {
                    Bitmap bitmap = BitmapFactory.decodeFile(localFile.getAbsolutePath());
                    picture.setImageBitmap(bitmap);
                }).addOnFailureListener(e -> {
                    Toast.makeText(EventDetailView.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    /**
     * Deletes event from Firebase
     * @param event_id
     *  ID of the event to delete.
     */
    private void deleteEvent(String event_id){
        db.collection("events").document(event_id).delete().addOnCompleteListener(this, new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                // successful deletion of event
                if (task.isSuccessful()) {
                    Log.d(TAG, "deletedEvent:success");
                    Toast.makeText(EventDetailView.this, "Successfully deleted event",
                            Toast.LENGTH_SHORT).show();
                }
                // unsuccessful deletion of event
                else {
                    Log.w(TAG, "deletedEvent:failure", task.getException());
                    Toast.makeText(EventDetailView.this, "Failed to delete event",
                            Toast.LENGTH_SHORT).show();
                }
                finish();
            }
        });
    }

    /**
     * Store the event information want to pass the edit event page
     */
    private void editEvent(){
        edit_event.putExtra("event ID", event.getEventID());
        edit_event.putExtra("event name", event.getName());
        edit_event.putExtra("event facility", event.getFacilityName());
        edit_event.putExtra("event create date", event.getCreatedDate());
        edit_event.putExtra("event date", event.getEventDate());
        edit_event.putExtra("event description", event.getDescription());
        edit_event.putExtra("event organizer", event.getOrganizerID());
        edit_event.putExtra("event image", event.getPicture());
        edit_event.putExtra("viewer", "edit");
    }
}