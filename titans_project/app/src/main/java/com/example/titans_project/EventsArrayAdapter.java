package com.example.titans_project;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

/**
 * Custom ArrayAdapter for displaying event details in a ListView or RecyclerView.
 * This adapter binds each Event object to a layout view representing the event's name and date.
 */
public class EventsArrayAdapter extends ArrayAdapter<Event> {

    private Context context;       // The context of the activity or fragment using this adapter
    private ArrayList<Event> events; // The list of events to display

    /**
     * Constructor for initializing the EventsArrayAdapter with context and a list of events.
     *
     * @param context The context in which the adapter is used.
     * @param events  The list of Event objects to display in the adapter.
     */
    public EventsArrayAdapter(Context context, ArrayList<Event> events) {
        super(context, 0, events);  // Call the superclass constructor with a layout resource and list of events
        this.context = context;
        this.events = events;
    }

    /**
     * Creates and returns a view for each event item in the ListView or RecyclerView.
     * This method is responsible for binding data from the Event object to the UI elements.
     *
     * @param position    The position of the item within the data set.
     * @param convertView A recycled view to reuse (if available).
     * @param parent      The parent view that this item view will be attached to.
     * @return The view representing the event at the specified position.
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // If the view is not reused, inflate a new view from the layout resource
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.content_browse_events, parent, false);
        }

        // Get the event object at the current position
        Event event = getItem(position);

        // Find the TextViews for displaying event name and date
        TextView name = convertView.findViewById(R.id.event_name);
        TextView date = convertView.findViewById(R.id.event_date);

        // Set the text of the TextViews based on the event object data
        if (event != null) {
            name.setText(event.getName());  // Set the event's name
            date.setText(event.getEventDate());  // Set the event's date
        }

        // Return the updated view for this event
        return convertView;
    }
}
