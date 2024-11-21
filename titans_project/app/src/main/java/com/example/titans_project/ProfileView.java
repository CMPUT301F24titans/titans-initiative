package com.example.titans_project;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;


/**
 * This is a class that defines the Profile screen
 */
public class ProfileView extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private static final String TAG = "userDeletion";
    private Boolean adminView = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        db = FirebaseFirestore.getInstance();

        // Get ref to user in Firebase
        DocumentReference userRef = db.collection("user").document(user.getUid());

        EdgeToEdge.enable(this);
        setContentView(R.layout.fragment_profile);

        Button return_button = findViewById(R.id.button_return);
        EditText name = findViewById(R.id.edit_text_full_name);
        EditText email = findViewById(R.id.edit_text_email);
        EditText phone_number = findViewById(R.id.edit_text_phone_number);
        EditText facility = findViewById(R.id.edit_text_facility);
        CheckBox notifications = findViewById(R.id.checkbox_notifications);
        Button save_changes_button = findViewById(R.id.button_save_changes);
        TextView initials = findViewById(R.id.textview_initials);

        // Create hashmap to store user's data from Firebase
        HashMap<String, String> userData= new HashMap<>();

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
                                name.setText(document.getString("full_name"));
                                email.setText(document.getString("email"));
                                phone_number.setText(document.getString("phone_number"));
                                facility.setText(document.getString("facility"));
                                // Generate initials of user
                                initials.setText(getInitials(name.getText().toString()));
                                notifications.setChecked(document.getBoolean("notifications"));

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

        return_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        // admin is viewing another profile
        if ("admin".equals(getIntent().getStringExtra("viewer"))){
            // save changes button becomes deletion button
            save_changes_button.setText("Delete User");
            adminView = Boolean.TRUE;
        }
        save_changes_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // admin deletes a user
                if (adminView) {
                    String profileId = getIntent().getStringExtra("user_id");
                    Log.d(TAG, "Deleting profile with ID: " + profileId);

                    if (profileId != null) {
                        /**
                         * DELETION WORKS JUST NEED TO CLARIFY HOW TO RETRIEVE THE PROFILE ID add
                         */
                        deleteProfile(profileId);
                    } else {
                        Log.e(TAG, "Profile ID is null. Cannot delete.");
                    }
                }
                // user editing their OWN profile
                else {
                    // Input validation before proceeding with the update
                    String nameInput = name.getText().toString().trim();
                    String emailInput = email.getText().toString().trim();
                    String phoneInput = phone_number.getText().toString().trim();
                    String facilityInput = facility.getText().toString().trim();

                    // Validate email
                    if (!emailInput.isEmpty() && !emailInput.matches("^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$")) {  // regex to validate email
                        email.setError("Please enter a valid email address");
                        return; // Don't proceed with the update if validation fails
                    }

                    // Validate phone number
                    if (!phoneInput.isEmpty() && !phoneInput.matches("[0-9]{3}-[0-9]{3}-[0-9]{4}")) {
                        phone_number.setError("Please enter a valid phone number");
                        return; // Don't proceed with the update if validation fails
                    }

                    // Get the document
                    userRef.get().addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            DocumentSnapshot user = task.getResult();
                            // Prepare data to update in a HashMap
                            HashMap<String, Object> userData = new HashMap<>();
                            userData.put("full_name", nameInput);
                            userData.put("email", emailInput);
                            userData.put("phone_number", phoneInput);
                            userData.put("facility", facilityInput);
                            userData.put("notifications", notifications.isChecked());

                            if (user.exists()) {
                                // Perform the update in Firestore
                                userRef.update(userData)
                                        .addOnSuccessListener(aVoid -> {
                                            // Handle success
                                            Log.d("ProfileView", "User details updated successfully!");
                                        })
                                        .addOnFailureListener(e -> {
                                            // Handle failure
                                            Log.e("ProfileView", "Error updating user details", e);
                                        });
                            } else {
                                mAuth.signInAnonymously();
                                FirebaseUser currentUser = mAuth.getCurrentUser();
                                // Create nested HashMap to store user's events
                                HashMap<String, Object> eventData = new HashMap<>();  // empty initially
                                userData.put("Events", eventData);
                                // Store in Firebase
                                db.collection("user").document(currentUser.getUid()).set(userData);
                            }
                        }
                    });
                    Toast.makeText(ProfileView.this, "Changes Saved",
                            Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
    });
    }

    /**
     * This is a function to generate initials from a string
     * @param full_name
     * @return
     */
    public static String getInitials(String full_name) {
        // Split the string by spaces to separate each word
        String[] words = full_name.trim().split("\\s+");  // handles multiple spaces
        StringBuilder initials = new StringBuilder();

        for (String word : words) {
            if (!word.isEmpty()) {
                // Take the first character of each word and convert it to uppercase
                initials.append(Character.toUpperCase(word.charAt(0)));
            }
        }

        return initials.toString();
    }

    /**
     * Deletes a profile from Firebase
     * @param profile_id
     *  ID of the profile to delete.
     */
    private void deleteProfile(String profile_id){
        db.collection("user").document(profile_id).delete().addOnCompleteListener(this, new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                // successful deletion of user
                if (task.isSuccessful()) {
                    Log.d(TAG, "deletedUser:success");
                    Toast.makeText(ProfileView.this, "Successfully deleted user.",
                            Toast.LENGTH_SHORT).show();
                }
                // unsuccessful deletion of user
                else {
                    Log.w(TAG, "deletedUser:failure", task.getException());
                    Toast.makeText(ProfileView.this, "Failed to delete user.",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}