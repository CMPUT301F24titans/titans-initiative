package com.example.titans_project;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
    private ImageView picture;
    private int eventIndex = -1, event_image=0;
    private Button add_poster, return_button, submit_button;
    private EditText facility_name, event_name, event_date, event_details, applicant_limit;
    private Uri uri;
    private Integer default_limit = 10000;

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

        return_button = findViewById(R.id.button_return);
        add_poster = findViewById(R.id.button_add_poster);
        submit_button = findViewById(R.id.submitButton);
        facility_name = findViewById(R.id.organizerEdit);
        event_name = findViewById(R.id.eventTitleEdit);
        event_date = findViewById(R.id.eventDateEdit);
        event_details = findViewById(R.id.eventDetailsEdit);
        picture = findViewById(R.id.imageView);
        applicant_limit = findViewById(R.id.eventLimitEdit);

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


        add_poster.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent event_image = new Intent(Intent.ACTION_GET_CONTENT, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(event_image, 1);
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

                String applicantLimitString = applicant_limit.getText().toString();
                Integer applicantLimitInput;
                // User does not enter a value -> default limit value
                if (applicantLimitString.isEmpty()){
                    applicantLimitInput = default_limit;
                }
                // User enters a value
                else {
                    applicantLimitInput = Integer.parseInt(applicantLimitString);
                }

                Event event;
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                    event = new Event(eventNameInput, facilityInput, LocalDate.now().toString(), dateInput, eventDetailsInput, applicantLimitInput);
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
                            Intent intent = new Intent(CreateEventView.this, QRCodeActivity.class);
                            intent.putExtra("eventID", documentReference.getId());
                            startActivity(intent);
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

    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
            uri = data.getData();
            picture.setImageURI(uri);
        }
    }
}
