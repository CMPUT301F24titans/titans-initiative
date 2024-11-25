package com.example.titans_project;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AttendeesActivity extends AppCompatActivity {

    private RecyclerView attendeesRecyclerView;
    private AttendeesAdapter attendeesAdapter;
    private List<Attendee> attendeesList;
    private FirebaseFirestore db;
    private String eventID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendees);

        attendeesRecyclerView = findViewById(R.id.attendeesRecyclerView);
        attendeesRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        attendeesList = new ArrayList<>();
        attendeesAdapter = new AttendeesAdapter(attendeesList);
        attendeesRecyclerView.setAdapter(attendeesAdapter);

        db = FirebaseFirestore.getInstance();
        eventID = getIntent().getStringExtra("eventID");

        if (eventID == null || eventID.isEmpty()) {
            Toast.makeText(this, "Invalid event ID", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        loadAttendees();

        findViewById(R.id.returnButton).setOnClickListener(v -> finish());
    }

    private void loadAttendees() {
        DocumentReference eventRef = db.collection("events").document(eventID);
        eventRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                List<Map<String, String>> attendees = (List<Map<String, String>>) documentSnapshot.get("attendees");

                if (attendees != null && !attendees.isEmpty()) {
                    for (Map<String, String> attendeeData : attendees) {
                        String name = attendeeData.get("full_name");
                        String userID = attendeeData.get("user_id");

                        if (name != null && userID != null) {
                            attendeesList.add(new Attendee(name, userID));
                        }
                    }
                    attendeesAdapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(this, "No attendees found", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Event not found", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(e -> Toast.makeText(this, "Failed to load attendees", Toast.LENGTH_SHORT).show());
    }
}
