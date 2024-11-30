package com.example.titans_project;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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
    Intent send_notification = new Intent();
    Intent generate_lottery = new Intent();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_waitlist);

        Button sendNotification = findViewById(R.id.buttonSendNotification);
        Button generateLottery = findViewById(R.id.buttonGenerateLottery);

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

        // User clicks generate lottery button
        findViewById(R.id.buttonGenerateLottery).setOnClickListener(v -> {
            generate_lottery.setClass(WaitlistActivity.this, LotteryActivity.class);
            generate_lottery.putExtra("eventID", eventID);
            startActivity(generate_lottery);
        });

        // When sending notification, get the selected users
        findViewById(R.id.buttonSendNotification).setOnClickListener(v -> {
            send_notification.setClass(WaitlistActivity.this, SendNotification.class);
            // Retrieve list of attendees' user ids to send to next activity
            ArrayList<String> waitlist_ids = new ArrayList<>();
            if (waitlist != null && (!waitlist.isEmpty())) {
                for (Attendee attendee: waitlist) {
                    waitlist_ids.add(attendee.getUserId());
                }
            }
            send_notification.putExtra("waitlist", waitlist_ids);
            startActivity(send_notification);
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
