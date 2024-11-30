package com.example.titans_project;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LotteryActivity extends AppCompatActivity {

    private FirebaseFirestore db;
    private String eventID;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lottery);

        db = FirebaseFirestore.getInstance();
        eventID = getIntent().getStringExtra("eventID");

        // check for non-null or empty eventID
        if (eventID == null || eventID.isEmpty()) {
            Toast.makeText(this, "Event ID not found.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        startLottery();
    }

    private void startLottery() {
        db.collection("events").document(eventID).get().addOnSuccessListener(documentSnapshot -> {
            if (!documentSnapshot.exists()) {
                Toast.makeText(this, "Event not found.", Toast.LENGTH_SHORT).show();
                finish();
                return;
            }

            // Validate event object
            Event event = documentSnapshot.toObject(Event.class);
            if (event == null) {
                Toast.makeText(this, "Failed to load event details.", Toast.LENGTH_SHORT).show();
                finish();
                return;
            }

            // Validate waitlist and applicant limit
            List<Map<String, String>> waitlist = event.getWaitlist();
            Integer applicantLimit = event.getApplicantLimit();
            if (waitlist == null || waitlist.isEmpty()) {
                Toast.makeText(this, "Waitlist is empty. No lottery needed.", Toast.LENGTH_SHORT).show();
                finish();
                return;
            }
            if (applicantLimit == null || waitlist.size() <= applicantLimit) {
                Toast.makeText(this, "Applicants are fewer than or equal to the limit. No lottery needed.", Toast.LENGTH_SHORT).show();
                finish();
                return;
            }

            // Shuffle and select applicants
            List<Map<String, String>> selectedLottery = new ArrayList<>();
            Collections.shuffle(waitlist);
            for (int i = 0; i < applicantLimit; i++) {
                selectedLottery.add(waitlist.get(i));
            }

            // Transaction to update waitlist and lottery
            db.runTransaction(transaction -> {
                DocumentSnapshot snapshot = transaction.get(db.collection("events").document(eventID));

                // Safe cast and validate data
                List<Map<String, String>> currentWaitlist = (List<Map<String, String>>) snapshot.get("waitlist");
                if (currentWaitlist == null) currentWaitlist = new ArrayList<>();
                currentWaitlist.removeAll(selectedLottery);

                List<Map<String, String>> currentLottery = (List<Map<String, String>>) snapshot.get("lottery");
                if (currentLottery == null) currentLottery = new ArrayList<>();
                currentLottery.addAll(selectedLottery);

                // Update Firestore
                transaction.update(db.collection("events").document(eventID), "waitlist", currentWaitlist);
                transaction.update(db.collection("events").document(eventID), "lottery", currentLottery);

                return null;
            }).addOnSuccessListener(aVoid -> {
                Toast.makeText(this, "Lottery completed successfully!", Toast.LENGTH_SHORT).show();
                finish();
            }).addOnFailureListener(e -> {
                Toast.makeText(this, "Failed to update lottery results.", Toast.LENGTH_SHORT).show();
                Log.e("Lottery", "Transaction error", e);
            });

        }).addOnFailureListener(e -> {
            Toast.makeText(this, "Failed to fetch event data.", Toast.LENGTH_SHORT).show();
            Log.e("Lottery", "Error fetching document", e);
        });
    }

}
