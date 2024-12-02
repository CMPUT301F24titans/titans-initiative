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
 * Custom ArrayAdapter used to display a list of User profiles in a ListView.
 * This adapter binds a list of User objects to a list item layout where each item
 * represents a profile. The adapter inflates the layout for each item and
 * populates it with the user's name.
 *
 * <p>The {@link ProfilesArrayAdapter} allows you to easily display a list of User objects
 * in the context of the application. Each user profile is shown with a simple TextView
 * containing the user's name.</p>
 *
 * @see ArrayAdapter
 */
public class ProfilesArrayAdapter extends ArrayAdapter<User> {

    private Context context;       // Context for inflating views
    private ArrayList<User> users; // List of users to display

    /**
     * Constructor for the ProfilesArrayAdapter.
     *
     * @param context The context where the adapter is being used (usually an Activity or Fragment).
     * @param users   The list of User objects to be displayed.
     */
    public ProfilesArrayAdapter(Context context, ArrayList<User> users) {
        super(context, 0, users);  // Call the superclass constructor to initialize the adapter
        this.context = context;
        this.users = users;
    }

    /**
     * Returns a view for each item in the ListView. This method is responsible for
     * inflating the item layout and populating it with data from the User object.
     *
     * @param position    The position of the item within the list (in this case, the list of users).
     * @param convertView The recycled view to reuse, if available. If it's null, a new view will be created.
     * @param parent      The parent view that this view will eventually be attached to (not used in this case).
     * @return The View object for the item at the given position in the list.
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // If the convertView is null (i.e., the view is being created for the first time),
        // inflate the layout for the profile item.
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.content_browse_profiles, parent, false);
        }

        // Retrieve the User object at the given position
        User user = getItem(position);

        // Find the TextView where we want to display the user's name
        TextView name = convertView.findViewById(R.id.user_name);

        // Ensure that the user object is not null
        assert user != null;

        // Set the user's name to the TextView
        name.setText(user.getName());

        // Return the fully populated view for the item
        return convertView;
    }
}
