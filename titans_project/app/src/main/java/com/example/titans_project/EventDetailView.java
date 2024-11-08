package com.example.titans_project;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

public class EventDetailView extends AppCompatActivity {
    private Button return_button;
    private TextView name, organizer, description, date;
    private ImageView picture;
    private int event_image;
    Intent upload = new Intent();

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
        picture = findViewById(R.id.event_pic);

        name.setText(getIntent().getStringExtra("event name"));
        organizer.setText(getIntent().getStringExtra("event organizer"));
        description.setText(getIntent().getStringExtra("event description"));
        date.setText(getIntent().getStringExtra("event date"));

        picture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                upload.setType("image/*");
                startActivityForResult(upload, event_image);

            }
        });

        return_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}
