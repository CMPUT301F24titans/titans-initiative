package com.example.titans_project;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

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

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lottery);

        db = FirebaseFirestore.getInstance();
        eventID = getIntent().getStringExtra("eventID");

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

            Event event = documentSnapshot.toObject(Event.class);
            if (event == null) {
                Toast.makeText(this, "Failed to load event details.", Toast.LENGTH_SHORT).show();
                finish();
                return;
            }

            List<Map<String, String>> waitlist = event.getWaitlist();
            int applicantLimit = event.getApplicantLimit();

            if (waitlist == null || waitlist.isEmpty()) {
                Toast.makeText(this, "Waitlist is empty. No lottery needed.", Toast.LENGTH_SHORT).show();
                finish();
                return;
            }

            if (waitlist.size() <= applicantLimit) {
                Toast.makeText(this, "Applicants are fewer than or equal to the limit. No lottery needed.", Toast.LENGTH_SHORT).show();
                finish();
                return;
            }

            List<Map<String, String>> selectedLottery = new ArrayList<>();
            Collections.shuffle(waitlist); // 随机打乱 waitlist
            for (int i = 0; i < applicantLimit; i++) {
                selectedLottery.add(waitlist.get(i));
            }

            Map<String, Object> updates = new HashMap<>();
            updates.put("lottery", FieldValue.arrayUnion(selectedLottery.toArray())); // 将 selectedLottery 添加到 lottery 中
            updates.put("waitlist", FieldValue.arrayRemove(selectedLottery.toArray())); // 从 waitlist 中移除 selectedLottery

            db.collection("events").document(eventID).update(updates)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(this, "Lottery completed successfully!", Toast.LENGTH_SHORT).show();
                        finish();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Failed to update lottery results.", Toast.LENGTH_SHORT).show();
                    });
        }).addOnFailureListener(e -> {
            Toast.makeText(this, "Failed to fetch event data.", Toast.LENGTH_SHORT).show();
        });
    }
}
