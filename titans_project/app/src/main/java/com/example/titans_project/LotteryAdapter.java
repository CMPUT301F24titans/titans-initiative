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

public class LotteryAdapter extends RecyclerView.Adapter<LotteryAdapter.LotteryViewHolder> {

    private Context context;
    private List<Map<String, String>> lotteryList;
    private String eventID;
    private FirebaseFirestore db;

    public LotteryAdapter(Context context, List<Map<String, String>> lotteryList, String eventID) {
        this.context = context;
        this.lotteryList = lotteryList;
        this.eventID = eventID;
        this.db = FirebaseFirestore.getInstance();
    }

    @NonNull
    @Override
    public LotteryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.content_lottery, parent, false);
        return new LotteryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LotteryViewHolder holder, int position) {
        Map<String, String> user = lotteryList.get(position);
        String fullName = user.get("full_name");
        String userId = user.get("user_id");

        holder.userName.setText(fullName + " (" + userId + ")");

        holder.rejectButton.setOnClickListener(v -> {
            removeUserFromLottery(user, position);
        });
    }

    @Override
    public int getItemCount() {
        return lotteryList.size();
    }

    private void removeUserFromLottery(Map<String, String> user, int position) {
        String userId = user.get("user_id");

        // Remove user from the lottery list in Firestore
        db.collection("events").document(eventID).update("lottery", com.google.firebase.firestore.FieldValue.arrayRemove(user))
                .addOnSuccessListener(aVoid -> {
                    // Remove event from the user's accepted list in Firestore
                    db.collection("user").document(userId)
                            .update("accepted", com.google.firebase.firestore.FieldValue.arrayRemove(eventID))
                            .addOnSuccessListener(aVoid2 -> {
                                Toast.makeText(context, "User removed from lottery.", Toast.LENGTH_SHORT).show();
                                // Update local list
                                lotteryList.remove(position);
                                notifyItemRemoved(position);
                            })
                            .addOnFailureListener(e -> {
                                Log.e("LotteryAdapter", "Failed to update user's accepted list: " + e.getMessage());
                                Toast.makeText(context, "Failed to update user's accepted list.", Toast.LENGTH_SHORT).show();
                            });
                })
                .addOnFailureListener(e -> {
                    Log.e("LotteryAdapter", "Failed to remove user from lottery: " + e.getMessage());
                    Toast.makeText(context, "Failed to remove user from lottery.", Toast.LENGTH_SHORT).show();
                });
    }


    public static class LotteryViewHolder extends RecyclerView.ViewHolder {
        TextView userName;
        Button rejectButton;

        public LotteryViewHolder(@NonNull View itemView) {
            super(itemView);
            userName = itemView.findViewById(R.id.userName);
            rejectButton = itemView.findViewById(R.id.rejectButton);
        }
    }
}
