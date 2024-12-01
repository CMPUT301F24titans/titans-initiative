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

public class ViewLotteryActivity extends AppCompatActivity {

    private FirebaseFirestore db;
    private String eventID;
    private RecyclerView recyclerView;
    private ImageButton sendNotification;
    Intent send_notification = new Intent();
    private LotteryAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_view_lottery);

        // Initialize Firestore
        db = FirebaseFirestore.getInstance();

        // Get event ID from intent
        eventID = getIntent().getStringExtra("eventID");
        if (eventID == null || eventID.isEmpty()) {
            Toast.makeText(this, "Event ID not found.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Initialize RecyclerView
        recyclerView = findViewById(R.id.lotteryRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        sendNotification = findViewById(R.id.buttonSendNotification);

        // Load lottery list
        loadLotteryList();

        sendNotification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                send_notification.setClass(ViewLotteryActivity.this, SendNotification.class);
                send_notification.putExtra("eventID", eventID);
                startActivity(send_notification);
            }
        });
    }

    private void loadLotteryList() {
        db.collection("events").document(eventID).get().addOnSuccessListener(documentSnapshot -> {
            if (!documentSnapshot.exists()) {
                Toast.makeText(this, "Event not found.", Toast.LENGTH_SHORT).show();
                return;
            }

            List<Map<String, String>> lotteryList = (List<Map<String, String>>) documentSnapshot.get("lottery");
            if (lotteryList == null || lotteryList.isEmpty()) {
                Toast.makeText(this, "No users in the lottery list.", Toast.LENGTH_SHORT).show();
                return;
            }

            adapter = new LotteryAdapter(this, lotteryList, eventID);
            recyclerView.setAdapter(adapter);

        }).addOnFailureListener(e -> {
            Toast.makeText(this, "Failed to load lottery list.", Toast.LENGTH_SHORT).show();
        });
    }
}
