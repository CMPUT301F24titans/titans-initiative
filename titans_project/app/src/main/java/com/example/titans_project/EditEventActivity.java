package com.example.titans_project;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class EditEventActivity extends AppCompatActivity {

    private EditText eventTitleEdit, organizerEdit, eventDetailsEdit, eventDateEdit;
    private Button saveButton;

    private FirebaseFirestore db;
    private DocumentReference eventRef;
    private String eventId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_event);

        // 从 Intent 获取 Event ID
        eventId = getIntent().getStringExtra("eventId");

        // 初始化 Firebase
        db = FirebaseFirestore.getInstance();
        eventRef = db.collection("events").document(eventId);

        // 初始化布局中的组件
        eventTitleEdit = findViewById(R.id.eventTitleEdit);
        organizerEdit = findViewById(R.id.organizerEdit);
        eventDetailsEdit = findViewById(R.id.eventDetailsEdit);
        eventDateEdit = findViewById(R.id.eventDateEdit);
        saveButton = findViewById(R.id.saveButton);

        // 加载活动数据
        loadEventData();

        // 保存按钮点击事件
        saveButton.setOnClickListener(v -> saveEventData());
    }

    private void loadEventData() {
        eventRef.get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        eventTitleEdit.setText(documentSnapshot.getString("Event Name"));
                        organizerEdit.setText(documentSnapshot.getString("Organizer"));
                        eventDetailsEdit.setText(documentSnapshot.getString("Description"));
                        eventDateEdit.setText(documentSnapshot.getString("Event Date"));
                    } else {
                        Toast.makeText(this, "Event not found", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Failed to load event data", Toast.LENGTH_SHORT).show());
    }

    private void saveEventData() {
        // 将编辑的数据更新回 Firebase
        eventRef.update(
                "Event Name", eventTitleEdit.getText().toString(),
                "Organizer", organizerEdit.getText().toString(),
                "Description", eventDetailsEdit.getText().toString(),
                "Event Date", eventDateEdit.getText().toString()
        ).addOnSuccessListener(aVoid -> {
            Toast.makeText(this, "Event updated successfully", Toast.LENGTH_SHORT).show();
            finish(); // 返回到 ViewEventActivityOrganizer
        }).addOnFailureListener(e -> Toast.makeText(this, "Failed to update event", Toast.LENGTH_SHORT).show());
    }
}
