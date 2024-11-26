package com.example.titans_project;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.firebase.Firebase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.net.URI;
import java.time.LocalDate;
import java.util.UUID;

public class CreateEventView extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private ImageView picture;
    private int eventIndex = -1, image_code=1;
    private Button add_poster, return_button, submit_button;
    private EditText facility_name, event_name, event_date, event_details, applicant_limit;
    private Integer default_limit = 10000;
    private String organizer_id;
    private StorageReference storageReference;
    private Uri uri;
    private final ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {
            if(result.getResultCode() == RESULT_OK){
                if (result.getData() != null){
                    uri = result.getData().getData();
                    Glide.with(getApplicationContext()).load(uri).into(picture);
                }
            }
            else{
                Toast.makeText(CreateEventView.this, "Please select an image", Toast.LENGTH_SHORT).show();
            }
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        storageReference = FirebaseStorage.getInstance().getReference("event image");

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

        // Only assign value to organizer_id if the device id is found
        organizer_id = null;

        // User exists and continue
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
                                organizer_id = document.getString("user_id");
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
                selectImage();
            }
        });


        // user clicks to submit and create event -> store in Firebase
        submit_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (organizer_id == null){
                    Toast.makeText(CreateEventView.this, "Invalid User ID: Please Sign In Again",
                            Toast.LENGTH_SHORT).show();
                    return;
                }
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
                    event = new Event(eventNameInput, facilityInput, LocalDate.now().toString(), dateInput, eventDetailsInput, applicantLimitInput, organizer_id, uri.toString());
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

    private void selectImage(){
        Intent event_image = new Intent(Intent.ACTION_GET_CONTENT);
        event_image.setType("image/*");
        startActivityForResult(event_image, image_code);
    }

    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == image_code && resultCode == RESULT_OK && data != null && data.getData() != null) {
            uri = data.getData();
            picture.setImageURI(uri);
            //uploadImage(uri);
        }
    }

    private void uploadImage(Uri uri){
        StorageReference  reference = storageReference.child("test_image.jpg");
        reference.putFile(uri)
                .addOnSuccessListener(taskSnapshot -> {
                    Toast.makeText(CreateEventView.this, "Image successfully upload!", Toast.LENGTH_SHORT).show();
        }).addOnFailureListener(e -> {
                    Toast.makeText(CreateEventView.this, "There was an error while upload", Toast.LENGTH_SHORT).show();
        });
    }
}
