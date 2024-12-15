package com.example.titans_project;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

/**
 * Activity for viewing and managing created events.
 * Allows users to view a list of events, create new events, and delete or share existing events.
 */
public class CreatedEventsView extends AppCompatActivity {

    private LinearLayout eventsContainer;
    private TextView noEventsText;
    private FirebaseFirestore db;
    private CollectionReference eventRef;

    private FrameLayout qrPopupLayout;
    private ImageView qrImageView;
    private ImageView shareQrButton;
    private View closeQrButton;

    /**
     * Called when the activity is created. Initializes the UI elements and loads events.
     *
     * @param savedInstanceState the saved instance state of the activity
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = FirebaseFirestore.getInstance();
        eventRef = db.collection("events");

        setContentView(R.layout.fragment_created_events);

        eventsContainer = findViewById(R.id.eventsContainer);
        noEventsText = findViewById(R.id.no_events_text);
        qrPopupLayout = findViewById(R.id.qrPopupLayout);
        qrImageView = findViewById(R.id.qrImageView);
        shareQrButton = findViewById(R.id.shareQrButton);
        closeQrButton = findViewById(R.id.closeQrButton);

        ImageView backIcon = findViewById(R.id.back_icon);
        backIcon.setOnClickListener(v -> onBackPressed());

        // FAB to add new events
        View fabAddEvent = findViewById(R.id.fab_add_event);
        fabAddEvent.setOnClickListener(v -> {
            Intent createEventIntent = new Intent(CreatedEventsView.this, CreateEventView.class);
            startActivityForResult(createEventIntent, 1);
        });

        closeQrButton.setOnClickListener(v -> qrPopupLayout.setVisibility(View.GONE));
        // Load events from Firestore
        loadEvents();
    }

    /**
     * Loads events from Firestore and updates the UI.
     */
    private void loadEvents() {
        eventsContainer.removeAllViews();

        eventRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (DocumentSnapshot document : task.getResult()) {
                    // Extract event details from Firestore document
                    String eventName = document.getString("event_name");
                    String organizerName = document.getString("organizerName");
                    String eventDetails = document.getString("eventDetails");
                    String eventDate = document.getString("eventDate");
                    String eventTime = document.getString("eventTime");

                    // Add event to UI
                    addOrUpdateEvent(-1, eventName, organizerName, eventDetails, eventDate, eventTime, document.getId());
                }
                checkIfEventsExist();
            } else {
                Toast.makeText(CreatedEventsView.this, "Failed to load events", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Checks if any events are loaded, and updates the UI to show or hide the "No Events" message.
     */
    private void checkIfEventsExist() {
        if (eventsContainer.getChildCount() == 0) {
            noEventsText.setVisibility(View.VISIBLE);
        } else {
            noEventsText.setVisibility(View.GONE);
        }
    }

    /**
     * Adds a new event to the UI or updates an existing event.
     *
     * @param eventIndex       The index of the event in the list (or -1 for a new event).
     * @param eventName        The name of the event.
     * @param organizerName    The name of the event's organizer.
     * @param eventDetails     The details of the event.
     * @param eventDate        The date of the event.
     * @param eventTime        The time of the event.
     * @param eventDocumentId  The Firestore document ID of the event.
     */
    public void addOrUpdateEvent(int eventIndex, String eventName, String organizerName, String eventDetails, String eventDate, String eventTime, String eventDocumentId) {
        noEventsText.setVisibility(View.GONE);

        if (eventIndex == -1) {
            // New event - create a new view for this event
            View eventItemView = createEventViewItem(eventName, organizerName, eventDetails, eventDate, eventTime, eventDocumentId);
            eventsContainer.addView(eventItemView);

            // Store event data in Firestore
            HashMap<String, Object> eventData = new HashMap<>();
            eventData.put("event_name", eventName);
            eventData.put("organizerName", organizerName);
            eventData.put("eventDetails", eventDetails);
            eventData.put("eventDate", eventDate);
            eventData.put("eventTime", eventTime);

            // Save document using the event's unique Firestore document ID
            eventRef.document(eventDocumentId).set(eventData);

            Toast.makeText(CreatedEventsView.this, "Successfully created event: " + eventName, Toast.LENGTH_LONG).show();
        } else {
            // Update existing event - update the details in the view
            View eventItemView = eventsContainer.getChildAt(eventIndex);
            if (eventItemView != null) {
                TextView eventTitle = eventItemView.findViewById(R.id.eventTitle);
                TextView eventDetailsText = eventItemView.findViewById(R.id.eventDetails);
                TextView eventDateTimeText = eventItemView.findViewById(R.id.eventDateTime);

                eventTitle.setText(eventName + " by " + organizerName);
                eventDetailsText.setText(eventDetails);
                eventDateTimeText.setText(eventDate + " at " + eventTime);
            }
        }
        checkIfEventsExist();
    }

    /**
     * Creates a new view for a single event item.
     *
     * @param eventName       The name of the event.
     * @param organizerName   The name of the event's organizer.
     * @param eventDetails    The details of the event.
     * @param eventDate       The date of the event.
     * @param eventTime       The time of the event.
     * @param eventDocumentId The Firestore document ID of the event.
     * @return A View representing the event item.
     */
    private View createEventViewItem(String eventName, String organizerName, String eventDetails, String eventDate, String eventTime, String eventDocumentId) {
        View eventItemView = getLayoutInflater().inflate(R.layout.fragment_event_item, eventsContainer, false);
        TextView eventTitle = eventItemView.findViewById(R.id.eventTitle);
        TextView eventDetailsText = eventItemView.findViewById(R.id.eventDetails);
        TextView eventDateTimeText = eventItemView.findViewById(R.id.eventDateTime);
        ImageView menuIcon = eventItemView.findViewById(R.id.menuIcon);

        eventTitle.setText(eventName + " by " + organizerName);
        eventDetailsText.setText(eventDetails);
        eventDateTimeText.setText(eventDate + " at " + eventTime);

        int eventIndex = eventsContainer.getChildCount() - 1;
        menuIcon.setOnClickListener(v -> showPopupMenu(v, eventIndex, eventName, organizerName, eventDetails, eventDate, eventTime, eventDocumentId));

        return eventItemView;
    }



    /**
     * Shows the popup menu for an event.
     *
     * @param anchor            The view that the menu is anchored to.
     * @param eventIndex       The index of the event in the list.
     * @param eventName        The name of the event.
     * @param organizerName    The name of the event's organizer.
     * @param eventDetails     The details of the event.
     * @param eventDate        The date of the event.
     * @param eventTime        The time of the event.
     * @param eventDocumentId  The Firestore document ID of the event.
     */
    private void showPopupMenu(View anchor, int eventIndex, String eventName, String organizerName, String eventDetails, String eventDate, String eventTime, String eventDocumentId) {
        PopupMenu popupMenu = new PopupMenu(this, anchor);
        popupMenu.getMenuInflater().inflate(R.menu.event_menu, popupMenu.getMenu());

        popupMenu.setOnMenuItemClickListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.action_share_qr) {
                showQrCodePopup(eventName, organizerName, eventDetails, eventDate, eventTime);
                return true;
            } else if (itemId == R.id.action_view_details) {
                Intent viewEventDetailsIntent = new Intent(CreatedEventsView.this, EventDetailsView.class);
                viewEventDetailsIntent.putExtra("eventName", eventName);
                viewEventDetailsIntent.putExtra("organizerName", organizerName);
                viewEventDetailsIntent.putExtra("eventDetails", eventDetails);
                viewEventDetailsIntent.putExtra("eventDate", eventDate);
                viewEventDetailsIntent.putExtra("eventTime", eventTime);
                viewEventDetailsIntent.putExtra("eventDocumentId", eventDocumentId);
                startActivityForResult(viewEventDetailsIntent, 1);
                return true;
            } else if (itemId == R.id.action_delete_event) {
                confirmDeleteEvent(eventName, eventDocumentId, anchor);
                return true;
            } else {
                return false;
            }
        });
        popupMenu.show();
    }

    private void showQrCodePopup(String eventName, String organizerName, String eventDetails, String eventDate, String eventTime) {
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        try {
            String qrData = "Event: " + eventName + "\nOrganizer: " + organizerName + "\nDetails: " + eventDetails + "\nDate: " + eventDate + "\nTime: " + eventTime;
            BitMatrix bitMatrix = qrCodeWriter.encode(qrData, BarcodeFormat.QR_CODE, 400, 400);
            Bitmap qrBitmap = Bitmap.createBitmap(400, 400, Bitmap.Config.RGB_565);

            for (int x = 0; x < 400; x++) {
                for (int y = 0; y < 400; y++) {
                    qrBitmap.setPixel(x, y, bitMatrix.get(x, y) ? Color.BLACK : Color.WHITE);
                }
            }

            qrImageView.setImageBitmap(qrBitmap);

            shareQrButton.setOnClickListener(v -> {
                String path = MediaStore.Images.Media.insertImage(getContentResolver(), qrBitmap, "QR Code", null);
                Uri uri = Uri.parse(path);

                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("image/*");
                shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
                startActivity(Intent.createChooser(shareIntent, "Share QR Code"));
            });

            qrPopupLayout.setVisibility(View.VISIBLE);
        } catch (WriterException e) {
            e.printStackTrace();
        }
    }

    /**
     * Displays a confirmation dialog before deleting an event.
     *
     * @param eventName       The name of the event.
     * @param eventDocumentId The Firestore document ID of the event.
     * @param anchor          The view to anchor the dialog to.
     */
    private void confirmDeleteEvent(String eventName, String eventDocumentId, View anchor) {
        new AlertDialog.Builder(this)
                .setTitle("Delete Event")
                .setMessage("Are you sure you want to delete the event: " + eventName + "?")
                .setPositiveButton("Yes", (dialog, which) -> deleteEvent(eventDocumentId))
                .setNegativeButton("No", null)
                .show();
    }

    /**
     * Deletes an event from Firestore and updates the UI.
     *
     * @param eventDocumentId The Firestore document ID of the event.
     */
    private void deleteEvent(String eventDocumentId) {
        eventRef.document(eventDocumentId).delete()
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(CreatedEventsView.this, "Event deleted", Toast.LENGTH_SHORT).show();
                    loadEvents();  // Refresh the event list
                })
                .addOnFailureListener(e -> Toast.makeText(CreatedEventsView.this, "Failed to delete event", Toast.LENGTH_SHORT).show());
    }

    /**
     * Handles the result of an activity started for a result (e.g., creating or viewing an event).
     *
     * @param requestCode The request code passed to startActivityForResult.
     * @param resultCode  The result code returned by the activity.
     * @param data        The data returned by the activity.
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && data != null) {
            String eventName = data.getStringExtra("eventName");
            String organizerName = data.getStringExtra("organizerName");
            String eventDetails = data.getStringExtra("eventDetails");
            String eventDate = data.getStringExtra("eventDate");
            String eventTime = data.getStringExtra("eventTime");

            String eventDocumentId = data.getStringExtra("eventDocumentId");
            addOrUpdateEvent(-1, eventName, organizerName, eventDetails, eventDate, eventTime, eventDocumentId);
        }
    }

    /**
     * Removes an event item from the UI.
     *
     * @param eventItem The event item view to remove.
     */
    public void removeEvent(View eventItem) {
        eventsContainer.removeView(eventItem);
        checkIfEventsExist();  // Update UI after removal
    }
}
