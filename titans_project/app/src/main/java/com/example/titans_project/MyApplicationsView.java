package com.example.titans_project;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.Firebase;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

/**
 * This is a class that defines the My Applications screen
 */
public class MyApplicationsView extends AppCompatActivity {
    private Button return_button;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private ListView eventList;
    private ArrayList<Event> eventsdataList;
    private EventsArrayAdapter eventsArrayAdapter;
    private Integer default_applicant_limit = 10000;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.fragment_my_applications);

        // Initialize Firebase
        FirebaseApp.initializeApp(this);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        return_button = findViewById(R.id.button_return);
        eventList = findViewById(R.id.listview_events);
        eventsdataList = new ArrayList<>();
        eventsArrayAdapter = new EventsArrayAdapter(this, eventsdataList);
        eventList.setAdapter(eventsArrayAdapter);

        // Retrieve applications
        retrieveApplications(applications -> {
            if (applications != null) {
                // now applications should contain all of the user's applied events' event ids
                Log.d("retrieveApplications","Applications: " + applications);
                retrieveEventsById(applications);
                eventsArrayAdapter.notifyDataSetChanged();
            } else {
                Log.d("retrieveApplications","No applications retrieved.");
            }
        });

        return_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void retrieveApplications(OnApplicationsRetrievedListener listener) {
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {
            db.collection("user").document(currentUser.getUid())
                    .get()
                    .addOnSuccessListener(document -> {
                        if (document.exists()) {
                            ArrayList<String> applications = (ArrayList<String>) document.get("applications");
                            listener.onApplicationsRetrieved(applications);
                        } else {
                            Toast.makeText(MyApplicationsView.this, "No applications found in the database",
                                    Toast.LENGTH_SHORT).show();
                            listener.onApplicationsRetrieved(null);
                        }
                    })
                    .addOnFailureListener(exception -> {
                        Toast.makeText(MyApplicationsView.this, "Error retrieving applications",
                                Toast.LENGTH_SHORT).show();
                        listener.onApplicationsRetrieved(null);
                    });
        } else {
            Toast.makeText(MyApplicationsView.this, "User not logged in",
                    Toast.LENGTH_SHORT).show();
            listener.onApplicationsRetrieved(null);
        }
    }

    public interface OnApplicationsRetrievedListener {
        void onApplicationsRetrieved(ArrayList<String> applications);
    }

    private void retrieveEventsById(List<String> documentIds) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        int[] completedCount = {0}; // Counter for completed fetches

        for (String docId : documentIds) {
            db.collection("events").document(docId)
                    .get()
                    .addOnSuccessListener(document -> {
                        if (document.exists()) {
                            String organizer_id = document.getString("organizerID");
                            String event_name = document.getString("name");
                            String organizer = document.getString("facilityName");
                            String event_id = document.getString("eventID");
                            String created_date = document.getString("createdDate");
                            String event_date = document.getString("eventDate");
                            String description = document.getString("description");
                            String picture = document.getString("picture");
                            Integer applicant_limit = default_applicant_limit;
                            Object applicantLimitObj = document.get("applicantLimit");
                            if (applicantLimitObj != null) {
                                applicant_limit = ((Long) applicantLimitObj).intValue(); // Cast to Integer
                            }
                            eventsdataList.add(new Event(event_id, event_name, organizer, created_date, event_date, description, applicant_limit, organizer_id, picture));
                        }
                        completedCount[0]++; // Increment the counter
                        if (completedCount[0] == documentIds.size()) {
                            // Notify adapter only when all documents are fetched
                            runOnUiThread(() -> eventsArrayAdapter.notifyDataSetChanged());
                        }
                    })
                    .addOnFailureListener(e -> {
                        completedCount[0]++;
                        Log.e("MyApplicationsView", "Error fetching document with ID " + docId, e);
                        if (completedCount[0] == documentIds.size()) {
                            runOnUiThread(() -> eventsArrayAdapter.notifyDataSetChanged());
                        }
                    });
        }
    }


}
