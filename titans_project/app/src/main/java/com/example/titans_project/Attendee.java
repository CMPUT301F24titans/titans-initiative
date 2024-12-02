package com.example.titans_project;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 * This is a class that defined Attendee
 */
public class Attendee {
    private String name;
    private String userID;
    private String email;

    /**
     * This initial attendee class
     * @param name
     *  The private attribute name of attendee
     * @param userID
     *  The private attribute user id of attendee
     * @param email
     *  The private attribute email of attendee
     */
    public Attendee(String name, String userID, String email) {
        this.name = name;
        this.userID = userID;
        this.email = email;
    }

    /**
     * This set the name of attendee
     * @param name
     *  The name of attendee
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * This set the user id of attendee
     * @param userID
     *  The user id of attendee
     */
    public void setUserID(String userID) {
        this.userID = userID;
    }

    /**
     * This set the email of attendee
     * @param email
     *  The email of attendee
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * This get the name of attendee
     * @return
     *  return the name of attendee
     */
    public String getName() {
        return name;
    }

    /**
     * This get the user id of attendee
     * @return
     *  return the user id of attendee
     */
    public String getUserId() {
        return userID;
    }

    /**
     * This get the email of attendee
     * @return
     *  return the email of attendee
     */
    public String getEmail() {
        return email;
    }
}