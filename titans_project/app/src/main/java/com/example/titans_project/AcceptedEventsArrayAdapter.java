package com.example.titans_project;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;

public class AcceptedEventsArrayAdapter extends ArrayAdapter<Event> {
    private Context context;
    private ArrayList<Event> events;

    /**
     * Constructor for AcceptedEventsArrayAdapter
     * @param context
     *  The context in which the adapter is being used, this is used for accessing resources and services needed by the adapter
     * @param events
     *  The list of Event objects to be displayed in the adapter
     */
    public AcceptedEventsArrayAdapter(Context context, ArrayList<Event> events) {
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
            convertView = LayoutInflater.from(context).inflate(R.layout.content_accepted_events, parent, false);
        }

        Event event = getItem(position);
        TextView name = convertView.findViewById(R.id.event_name);
        Button decline_button = convertView.findViewById(R.id.button_decline);
        Button enroll_button = convertView.findViewById(R.id.button_enroll);

        name.setText(event.getName());

        decline_button.setOnClickListener(view -> onLeaveLotteryClick(event, false));

        enroll_button.setOnClickListener(view -> onLeaveLotteryClick(event, true));

        return convertView;
    }

    /**
     * Method that triggers when user clicks enroll/decline for an event
     * @param event
     *  Event to decline/join
     * @param join
     *  True if user to join event, false if to decline
     */
    private void onLeaveLotteryClick(Event event, Boolean join) {
        // Get the current user ID
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Get Firestore instance
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Retrieve user's waitlist data
        retrieveUserAcceptedEvents(userId, db, event, join);
    }

    /**
     * Retrieves the user's won lottery events from Firebase
     * @param userId
     *  Current user's id
     * @param db
     *  Firebase database
     * @param event
     *  Event to unlink the user from (user leaves event lottery list)
     * @param join
     *  True if user to join event, false if to decline
     */
    private void retrieveUserAcceptedEvents(String userId, FirebaseFirestore db, Event event, Boolean join) {
        db.collection("user").document(userId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        ArrayList<String> accepted_events = (ArrayList<String>) documentSnapshot.get("accepted");

                        if (accepted_events != null && accepted_events.contains(event.getEventID()) && !join) {
                            removeUserFromLottery(userId, db, event, accepted_events);
                        }
                        else if (accepted_events != null && accepted_events.contains(event.getEventID()) && join) {
                            addUserFromLottery(userId, db, event, accepted_events);
                        }
                    } else {
                        showToast("User data not found");
                    }
                })
                .addOnFailureListener(e -> showToast("Failed to retrieve user data"));
    }

    /**
     * Method to add the USER from EVENT waitlist to enrolled
     * @param userId
     *  Current user's id
     * @param db
     *  Firebase database
     * @param event
     *  Event to enroll the user in
     * @param accepted
     *  The user's accepted events
     */
    private void addUserFromLottery(String userId, FirebaseFirestore db, Event event, ArrayList<String> accepted) {
        // Move the event from user's accepted events to user's enrolled events
        accepted.remove(event.getEventID());

        // Remove event first from lottery
        db.collection("user").document(userId)
                .update("accepted", accepted)
                .addOnSuccessListener(aVoid -> updateEventLottery(userId, db, event, true))
                .addOnFailureListener(e -> showToast("Failed to join event"));

        // Now add the event in enrolled
        db.collection("user").document(userId)
                .update("enrolled", FieldValue.arrayUnion(event.getEventID()))
                .addOnSuccessListener(aVoid -> addUserToEvent(userId, db, event));
    }

    /**
     * Method to add the user to the event's enrolled list
     * @param userId
     *  Current user's id
     * @param db
     *  Firebase database
     * @param event
     *  Event to enroll the user in
     */
    private void addUserToEvent(String userId, FirebaseFirestore db, Event event) {
        db.collection("user").document(userId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String full_name = documentSnapshot.getString("full_name");
                        HashMap<String, String> user = new HashMap<>();
                        user.put("user_id", userId);
                        user.put("full_name", full_name);

                        // Update the attendees field in the event document
                        db.collection("events").document(event.getEventID())
                                .update("attendees", FieldValue.arrayUnion(user))
                                .addOnSuccessListener(aVoid -> {
                                    // Toast message to show successful enrollment
                                    showToast("Successfully enrolled in event");
                                })
                                .addOnFailureListener(e -> {
                                    // Handle the failure case (optional)
                                    showToast("Failed to enroll in event");
                                });
                    } else {
                        showToast("User does not exist.");
                    }
                })
                .addOnFailureListener(e -> {
                    // Handle the failure case for getting the user document (optional)
                    showToast("Failed to retrieve user data.");
                });
    }

    /**
     * Method to remove the USER from EVENT lottery AND remove the EVENT from the USER accepted events
     * @param userId
     *  Current user's id
     * @param db
     *  Firebase database
     * @param event
     *  Event to unlink from user
     * @param accepted
     *  The user's accepted events
     */
    private void removeUserFromLottery(String userId, FirebaseFirestore db, Event event, ArrayList<String> accepted) {
        // Remove the event from user's accepted events
        accepted.remove(event.getEventID());

        db.collection("user").document(userId)
                .update("accepted", accepted)
                .addOnSuccessListener(aVoid -> updateEventLottery(userId, db, event, false))
                .addOnFailureListener(e -> showToast("Failed to decline event"));
    }

    /**
     * Method to update the event lottery list
     * @param userId
     *  Current user's id
     * @param db
     *  Firebase database
     * @param event
     *  Event to remove user from lottery
     * @param join
     *  True if user is joining event, false if user is declining event
     */
    private void updateEventLottery(String userId, FirebaseFirestore db, Event event, Boolean join) {
        db.collection("events").document(event.getEventID())
                .get()
                .addOnSuccessListener(eventDocument -> {
                    if (eventDocument.exists()) {
                        ArrayList<HashMap<String, String>> eventLottery =
                                (ArrayList<HashMap<String, String>>) eventDocument.get("lottery");

                        if (eventLottery != null) {
                            removeUserFromEventLottery(userId, eventLottery, db, event);
                        }
                    } else {
                        if (!join) {
                            showToast("Failed to retrieve event data");
                        }
                    }
                })
                .addOnFailureListener(e -> showToast("Failed to retrieve event data"));
    }

    /**
     * Method to remove the user from the event lottery (locally first and then use local waitlist to update Firebase waitlist)
     * @param userId
     *  Current user's id
     * @param eventLottery
     *  The local won lottery events of the user
     * @param db
     *  Firebase database
     * @param event
     *  Event to remove user from (lottery)
     */
    private void removeUserFromEventLottery(String userId, ArrayList<HashMap<String, String>> eventLottery,
                                            FirebaseFirestore db, Event event) {
        for (int i = 0; i < eventLottery.size(); i++) {
            HashMap<String, String> userLotteryEntry = eventLottery.get(i);
            if (userLotteryEntry.containsKey("user_id") && userLotteryEntry.get("user_id").equals(userId)) {
                eventLottery.remove(i);
                break;
            }
        }

        // Update the event's lottery in Firestore
        db.collection("events").document(event.getEventID())
                .update("lottery", eventLottery)
                .addOnSuccessListener(aVoid -> {
                    // Successfully updated event document
                    removeEventLocally(event);
                    showToast("Successfully declined event");
                })
                .addOnFailureListener(e -> showToast("Failed to update event lottery"));
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

