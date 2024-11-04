package com.example.titans_project;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class ViewEventActivityOrganizer extends AppCompatActivity {

    private Button qrCodeButton, deleteButton, attendeesButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_event_organizer);

        // 初始化布局中的组件
        TextView eventTitle = findViewById(R.id.eventTitle);
        TextView organizerInfo = findViewById(R.id.organizerInfo);
        ImageView eventImage = findViewById(R.id.eventImage);
        TextView eventDetails = findViewById(R.id.eventDetails);
        TextView eventDate = findViewById(R.id.eventDate);

        // 设置示例数据
        eventTitle.setText("Sample Event Title");
        organizerInfo.setText("Organized by John Doe");
        eventDetails.setText("This is a sample event description that would be replaced by actual data from Firebase.");
        eventDate.setText("Event Date: 2024-12-01");

        // 初始化按钮
        qrCodeButton = findViewById(R.id.qrCodeButton);
        deleteButton = findViewById(R.id.deleteButton);
        attendeesButton = findViewById(R.id.attendeesButton);

        // 设置 QR Code 按钮点击事件
        qrCodeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 打开 QR Code 页面
                Intent intent = new Intent(ViewEventActivityOrganizer.this, QRCodeActivity.class);
                startActivity(intent);
            }
        });

        // 设置 Delete 按钮点击事件
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 打开 Delete 页面
                Intent intent = new Intent(ViewEventActivityOrganizer.this, DeleteEventActivity.class);
                startActivity(intent);
            }
        });

        // 设置 Attendees 按钮点击事件
        attendeesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 打开 Attendees 页面
                Intent intent = new Intent(ViewEventActivityOrganizer.this, AttendeesActivity.class);
                startActivity(intent);
            }
        });
    }
}
