package com.example.titans_project;

import androidx.annotation.NonNull;

/**
 * This is a class that defines a Event.
 */
public class Event {
    private String name;
    private String organizer;
    private String created_date;
    private String event_date;
    private String description;
    private String  picture;

    /**
     * This initial the class Event
     * @param name
     *      The private attribute event name
     * @param organizer
     *      The private attribute organizer of the event
     * @param created_date
     *      The private attribute created date of event
     * @param event_date
     *      The private attribute date when the event start
     * @param description
     *      The private attribute description of event
     * @param picture
     *      The private attribute picture of event
     */
    public Event(String name, String organizer, String created_date, String event_date, String description, String picture) {
        this.name = name;
        this.organizer = organizer;
        this.created_date = created_date;
        this.event_date = event_date;
        this.description = description;
        this.picture = picture;
    }

    /**
     * This give the name of event
     * @return
     *      return the name of event
     */
    public String getName() {
        return name;
    }

    /**
     * This set the name of event
     * @param name
     *      The private attribute event name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * This give the organizer of event
     * @return
     *      return the organizer of event
     */
    public String getOrganizer() {
        return organizer;
    }

    /**
     * This set the organizer of event
     * @param organizer
     *      The private attribute organizer of the event
     */
    public void setOrganizer(String organizer) {
        this.organizer = organizer;
    }

    /**
     * This give the created date of event
     * @return
     *      return the created date of event
     */
    public String getCreated_date() {
        return created_date;
    }

    /**
     * This set the created date of event
     * @param created_date
     *      The private attribute created date of the event
     */
    public void setCreated_date(String created_date) {
        this.created_date = created_date;
    }

    /**
     * This give the start date of event
     * @return
     *      return the start date of event
     */
    public String getEvent_date() {
        return event_date;
    }

    /**
     * This set the start date of event
     * @param event_date
     *      The private attribute start date of the event
     */
    public void setEvent_date(String event_date) {
        this.event_date = event_date;
    }

    /**
     * This give the description of event
     * @return
     *      return the description of event
     */
    public String getDescription() {
        return description;
    }

    /**
     * This set the description of event
     * @param description
     *      The private attribute description of the event
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * This give the picture of event
     * @return
     *      return the picture of event
     */
    public String getPicture() {
        return picture;
    }

    /**
     * This set the picture of event
     * @param picture
     *      The private attribute picture of the event
     */
    public void setPicture(String picture) {
        this.picture = picture;
    }
}
