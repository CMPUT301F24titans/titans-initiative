package com.example.titans_project;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
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

        // Get ref to current user in Firebase
        DocumentReference userRef = db.collection("user").document(user.getUid());

        EdgeToEdge.enable(this);
        setContentView(R.layout.fragment_send_notification);

        Button returnButton = findViewById(R.id.button_return);
        Button sendButton = findViewById(R.id.button_send_notification);
        EditText editTitle = findViewById(R.id.edit_title);
        EditText editDescription = findViewById(R.id.edit_description);

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
                if (waitlist != null && !waitlist.isEmpty()) {
                    Notification notification = new Notification(
                            editTitle.getText().toString().trim(),
                            editDescription.getText().toString().trim(),
                            LocalDate.now().toString()
                    );

                    Map<String, Object> notification_map = notification.toMap();

                    for (String user_id : waitlist) {
                        Log.d("SendNotification", "Checking notification preference for user: " + user_id);

                        db.collection("user").document(user_id).get()
                                .addOnSuccessListener(documentSnapshot -> {
                                    if (documentSnapshot.exists()) {
                                        Boolean notificationsEnabled = documentSnapshot.getBoolean("notifications");

                                        if (notificationsEnabled != null && notificationsEnabled) {
                                            // Notifications are enabled, update the notification_list field
                                            db.collection("user").document(user_id)
                                                    .update("notification_list", FieldValue.arrayUnion(notification_map))
                                                    .addOnSuccessListener(aVoid -> {
                                                        Log.d("SendNotification", "Notification sent to user: " + user_id);
                                                    })
                                                    .addOnFailureListener(e -> {
                                                        Log.e("SendNotification", "Error updating Firestore for user: " + user_id, e);
                                                    });
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
                    Toast.makeText(SendNotification.this, "Sent notification", Toast.LENGTH_SHORT).show();
                    finish(); // Close the activity after updates
                } else {
                    Toast.makeText(SendNotification.this, "No recipients for notification", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
}
