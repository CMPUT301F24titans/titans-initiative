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
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.time.LocalDate;
import java.util.UUID;

public class CreateEventView extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private ImageView picture;
    private int eventIndex = -1, image_code=1;
    private TextView title;
    private Button add_poster, return_button, submit_button, delete_poster;
    private EditText facility_name, event_name, event_date, event_details, applicant_limit;
    private CheckBox geolocation;
    private Integer default_limit = 10000;
    private String organizer_id, user_type;
    private StorageReference storageReference;
    private Uri uri;
    private static final String DEFAULT_PIC = "default_image.jpg";
    private Event event = new Event(null, null, null, null, null, null, null, null, null, null);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        EdgeToEdge.enable(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_create_event);

        // Initialize storage
        storageReference = FirebaseStorage.getInstance().getReference("event image");

        // Initialize Firebase
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        db = FirebaseFirestore.getInstance();

        // Get ref to current user in Firebase
        DocumentReference userRef = db.collection("user").document(user.getUid());

        title = findViewById(R.id.event_name);
        return_button = findViewById(R.id.button_return);
        add_poster = findViewById(R.id.button_add_poster);
        delete_poster = findViewById(R.id.button_delete_poster);
        submit_button = findViewById(R.id.submitButton);
        facility_name = findViewById(R.id.organizerEdit);
        event_name = findViewById(R.id.eventTitleEdit);
        event_date = findViewById(R.id.eventDateEdit);
        event_details = findViewById(R.id.eventDetailsEdit);
        picture = findViewById(R.id.imageView);
        applicant_limit = findViewById(R.id.eventLimitEdit);
        geolocation = findViewById(R.id.checkbox_geolocation);

        picture.setScaleType(ImageView.ScaleType.CENTER_CROP);

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

        // get the user type
        user_type = getIntent().getStringExtra("viewer");
        if("edit".equals(user_type)){
            get_event();
            title.setText("Edit Event");
            add_poster.setText("Edit Poster");
            event_name.setText(event.getName());
            facility_name.setText(event.getFacilityName());
            event_date.setText(event.getEventDate());
            if (event.getPicture() == null || event.getPicture().isEmpty()){
                displayImage(DEFAULT_PIC);
            }
            else {
                displayImage(event.getPicture());
            }
            submit_button.setText("Edit");
        }

        // return to previous activity if user clicks return button
        return_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


        // Click add poster button to select the image for event
        add_poster.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectImage();
            }
        });

        // Click delete poster button to delete image
        delete_poster.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                picture.setImageURI(null);
                uri = null;
                displayImage(DEFAULT_PIC);
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
                String eventNameInput = event_name.getText().toString().trim();
                String facilityInput = facility_name.getText().toString().trim();
                String dateInput = event_date.getText().toString().trim();
                String eventDetailsInput = event_details.getText().toString().trim();
                String applicantLimitString = applicant_limit.getText().toString();
                Integer applicantLimitInput;
                Boolean geolocationInput = geolocation.isChecked();
                // User does not enter a value -> default limit value
                if (applicantLimitString.isEmpty()){
                    applicantLimitInput = default_limit;
                }
                // User enters a value
                else {
                    applicantLimitInput = Integer.parseInt(applicantLimitString);
                }

                if (uri != null) {
                    uploadImage(uri);
                }
                else {
                    displayImage("default_image.jpg");
                }

                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                    event.setName(eventNameInput);
                    event.setFacilityName(facilityInput);
                    event.setCreated_date(LocalDate.now().toString());
                    event.setEvent_date(dateInput);
                    event.setDescription(eventDetailsInput);
                    event.setOrganizerID(organizer_id);
                    event.setApplicantLimit(applicantLimitInput);
                    event.setGeolocation(geolocationInput);

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

    /**
     * set up the event with the data passed by previous page
     */
    private void get_event(){
        event.setEventID(getIntent().getStringExtra("event ID"));
        event.setName(getIntent().getStringExtra("event name"));
        event.setFacilityName(getIntent().getStringExtra("event facility"));
        event.setCreated_date(getIntent().getStringExtra("event create date"));
        event.setEvent_date(getIntent().getStringExtra("event date"));
        event.setDescription(getIntent().getStringExtra("event description"));
        event.setOrganizerID(getIntent().getStringExtra("event organizer"));
        event.setPicture(getIntent().getStringExtra("event image"));
    }

    /**
     * This select the image form local device
     */
    private void selectImage(){
        Intent event_image = new Intent(Intent.ACTION_GET_CONTENT);
        event_image.setType("image/*");
        startActivityForResult(event_image, image_code);
    }


    /**
     * This make the image display on image view
     * @param requestCode
     *  The action code
     * @param resultCode
     *  The result of action
     * @param data
     *  The data return from intent
     */
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == image_code && resultCode == RESULT_OK && data != null && data.getData() != null) {
            uri = data.getData();
            picture.setImageURI(uri);
        }
    }

    /**
     * This upload the image to the firebase storage
     * @param uri
     *  The uri of the image
     */
    private void uploadImage(Uri uri){
        // Generate a valid picture name
        String picture_name = UUID.randomUUID().toString();

        // Ensure that picture_name is not null or empty
        if (picture_name == null || picture_name.trim().isEmpty()) {
            Log.e("CreateEventView", "Generated picture_name is invalid");
            return; // Exit the method if the picture name is invalid
        }

        event.setPicture(picture_name); // Store the picture name in the event object
        StorageReference reference = storageReference.child(picture_name);
        reference.putFile(uri)
                .addOnSuccessListener(taskSnapshot -> {
                    Toast.makeText(CreateEventView.this, "Image successfully uploaded!", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(CreateEventView.this, "There was an error while uploading", Toast.LENGTH_SHORT).show();
                });
    }

    /**
     * Retrieve image from firebase storage and display it
     * @param picture_name
     */
    private void displayImage(String picture_name) {
        // Check if picture_name is valid before proceeding
        if (picture_name == null || picture_name.trim().isEmpty()) {
            Log.e("CreateEventView", "Invalid picture name: " + picture_name);
            picture.setImageDrawable(null); // Set an empty image or placeholder
            return;
        }

        StorageReference imageRef = storageReference.child(picture_name);
        final File localFile = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), picture_name + ".jpg");
        imageRef.getFile(localFile)
                .addOnSuccessListener(taskSnapshot -> {
                    Bitmap bitmap = BitmapFactory.decodeFile(localFile.getAbsolutePath());
                    picture.setImageBitmap(bitmap);
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(CreateEventView.this, "Failed to load image: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    picture.setImageDrawable(null); // Set an empty image or placeholder
                });
    }
}
