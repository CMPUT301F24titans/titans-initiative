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

/**
 * Fragment that displays the details of a notification. Allows the user to view, and delete a notification
 * from their notification list. The fragment is designed to be used in a dialog for better user interaction.
 *
 * <p>The notification details are passed as a serializable object to this fragment, and the user can either
 * cancel or delete the notification. If the notification is deleted, it is removed from the Firestore database.</p>
 *
 * <p>Design rationale: This fragment is used to show notification details in a cleaner, separate screen
 * instead of cluttering the main activity.</p>
 */
public class NotificationDetailsView extends DialogFragment {

    private static final String TAG = "NotificationDeletion";

    // Listener interface for delete callback
    public interface OnDeleteListener {
        void onDelete(Notification notification);
    }

    private OnDeleteListener onDeleteListener;

    /**
     * Static method to create a new instance of this fragment with a specific Notification.
     *
     * @param notification The Notification object that contains details of the notification to be displayed.
     * @return A new instance of NotificationDetailsView with the notification passed as an argument.
     */

    static NotificationDetailsView newInstance(Notification notification) {
        Bundle args = new Bundle();
        args.putSerializable("notification", notification);
        NotificationDetailsView fragment = new NotificationDetailsView();
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Sets the OnDeleteListener callback to be triggered when a notification is deleted.
     *
     * @param listener The listener to be called when the notification is deleted.
     */

    public void setOnDeleteListener(OnDeleteListener listener) {
        this.onDeleteListener = listener;
    }

    /**
     * Called to create the dialog for displaying the notification details.
     *
     * @param savedInstanceState The saved instance state, if any.
     * @return A Dialog that shows the details of the notification and allows the user to delete it.
     */
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


    /**
     * Deletes the notification from the Firestore database by searching for the notification in the user's
     * notification list and removing it.
     *
     * @param notification The notification to be deleted from the database.
     */
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
