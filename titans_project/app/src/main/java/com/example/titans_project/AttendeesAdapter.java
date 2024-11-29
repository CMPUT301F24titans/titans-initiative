package com.example.titans_project;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class AttendeesAdapter extends RecyclerView.Adapter<AttendeesAdapter.AttendeeViewHolder> {
    private final Context context;
    private final List<Attendee> attendees;

    public AttendeesAdapter(Context context, List<Attendee> attendees) {
        this.context = context;
        this.attendees = attendees;
    }

    @NonNull
    @Override
    public AttendeeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.attendee_item, parent, false);
        return new AttendeeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AttendeeViewHolder holder, int position) {
        Attendee attendee = attendees.get(position);
        holder.nameTextView.setText(attendee.getName());
        holder.emailTextView.setText(attendee.getUserId());
    }

    @Override
    public int getItemCount() {
        return attendees.size();
    }

    public static class AttendeeViewHolder extends RecyclerView.ViewHolder {
        TextView nameTextView, emailTextView;

        public AttendeeViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.attendee_name);
            emailTextView = itemView.findViewById(R.id.attendee_email);
        }
    }
}
