package com.example.titans_project;

import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class WaitlistActivity extends AppCompatActivity {

    private RecyclerView waitlistRecyclerView;
    private WaitlistAdapter waitlistAdapter;
    private List<Attendee> waitlist;
    private FirebaseFirestore db;
    private String eventID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_waitlist);

        Button sendNotification = findViewById(R.id.buttonSendNotification);

        waitlistRecyclerView = findViewById(R.id.waitlistRecyclerView);
        waitlistRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        db = FirebaseFirestore.getInstance();
        waitlist = new ArrayList<>();
        eventID = getIntent().getStringExtra("eventID");

        if (eventID == null || eventID.isEmpty()) {
            Toast.makeText(this, "Invalid event ID", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        waitlistAdapter = new WaitlistAdapter(this, waitlist, eventID);
        waitlistRecyclerView.setAdapter(waitlistAdapter);

        loadWaitlist();

        findViewById(R.id.returnButton).setOnClickListener(v -> finish());

        // When sending notification, get the selected users
        findViewById(R.id.buttonSendNotification).setOnClickListener(v -> {
            ArrayList<String> users = new ArrayList<>();
            if (waitlist != null && !(waitlist.isEmpty())) {
                for (Attendee attendee : waitlist) {
                    users.add(attendee.getUserId());
                }
                // Pass all users to the SendNotification fragment
                SendNotification sendNotificationFragment = SendNotification.newInstance(users);
                sendNotificationFragment.show(getSupportFragmentManager(), "SendNotification");
            }
            else {
                Toast.makeText(WaitlistActivity.this, "No recipients for notification",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadWaitlist() {
        db.collection("events").document(eventID)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    waitlist.clear();
                    List<Map<String, String>> firebaseWaitlist = (List<Map<String, String>>) documentSnapshot.get("waitlist");

                    if (firebaseWaitlist != null) {
                        for (Map<String, String> attendee : firebaseWaitlist) {
                            waitlist.add(new Attendee(attendee.get("full_name"), attendee.get("user_id"), ""));
                        }
                    }
                    waitlistAdapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Failed to load waitlist.", Toast.LENGTH_SHORT).show());
    }
}
