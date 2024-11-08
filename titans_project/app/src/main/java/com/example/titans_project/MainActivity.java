package com.example.titans_project;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * This is a class that defines the main activity of the app
 */
public class MainActivity extends AppCompatActivity {
    private ListView eventList;
    private ArrayList<Event> eventsdataList;
    private ArrayList<User> usersdataList;
    private EventsArrayAdapter eventsArrayAdapter;
    private Button profile_button, application_button;
    Intent profile = new Intent();
    Intent my_applications = new Intent();
    Intent event_detail = new Intent();
    private Event testEvent, fakeEvent;
    private User testUser, fakeUser;
    private FirebaseFirestore db;
    private CollectionReference eventRef, userRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_my_events);

        // firebase
        db = FirebaseFirestore.getInstance();
        eventRef = db.collection("events");
        userRef = db.collection("user");

        eventList = findViewById(R.id.listview_events);
        profile_button = findViewById(R.id.profile_button);
        application_button = findViewById(R.id.application_button);

        testEvent = new Event("testEventTitle", "use1", "2024/11/5", "2024/11/8", "nothing1", "picture1");
        fakeEvent = new Event("fakeEventTitle", "use2", "2055/11/5", "2055/11/8", "nothing2", "picture2");
        testUser = new User("testBot1", "12345678@ualberta.ca");
        fakeUser = new User("fakeBot1", "87654321@ualberta.ca");

        eventsdataList = new ArrayList<>();
        eventsArrayAdapter = new EventsArrayAdapter(this, eventsdataList);
        eventList.setAdapter(eventsArrayAdapter);
        addEvent(testEvent);
        addEvent(fakeEvent);

        usersdataList =  new ArrayList<>();
        addUser(testUser);
        addUser(fakeUser);

        // User clicks on the Profile button
        profile_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                profile.setClass(MainActivity.this, ProfileView.class);
                startActivity(profile);
            }
        });

        // User clicks on the My Applications button
        application_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                my_applications.setClass(MainActivity.this, MyApplicationsView.class);
    //            my_applications.putExtra("Event", )
                startActivity(my_applications);
            }
        });

        // User clicks on the event in events list
        eventList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                event_detail.setClass(MainActivity.this, EventDetailView.class);
                event_detail.putExtra("event name", eventsdataList.get(position).getName());
                event_detail.putExtra("event organizer", eventsdataList.get(position).getOrganizer());
                event_detail.putExtra("event description", eventsdataList.get(position).getDescription());
                event_detail.putExtra("event date", eventsdataList.get(position).getEvent_date());
                startActivity(event_detail);
            }
        });
    }

    /**
     * This used for add event into events list and also add into firebase
     * @param event
     *      The event want to add to event list
     */
    private void addEvent(Event event){
        eventsdataList.add(event);
        eventsArrayAdapter.notifyDataSetChanged();
        HashMap<String, String> eventdata = new HashMap<>();
        eventdata.put("Event Name", event.getName());
        eventdata.put("Organizer", event.getOrganizer());
        eventdata.put("Event Created Date", event.getCreated_date());
        eventdata.put("Event Date", event.getEvent_date());
        eventdata.put("Description", event.getDescription());
        eventdata.put("Picture", event.getPicture());
        eventdata.put("Event ID", event.getName()+event.getOrganizer()+event.getEvent_date());
        eventRef.document(event.getName()).set(eventdata);
    }

    /**
     * This used for add user into wait list and also add into firebase
     * @param user
     *      The user want to add to wait list
     */
    private void addUser(User user){
        usersdataList.add(user);

        HashMap<String, String> userdata = new HashMap<>();
        userdata.put("User name", user.getName());
        userdata.put("User email", user.getEmail());
        userRef.document(user.getEmail()).set(userdata);
    }
}