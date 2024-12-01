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

public class CancelledEntrantsActivity extends AppCompatActivity {
    private RecyclerView waitlistRecyclerView;
    private WaitlistAdapter waitlistAdapter;
    private List<Attendee> waitlist;
    private FirebaseFirestore db;
    private String eventID;
    Intent send_notification = new Intent();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_waitlist);

        ImageButton sendNotification = findViewById(R.id.buttonSendNotification);
        Button generateLottery = findViewById(R.id.buttonGenerateLottery);
        TextView lotterySizeTextView = findViewById(R.id.textViewLotterySize);

        EditText lotterySize = findViewById(R.id.editTextLotterySize);

        // Remove waitlist specific objects
        lotterySize.setVisibility(View.GONE);
        generateLottery.setVisibility(View.GONE);
        lotterySizeTextView.setVisibility(View.GONE);
        TextView title = findViewById(R.id.title);
        title.setText("Cancelled Entrants");

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

        loadCancelledEntrants();

        findViewById(R.id.returnButton).setOnClickListener(v -> finish());

        // When sending notification, get the selected users
        sendNotification.setOnClickListener(v -> {
            send_notification.setClass(CancelledEntrantsActivity.this, SendNotification.class);
            startActivity(send_notification);
        });

    }

    private void loadCancelledEntrants() {
        db.collection("events").document(eventID)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    waitlist.clear();
                    List<Map<String, String>> firebaseWaitlist = (List<Map<String, String>>) documentSnapshot.get("cancelled");

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
