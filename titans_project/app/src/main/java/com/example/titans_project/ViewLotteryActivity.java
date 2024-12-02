package com.example.titans_project;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;
import java.util.Map;

/**
 * Activity that allows the user to view the lottery list for a specific event.
 * It displays the list of participants in a RecyclerView and provides an option
 * to send notifications to the participants of the lottery.
 */
public class ViewLotteryActivity extends AppCompatActivity {

    private FirebaseFirestore db;  // Firestore instance for interacting with Firestore database
    private String eventID;  // ID of the event being viewed
    private RecyclerView recyclerView;  // RecyclerView to display the lottery list
    private ImageButton sendNotification;  // Button to send notifications to the lottery participants
    private Intent send_notification = new Intent();  // Intent to navigate to the SendNotification activity
    private LotteryAdapter adapter;  // Adapter for the RecyclerView to display the lottery list

    /**
     * Called when the activity is created. Initializes the UI components, sets up listeners,
     * and loads the lottery list for the specified event.
     *
     * @param savedInstanceState The saved instance state of the activity if it was previously destroyed.
     */
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_view_lottery);

        // Initialize Firestore
        db = FirebaseFirestore.getInstance();

        // Retrieve the event ID passed from the previous activity
        eventID = getIntent().getStringExtra("eventID");

        // Check if the event ID is valid
        if (eventID == null || eventID.isEmpty()) {
            Toast.makeText(this, "Event ID not found.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Initialize the RecyclerView and set its layout manager
        recyclerView = findViewById(R.id.lotteryRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Initialize the send notification button
        sendNotification = findViewById(R.id.buttonSendNotification);

        // Load the lottery list from Firestore
        loadLotteryList();

        // Set the click listener for the send notification button
        sendNotification.setOnClickListener(new View.OnClickListener() {
            /**
             * Navigates to the SendNotification activity when the button is clicked,
             * passing the event ID to the next activity.
             *
             * @param view The view that was clicked
             */
            @Override
            public void onClick(View view) {
                send_notification.setClass(ViewLotteryActivity.this, SendNotification.class);
                send_notification.putExtra("eventID", eventID);  // Pass the event ID to the next activity
                startActivity(send_notification);  // Start the SendNotification activity
            }
        });
    }

    /**
     * Loads the lottery list for the event from Firestore and sets the data into the RecyclerView.
     * If the event does not exist or has no lottery participants, appropriate messages are displayed.
     */
    private void loadLotteryList() {
        // Retrieve the event document from Firestore using the event ID
        db.collection("events").document(eventID).get().addOnSuccessListener(documentSnapshot -> {
            // Check if the event document exists
            if (!documentSnapshot.exists()) {
                Toast.makeText(this, "Event not found.", Toast.LENGTH_SHORT).show();
                return;
            }

            // Retrieve the lottery list from the event document
            List<Map<String, String>> lotteryList = (List<Map<String, String>>) documentSnapshot.get("lottery");

            // Check if the lottery list is null or empty
            if (lotteryList == null || lotteryList.isEmpty()) {
                Toast.makeText(this, "No users in the lottery list.", Toast.LENGTH_SHORT).show();
                return;
            }

            // Set the lottery list data into the RecyclerView using the LotteryAdapter
            adapter = new LotteryAdapter(this, lotteryList, eventID);
            recyclerView.setAdapter(adapter);

        }).addOnFailureListener(e -> {
            // Handle failure to retrieve the lottery list from Firestore
            Toast.makeText(this, "Failed to load lottery list.", Toast.LENGTH_SHORT).show();
        });
    }
}
