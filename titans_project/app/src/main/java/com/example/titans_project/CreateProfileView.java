package com.example.titans_project;

import static java.security.AccessController.getContext;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

/**
 * This is a class that defines the Profile screen
 */
public class CreateProfileView extends AppCompatActivity {
    private Button add_profile_pic_button;
    private Button create_button;
    private EditText full_name;
    private EditText email;
    private EditText phone_number;
    private CheckBox notifications;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.fragment_create_profile);

        Boolean validAcc = Boolean.TRUE;

        add_profile_pic_button = findViewById(R.id.button_add_profile_pic);
        create_button = findViewById(R.id.button_create_profile);
        full_name = findViewById(R.id.edit_text_full_name);
        email = findViewById(R.id.edit_text_email);
        phone_number = findViewById(R.id.edit_text_phone_number);
        notifications = findViewById(R.id.checkbox_notifications);

        // Check user has entered a full name in the field
        if (full_name.getText().toString().isEmpty()) {
            full_name.setError("Please enter your full name");
            validAcc = Boolean.FALSE;
        }

        // Check user has entered a valid email address
        if (email.getText().toString().isEmpty()) {
            email.setError("Please enter your email");
            validAcc = Boolean.FALSE;
        }

        // Check user has entered a phone number (not empty)
        if (!phone_number.getText().toString().isEmpty()) {
            if (!phone_number.getText().toString().matches("[0-9]{10}")){
                phone_number.setError("Phone numbers may only consists of numbers");
                validAcc = Boolean.FALSE;
            }
        }
        // User clicks on the Add Profile Pic button
        add_profile_pic_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // add profile pic feature
                finish();
            }
        });
        // User clicks on the Create Profile button
        create_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}
