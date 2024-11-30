package com.example.titans_project;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WaitlistAdapter extends RecyclerView.Adapter<WaitlistAdapter.WaitlistViewHolder> {

    private final Context context;
    private final List<Attendee> waitlist;
    private final String eventID;

    public WaitlistAdapter(Context context, List<Attendee> waitlist, String eventID) {
        this.context = context;
        this.waitlist = waitlist;
        this.eventID = eventID;
    }

    @NonNull
    @Override
    public WaitlistViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.waitlist_item, parent, false);
        return new WaitlistViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WaitlistViewHolder holder, int position) {
        Attendee attendee = waitlist.get(position);

        holder.nameTextView.setText(attendee.getName());
        holder.emailTextView.setText(attendee.getUserId());

        holder.approveButton.setOnClickListener(v -> {
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            DocumentReference eventRef = db.collection("events").document(eventID);

            Map<String, String> attendeeMap = new HashMap<>();
            attendeeMap.put("full_name", attendee.getName());
            attendeeMap.put("user_id", attendee.getUserId());

            eventRef.update("waitlist", FieldValue.arrayRemove(attendeeMap))
                    .addOnSuccessListener(aVoid -> {
                        eventRef.update("waitlist", FieldValue.arrayUnion(attendeeMap))
                                .addOnSuccessListener(aVoid1 -> {
                                    waitlist.remove(position);
                                    notifyItemRemoved(position);
                                    notifyItemRangeChanged(position, waitlist.size());
                                    Toast.makeText(context, "User approved", Toast.LENGTH_SHORT).show();
                                })
                                .addOnFailureListener(e -> Toast.makeText(context, "Failed to add user to attendees.", Toast.LENGTH_SHORT).show());
                    })
                    .addOnFailureListener(e -> Toast.makeText(context, "Failed to remove user from waitlist.", Toast.LENGTH_SHORT).show());
        });

        holder.rejectButton.setOnClickListener(v -> {
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            DocumentReference eventRef = db.collection("events").document(eventID);

            Map<String, String> attendeeMap = new HashMap<>();
            attendeeMap.put("full_name", attendee.getName());
            attendeeMap.put("user_id", attendee.getUserId());

            eventRef.update("waitlist", FieldValue.arrayRemove(attendeeMap))
                    .addOnSuccessListener(aVoid -> {
                        waitlist.remove(position);
                        notifyItemRemoved(position);
                        notifyItemRangeChanged(position, waitlist.size());
                        Toast.makeText(context, "User rejected", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> Toast.makeText(context, "Failed to remove user from waitlist.", Toast.LENGTH_SHORT).show());
        });
    }

    @Override
    public int getItemCount() {
        return waitlist.size();
    }

    public static class WaitlistViewHolder extends RecyclerView.ViewHolder {
        TextView nameTextView, emailTextView;
        Button approveButton, rejectButton;

        public WaitlistViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.nameTextView);
            emailTextView = itemView.findViewById(R.id.emailTextView);
            approveButton = itemView.findViewById(R.id.approveButton);
            rejectButton = itemView.findViewById(R.id.rejectButton);
        }
    }
}
