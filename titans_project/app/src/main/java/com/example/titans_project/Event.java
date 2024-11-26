package com.example.titans_project;

import androidx.annotation.NonNull;

import com.google.firebase.firestore.Exclude;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
    private String organizer_id;
    private Integer applicant_limit;
    private Integer default_applicant_limit = 10000;
    private List<Map<String, String>> attendees;
    private List<Map<String, String>> waitlist;

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
     * @param organizer_id
     *      The private attribute picture of event
     */
    public Event(String name, String facility_name, String created_date, String event_date, String description, String organizer_id) {
        this.name = name;
        this.facility_name = facility_name;
        this.created_date = created_date;
        this.event_date = event_date;
        this.description = description;
        this.picture = picture;
        this.event_id = null;
        this.applicant_limit = default_applicant_limit;
        this.attendees = new ArrayList<>();
        this.organizer_id = organizer_id;
        this.waitlist = new ArrayList<>();
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
        this.applicant_limit = default_applicant_limit;
        this.organizer_id = organizer_id;
        this.attendees = new ArrayList<>();
        this.waitlist = new ArrayList<>();
    }

    /**
     * Constructor for Event when not provided a picture
     * @param event_id
     *  Event's id in Firebase
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
     * @param applicantLimit
     *  Event applicant limit
     * @param organizer_id
     *  User's device id
     */
    public Event(String event_id, String name, String facility_name, String created_date, String event_date, String description, Integer applicantLimit, String organizer_id) {
        this.name = name;
        this.facility_name = facility_name;
        this.created_date = created_date;
        this.event_date = event_date;
        this.description = description;
        this.picture = null;
        this.event_id = event_id;
        this.applicant_limit = applicantLimit;
        this.attendees = new ArrayList<>();
        this.organizer_id = organizer_id;
        this.waitlist = new ArrayList<>();

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
     * @param applicantLimit
     *  Event applicant limit
     * @param organizer_id
     *  The organizer's device id
     */
    public Event(String name, String facility_name, String created_date, String event_date, String description, Integer applicantLimit, String organizer_id) {
        this.name = name;
        this.facility_name = facility_name;
        this.created_date = created_date;
        this.event_date = event_date;
        this.description = description;
        this.picture = null;
        this.event_id = null;
        this.applicant_limit = applicantLimit;
        this.organizer_id = organizer_id;
        this.attendees = new ArrayList<>();
        this.waitlist = new ArrayList<>();
    }

    /**
     * This gets the applicant limit for the event
     * @return
     *  returns the applicant limit
     */
    public Integer getApplicantLimit(){ return applicant_limit; }

    /**
     * This sets the applicant limit to a new value
     * @param applicant_limit
     *  The new applicant limit for the event
     */
    public void setApplicantLimit(Integer applicant_limit){
        this.applicant_limit = applicant_limit;
        this.attendees = new ArrayList<>();
    }

    /**
     * This gets the id of the organizer who created the event
     * @return
     *  return the organizer's device id
     */
    public String getOrganizerID(){ return organizer_id;    }

    /**
     * This sets the id of the organizer of the event
     * @param organizer_id
     *  The new organizer id
     */
    public void setOrganizerID(String organizer_id){
        this.organizer_id = organizer_id;
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
     * This clears and sets the picture URI of an event to null
     */
    public void clearPicture() {
        this.picture = null;
    }

    /**
     * Getter for attendees
     */
    public List<Map<String, String>> getAttendees() {
        return attendees;
    }

    /**
     * Setter for attendees
     * @param attendees
     *  New attendees to set to event
     */
    public void setAttendees(List<Map<String, String>> attendees) {
        this.attendees = attendees;
    }

    public List<Map<String, String>> getWaitlist() {
        return waitlist;
    }

    public void setWaitlist(List<Map<String, String>> waitlist) {
        this.waitlist = waitlist;
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
