package com.example.titans_project;

import androidx.annotation.NonNull;

import com.google.firebase.firestore.Exclude;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.io.Serializable;

/**
 * This is a class that defines a Event.
 */
public class Event {
    private String name;
    private String facility_name;
    private String created_date;
    private String event_date;
    private String description;
    private String  picture;
    private String event_id;

    /**
     * This initial the class Event
     * @param name
     *      The private attribute event name
     * @param facility_name
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
    public Event(String name, String facility_name, String created_date, String event_date, String description, String picture) {
        this.name = name;
        this.facility_name = facility_name;
        this.created_date = created_date;
        this.event_date = event_date;
        this.description = description;
        this.picture = picture;
        this.event_id = null;
    }

    /**
     * Constructor for Event when provided the event_id
     * @param event_id
     *  Event id
     * @param name
     *  Event name
     * @param facility_name
     *  Event's facility_name (facility name)
     * @param created_date
     *  Event's created date
     * @param event_date
     *  Date when event will occur
     * @param description
     *  Event description
     * @param picture
     *  Event poster
     */
    public Event(String event_id, String name, String facility_name, String created_date, String event_date, String description, String picture) {
        this.name = name;
        this.facility_name = facility_name;
        this.created_date = created_date;
        this.event_date = event_date;
        this.description = description;
        this.picture = null;
        this.event_id = event_id;
    }

    /**
     * Constructor for Event when not provided a picture or event_id
     * @param name
     *  Event name
     * @param facility_name
     *  Event's facility_name (facility name)
     * @param created_date
     *  Event's created date
     * @param event_date
     *  Date when event will occur
     * @param description
     *  Event description
     */
    public Event(String name, String facility_name, String created_date, String event_date, String description) {
        this.name = name;
        this.facility_name = facility_name;
        this.created_date = created_date;
        this.event_date = event_date;
        this.description = description;
        this.picture = null;
        this.event_id = null;
    }

    /**
     * This gets the id of an event
     * @return
     *  return the id of event
     */
    public String getEventID(){return event_id;    }

    /**
     * This sets the id of an event
     * @param event_id
     *  The new event id to set to the event
     */
    public void setEventID(String event_id){
        this.event_id = event_id;
    }

    /**
     * This gets the name of an event
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
    public String getFacilityName() {
        return facility_name;
    }

    /**
     * This set the organizer of event
     * @param facility_name
     *      The private attribute organizer of the event
     */
    public void setFacilityName(String facility_name) {
        this.facility_name = facility_name;
    }

    /**
     * This give the created date of event
     * @return
     *      return the created date of event
     */
    public String getCreatedDate() {
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
    public String getEventDate() {
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

    /**
     * Evaluates whether the current event is a valid event
     * @return
     *  returns true if event is valid
     */
    @Exclude
    public boolean isValid() {
        return facility_name != null && !facility_name.isEmpty() &&
                name != null && !name.isEmpty() &&
                event_date != null && !event_date.isEmpty();
    }
}
