package com.example.titans_project;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Firebase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.time.LocalDate;

public class CreateEventView extends AppCompatActivity {

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
        setContentView(R.layout.activity_create_event);

        Button return_button = findViewById(R.id.button_return);
        Button submit_button = findViewById(R.id.submitButton);
        EditText facility_name = findViewById(R.id.organizerEdit);
        EditText event_name = findViewById(R.id.eventTitleEdit);
        EditText event_date = findViewById(R.id.eventDateEdit);
        EditText event_details = findViewById(R.id.eventDetailsEdit);

        // User exists
        if (user != null) {
            userRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            Log.d("Firestore", "Document exists. Data: " + document.getData());

                            // Ensure this runs on the main thread
                            runOnUiThread(() -> {
                                facility_name.setText(document.getString("facility"));
                            });
                        } else {
                            Log.d("Firestore", "No document found");
                        }
                    } else {
                        Log.e("Firestore", "Error fetching document", task.getException());
                    }
                }
            });

        }

        // return to previous activity if user clicks return button
        return_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        // user clicks to submit and create event -> store in Firebase
        submit_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String facilityInput = facility_name.getText().toString().trim();
                String dateInput = event_date.getText().toString().trim();
                String eventNameInput = event_name.getText().toString().trim();
                String eventDetailsInput = event_details.getText().toString().trim();

                Event event;
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                    event = new Event(eventNameInput, facilityInput, LocalDate.now().toString(), dateInput, eventDetailsInput);
                } else {
                    event = null;
                }

                if (!event.isValid()) {
                    // Perform field-specific error handling
                    if (facilityInput.isEmpty()) facility_name.setError("Facility name may not be left blank");
                    if (dateInput.isEmpty()) event_date.setError("Event date may not be left blank");
                    if (eventNameInput.isEmpty()) event_name.setError("Event name may not be left blank");
                    return;
                }

                // Add event to Firestore
                db.collection("events")
                        .add(event)
                        .addOnSuccessListener(documentReference -> {
                            Log.d("Firestore", "Event added with ID: " + documentReference.getId());
                            event.setEventID(documentReference.getId());
                            db.collection("events").document(documentReference.getId()).update("eventID", documentReference.getId());
                            Toast.makeText(CreateEventView.this, "Event Successfully Created",
                                    Toast.LENGTH_SHORT).show();
                            finish(); // Optionally return to the previous activity
                        })
                        .addOnFailureListener(e -> {
                            Log.e("Firestore", "Error adding event", e);
                            Toast.makeText(CreateEventView.this, "Failed To Create Event",
                                    Toast.LENGTH_SHORT).show();
                        });
            }
        });

    }

}
