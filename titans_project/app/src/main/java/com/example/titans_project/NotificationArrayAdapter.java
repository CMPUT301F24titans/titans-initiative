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

import org.w3c.dom.Text;

import java.util.ArrayList;


/**
 * This is a class that defines an array adapter for notifications
 */
public class NotificationArrayAdapter extends ArrayAdapter<Notification> {

    private Context context;
    private ArrayList<Notification> notifications;

    /**
     * Constructor for notifications
     * @param context
     * @param notifications
     *  ArrayList of notifications
     */
    public NotificationArrayAdapter(Context context, ArrayList<Notification> notifications) {
        super(context, 0, notifications);  // Call the superclass constructor
        this.context = context;
        this.notifications = notifications;
    }

    /**
     * Function for getting the view for a notification
     * @param position
     * @param convertView
     * @param parent
     * @return
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.content_notifications, parent, false);
        }

        Notification notification = getItem(position);  // Use getItem to retrieve the event at the given position
        TextView title = convertView.findViewById(R.id.title);
        TextView description = convertView.findViewById(R.id.description);
        TextView date = convertView.findViewById(R.id.date);

        assert notification != null;
        title.setText(notification.getTitle());
        description.setText(notification.getDescription());
        date.setText(notification.getDate());

        return convertView;
    }

}
