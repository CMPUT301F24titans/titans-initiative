package com.example.titans_project;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;
import java.util.Map;

/**
 * Adapter for displaying users in the lottery list in a RecyclerView.
 * This adapter binds user data to the RecyclerView and allows removal of users from the lottery list.
 */
public class LotteryAdapter extends RecyclerView.Adapter<LotteryAdapter.LotteryViewHolder> {

    private Context context;             // Context for accessing application resources and displaying Toasts
    private List<Map<String, String>> lotteryList;   // List of users in the lottery (each user represented as a Map)
    private String eventID;              // The ID of the event associated with the lottery
    private FirebaseFirestore db;        // Firebase Firestore instance to interact with the database

    /**
     * Constructor for the LotteryAdapter.
     *
     * @param context    The context for accessing resources like Toasts.
     * @param lotteryList A list of users to be displayed in the lottery.
     * @param eventID    The ID of the event associated with the lottery.
     */
    public LotteryAdapter(Context context, List<Map<String, String>> lotteryList, String eventID) {
        this.context = context;
        this.lotteryList = lotteryList;
        this.eventID = eventID;
        this.db = FirebaseFirestore.getInstance();
    }

    /**
     * Creates a new ViewHolder for displaying a single item in the RecyclerView.
     *
     * @param parent   The parent ViewGroup that this item will be a part of.
     * @param viewType The type of view to be created (for different item layouts, if necessary).
     * @return A new instance of LotteryViewHolder.
     */
    @NonNull
    @Override
    public LotteryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the item layout for each user in the lottery list
        View view = LayoutInflater.from(context).inflate(R.layout.content_lottery, parent, false);
        return new LotteryViewHolder(view);
    }

    /**
     * Binds the user data to the views in the ViewHolder.
     *
     * @param holder   The ViewHolder that holds the views for this item.
     * @param position The position of the item in the list.
     */
    @Override
    public void onBindViewHolder(@NonNull LotteryViewHolder holder, int position) {
        // Get the user data at the current position
        Map<String, String> user = lotteryList.get(position);
        String fullName = user.get("full_name");
        String userId = user.get("user_id");

        // Set the user's full name and user ID in the respective TextView
        holder.userName.setText(fullName + " (" + userId + ")");

        // Set an OnClickListener on the "Reject" button to remove the user from the lottery
        holder.rejectButton.setOnClickListener(v -> {
            removeUserFromLottery(user, position);
        });
    }

    /**
     * Returns the total number of items (users) in the lottery list.
     *
     * @return The number of users in the lottery list.
     */
    @Override
    public int getItemCount() {
        return lotteryList.size();
    }

    /**
     * Removes a user from the lottery list in Firestore and updates the local list.
     *
     * @param user     The user to be removed from the lottery.
     * @param position The position of the user in the lottery list.
     */
    private void removeUserFromLottery(Map<String, String> user, int position) {
        String userId = user.get("user_id");

        // Remove the user from the event's lottery list in Firestore
        db.collection("events").document(eventID).update("lottery", com.google.firebase.firestore.FieldValue.arrayRemove(user))
                .addOnSuccessListener(aVoid -> {
                    // Once the user is removed from the lottery, also remove the event from the user's "accepted" list in Firestore
                    db.collection("user").document(userId)
                            .update("accepted", com.google.firebase.firestore.FieldValue.arrayRemove(eventID))
                            .addOnSuccessListener(aVoid2 -> {
                                // Notify the user that they have been removed from the lottery
                                Toast.makeText(context, "User removed from lottery.", Toast.LENGTH_SHORT).show();

                                // Update the local lottery list and notify the RecyclerView to remove the item
                                lotteryList.remove(position);
                                notifyItemRemoved(position);
                            })
                            .addOnFailureListener(e -> {
                                // Log and show an error if removing the user from their "accepted" list fails
                                Log.e("LotteryAdapter", "Failed to update user's accepted list: " + e.getMessage());
                                Toast.makeText(context, "Failed to update user's accepted list.", Toast.LENGTH_SHORT).show();
                            });
                })
                .addOnFailureListener(e -> {
                    // Log and show an error if removing the user from the lottery fails
                    Log.e("LotteryAdapter", "Failed to remove user from lottery: " + e.getMessage());
                    Toast.makeText(context, "Failed to remove user from lottery.", Toast.LENGTH_SHORT).show();
                });
    }

    /**
     * ViewHolder for an item in the lottery list. This holds the views that represent a user in the lottery.
     */
    public static class LotteryViewHolder extends RecyclerView.ViewHolder {
        TextView userName;       // TextView to display the user's full name and ID
        Button rejectButton;     // Button to reject (remove) the user from the lottery

        /**
         * Constructor to initialize the views for this item.
         *
         * @param itemView The view representing a single item in the lottery list.
         */
        public LotteryViewHolder(@NonNull View itemView) {
            super(itemView);
            userName = itemView.findViewById(R.id.userName);
            rejectButton = itemView.findViewById(R.id.rejectButton);
        }
    }
}
