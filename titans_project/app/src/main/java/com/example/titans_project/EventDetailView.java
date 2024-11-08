package com.example.titans_project;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

public class EventDetailView extends AppCompatActivity {
    private Button return_button;
    private TextView name, organizer, description, date;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.fragment_event_details);

        name = findViewById(R.id.event_name);
        organizer = findViewById(R.id.organizer);
        description = findViewById(R.id.event_description);
        date = findViewById(R.id.event_date);
        return_button = findViewById(R.id.button_return);

        name.setText(getIntent().getStringExtra("event name"));
        organizer.setText(getIntent().getStringExtra("event organizer"));
        description.setText(getIntent().getStringExtra("event description"));
        date.setText(getIntent().getStringExtra("event date"));

        return_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}
