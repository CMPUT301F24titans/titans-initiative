package com.example.titans_project;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

// Purpose: Fragment to enable user to view an existing book in the wishlist.
// Design Rationale: I am using a ViewBookFragment here to keep the user interface on the main activity cleaner,
// this way the user will have an entirely new screen to view details for a specific book. Not to mention
// this will also reduce the clutter that would otherwise be in the main activity class.
public class NotificationDetailsView extends DialogFragment {

    private static final String TAG = "NotificationDeletion";

    // Listener interface for delete callback
    public interface OnDeleteListener {
        void onDelete(Notification notification);
    }

    private OnDeleteListener onDeleteListener;

    static NotificationDetailsView newInstance(Notification notification) {
        Bundle args = new Bundle();
        args.putSerializable("notification", notification);
        NotificationDetailsView fragment = new NotificationDetailsView();
        fragment.setArguments(args);
        return fragment;
    }

    public void setOnDeleteListener(OnDeleteListener listener) {
        this.onDeleteListener = listener;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.fragment_notification_details, null);

        TextView title = view.findViewById(R.id.notification_title);
        TextView description = view.findViewById(R.id.notification_description);

        // Retrieve the book from the arguments
        Bundle bundle = getArguments();
        Notification notification = (Notification) bundle.getSerializable("notification");

        // Auto-populate the fields with the current book information
        title.setText(notification.getTitle());
        description.setText(notification.getDescription());

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        AlertDialog dialog = builder
                .setView(view)
                .setNegativeButton("Cancel", null)
                .setPositiveButton("Delete", null)  // Temporarily set to null
                .create();

        dialog.show();  // Show the dialog to access the buttons

        // Handles the click manually
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
            if (notification != null) {
                deleteNotificationFromFirestore(notification);
            }
            if (onDeleteListener != null && notification != null) {
                onDeleteListener.onDelete(notification); // Trigger callback
            }
            dismiss();
        });
        return dialog;
    }

    private void deleteNotificationFromFirestore(Notification notification) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("user")
                .whereArrayContains("notification_list", notification.toMap()) // Check if the user contains the notification
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                            db.collection("user")
                                    .document(document.getId())
                                    .update("notification_list", FieldValue.arrayRemove(notification.toMap()))
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            // Successfully removed notification from Firestore
                                            Log.d(TAG, "Notification Successfully Deleted.");
                                        }
                                    });
                        }
                    }
                });
    }

}
