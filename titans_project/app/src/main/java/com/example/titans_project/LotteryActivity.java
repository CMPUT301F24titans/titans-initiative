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
    private Integer lotterySize;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lottery);

        db = FirebaseFirestore.getInstance();
        eventID = getIntent().getStringExtra("eventID");
        lotterySize = getIntent().getIntExtra("lotterySize", 0);

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
            if (waitlist == null || waitlist.isEmpty()) {
                Toast.makeText(this, "Waitlist is empty. No lottery needed.", Toast.LENGTH_SHORT).show();
                finish();
                return;
            }
            if (lotterySize == null || waitlist.size() <= lotterySize) {
                Toast.makeText(this, "Applicants are fewer than or equal to the limit. No lottery needed.", Toast.LENGTH_SHORT).show();
                finish();
                return;
            }

            // Shuffle and select applicants
            List<Map<String, String>> selectedLottery = new ArrayList<>();
            Collections.shuffle(waitlist);
            for (int i = 0; i < lotterySize; i++) {
                selectedLottery.add(waitlist.get(i));
            }

            // Transaction to update event and user waitlists/lotteries
            db.runTransaction(transaction -> {
                DocumentSnapshot eventSnapshot = transaction.get(db.collection("events").document(eventID));

                // Check for null data on the event
                if (!eventSnapshot.exists()) {
                    Log.e("Lottery", "Event snapshot does not exist.");
                    return null;
                }

                // Update event's waitlist and lottery
                List<Map<String, String>> currentWaitlist = (List<Map<String, String>>) eventSnapshot.get("waitlist");
                if (currentWaitlist == null) currentWaitlist = new ArrayList<>();
                currentWaitlist.removeAll(selectedLottery);

                List<Map<String, String>> currentLottery = (List<Map<String, String>>) eventSnapshot.get("lottery");
                if (currentLottery == null) currentLottery = new ArrayList<>();
                currentLottery.addAll(selectedLottery);

                // Read all users' data (before performing writes)
                List<DocumentSnapshot> userSnapshots = new ArrayList<>();
                for (Map<String, String> applicant : selectedLottery) {
                    String userID = applicant.get("user_id");
                    if (userID != null) {
                        DocumentSnapshot userSnapshot = transaction.get(db.collection("user").document(userID));
                        userSnapshots.add(userSnapshot);
                    }
                }

                // Perform all updates (writes) after the reads
                for (int i = 0; i < selectedLottery.size(); i++) {
                    Map<String, String> applicant = selectedLottery.get(i);
                    String userID = applicant.get("user_id");
                    if (userID != null) {
                        DocumentSnapshot userSnapshot = userSnapshots.get(i);
                        if (userSnapshot.exists()) {
                            List<String> userWaitlist = (List<String>) userSnapshot.get("applications");
                            if (userWaitlist == null) userWaitlist = new ArrayList<>();
                            userWaitlist.remove(eventID);

                            List<String> userLottery = (List<String>) userSnapshot.get("accepted");
                            if (userLottery == null) userLottery = new ArrayList<>();
                            userLottery.add(eventID);

                            // Update user data in the transaction
                            transaction.update(db.collection("user").document(userID), "applications", userWaitlist);
                            transaction.update(db.collection("user").document(userID), "accepted", userLottery);
                        } else {
                            Log.e("Lottery", "User snapshot does not exist for userID: " + userID);
                        }
                    } else {
                        Log.e("Lottery", "userID is null for applicant: " + applicant);
                    }
                }

                // Perform the event updates
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
