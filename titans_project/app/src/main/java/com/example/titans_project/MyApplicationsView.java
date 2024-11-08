package com.example.titans_project;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * This is a class that defines the My Applications screen
 */
public class MyApplicationsView extends AppCompatActivity {
    private ListView applicateList;
    private ArrayList<Event> applicatedataList;
    private EventsArrayAdapter applicateArrayAdapter;
    private Button return_button;
    private TextView application_title;
    private Event testEvent, fakeEvent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.fragment_my_applications);

        application_title = findViewById(R.id.my_application_title);
        applicateList = findViewById(R.id.listview_events);
        return_button = findViewById(R.id.button_return);

        testEvent = new Event("testEventTitle", "use1", "2024/11/5", "2024/11/8", "nothing1", "picture1");
        fakeEvent = new Event("fakeEventTitle", "use2", "2055/11/5", "2055/11/8", "nothing2", "picture2");
        applicatedataList = new ArrayList<>();
        applicateArrayAdapter = new EventsArrayAdapter(this, applicatedataList);
        applicateList.setAdapter(applicateArrayAdapter);
        addEvent(testEvent);
        addEvent(fakeEvent);

        return_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        applicateList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long l) {

                return false;
            }
        });
    }

    private void addEvent(Event event){
        applicatedataList.add(event);
        applicateArrayAdapter.notifyDataSetChanged();
//        HashMap<String, String> eventdata = new HashMap<>();
//        eventdata.put("Event Name", event.getName());
//        eventdata.put("Organizer", event.getOrganizer());
//        eventdata.put("Event Created Date", event.getCreated_date());
//        eventdata.put("Event Date", event.getEvent_date());
//        eventdata.put("Description", event.getDescription());
//        eventdata.put("Picture", event.getPicture());
//        eventdata.put("Event ID", event.getName()+event.getOrganizer()+event.getEvent_date());
    }
}
