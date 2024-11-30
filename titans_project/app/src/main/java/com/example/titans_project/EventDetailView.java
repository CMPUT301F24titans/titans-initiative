package com.example.titans_project;

import android.content.Intent;
import android.os.Bundle;
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
import com.google.firebase.firestore.FirebaseFirestore;

import org.w3c.dom.Text;

public class EventDetailView extends AppCompatActivity {
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

        // Initialize TextViews
        TextView name = findViewById(R.id.event_name);
        TextView organizer = findViewById(R.id.organizer);
        TextView description = findViewById(R.id.event_description);
        TextView date = findViewById(R.id.event_date);
        TextView application_limit = findViewById(R.id.event_applicant_limit);

        // Initialize Buttons
        Button return_button = findViewById(R.id.button_return);
        Button apply_button = findViewById(R.id.button_apply);
        Button view_waitlist_button = findViewById(R.id.button_view_waitlist);
        Button remove_poster_button = findViewById(R.id.button_remove_poster);

        // Initialize ImageView
        ImageView event_poster = findViewById(R.id.event_poster);

        // Initialize Checkbox
        CheckBox geolocation = findViewById(R.id.checkbox_geolocation);

        // admin user viewing
        if ("admin".equals(user_type)){
            apply_button.setText("Delete Event");  // apply button becomes delete button for admin
            geolocation.setVisibility(View.GONE);  // remove geolocation option for users already enrolled/applied
            view_waitlist_button.setVisibility(View.GONE);
        }
        // already enrolled/applied entrant viewing
        else if ("enrolled".equals(user_type)) {
            apply_button.setVisibility(View.GONE);  // remove button for entrant users already enrolled/applied
            geolocation.setVisibility(View.GONE);  // remove geolocation option for users already enrolled/applied
            view_waitlist_button.setVisibility(View.GONE);  // remove view waitlist button for entrants
            remove_poster_button.setVisibility(View.GONE);  // do not allow entrants to delete event posters
        }

        // organizer viewing their own event
        else if ("organizer".equals(user_type)) {
            apply_button.setText("View Attendees");
            remove_poster_button.setText("Edit Poster");  // remove poster button becomes edit poster button for organizers
        }

        name.setText(getIntent().getStringExtra("event name"));
        organizer.setText("Organized by " + getIntent().getStringExtra("event organizer"));
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

        remove_poster_button.setOnClickListener(new View.OnClickListener() {
            /**
             * User clicks on the Remove/Edit Poster button, allow organizers to edit poster and allow admin to delete poster
             * @param view
             */
            @Override
            public void onClick(View view) {
                // Delete the event poster
                if ("admin".equals(user_type)) {
                    // First delete locally
                    event_poster.setImageDrawable(null);
                    // Get the event ID from the intent
                    String eventID = getIntent().getStringExtra("eventID");
                    if (eventID != null && !eventID.isEmpty()) {
                        // Update the "picture" field to null
                        db.collection("events").document(eventID)
                                .update("picture", null)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        Log.d("FirestoreUpdate", "Picture Field updated successfully");
                                        Toast.makeText(EventDetailView.this, "Poster successfully deleted", Toast.LENGTH_SHORT).show();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.w("FirestoreUpdate", "Error updating document", e);
                                        Toast.makeText(EventDetailView.this, "Error deleting poster", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    } else {
                        Log.w("FirestoreUpdate", "Event ID is null or empty");
                    }
                }
                // Edit the event poster
                else if ("organizer".equals(user_type)) {

                }
            }
        });

        /**
         * User clicks on the View Waitlist button
         */
        view_waitlist_button.setOnClickListener(v -> {
            Intent intent = new Intent(EventDetailView.this, WaitlistActivity.class);
            intent.putExtra("eventID", getIntent().getStringExtra("eventID"));
            startActivity(intent);
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