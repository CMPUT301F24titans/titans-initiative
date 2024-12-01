package com.example.titans_project;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class WaitlistArrayAdapter extends RecyclerView.Adapter<WaitlistArrayAdapter.WaitlistViewHolder> {

    private final Context context;
    private final List<Attendee> waitlist;
    private final String eventID;

    public WaitlistArrayAdapter(Context context, List<Attendee> waitlist, String eventID) {
        this.context = context;
        this.waitlist = waitlist;
        this.eventID = eventID;
    }

    @NonNull
    @Override
    public WaitlistViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.content_waitlist, parent, false);
        return new WaitlistViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WaitlistViewHolder holder, int position) {
        Attendee attendee = waitlist.get(position);

        holder.nameTextView.setText(attendee.getName());
        holder.userIdTextView.setText(attendee.getUserId());
    }

    @Override
    public int getItemCount() {
        return waitlist.size();
    }

    public static class WaitlistViewHolder extends RecyclerView.ViewHolder {
        TextView nameTextView, userIdTextView;

        public WaitlistViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.nameTextView);
            userIdTextView = itemView.findViewById(R.id.userIdTextView);
        }
    }
}
