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
    private int event_image=0;

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
                Intent upload = new Intent(Intent.ACTION_GET_CONTENT);
                upload.addCategory(Intent.CATEGORY_OPENABLE);
                upload.setType("image/*");
                startActivityForResult(upload, 1);


                System.out.println("\n");
                System.out.println("here is the picture data");
                System.out.println(event_image);
                System.out.println(upload.getData());
                System.out.println("\n");
            }
        });



        return_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void change_picture(){

    }
}
