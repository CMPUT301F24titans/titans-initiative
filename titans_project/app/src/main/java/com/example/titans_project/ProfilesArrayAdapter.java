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

public class ProfilesArrayAdapter extends ArrayAdapter<User> {

    private Context context;
    private ArrayList<User> users;

    public ProfilesArrayAdapter(Context context, ArrayList<User> users) {
        super(context, 0, users);  // Call the superclass constructor
        this.context = context;
        this.users = users;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.content_browse_profiles, parent, false);
        }

        User user = getItem(position);  // Use getItem to retrieve the event at the given position
        TextView name = convertView.findViewById(R.id.user_name);

        assert user != null;
        name.setText(user.getName());

        return convertView;
    }

}
