package com.example.titans_project;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Activity that manages the waitlist for an event. Users can view the waitlist, generate a lottery
 * for the event, and send notifications to the users on the waitlist.
 */
public class WaitlistActivity extends AppCompatActivity {

    private RecyclerView waitlistRecyclerView;  // RecyclerView to display the waitlist
    private WaitlistArrayAdapter waitlistAdapter;  // Adapter to bind waitlist data to the RecyclerView
    private List<Attendee> waitlist;  // List of attendees on the waitlist
    private FirebaseFirestore db;  // Firestore instance for interacting with the Firestore database
    private String eventID;  // ID of the event associated with the waitlist
    private Intent send_notification = new Intent();  // Intent for sending notifications
    private Intent generate_lottery = new Intent();  // Intent for generating lottery

    /**
     * Called when the activity is created. Initializes the UI components, sets up listeners,
     * and loads the waitlist data from Firestore.
     *
     * @param savedInstanceState The saved instance state of the activity if it was previously destroyed.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_waitlist);

        // Initialize UI components
        ImageButton sendNotification = findViewById(R.id.buttonSendNotification);
        Button generateLottery = findViewById(R.id.buttonGenerateLottery);
        TextView lotterySizeTextView = findViewById(R.id.textViewLotterySize);
        EditText lotterySize = findViewById(R.id.editTextLotterySize);

        // Initialize RecyclerView for displaying waitlist
        waitlistRecyclerView = findViewById(R.id.waitlistRecyclerView);
        waitlistRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Initialize Firestore instance and list of waitlisted attendees
        db = FirebaseFirestore.getInstance();
        waitlist = new ArrayList<>();

        // Get the event ID passed from the previous activity
        eventID = getIntent().getStringExtra("eventID");

        // Check if lottery has already been generated for the event
        db.collection("events").document(eventID)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        // If lottery has already been generated, hide the generate lottery UI components
                        if (documentSnapshot.contains("generatedLottery")) {
                            lotterySize.setVisibility(View.GONE);
                            generateLottery.setVisibility(View.GONE);
                            lotterySizeTextView.setVisibility(View.GONE);
                        }
                    }
                });

        // Validate event ID
        if (eventID == null || eventID.isEmpty()) {
            Toast.makeText(this, "Invalid event ID", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Set up the RecyclerView adapter
        waitlistAdapter = new WaitlistArrayAdapter(this, waitlist, eventID);
        waitlistRecyclerView.setAdapter(waitlistAdapter);

        // Load the waitlist data from Firestore
        loadWaitlist();

        // Set up the listener for the return button to go back to the previous screen
        findViewById(R.id.returnButton).setOnClickListener(v -> finish());

        // Set up the listener for the generate lottery button
        generateLottery.setOnClickListener(v -> {
            if (lotterySize.getText().toString().isEmpty()) {
                lotterySize.setError("Lottery size required");
            } else {
                // Update Firestore with the lottery size
                db.collection("events").document(eventID).update("lotterySize", Integer.parseInt(lotterySize.getText().toString()));
                // Start the LotteryActivity to generate the lottery
                generate_lottery.setClass(WaitlistActivity.this, LotteryActivity.class);
                generate_lottery.putExtra("eventID", eventID);
                generate_lottery.putExtra("lotterySize", Integer.parseInt(lotterySize.getText().toString()));
                startActivity(generate_lottery);
            }
        });

        // Set up the listener for the send notification button
        sendNotification.setOnClickListener(v -> {
            // Navigate to SendNotification activity to send notifications to waitlisted users
            send_notification.setClass(WaitlistActivity.this, SendNotification.class);
            send_notification.putExtra("eventID", eventID);
            startActivity(send_notification);
        });
    }

    /**
     * Loads the waitlist from Firestore and updates the RecyclerView with the list of attendees.
     * If the waitlist is empty or fails to load, appropriate messages are shown to the user.
     */
    private void loadWaitlist() {
        // Retrieve the event document from Firestore
        db.collection("events").document(eventID)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    // Clear the current waitlist and load the new one
                    waitlist.clear();
                    List<Map<String, String>> firebaseWaitlist = (List<Map<String, String>>) documentSnapshot.get("waitlist");

                    // Check if the waitlist is not null
                    if (firebaseWaitlist != null) {
                        // Populate the waitlist with attendees data
                        for (Map<String, String> attendee : firebaseWaitlist) {
                            waitlist.add(new Attendee(attendee.get("full_name"), attendee.get("user_id"), ""));
                        }
                    }
                    // Notify the adapter that the data has changed
                    waitlistAdapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Failed to load waitlist.", Toast.LENGTH_SHORT).show());
    }
}
