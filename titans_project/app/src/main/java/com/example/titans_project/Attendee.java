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
     *      The private attribute name of attendee
     * @param userID
     *      The private attribute user id of attendee
     * @param email
     *      The private attribute email of attendee
     */
    public Attendee(String name, String userID, String email) {
        this.name = name;
        this.userID = userID;
        this.email = email;
    }

    /**
     * Generates lottery for a waitlist
     * @param waitList
     *  The current waitlist to get into an event
     * @param selectAmount
     *  The maximum number of users allowed to be selected into the event
     * @return
     *  The hash table of attendee
     */
    public HashMap<String, String> generateLottery(HashMap<String, String> waitList, Integer selectAmount) {
        // Check if the select amount exceeds the number of user_ids in the map
        if (selectAmount > waitList.size()) {
            return waitList;  // return wishlist if there is less people than the selection amount
        }
        // Convert the user_ids of the HashMap into a list
        List<String> userIDs = new ArrayList<>(waitList.keySet());
        // Shuffle the list of user_ids for random selection
        Collections.shuffle(userIDs);
        // Create a HashMap to store selected users
        HashMap<String, String> selectedUsers = new HashMap<>();

        // Select the required number of user_ids and add them to the result map
        for (int i = 0; i < selectAmount; i++) {
            String key = userIDs.get(i);
            selectedUsers.put(key, waitList.get(key));
        }
        return selectedUsers;
    }

    /**
     * This set the name of attendee
     * @param name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * This set the user id of attendee
     * @param userID
     */
    public void setUserID(String userID) {
        this.userID = userID;
    }

    /**
     * This set the email of attendee
     * @param email
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * This get the name of attendee
     * @return
     */
    public String getName() {
        return name;
    }

    /**
     * This get the user id of attendee
     * @return
     */
    public String getUserId() {
        return userID;
    }

    /**
     * This get the email of attendee
     * @return
     */
    public String getEmail() {
        return email;
    }

    /**
     * This get the user Id of attendee
     * @return
     */
    public String getUserID() {
        return userID;
    }
}