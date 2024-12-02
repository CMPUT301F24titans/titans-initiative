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
 * This is a class that defines the AttendeesActivity
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

        // get the name of event
        eventID = getIntent().getStringExtra("eventID");
        if (eventID == null || eventID.isEmpty()) {
            Toast.makeText(this, "Invalid event ID", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // load the attendees
        loadAttendees();

        sendNotification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                send_notification.setClass(AttendeesActivity.this, SendNotification.class);
                send_notification.putExtra("eventID", eventID);
                startActivity(send_notification);
            }
        });

        // Click the return button
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void loadAttendees() {
        DocumentReference eventRef = db.collection("events").document(eventID);
        eventRef.get().addOnSuccessListener(documentSnapshot -> {
            attendeesList.clear();
            try {
                List<Map<String, String>> attendees = (List<Map<String, String>>) documentSnapshot.get("attendees");
                if (attendees != null && !attendees.isEmpty()) {
                    for (Map<String, String> attendeeData : attendees) {
                        String name = attendeeData.get("full_name");
                        String userID = attendeeData.get("user_id");
                        String email = attendeeData.get("email"); //
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
