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

/**
 * This is a class that that an ApplicationsArrayAdapter
 */
public class ApplicationsArrayAdapter extends ArrayAdapter<Event> {

    private Context context;
    private ArrayList<Event> events;

    /**
     * Constructor for ApplicationsArrayAdapter
     * @param context
     *  The context in which the adapter is being used, this is used for accessing resources and services needed by the adapter
     * @param events
     *  The list of Event objects to be displayed in the adapter
     */
    public ApplicationsArrayAdapter(Context context, ArrayList<Event> events) {
        super(context, 0, events);  // Call the superclass constructor
        this.context = context;
        this.events = events;
    }

    /**
     * Returns a View for an item in the adapter's data set.
     *
     * @param position
     *  The position of the item within the adapter's data set for which the View is required
     * @param convertView
     *  The recycled View to reuse, if possible. If null, a new View will be created
     * @param parent
     *  The parent ViewGroup that this View will be attached to
     * @return
     *  Returns a View corresponding to the data at the specified position
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.content_my_applications, parent, false);
        }

        Event event = getItem(position);
        TextView name = convertView.findViewById(R.id.event_name);
        Button leave_waitlist_button = convertView.findViewById(R.id.button_leave_waitlist);

        name.setText(event.getName());

        leave_waitlist_button.setOnClickListener(view -> onLeaveWaitlistClick(event));

        return convertView;
    }

    /**
     * Method that triggers when user clicks leave waitlist fro an event
     * @param event
     *  Event to leave the waitlist for
     */
    private void onLeaveWaitlistClick(Event event) {
        // Get the current user ID
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Get Firestore instance
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Retrieve user's waitlist data
        retrieveUserWaitlist(userId, db, event);
    }

    /**
     * Retrieves the user's waitlist from Firebase
     * @param userId
     *  Current user's id
     * @param db
     *  Firebase database
     * @param event
     *  Event to unlink the user from (user leaves event waitlist)
     */
    private void retrieveUserWaitlist(String userId, FirebaseFirestore db, Event event) {
        db.collection("user").document(userId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        ArrayList<String> waitlist = (ArrayList<String>) documentSnapshot.get("applications");

                        if (waitlist != null && waitlist.contains(event.getEventID())) {
                            removeUserFromWaitlist(userId, db, event, waitlist);
                        }
                    } else {
                        showToast("User data not found");
                    }
                })
                .addOnFailureListener(e -> showToast("Failed to retrieve user data"));
    }

    /**
     * Method to remove the USER from EVENT waitlist AND remove the EVENT from the USER applications
     * @param userId
     *  Current user's id
     * @param db
     *  Firebase database
     * @param event
     *  Event to unlink from user
     * @param waitlist
     *  The user's waitlist
     */
    private void removeUserFromWaitlist(String userId, FirebaseFirestore db, Event event, ArrayList<String> waitlist) {
        // Remove the event from user's waitlist
        waitlist.remove(event.getEventID());

        db.collection("user").document(userId)
                .update("applications", waitlist)
                .addOnSuccessListener(aVoid -> updateEventWaitlist(userId, db, event))
                .addOnFailureListener(e -> showToast("Failed to leave waitlist"));
    }

    /**
     * Method to update the event waitlist
     * @param userId
     *  Current user's id
     * @param db
     *  Firebase database
     * @param event
     *  Event to remove user from waitlist
     */
    private void updateEventWaitlist(String userId, FirebaseFirestore db, Event event) {
        db.collection("events").document(event.getEventID())
                .get()
                .addOnSuccessListener(eventDocument -> {
                    if (eventDocument.exists()) {
                        ArrayList<HashMap<String, String>> eventWaitlist =
                                (ArrayList<HashMap<String, String>>) eventDocument.get("waitlist");

                        if (eventWaitlist != null) {
                            removeUserFromEventWaitlist(userId, eventWaitlist, db, event);
                        }
                    } else {
                        showToast("Failed to retrieve event data");
                    }
                })
                .addOnFailureListener(e -> showToast("Failed to retrieve event data"));
    }

    /**
     * Method to remove the user from the event waitlist (locally first and then use local waitlist to update Firebase waitlist)
     * @param userId
     *  Current user's id
     * @param eventWaitlist
     *  The local event waitlist of the user
     * @param db
     *  Firebase database
     * @param event
     *  Event to remove user from (waitlist)
     */
    private void removeUserFromEventWaitlist(String userId, ArrayList<HashMap<String, String>> eventWaitlist,
                                             FirebaseFirestore db, Event event) {
        for (int i = 0; i < eventWaitlist.size(); i++) {
            HashMap<String, String> userWaitlistEntry = eventWaitlist.get(i);
            if (userWaitlistEntry.containsKey("user_id") && userWaitlistEntry.get("user_id").equals(userId)) {
                eventWaitlist.remove(i);
                break;
            }
        }

        // Update the event's waitlist in Firestore
        db.collection("events").document(event.getEventID())
                .update("waitlist", eventWaitlist)
                .addOnSuccessListener(aVoid -> {
                    // Successfully updated event document
                    removeEventLocally(event);
                    showToast("Successfully left waitlist");
                })
                .addOnFailureListener(e -> showToast("Failed to update event waitlist"));
    }

    /**
     * Method to remove the event from the local ArrayList
     * @param event
     *  Event to remove
     */
    private void removeEventLocally(Event event) {
        events.remove(event);
        notifyDataSetChanged();
    }

    /**
     * ShowToast method for short duration
     * @param message
     *  Message to display in toast
     */
    private void showToast(String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }
}
