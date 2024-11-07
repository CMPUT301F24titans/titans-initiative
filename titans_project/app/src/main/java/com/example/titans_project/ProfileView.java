package com.example.titans_project;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

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
        initials.setText("JF");

        return_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        save_changes_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Input validation before proceeding with the update
                String nameInput = name.getText().toString().trim();
                String emailInput = email.getText().toString().trim();
                String phoneInput = phone_number.getText().toString().trim();
                String facilityInput = facility.getText().toString().trim();

                // Validate email
                if (emailInput.isEmpty() || !emailInput.matches("^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$")) {
                    email.setError("Please enter a valid email address");
                    return; // Don't proceed with the update if validation fails
                }

                // Validate phone number
                if (phoneInput.isEmpty() || !phoneInput.matches("[0-9]{3}-[0-9]{3}-[0-9]{4}")) {
                    phone_number.setError("Please enter a valid phone number");
                    return; // Don't proceed with the update if validation fails
                }

                // Get the document
                userRef.get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot user = task.getResult();
                        // Prepare data to update in a HashMap
                        HashMap<String, Object> userData = new HashMap<>();
                        userData.put("name", nameInput);
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
                        }
                        else {
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
    });
    }
}