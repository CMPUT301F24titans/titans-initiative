package com.example.titans_project;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;

import java.util.ArrayList;

/**
 * This is a class that defines the main activity of the app
 */
public class MainActivity extends AppCompatActivity {
    private ListView eventList;
    private ArrayList<Event> dataList;
    private EventsArrayAdapter eventsArrayAdapter;
    private Button profile_button, application_button;
    Intent profile = new Intent();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_my_events);

        eventList = findViewById(R.id.listview_events);
        profile_button = findViewById(R.id.profile_button);
        application_button = findViewById(R.id.application_button);

        String[] events = {"Edmonton", "Vancouver", "Toronto"};
        String[] date = {"2024/11/5", "2024/12/5", "2025/11/5"};
        dataList = new ArrayList<>();
        for (int i = 0; i < events.length; i++) {
            dataList.add(new Event(events[i], "organizer", "created date", date[i], "description", "picture"));
        }
        eventsArrayAdapter = new EventsArrayAdapter(this, dataList);
        eventList.setAdapter(eventsArrayAdapter);

        profile_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                profile.setClass(MainActivity.this, ProfileView.class);
                startActivity(profile);
            }
        });
    }
}