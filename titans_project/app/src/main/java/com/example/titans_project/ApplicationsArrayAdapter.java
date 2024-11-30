package com.example.titans_project;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;

public class ApplicationsArrayAdapter extends ArrayAdapter<Event> {

    private Context context;
    private ArrayList<Event> events;

    public ApplicationsArrayAdapter(Context context, ArrayList<Event> events) {
        super(context, 0, events);  // Call the superclass constructor
        this.context = context;
        this.events = events;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.content_my_applications, parent, false);
        }

        Event event = getItem(position);  // Use getItem to retrieve the event at the given position
        TextView name = convertView.findViewById(R.id.event_name);
        Button leave_waitlist_button = convertView.findViewById(R.id.button_leave_waitlist);

        name.setText(event.getName());

        leave_waitlist_button.setOnClickListener(new View.OnClickListener() {
            /**
             * User clicks leave waitlist button, remove them from waitlist
             * @param view
             */
            @Override
            public void onClick(View view) {
                // Get the current user
                FirebaseAuth mAuth = FirebaseAuth.getInstance();
                String userId = mAuth.getCurrentUser().getUid();

                // Get the Firestore database instance
                FirebaseFirestore db = FirebaseFirestore.getInstance();

                // Get the document reference for the current user's application data
                db.collection("user").document(userId)
                        .get()
                        .addOnSuccessListener(documentSnapshot -> {
                            if (documentSnapshot.exists()) {
                                // Retrieve the current list of waitlisted events (stored in an array)
                                ArrayList<String> waitlist = (ArrayList<String>) documentSnapshot.get("applications");

                                if (waitlist != null) {
                                    // Remove the event from the user's waitlist
                                    String eventIdToRemove = event.getEventID();
                                    waitlist.remove(eventIdToRemove);

                                    // Update in Firebase
                                    db.collection("user").document(userId)
                                            .update("applications", waitlist)
                                            .addOnSuccessListener(aVoid -> {
                                                // Successfully removed from user’s waitlist

                                                // Remove the user from the event's waitlist
                                                db.collection("events").document(eventIdToRemove)
                                                        .get()
                                                        .addOnSuccessListener(eventDocument -> {
                                                            if (eventDocument.exists()) {
                                                                // Retrieve the event's current waitlist
                                                                ArrayList<HashMap<String, String>> eventWaitlist =
                                                                        (ArrayList<HashMap<String, String>>) eventDocument.get("waitlist");

                                                                if (eventWaitlist != null) {
                                                                    // Iterate over the waitlist to find the user by user_id
                                                                    for (int i = 0; i < eventWaitlist.size(); i++) {
                                                                        HashMap<String, String> userWaitlistEntry = eventWaitlist.get(i);
                                                                        if (userWaitlistEntry.containsKey("user_id") &&
                                                                                userWaitlistEntry.get("user_id").equals(userId)) {
                                                                            // User found in the waitlist, remove them
                                                                            eventWaitlist.remove(i);
                                                                            break;
                                                                        }
                                                                    }

                                                                    // Update the event document with the modified waitlist
                                                                    db.collection("events").document(eventIdToRemove)
                                                                            .update("waitlist", eventWaitlist)
                                                                            .addOnSuccessListener(aVoid1 -> {
                                                                                // Successfully updated event document
                                                                                events.remove(event);  // Remove from the local list
                                                                                notifyDataSetChanged();  // Update UI
                                                                                Toast.makeText(context, "Successfully left waitlist", Toast.LENGTH_SHORT).show();
                                                                            })
                                                                            .addOnFailureListener(e -> {
                                                                                // Handle failure
                                                                                Toast.makeText(context, "Failed to update event waitlist", Toast.LENGTH_SHORT).show();
                                                                            });
                                                                }
                                                            }
                                                        })
                                                        .addOnFailureListener(e -> {
                                                            // Handle Firestore error for event retrieval
                                                            Toast.makeText(context, "Failed to retrieve event data", Toast.LENGTH_SHORT).show();
                                                        });
                                            })
                                            .addOnFailureListener(e -> {
                                                // Handle failure for user document update
                                                Toast.makeText(context, "Failed to leave waitlist", Toast.LENGTH_SHORT).show();
                                            });
                                }
                            } else {
                                // Document does not exist
                                Toast.makeText(context, "User data not found", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .addOnFailureListener(e -> {
                            // Handle Firestore error for user document retrieval
                            Toast.makeText(context, "Failed to retrieve user data", Toast.LENGTH_SHORT).show();
                        });
            }


        });

        return convertView;
    }

}

