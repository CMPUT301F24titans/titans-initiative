package com.example.titans_project;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class DeleteEventActivity extends AppCompatActivity {

    private FirebaseFirestore db;
    private DocumentReference eventRef;
    private String eventId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_event);

        eventId = getIntent().getStringExtra("eventId");

        db = FirebaseFirestore.getInstance();
        eventRef = db.collection("events").document(eventId);

        TextView deleteEventTitle = findViewById(R.id.deleteEventTitle);
        deleteEventTitle.setText("Are you sure you want to delete this event?");

        Button yesButton = findViewById(R.id.yesButton);
        Button noButton = findViewById(R.id.noButton);

        yesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteEventFromFirebase();
            }
        });

        noButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void deleteEventFromFirebase() {
        eventRef.delete()
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(DeleteEventActivity.this, "Event deleted successfully", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(DeleteEventActivity.this, "Failed to delete event", Toast.LENGTH_SHORT).show();
                });
    }
}
