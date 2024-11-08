package com.example.titans_project;

import com.google.firebase.firestore.IgnoreExtraProperties;
import com.google.firebase.firestore.PropertyName;

/**
 * This is a class that defines an Event.
 */
@IgnoreExtraProperties
public class Event {
    private String name;
    private String organizer;
    private String created_date;
    private String event_date;
    private String description;
    private String picture;

    // Firebase requires a no-argument constructor for model deserialization
    public Event() {
        // Default constructor required for Firebase Firestore
    }

    /**
     * This initializes the class Event.
     * @param name The event name.
     * @param organizer The organizer of the event.
     * @param created_date The creation date of the event.
     * @param event_date The start date of the event.
     * @param description The description of the event.
     * @param picture The picture URL for the event.
     */
    public Event(String name, String organizer, String created_date, String event_date, String description, String picture) {
        this.name = name;
        this.organizer = organizer;
        this.created_date = created_date;
        this.event_date = event_date;
        this.description = description;
        this.picture = picture;
    }

    @PropertyName("name")
    public String getName() {
        return name;
    }

    @PropertyName("name")
    public void setName(String name) {
        this.name = name;
    }

    @PropertyName("organizer")
    public String getOrganizer() {
        return organizer;
    }

    @PropertyName("organizer")
    public void setOrganizer(String organizer) {
        this.organizer = organizer;
    }

    @PropertyName("created_date")
    public String getCreated_date() {
        return created_date;
    }

    @PropertyName("created_date")
    public void setCreated_date(String created_date) {
        this.created_date = created_date;
    }

    @PropertyName("event_date")
    public String getEvent_date() {
        return event_date;
    }

    @PropertyName("event_date")
    public void setEvent_date(String event_date) {
        this.event_date = event_date;
    }

    @PropertyName("description")
    public String getDescription() {
        return description;
    }

    @PropertyName("description")
    public void setDescription(String description) {
        this.description = description;
    }

    @PropertyName("picture")
    public String getPicture() {
        return picture;
    }

    @PropertyName("picture")
    public void setPicture(String picture) {
        this.picture = picture;
    }
}
