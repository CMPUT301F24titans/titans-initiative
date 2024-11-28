package com.example.titans_project;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.time.LocalDate;
import java.util.ArrayList;

public class SendNotification extends DialogFragment {

    private static final String TAG = "AddNotification";

    private ArrayList<String> users; // Add this to hold the list of users

    // Accept a list of users when initializing the fragment
    static SendNotification newInstance(ArrayList<String> users) {
        Bundle args = new Bundle();
        args.putStringArrayList("users", users);  // Pass the list of user IDs to the fragment
        SendNotification fragment = new SendNotification();
        fragment.setArguments(args);
        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.fragment_send_notification, null);

        EditText editTitle = view.findViewById(R.id.edit_title);
        EditText editDescription = view.findViewById(R.id.edit_description);

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        AlertDialog dialog = builder
                .setView(view)
                .setNegativeButton("Cancel", null)
                .setPositiveButton("Send", null)  // Temporarily set to null
                .create();

        dialog.show();

        // Handle the send button click manually
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
            String title = editTitle.getText().toString();
            String description = editDescription.getText().toString();

            // Create Notification
            Notification notification = new Notification(title, description, LocalDate.now().toString());
            // Send notification to selected users in the list
            dialog.dismiss();
        });

        return dialog;
    }
}

