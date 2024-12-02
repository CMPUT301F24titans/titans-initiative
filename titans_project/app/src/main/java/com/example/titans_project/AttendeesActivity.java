package com.example.titans_project;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * This activity displays the list of attendees for a particular event.
 */
public class AttendeesActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private TextView emptyTextView;
    private AttendeesAdapter attendeesAdapter;
    private List<Attendee> attendeesList;
    private FirebaseFirestore db;
    private String eventID;
    private Button back;
    private ImageButton sendNotification;
    Intent send_notification = new Intent();

    /**
     * Initializes the activity, sets up the layout, and loads the attendees for the event.
     *
     * @param savedInstanceState the previous saved instance state, if any.
     */
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_attendees);

        // Initialize the objects in layout
        recyclerView = findViewById(R.id.attendeesRecyclerView);
        emptyTextView = findViewById(R.id.emptyTextView);
        back = findViewById(R.id.returnButton);
        sendNotification = findViewById(R.id.buttonSendNotification);

        // Initialize the firebase
        db = FirebaseFirestore.getInstance();

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        attendeesList = new ArrayList<>();
        attendeesAdapter = new AttendeesAdapter(this, attendeesList);
        recyclerView.setAdapter(attendeesAdapter);

        // Get the event ID from the Intent
        eventID = getIntent().getStringExtra("eventID");
        if (eventID == null || eventID.isEmpty()) {
            Toast.makeText(this, "Invalid event ID", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Load the attendees for the event
        loadAttendees();

        // Set up the send notification button
        sendNotification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                send_notification.setClass(AttendeesActivity.this, SendNotification.class);
                send_notification.putExtra("eventID", eventID);
                startActivity(send_notification);
            }
        });

        // Set up the back button to return to the previous screen
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    /**
     * Loads the list of attendees for the event from Firebase Firestore.
     */
    private void loadAttendees() {
        DocumentReference eventRef = db.collection("events").document(eventID);
        eventRef.get().addOnSuccessListener(documentSnapshot -> {
            attendeesList.clear();
            try {
                // Retrieve the list of attendees from the document
                List<Map<String, String>> attendees = (List<Map<String, String>>) documentSnapshot.get("attendees");
                if (attendees != null && !attendees.isEmpty()) {
                    // Populate the attendees list with data
                    for (Map<String, String> attendeeData : attendees) {
                        String name = attendeeData.get("full_name");
                        String userID = attendeeData.get("user_id");
                        String email = attendeeData.get("email");
                        attendeesList.add(new Attendee(name, userID, email));
                    }
                    attendeesAdapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(this, "No attendees found", Toast.LENGTH_SHORT).show();
                }
            } catch (ClassCastException e) {
                Toast.makeText(this, "Data type mismatch", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(e -> {
            Toast.makeText(this, "Error loading attendees: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        });
    }
}
