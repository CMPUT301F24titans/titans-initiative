package com.example.titans_project;

import static java.security.AccessController.getContext;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

public class EventsArrayAdapter extends ArrayAdapter<Event> {

    private Context context;
    private ArrayList<Event> events;

    public EventsArrayAdapter(Context context, ArrayList<Event> events) {
        super(context, 0, events);  // Call the superclass constructor
        this.context = context;
        this.events = events;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.content_browse_events, parent, false);
        }

        Event event = getItem(position);  // Use getItem to retrieve the event at the given position
        TextView name = convertView.findViewById(R.id.event_name);
        TextView date = convertView.findViewById(R.id.event_date);

        name.setText(event.getName());
        date.setText(event.getEventDate());

        return convertView;
    }

}
