package com.example.titans_project;

import java.util.List;

public class Waitlist {
    private String event;
    private String organizer;
    private List<User> waitlist;

    public Waitlist(String event, List<User> waitlist, String organizer) {
        this.event = event;
        this.waitlist = waitlist;
        this.organizer = organizer;
    }

    public List<User> getWaitlist() {
        return waitlist;
    }

    public String getOrganizer() {
        return organizer;
    }

    public String getEvent() {
        return event;
    }
}
