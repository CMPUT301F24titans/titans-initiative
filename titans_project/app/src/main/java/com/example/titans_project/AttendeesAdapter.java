package com.example.titans_project;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

/**
 * Adapter to display a list of attendees in a RecyclerView.
 */
public class AttendeesAdapter extends RecyclerView.Adapter<AttendeesAdapter.AttendeeViewHolder> {
    private final Context context;
    private final List<Attendee> attendees;

    /**
     * Constructor to initialize the adapter with context and the list of attendees.
     *
     * @param context the context in which the adapter is being used.
     * @param attendees the list of attendees to display.
     */
    public AttendeesAdapter(Context context, List<Attendee> attendees) {
        this.context = context;
        this.attendees = attendees;
    }

    /**
     * Creates a new ViewHolder for the RecyclerView.
     *
     * @param parent the parent ViewGroup.
     * @param viewType the view type of the new View.
     * @return a new AttendeeViewHolder.
     */
    @NonNull
    @Override
    public AttendeeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.content_waitlist, parent, false);
        return new AttendeeViewHolder(view);
    }

    /**
     * Binds the data for a specific attendee to the ViewHolder.
     *
     * @param holder the ViewHolder to bind the data to.
     * @param position the position of the attendee in the list.
     */
    @Override
    public void onBindViewHolder(@NonNull AttendeeViewHolder holder, int position) {
        Attendee attendee = attendees.get(position);
        holder.nameTextView.setText(attendee.getName());
        holder.userIdTextView.setText(attendee.getUserId());
    }

    /**
     * Returns the total number of attendees in the list.
     *
     * @return the size of the attendees list.
     */
    @Override
    public int getItemCount() {
        return attendees.size();
    }

    /**
     * ViewHolder class to hold references to the views for each attendee.
     */
    public static class AttendeeViewHolder extends RecyclerView.ViewHolder {
        TextView nameTextView, userIdTextView;

        /**
         * Constructor to initialize the views for the attendee.
         *
         * @param itemView the view for an individual item in the RecyclerView.
         */
        public AttendeeViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.nameTextView);
            userIdTextView = itemView.findViewById(R.id.userIdTextView);
        }
    }
}
