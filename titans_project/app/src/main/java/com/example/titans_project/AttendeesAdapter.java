package com.example.titans_project;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class AttendeesAdapter extends RecyclerView.Adapter<AttendeesAdapter.AttendeeViewHolder> {

    private List<String> attendees;

    public AttendeesAdapter(List<String> attendees) {
        this.attendees = attendees;
    }

    @NonNull
    @Override
    public AttendeeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.attendee_item, parent, false);
        return new AttendeeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AttendeeViewHolder holder, int position) {
        holder.attendeeName.setText(attendees.get(position));
        // 可在这里设置占位符图像或其他数据
    }

    @Override
    public int getItemCount() {
        return attendees.size();
    }

    static class AttendeeViewHolder extends RecyclerView.ViewHolder {
        ImageView attendeeImage;
        TextView attendeeName;
        TextView attendeeDescription;

        public AttendeeViewHolder(@NonNull View itemView) {
            super(itemView);
            attendeeImage = itemView.findViewById(R.id.attendeeImage);
            attendeeName = itemView.findViewById(R.id.attendeeName);
            attendeeDescription = itemView.findViewById(R.id.attendeeDescription);
        }
    }
}
