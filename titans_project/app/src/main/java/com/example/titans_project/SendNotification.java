package com.example.titans_project;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.lang.reflect.Array;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SendNotification extends AppCompatActivity{
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize Firebase
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        db = FirebaseFirestore.getInstance();

        String eventID = getIntent().getStringExtra("eventID");

        // Get ref to current user in Firebase
        DocumentReference userRef = db.collection("user").document(user.getUid());

        EdgeToEdge.enable(this);
        setContentView(R.layout.fragment_send_notification);

        Button returnButton = findViewById(R.id.button_return);
        Button sendButton = findViewById(R.id.button_send_notification);
        EditText editTitle = findViewById(R.id.edit_title);
        EditText editDescription = findViewById(R.id.edit_description);
        Spinner dropdownSpinner = findViewById(R.id.dropdown_recipients);

        // Setup the spinner
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.dropdown_items, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        dropdownSpinner.setAdapter(adapter);

        // ArrayList of all user ids of users on the waitlist
        ArrayList<String> waitlist = getIntent().getStringArrayListExtra("waitlist");
        Log.d("SendNotification", "Waitlist: " + waitlist);

        returnButton.setOnClickListener(new View.OnClickListener() {
            /**
             * User clicks return button, return to previous activity
             * @param view
             */
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        sendButton.setOnClickListener(new View.OnClickListener() {
            /**
             * When send notification button is clicked, send notification to all users in waitlist
             * @param view
             */
            @Override
            public void onClick(View view) {

                String selectedRecipient = dropdownSpinner.getSelectedItem().toString();

                if (eventID == null || eventID.isEmpty()) {
                    Toast.makeText(SendNotification.this, "Invalid event ID", Toast.LENGTH_SHORT).show();
                    finish();
                    return;
                }

                // Create notification
                String title = editTitle.getText().toString().trim();
                String description = editDescription.getText().toString().trim();
                String date = LocalDate.now().toString();

                Notification notification = new Notification(title, description, date);

                Map<String, Object> notification_map = notification.toMap();

                getRecipients(eventID, selectedRecipient, new FirestoreCallback() {
                    @Override
                    public void onCallback(ArrayList<String> recipients) {
                        // Handle the recipients list here
                        for (String recipient : recipients) {
                            Log.d("Recipient", recipient);
                            sendNotificationToUser(recipient, notification_map);
                        }
                    }
                });

                Toast.makeText(SendNotification.this, "Sent notification", Toast.LENGTH_SHORT).show();
                finish();
            }
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

    public interface FirestoreCallback {
        void onCallback(ArrayList<String> recipients);
    }

    private void getRecipients(String eventID, String selectedRecipient, FirestoreCallback callback) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("events").document(eventID).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        // Extract the data
                        ArrayList<HashMap<String, String>> recipientsMap =
                                (ArrayList<HashMap<String, String>>) document.get(selectedRecipient.toLowerCase());
                        ArrayList<String> recipients = new ArrayList<>();
                        if (recipientsMap != null) {
                            for (HashMap<String, String> recipient : recipientsMap) {
                                recipients.add(recipient.get("user_id"));
                            }
                        }
                        callback.onCallback(recipients);
                    } else {
                        Log.d("Firestore", "No such document!");
                        callback.onCallback(new ArrayList<>()); // Return an empty list
                    }
                } else {
                    Log.e("Firestore", "Error getting document: ", task.getException());
                    callback.onCallback(new ArrayList<>()); // Return an empty list
                }
            }
        });
    }

}
