package com.example.titans_project;

import androidx.annotation.NonNull;

public class Event {
    private String name;
    private String organizer;
    private String created_date;
    private String event_date;
    private String description;
    private String picture;
    private String eventID;


    public Event(String name, String organizer, String created_date, String event_date, String description, String picture) {
        this.name = name;
        this.organizer = organizer;
        this.created_date = created_date;
        this.event_date = event_date;
        this.description = description;
        this.picture = picture;
        this.eventID = generateEventID();
    }


    public Event() {}


    private String generateEventID() {
        return name + organizer + event_date;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
        this.eventID = generateEventID();
    }

    public String getOrganizer() {
        return organizer;
    }

    public void setOrganizer(String organizer) {
        this.organizer = organizer;
        this.eventID = generateEventID();
    }

    public String getCreated_date() {
        return created_date;
    }

    public void setCreated_date(String created_date) {
        this.created_date = created_date;
    }

    public String getEvent_date() {
        return event_date;
    }

    public void setEvent_date(String event_date) {
        this.event_date = event_date;
        this.eventID = generateEventID();
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public String getEventID() {
        return eventID;
    }
}