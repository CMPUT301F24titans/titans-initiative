package com.example.titans_project;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class EventDetailView extends AppCompatActivity {
    private Button return_button, apply_button;
    private TextView name, organizer, description, date, application_limit;
    CheckBox geolocation;
    private String user_type;
    private FirebaseFirestore db;
    private static final String TAG = "eventDeletion";
    private Integer default_applicant_limit = 10000;
    Intent view_attendees = new Intent();

    /**
     * Called when activity starts, create all activity objects here
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.fragment_event_details);

        // Initialize Firebase
        FirebaseApp.initializeApp(this);
        db = FirebaseFirestore.getInstance();

        user_type = getIntent().getStringExtra("viewer");

        name = findViewById(R.id.event_name);
        organizer = findViewById(R.id.organizer);
        description = findViewById(R.id.event_description);
        date = findViewById(R.id.event_date);
        return_button = findViewById(R.id.button_return);
        apply_button = findViewById(R.id.button_apply);
        geolocation = findViewById(R.id.checkbox_geolocation);
        application_limit = findViewById(R.id.event_applicant_limit);

        // admin user viewing
        if ("admin".equals(user_type)){
            apply_button.setText("Delete Event");  // apply button becomes delete button for admin
            geolocation.setVisibility(View.GONE);  // remove geolocation option for users already enrolled/applied
        }
        // already enrolled/applied entrant viewing
        else if ("enrolled".equals(user_type)) {
            apply_button.setVisibility(View.GONE);  // remove button for entrant users already enrolled/applied
            geolocation.setVisibility(View.GONE);  // remove geolocation option for users already enrolled/applied
        }

        // organizer viewing their own event
        else if ("organizer".equals(user_type)) {
            apply_button.setText("View Attendees");
        }

        name.setText(getIntent().getStringExtra("event name"));
        organizer.setText(getIntent().getStringExtra("event organizer"));
        // Only display description if user set one
        if (!(getIntent().getStringExtra("event description").isEmpty())){
            description.setText(getIntent().getStringExtra("event description"));
        }
        date.setText(getIntent().getStringExtra("event date"));
        // Display the limit event if user set one
        int eventLimit = getIntent().getIntExtra("event limit", default_applicant_limit); // defaultLimit is a fallback value if "event limit" is not found
        Log.w(TAG, "applicantLimit (from EventDetailView): " + eventLimit);
        if (eventLimit != default_applicant_limit) {
            application_limit.setText("The limit of applicants is " + eventLimit);
        }

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
                    // go to view attendees
                    view_attendees.setClass(EventDetailView.this, AttendeesActivity.class);
                    view_attendees.putExtra("eventID", getIntent().getStringExtra("eventID"));
                    startActivity(view_attendees);
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
}
