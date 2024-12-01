package com.example.titans_project;

import static com.example.titans_project.R.id.dropdown_menu;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;


public class EventDetailView extends AppCompatActivity {
    private Button return_button, apply_button;
    private TextView name, organizer, description, date, application_limit;
    private CheckBox geolocation;
    private String user_type, picture_name;
    private FirebaseFirestore db;
    private static final String TAG = "eventDeletion";
    private Integer default_applicant_limit = 10000;
    Intent view_attendees = new Intent();
    Intent view_waitList = new Intent();
    Intent edit_event = new Intent();
    Intent view_lottery = new Intent();
    Intent view_cancelled = new Intent();
    Intent send_notification = new Intent();
    private ImageView picture;
    private StorageReference storageReference;
    private Event event = new Event(null, null, null, null, null, null, null, null, null, null);

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
        final ImageButton dropdown_button = findViewById(R.id.dropdown_menu);

        // get the user type
        user_type = getIntent().getStringExtra("viewer");
        // admin user viewing
        if ("admin".equals(user_type)){
            apply_button.setText("Delete Event");  // apply button becomes delete button for admin
            geolocation.setVisibility(View.GONE);  // remove geolocation option for users already enrolled/applied
            dropdown_button.setVisibility(View.GONE);
        }
        // already enrolled/applied entrant viewing
        else if ("enrolled".equals(user_type)) {
            apply_button.setVisibility(View.GONE);  // remove button for entrant users already enrolled/applied
            geolocation.setVisibility(View.GONE);  // remove geolocation option for users already enrolled/applied
            dropdown_button.setVisibility(View.GONE);
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

        final PopupMenu dropDownMenu = new PopupMenu(EventDetailView.this, dropdown_button);
        final Menu menu = dropDownMenu.getMenu();

        // add your items:
        menu.add(0, 0, 0, "View Waitlist");
        menu.add(0, 1, 0, "View Lottery");
        menu.add(0, 2, 0, "View Attendees");
        menu.add(0, 3, 0, "View Cancelled Users");
        menu.add(0, 4, 0, "Send Notification");

        dropDownMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case 0:
                        view_waitList.setClass(EventDetailView.this, WaitlistActivity.class);
                        view_waitList.putExtra("eventID", event.getEventID());
                        startActivity(view_waitList);
                        return true;
                    case 1:
                        view_lottery.setClass(EventDetailView.this, ViewLotteryActivity.class);
                        view_lottery.putExtra("eventID", event.getEventID());
                        startActivity(view_lottery);
                        return true;
                    case 2:
                        view_attendees.setClass(EventDetailView.this, AttendeesActivity.class);
                        view_attendees.putExtra("eventID", event.getEventID());
                        startActivity(view_attendees);
                        return true;
                    case 3:
                        view_cancelled.setClass(EventDetailView.this, CancelledEntrantsActivity.class);
                        view_cancelled.putExtra("eventID", event.getEventID());
                        startActivity(view_cancelled);
                        return true;
                    case 4:
                        send_notification.setClass(EventDetailView.this, SendNotification.class);
                        send_notification.putExtra("eventID", event.getEventID());
                        startActivity(send_notification);
                        return true;
                }
                return false;
            }
        });

        dropdown_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dropDownMenu.show();
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
                    Toast.makeText(EventDetailView.this, "event detail page", Toast.LENGTH_SHORT).show();
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