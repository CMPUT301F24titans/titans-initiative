package com.example.titans_project;

import com.google.firebase.firestore.DocumentReference;

public class Notification {
    private String title;
    private String description;
    private String date;

    /**
     * Constructor for notification object
     * @param title
     * @param description
     */
    public Notification(String title, String description, String date) {
        this.title = title;
        this.description = description;
        this.date = date;
    }

    /**
     * Gets the title of the notification
     * @return
     *  Returns notification title
     */
    public String getTitle(){ return title; }

    /**
     * Sets the title of the notification
     * @param title
     *  New title to set for the notification
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Gets the description of the notification
     * @return
     *  Returns notification description
     */
    public String getDescription() { return description;    }

    /**
     * Sets the description of the notification
     * @param description
     *  New description to set for the description
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Gets the date the notification was sent
     * @return
     *  Date notification was sent
     */
    public String getDate(){ return date;   }

    /**
     * Sets the date of the notification to a new date
     * @param date
     *  The new date to be assigned to the notification
     */
    public void setDate(String date) {
        this.date = date;
    }

}
