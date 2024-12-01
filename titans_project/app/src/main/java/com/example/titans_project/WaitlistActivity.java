package com.example.titans_project;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import org.w3c.dom.Text;

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

        ImageButton sendNotification = findViewById(R.id.buttonSendNotification);
        Button generateLottery = findViewById(R.id.buttonGenerateLottery);
        TextView lotterySizeTextView = findViewById(R.id.textViewLotterySize);

        EditText lotterySize = findViewById(R.id.editTextLotterySize);

        waitlistRecyclerView = findViewById(R.id.waitlistRecyclerView);
        waitlistRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        db = FirebaseFirestore.getInstance();
        waitlist = new ArrayList<>();
        eventID = getIntent().getStringExtra("eventID");

        // Check if lottery has already been generated for the event
        db.collection("events").document(eventID)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        // Check if the generatedLottery field exists
                        if (documentSnapshot.contains("generatedLottery")) {
                            // Remove option to generate lottery is lottery has already been generated
                            lotterySize.setVisibility(View.GONE);
                            generateLottery.setVisibility(View.GONE);
                            lotterySizeTextView.setVisibility(View.GONE);
                        }
                    }
                });

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
        generateLottery.setOnClickListener(v -> {

            if (lotterySize.getText().toString().isEmpty()) {
                lotterySize.setError("Lottery size required");
            }
            else {
                db.collection("events").document(eventID).update("lotterySize", Integer.parseInt(lotterySize.getText().toString()));
                generate_lottery.setClass(WaitlistActivity.this, LotteryActivity.class);
                generate_lottery.putExtra("eventID", eventID);
                generate_lottery.putExtra("lotterySize", Integer.parseInt(lotterySize.getText().toString()));
                startActivity(generate_lottery);
            }
        });

        // When sending notification, get the selected users
        sendNotification.setOnClickListener(v -> {
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
