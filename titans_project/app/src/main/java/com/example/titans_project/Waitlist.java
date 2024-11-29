package com.example.titans_project;

import java.util.List;

/**
 * This is a class that defines a Wait List.
 */
public class Waitlist {
    private String event;
    private String organizer;
    private List<User> waitlist;

    /**
     * This This initial the class wait list
     * @param event
     *  The private attribute event of wait list
     * @param waitlist
     *  The private attribute array of wait list
     * @param organizer
     *  The private attribute organizer of wait list
     */
    public Waitlist(String event, List<User> waitlist, String organizer) {
        this.event = event;
        this.waitlist = waitlist;
        this.organizer = organizer;
    }

    /**
     * This set the event for wait list
     * @param event
     *  The event of the wait list
     */
    public void setEvent(String event) {
        this.event = event;
    }

    /**
     * This set the organizer of wait list
     * @param organizer
     *  The organizer of event
     */
    public void setOrganizer(String organizer) {
        this.organizer = organizer;
    }

    /**
     * This set the array for wait list
     * @param waitlist
     *  The list of wait list
     */
    public void setWaitlist(List<User> waitlist) {
        this.waitlist = waitlist;
    }

    /**
     * This get the array of wait list
     * @return
     *  return the list of wait list
     */
    public List<User> getWaitlist() {
        return waitlist;
    }

    /**
     * This get the organizer of wait list
     * @return
     *  return organizer of wait list
     */
    public String getOrganizer() {
        return organizer;
    }

    /**
     * This get the event of wait list
     * @return
     *  return the event of wait list
     */
    public String getEvent() {
        return event;
    }
}
