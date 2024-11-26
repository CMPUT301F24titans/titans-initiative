package com.example.titans_project;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class Attendee {
    private String name;
    private String email;

    public Attendee(String name, String email) {
        this.name = name;
        this.email = email;
    }

    /**
     * Generates lottery for a waitlist
     * @param waitList
     *  The current waitlist to get into an event
     * @param selectAmount
     *  The maximum number of users allowed to be selected into the event
     * @return
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

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }
}
