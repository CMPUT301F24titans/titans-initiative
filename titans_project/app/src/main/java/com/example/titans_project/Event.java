package com.example.titans_project;

import androidx.annotation.Nullable;

import com.google.firebase.firestore.Exclude;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This is a class that defines a Event.
 */
public class Event {
    private String event_id;
    private String name;
    private String facility_name;
    private String created_date;
    private String event_date;
    private String description;
    private String organizer_id;
    private String picture;
    private Integer applicant_limit;
    private Integer lottery_size;
    private List<Map<String, String>> attendees;
    private List<Map<String, String>> waitlist;
    private List<Map<String, String>> lottery;
    private List<Map<String, String>> cancelled;
    private Boolean geolocation;
    private List<Map<String, Double>> locations;

    public Event() {}

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
     * @param organizer_id
     *  User's device id
     * @param picture
     *  Event image
     */
    public Event(@Nullable String event_id, @Nullable String name, @Nullable String facility_name, @Nullable String created_date, @Nullable String event_date, @Nullable String description, @Nullable String organizer_id, @Nullable String picture, @Nullable Integer applicant_limit, @Nullable Boolean geolocation ) {
        this.event_id = event_id;
        this.name = name;
        this.facility_name = facility_name;
        this.created_date = created_date;
        this.event_date = event_date;
        this.description = description;
        this.organizer_id = organizer_id;
        this.picture = picture;
        this.applicant_limit = applicant_limit;
        this.attendees = new ArrayList<>();
        this.waitlist = new ArrayList<>();
        this.lottery = new ArrayList<>();
        this.cancelled = new ArrayList<>();
        this.lottery_size = null;
        this.geolocation = geolocation;
        this.locations = new ArrayList<>();
    }

    public void default_event(){
        this.event_id = null;
        this.name = null;
        this.facility_name = null;
        this.created_date = null;
        this.event_date = null;
        this.description = null;
        this.organizer_id = null;
        this.picture = null;
        this.applicant_limit = 10000;
        this.attendees = new ArrayList<>();
        this.waitlist = new ArrayList<>();
        this.lottery = new ArrayList<>();
        this.cancelled = new ArrayList<>();
        this.lottery_size = null;
        this.geolocation = null;
        this.locations = new ArrayList<>();
    }

    /**
     * This gets the id of an event
     * @return
     *  return the id of event
     */
    public String getEventID(){return event_id; }

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
     *  return the name of event
     */
    public String getName() {
        return name;
    }

    /**
     * This set the name of event
     * @param name
     *  The private attribute event name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * This give the organizer of event
     * @return
     *  return the organizer of event
     */
    public String getFacilityName() {
        return facility_name;
    }

    /**
     * This set the organizer of event
     * @param facility_name
     *  The private attribute organizer of the event
     */
    public void setFacilityName(String facility_name) {
        this.facility_name = facility_name;
    }

    /**
     * This give the created date of event
     * @return
     *  return the created date of event
     */
    public String getCreatedDate() {
        return created_date;
    }

    /**
     * This set the created date of event
     * @param created_date
     *  The private attribute created date of the event
     */
    public void setCreated_date(String created_date) {
        this.created_date = created_date;
    }

    /**
     * This give the start date of event
     * @return
     *  return the start date of event
     */
    public String getEventDate() {
        return event_date;
    }

    /**
     * This set the start date of event
     * @param event_date
     *  The private attribute start date of the event
     */
    public void setEvent_date(String event_date) {
        this.event_date = event_date;
    }

    /**
     * This give the description of event
     * @return
     *  return the description of event
     */
    public String getDescription() {
        return description;
    }

    /**
     * This set the description of event
     * @param description
     *  The private attribute description of the event
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * This gets the id of the organizer who created the event
     * @return
     *  return the organizer's device id
     */
    public String getOrganizerID(){ return organizer_id; }

    /**
     * This sets the id of the organizer of the event
     * @param organizer_id
     *  The new organizer id
     */
    public void setOrganizerID(String organizer_id){
        this.organizer_id = organizer_id;
    }

    /**
     * This give the picture of event
     * @return
     *  return the picture of event
     */
    public String getPicture() {
        return picture;
    }

    /**
     * This set the picture of event
     * @param picture
     *  The private attribute picture of the event
     */
    public void setPicture(String picture) {
        this.picture = picture;
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
     * This gets the attendee list for the event
     * @return
     *  return the attendee list
     */
    public List<Map<String, String>> getAttendees() {
        return attendees;
    }

    /**
     * This sets the attendee list
     * @param attendees
     *  The new attendees to set to event
     */
    public void setAttendees(List<Map<String, String>> attendees) {
        this.attendees = attendees;
    }

    /**
     * This gets the wait list for the event
     * @return
     *  return the wait list
     */
    public List<Map<String, String>> getWaitlist() {
        return waitlist;
    }

    /**
     * This sets the wait list
     * @param waitlist
     *  The new wait to set to event
     */
    public void setWaitlist(List<Map<String, String>> waitlist) {
        this.waitlist = waitlist;
    }

    public List<Map<String, String>> getLottery() {
        return lottery;
    }

    public void setLottery(List<Map<String, String>> lottery) {
        this.lottery = lottery;
    }

    public List<Map<String, String>> getCancelled() {
        return cancelled;
    }

    public void setCancelled(List<Map<String, String>> cancelled) {
        this.cancelled = cancelled;
    }

    public Integer getLotterySize(){ return lottery_size; }

    public void setLotterySize(Integer lottery_size) {
        this.lottery_size = lottery_size;
    }

    public Boolean getGeolocation() { return geolocation; }

    public void setGeolocation(Boolean geolocation) {
        this.geolocation = geolocation;
    }

    public List<Map<String, Double>> getLocations() { return locations; }

    public void setLocations(List<Map<String, Double>> locations) {
        this.locations = locations;
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
