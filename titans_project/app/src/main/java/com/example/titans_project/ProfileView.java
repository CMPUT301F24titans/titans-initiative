package com.example.titans_project;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
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

        // Check user has entered a phone number
        if (!phone_number.getText().toString().isEmpty()) {  // not empty
            if (!phone_number.getText().toString().matches("[0-9]{3}-[0-9]{3}-[0-9]{4}")){
                phone_number.setError("Please enter a valid phone number");
            }
        }

        // Check user has entered a valid email
        if (!email.getText().toString().isEmpty()){  // not empty
            // check if there was an invalid email address entered
            if (!email.getText().toString().matches("^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$")) {  // regex to check if valid email (TOTAL PAIN!!!)
                email.setError("Please enter a valid email address");
            }
        }

        return_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        save_changes_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                userRef.update("name", name.getText().toString());
                userRef.update("email", email.getText().toString());
                userRef.update("phone_number", phone_number.getText().toString());
                userRef.update("facility", facility.getText().toString());
                userRef.update("notifications", notifications.isChecked());
            }
        });
    }
}