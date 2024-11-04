package com.example.titans_project;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class AttendeesActivity extends AppCompatActivity {

    private RecyclerView attendeesRecyclerView;
    private AttendeesAdapter attendeesAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendees);

        // 返回按钮
        ImageButton backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // 初始化 RecyclerView
        attendeesRecyclerView = findViewById(R.id.attendeesRecyclerView);
        attendeesRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // 添加占位符数据（待替换为从 Firebase 获取的实际数据）
        List<String> placeholderAttendees = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            placeholderAttendees.add("Attendee " + (i + 1));
        }

        attendeesAdapter = new AttendeesAdapter(placeholderAttendees);
        attendeesRecyclerView.setAdapter(attendeesAdapter);
    }
}
