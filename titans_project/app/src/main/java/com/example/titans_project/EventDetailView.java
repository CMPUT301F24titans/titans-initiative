package com.example.titans_project;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.StorageReference;

public class EventDetailView extends AppCompatActivity {
    private Button return_button;
    private TextView name, organizer, description, date;
    private ImageView picture;
    private int event_image=0;
    private Uri uri;

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
                Intent event_image = new Intent(Intent.ACTION_GET_CONTENT, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(event_image, 1);


                System.out.println("\n");
                System.out.println("here is the picture data");
                System.out.println(event_image);
                System.out.println(event_image.getData());
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
            uri = data.getData();
            picture.setImageURI(uri);

            System.out.println("\n");
            System.out.println("here is the picture data");
            System.out.println(uri);
        }
    }
}
