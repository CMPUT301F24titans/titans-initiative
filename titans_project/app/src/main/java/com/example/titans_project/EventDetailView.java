package com.example.titans_project;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class EventDetailView extends AppCompatActivity {
    private Button return_button, apply_button;
    private TextView name, organizer, description, date;
    private String user_type;
    private FirebaseFirestore db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.fragment_event_details);

        // Initialize Firebase
        FirebaseApp.initializeApp(this);
        db = FirebaseFirestore.getInstance();

        user_type = getIntent().getStringExtra("viewer");

        name = findViewById(R.id.event_name);
        organizer = findViewById(R.id.organizer);
        description = findViewById(R.id.event_description);
        date = findViewById(R.id.event_date);
        return_button = findViewById(R.id.button_return);
        apply_button = findViewById(R.id.button_apply);

        // admin user viewing
        if ("admin".equals(user_type)){
            apply_button.setText("Delete Event");  // apply button becomes delete button for admin
        }
        // already enrolled/applied entrant viewing
        else if ("enrolled".equals(user_type)) {
            apply_button.setVisibility(View.GONE);  // remove button for entrant users already enrolled/applied
        }

        name.setText(getIntent().getStringExtra("event name"));
        organizer.setText(getIntent().getStringExtra("event organizer"));
        description.setText(getIntent().getStringExtra("event description"));
        date.setText(getIntent().getStringExtra("event date"));

        apply_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if ("admin".equals(user_type)){
                    // delete event

                    /**
                     * THIS WORKS JUST NEED TO PUT IN EVENT_ID WHERE "event name" IS
                    deleteEvent(getIntent().getStringExtra("event name"));
                     **/
                }
                else {
                    // enroll entrant into event
                }
            }
        });

        return_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    /**
     * Deletes event from Firebase
     * @param event_id
     *  The event id stored in the Firebase event collection
     */
    private void deleteEvent(String event_id){
        db.collection("events").document(event_id).delete();
    }
}
