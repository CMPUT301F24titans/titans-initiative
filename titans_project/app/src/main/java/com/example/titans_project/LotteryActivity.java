package com.example.titans_project;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class LotteryActivity extends AppCompatActivity {

    private FirebaseFirestore db;
    private String eventID;
    private Integer lotterySize;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_lottery);

        db = FirebaseFirestore.getInstance();
        eventID = getIntent().getStringExtra("eventID");
        lotterySize = getIntent().getIntExtra("lotterySize", 0);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String documentPath = "collectionName/documentID";
        String fieldName = "fieldToCheck";

        // check for non-null or empty eventID
        if (eventID == null || eventID.isEmpty()) {
            Toast.makeText(this, "Event ID not found.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        startLottery();
        // mark that the event has already generated a lottery (max once allowed)
        db.collection("events").document(eventID).update("generatedLottery", true);
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

            String eventName = documentSnapshot.getString("name");
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
                // Send notifications to "winners"
                for (Map<String, String> applicant : selectedLottery) {
                    String userID = applicant.get("user_id");
                    Notification notification = new Notification(eventName,
                            "Congratulations! You have been accepted to join " + eventName + ". Please sign up as soon as possible!",
                            LocalDate.now().toString());

                    sendNotificationToUser(userID, notification.toMap());
                }
                // Send notifications to "losers"
                for (Map<String, String> applicant : currentWaitlist) {
                    String userID = applicant.get("user_id");
                    Notification notification = new Notification(eventName,
                            "You were not unable to join " + eventName + ".",
                            LocalDate.now().toString());
                    sendNotificationToUser(userID, notification.toMap());
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


    /**
     * Method for sending notifications to user
     * @param user_id
     *  User ID of user to send the notification to
     * @param notification_map
     *  Notification converted into Map format since that is how notifications are stored in Firebase
     */
    private void sendNotificationToUser(String user_id, Map<String, Object> notification_map) {
        Log.d("SendNotification", "Checking notification preference for user: " + user_id);

        db.collection("user").document(user_id).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        Boolean notificationsEnabled = documentSnapshot.getBoolean("notifications");
                        if (notificationsEnabled != null && notificationsEnabled) {
                            updateUserNotificationList(user_id, notification_map);
                        } else {
                            Log.d("SendNotification", "Notifications are disabled for user: " + user_id);
                        }
                    } else {
                        Log.w("SendNotification", "User document not found: " + user_id);
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("SendNotification", "Error fetching Firestore document for user: " + user_id, e);
                });
    }


    /**
     * Method to update (add notification) to user in Firebase
     * @param user_id
     *  User ID of user to add notification to in Firebase
     * @param notification_map
     *  Notification converted into Map format since that is how notifications are stored in Firebase
     */
    private void updateUserNotificationList(String user_id, Map<String, Object> notification_map) {
        db.collection("user").document(user_id)
                .update("notification_list", FieldValue.arrayUnion(notification_map))
                .addOnSuccessListener(aVoid -> {
                    Log.d("SendNotification", "Notification sent to user: " + user_id);
                })
                .addOnFailureListener(e -> {
                    Log.e("SendNotification", "Error updating Firestore for user: " + user_id, e);
                });
    }
}