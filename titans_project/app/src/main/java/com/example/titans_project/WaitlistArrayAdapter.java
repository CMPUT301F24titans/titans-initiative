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
 * Adapter for displaying the waitlist of attendees in a RecyclerView.
 * Binds data from a list of `Attendee` objects to the views in the `RecyclerView` for each attendee.
 */
public class WaitlistArrayAdapter extends RecyclerView.Adapter<WaitlistArrayAdapter.WaitlistViewHolder> {

    private final Context context;  // Context for inflating views and accessing resources
    private final List<Attendee> waitlist;  // List of attendees to be displayed in the RecyclerView
    private final String eventID;  // Event ID associated with the waitlist

    /**
     * Constructor to initialize the adapter with context, waitlist, and event ID.
     *
     * @param context The context in which the adapter is being used.
     * @param waitlist The list of attendees on the waitlist.
     * @param eventID The ID of the event associated with the waitlist.
     */
    public WaitlistArrayAdapter(Context context, List<Attendee> waitlist, String eventID) {
        this.context = context;
        this.waitlist = waitlist;
        this.eventID = eventID;
    }

    /**
     * Creates a new view holder for a waitlist item.
     * Inflates the layout for each waitlist item and returns a new `WaitlistViewHolder`.
     *
     * @param parent The parent view group into which the new view will be added.
     * @param viewType The type of view to create (unused in this implementation).
     * @return A new `WaitlistViewHolder` instance.
     */
    @NonNull
    @Override
    public WaitlistViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the layout for each waitlist item
        View view = LayoutInflater.from(context).inflate(R.layout.content_waitlist, parent, false);
        return new WaitlistViewHolder(view);
    }

    /**
     * Binds the data for a particular attendee to the view holder.
     * Updates the views in the `WaitlistViewHolder` with the attendee's information.
     *
     * @param holder The view holder that contains the views to be updated.
     * @param position The position of the attendee in the waitlist list.
     */
    @Override
    public void onBindViewHolder(@NonNull WaitlistViewHolder holder, int position) {
        Attendee attendee = waitlist.get(position);

        // Set the name and user ID text views for the attendee
        holder.nameTextView.setText(attendee.getName());
        holder.userIdTextView.setText(attendee.getUserId());
    }

    /**
     * Returns the total number of items in the waitlist.
     *
     * @return The size of the waitlist.
     */
    @Override
    public int getItemCount() {
        return waitlist.size();
    }

    /**
     * ViewHolder class for holding references to the views in each waitlist item.
     * This is used to optimize view lookup for each item in the RecyclerView.
     */
    public static class WaitlistViewHolder extends RecyclerView.ViewHolder {
        TextView nameTextView, userIdTextView;

        /**
         * Constructor that initializes the views for a single waitlist item.
         *
         * @param itemView The view representing a single item in the RecyclerView.
         */
        public WaitlistViewHolder(@NonNull View itemView) {
            super(itemView);
            // Initialize the views using their IDs
            nameTextView = itemView.findViewById(R.id.nameTextView);
            userIdTextView = itemView.findViewById(R.id.userIdTextView);
        }
    }
}
