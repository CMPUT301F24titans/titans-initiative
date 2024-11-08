package com.example.titans_project;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.common.BitMatrix;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.CollectionReference;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;

public class CreatedEventsView extends AppCompatActivity {

    private LinearLayout eventsContainer;
    private TextView noEventsText;
    private int editingEventIndex = -1;
    private FirebaseFirestore db;
    private CollectionReference eventRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = FirebaseFirestore.getInstance();
        eventRef = db.collection("events");


        setContentView(R.layout.fragment_created_events);

        eventsContainer = findViewById(R.id.eventsContainer);
        noEventsText = findViewById(R.id.no_events_text);

        ImageView backIcon = findViewById(R.id.back_icon);
        backIcon.setOnClickListener(v -> onBackPressed());

        // FAB to add new events
        View fabAddEvent = findViewById(R.id.fab_add_event);
        fabAddEvent.setOnClickListener(v -> {
            editingEventIndex = -1; // Reset for new event
            Intent createEventIntent = new Intent(CreatedEventsView.this, CreateEventView.class);
            startActivityForResult(createEventIntent, 1);
        });

        checkIfEventsExist();
    }

    private void checkIfEventsExist() {
        // If there are no events in the container, show the placeholder text
        if (eventsContainer.getChildCount() == 0) {
            noEventsText.setVisibility(View.VISIBLE);
        } else {
            noEventsText.setVisibility(View.GONE);
        }
    }

    // Add or update event in the list
    public void addOrUpdateEvent(int eventIndex, String eventName, String organizerName, String eventDetails, String eventDate, String eventTime) {
        noEventsText.setVisibility(View.GONE);

        if (eventIndex == -1) {
            // New event
            View eventItemView = createEventViewItem(eventName, organizerName, eventDetails, eventDate, eventTime);
            eventsContainer.addView(eventItemView);
            HashMap<String, Object> eventData = new HashMap<>();
            eventData.put("event_name", eventName);
            eventData.put("organizerName", organizerName);
            eventData.put("eventDetails", eventDetails);
            eventData.put("eventDate", eventDate);
            eventData.put("eventTime", eventTime);

            // Putting event into Firebase using eventName + organizerName + eventDate as unique identifier
            eventRef.document(eventName+organizerName+eventDate).set(eventData);

            Toast.makeText(CreatedEventsView.this, "Successfully created event, Event Name: " + eventName, Toast.LENGTH_LONG).show();
        } else {
            // Update existing event
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
        checkIfEventsExist(); // Recheck if there are any events
    }

    private View createEventViewItem(String eventName, String organizerName, String eventDetails, String eventDate, String eventTime) {
        View eventItemView = getLayoutInflater().inflate(R.layout.fragment_event_item, eventsContainer, false);
        TextView eventTitle = eventItemView.findViewById(R.id.eventTitle);
        TextView eventDetailsText = eventItemView.findViewById(R.id.eventDetails);
        TextView eventDateTimeText = eventItemView.findViewById(R.id.eventDateTime);
        ImageView menuIcon = eventItemView.findViewById(R.id.menuIcon);

        eventTitle.setText(eventName + " by " + organizerName);
        eventDetailsText.setText(eventDetails);
        eventDateTimeText.setText(eventDate + " at " + eventTime);

        int eventIndex = eventsContainer.getChildCount();
        menuIcon.setOnClickListener(v -> showPopupMenu(v, eventIndex, eventName, organizerName, eventDetails, eventDate, eventTime));

        return eventItemView;
    }

    // Show event-specific options in a menu
    private void showPopupMenu(View anchor, int eventIndex, String eventName, String organizerName, String eventDetails, String eventDate, String eventTime) {
        PopupMenu popupMenu = new PopupMenu(this, anchor);
        popupMenu.getMenuInflater().inflate(R.menu.event_menu, popupMenu.getMenu());

        popupMenu.setOnMenuItemClickListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.action_share_qr) {
                showQrCodePopup(eventName, organizerName, eventDetails, eventDate, eventTime);
                return true;
            } else if (itemId == R.id.action_view_details) {
                editingEventIndex = eventIndex;
                Intent viewEventDetailsIntent = new Intent(CreatedEventsView.this, EventDetailsView.class);
                viewEventDetailsIntent.putExtra("eventName", eventName);
                viewEventDetailsIntent.putExtra("organizerName", organizerName);
                viewEventDetailsIntent.putExtra("eventDetails", eventDetails);
                viewEventDetailsIntent.putExtra("eventDate", eventDate);
                viewEventDetailsIntent.putExtra("eventTime", eventTime);
                viewEventDetailsIntent.putExtra("eventIndex", eventIndex);
                startActivityForResult(viewEventDetailsIntent, 1); // Handle results from EventDetailsView
                return true;
            } else if (itemId == R.id.action_delete_event) {
                removeEvent(anchor); // Immediate deletion from the UI
                return true;
            } else {
                return false;
            }
        });
        popupMenu.show();
    }

    // Show QR code for event sharing
    private void showQrCodePopup(String eventName, String organizerName, String eventDetails, String eventDate, String eventTime) {
        String eventData = "Event: " + eventName + "\nOrganizer: " + organizerName + "\nDetails: " + eventDetails + "\nDate: " + eventDate + "\nTime: " + eventTime;

        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        try {
            BitMatrix bitMatrix = qrCodeWriter.encode(eventData, BarcodeFormat.QR_CODE, 200, 200);
            Bitmap qrBitmap = toBitmap(bitMatrix);

            View qrPopupView = LayoutInflater.from(this).inflate(R.layout.qr_popup, eventsContainer, false);
            ImageView qrImageView = qrPopupView.findViewById(R.id.qrCodeImage);
            qrImageView.setImageBitmap(qrBitmap);

            ImageView shareButton = qrPopupView.findViewById(R.id.shareQrButton);
            shareButton.setOnClickListener(v -> shareQrCode(qrBitmap));

            qrPopupView.findViewById(R.id.closeQrPopup).setOnClickListener(v -> eventsContainer.removeView(qrPopupView));

            eventsContainer.addView(qrPopupView);

        } catch (WriterException e) {
            e.printStackTrace();
        }
    }

    private Bitmap toBitmap(BitMatrix matrix) {
        int width = matrix.getWidth();
        int height = matrix.getHeight();
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                bitmap.setPixel(x, y, matrix.get(x, y) ? Color.BLACK : Color.WHITE);
            }
        }
        return bitmap;
    }

    private void shareQrCode(Bitmap qrBitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        qrBitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();

        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("image/png");
        shareIntent.putExtra(Intent.EXTRA_STREAM, byteArray);
        startActivity(Intent.createChooser(shareIntent, "Share QR Code"));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && data != null) {
            String eventName = data.getStringExtra("eventName");
            String organizerName = data.getStringExtra("organizerName");
            String eventDetails = data.getStringExtra("eventDetails");
            String eventDate = data.getStringExtra("eventDate");
            String eventTime = data.getStringExtra("eventTime");

            int eventIndex = data.getIntExtra("eventIndex", -1);
            addOrUpdateEvent(eventIndex, eventName, organizerName, eventDetails, eventDate, eventTime);
        }
    }

    // This method removes the event and updates the UI immediately
    public void removeEvent(View eventItem) {
        eventsContainer.removeView(eventItem);
        checkIfEventsExist();  // Update UI after removal
    }
}
