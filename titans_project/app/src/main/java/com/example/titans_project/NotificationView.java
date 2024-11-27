package com.example.titans_project;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * This is a class that defines the My Created Events activity of the app
 */
public class NotificationView extends AppCompatActivity {
    private ListView notificationList;
    private ArrayList<Notification> notificationDataList;
    private NotificationArrayAdapter notificationArrayAdapter;
    private Button return_button;
    Intent notification_detail = new Intent();
    private FirebaseFirestore db;
    private DocumentReference userRef;
    private static final String TAG = "Notification";
    private FirebaseAuth mAuth;


    /**
     * This runs after the onStart function above
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);

        // Initialize Firebase
        FirebaseApp.initializeApp(this);
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        db = FirebaseFirestore.getInstance();

        userRef = db.collection("user").document(user.getUid());

        notificationList = findViewById(R.id.listview_notifications);
        return_button = findViewById(R.id.button_return);

        notificationDataList = new ArrayList<>();
        notificationArrayAdapter = new NotificationArrayAdapter(this, notificationDataList);
        notificationList.setAdapter(notificationArrayAdapter);

        userRef.get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        // Get the notification list from the document
                        List<Map<String, Object>> notifications = (List<Map<String, Object>>) documentSnapshot.get("notification_list");
                        if (notifications != null) {
                            // Clear the existing list and add the new notifications
                            notificationDataList.clear();

                            // Convert Firestore maps to Notification objects
                            for (Map<String, Object> notificationMap : notifications) {
                                String title = (String) notificationMap.get("title");
                                String description = (String) notificationMap.get("description");
                                String date = (String) notificationMap.get("date");
                                notificationDataList.add(new Notification(title, description, date));
                            }

                            notificationArrayAdapter.notifyDataSetChanged();  // Refresh the ListView
                        }
                    }
                })
                .addOnFailureListener(e -> {
                    // Handle error
                    Log.w(TAG, "Failed to retrieve notifications", e);
                });

        return_button.setOnClickListener(new View.OnClickListener() {
            /**
             * User clicks on Return button, return to previous activity
             * @param view
             */
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        notificationList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            /**
             * User clicks on an notification in the notifications list, send user to Notification Detail activity
             */
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

            }
        });
    }
}
