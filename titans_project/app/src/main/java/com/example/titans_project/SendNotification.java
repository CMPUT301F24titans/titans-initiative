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

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Activity that allows an admin or event organizer to send a notification to users on the event's waitlist.
 * The user can select the recipient group from a dropdown menu and enter a title and description for the notification.
 */
public class SendNotification extends AppCompatActivity {

    private FirebaseAuth mAuth;  // Firebase authentication instance for user management
    private FirebaseFirestore db;  // Firestore instance for interacting with Firestore database

    /**
     * Called when the activity is created. Initializes UI components and sets up event listeners for sending notifications.
     *
     * @param savedInstanceState The saved instance state of the activity if it was previously destroyed.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize Firebase
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        db = FirebaseFirestore.getInstance();

        // Get the event ID passed from the previous activity
        String eventID = getIntent().getStringExtra("eventID");

        // Get reference to the current user in Firestore
        DocumentReference userRef = db.collection("user").document(user.getUid());

        // Enable EdgeToEdge for this activity
        EdgeToEdge.enable(this);

        // Set the layout for this activity
        setContentView(R.layout.fragment_send_notification);

        // Initialize UI components
        Button returnButton = findViewById(R.id.button_return);
        Button sendButton = findViewById(R.id.button_send_notification);
        EditText editTitle = findViewById(R.id.edit_title);
        EditText editDescription = findViewById(R.id.edit_description);
        Spinner dropdownSpinner = findViewById(R.id.dropdown_recipients);

        // Setup the dropdown spinner with recipient options (waitlist)
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.dropdown_items, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        dropdownSpinner.setAdapter(adapter);

        // Retrieve the waitlist from the intent
        ArrayList<String> waitlist = getIntent().getStringArrayListExtra("waitlist");
        Log.d("SendNotification", "Waitlist: " + waitlist);

        // Set click listener for the return button to go back to the previous activity
        returnButton.setOnClickListener(new View.OnClickListener() {
            /**
             * Navigates back to the previous activity when the return button is clicked.
             *
             * @param view The view that was clicked
             */
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        // Set click listener for the send button to send the notification
        sendButton.setOnClickListener(new View.OnClickListener() {
            /**
             * Sends the notification to the selected recipient(s) when the send button is clicked.
             *
             * @param view The view that was clicked
             */
            @Override
            public void onClick(View view) {
                // Get the recipient group selected from the spinner
                String selectedRecipient = dropdownSpinner.getSelectedItem().toString();

                // Validate the event ID
                if (eventID == null || eventID.isEmpty()) {
                    Toast.makeText(SendNotification.this, "Invalid event ID", Toast.LENGTH_SHORT).show();
                    finish();
                    return;
                }

                // Create the notification using input data
                String title = editTitle.getText().toString().trim();
                String description = editDescription.getText().toString().trim();
                String date = LocalDate.now().toString();

                // Create Notification object and convert it to a Map
                Notification notification = new Notification(title, description, date);
                Map<String, Object> notification_map = notification.toMap();

                // Retrieve the list of recipients based on the selected recipient group (waitlist or others)
                getRecipients(eventID, selectedRecipient, new FirestoreCallback() {
                    @Override
                    public void onCallback(ArrayList<String> recipients) {
                        // Send notification to all recipients
                        for (String recipient : recipients) {
                            Log.d("Recipient", recipient);
                            sendNotificationToUser(recipient, notification_map);
                        }
                    }
                });

                // Notify the user that the notification has been sent
                Toast.makeText(SendNotification.this, "Sent notification", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    /**
     * Sends a notification to a specific user if they have notifications enabled.
     *
     * @param user_id       The user ID of the recipient.
     * @param notification_map A map representation of the notification.
     */
    private void sendNotificationToUser(String user_id, Map<String, Object> notification_map) {
        Log.d("SendNotification", "Checking notification preference for user: " + user_id);

        // Retrieve the user's document from Firestore
        db.collection("user").document(user_id).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        // Check if the user has notifications enabled
                        Boolean notificationsEnabled = documentSnapshot.getBoolean("notifications");
                        if (notificationsEnabled != null && notificationsEnabled) {
                            // If notifications are enabled, add the notification to the user's notification list
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
     * Adds the notification to the user's notification list in Firestore.
     *
     * @param user_id       The user ID of the recipient.
     * @param notification_map A map representation of the notification.
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

    /**
     * Interface to handle the callback for fetching the list of recipients.
     */
    public interface FirestoreCallback {
        void onCallback(ArrayList<String> recipients);
    }

    /**
     * Retrieves the list of recipients for the notification based on the selected recipient group.
     *
     * @param eventID            The ID of the event.
     * @param selectedRecipient  The selected recipient group (e.g., waitlist).
     * @param callback           The callback to handle the retrieved list of recipients.
     */
    private void getRecipients(String eventID, String selectedRecipient, FirestoreCallback callback) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("events").document(eventID).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        // Extract the list of recipients based on the selected recipient group
                        ArrayList<HashMap<String, String>> recipientsMap =
                                (ArrayList<HashMap<String, String>>) document.get(selectedRecipient.toLowerCase());
                        ArrayList<String> recipients = new ArrayList<>();
                        if (recipientsMap != null) {
                            for (HashMap<String, String> recipient : recipientsMap) {
                                recipients.add(recipient.get("user_id"));
                            }
                        }
                        callback.onCallback(recipients);  // Pass the list of recipients to the callback
                    } else {
                        Log.d("Firestore", "No such document!");
                        callback.onCallback(new ArrayList<>());  // Return an empty list if document does not exist
                    }
                } else {
                    Log.e("Firestore", "Error getting document: ", task.getException());
                    callback.onCallback(new ArrayList<>());  // Return an empty list on failure
                }
            }
        });
    }
}
