package com.example.titans_project;


import com.google.firebase.firestore.DocumentReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * This is a class that defines a user
 */
public class User {

    private String name;
    private String email;
    private String phone_number;
    private String facility;
    private Boolean notifications;
    private String user_id;
    private String profile_pic;
    private ArrayList<Notification> pendingNotifications;
    private ArrayList<String> applications;
    private ArrayList<String> accepted;
    private ArrayList<String> enrolled;


    /**
     * This creates a user object if name, email, phone number, facility, and notifications are provided
     * @param name
     *  The user's full name
     * @param email
     *  The user's email address, in the general form of xxxxx@domain.ca
     * @param phone_number
     *  The user's phone number, in the general form of (123) 456-7891
     * @param facility
     *  The user's facility name (for Organizer)
     * @param notifications
     *  True if the user elects to receive notifications from other Organizers and Admin
     * @param user_id
     *  The user's anonymous sign in id
     */
    public User(String name, String email, String phone_number, String facility, Boolean notifications, String user_id){
        this.name = name;
        this.email = email;
        this.phone_number = phone_number;
        this.facility = facility;
        this.notifications = notifications;
        this.user_id = user_id;
        this.profile_pic = null;
        this.pendingNotifications = new ArrayList<>();
        this.applications = new ArrayList<>();
        this.accepted = new ArrayList<>();
        this.enrolled = new ArrayList<>();
    }

    /**
     * This creates a user object if ALL of name, email, phone number, facility, notifications, and profile pic are provided
     * @param name
     *  The user's full name
     * @param email
     *  The user's email address, in the general form of xxxxx@domain.ca
     * @param phone_number
     *  The user's phone number, in the general form of (123) 456-7891
     * @param facility
     *  The user's facility name (for Organizer)
     * @param notifications
     *  True if the user elects to receive notifications from other Organizers and Admin
     * @param user_id
     *  The user's anonymous sign in id
     * @param profile_pic
     *  The user's profile pic URI
     */
    public User(String name, String email, String phone_number, String facility, Boolean notifications, String user_id, String profile_pic){
        this.name = name;
        this.email = email;
        this.phone_number = phone_number;
        this.facility = facility;
        this.notifications = notifications;
        this.user_id = user_id;
        this.profile_pic = profile_pic;
        this.pendingNotifications = new ArrayList<>();
        this.applications = new ArrayList<>();
        this.accepted = new ArrayList<>();
        this.enrolled = new ArrayList<>();
    }

    /**
     * This returns the user's anonymous sign in id
     * @return
     *  Return user's anonymous sign in id
     */
    public String getUserID(){
        return this.user_id;
    }

    /**
     * This sets the user's id to a new value
     * @param user_id
     *  The new id to set to the user
     */
    public void setUserID(String user_id){
        this.user_id = user_id;
    }

    /**
     * This returns the user's full name
     * @return
     *  Return user's full name
     */
    public String getName(){
        return this.name;
    }

    /**
     * This sets the user's full name to a new value
     * @param name
     *  The new name to assign to the user
     */
    public void setName(String name){
        this.name = name;
    }

    /**
     * This returns the user's email address
     * @return
     *  Returns user's email address
     */
    public String getEmail(){
        return this.email;
    }

    /**
     * This sets the user's email address to a new value
     * @param email
     *  The new email address to assign to the user
     */
    public void setEmail(String email){
        this.email = email;
    }

    /**
     * This return's the user's phone number, returns empty string if no phone number was provided
     * @return
     *  Returns user's phone number
     */
    public String getPhoneNumber(){
        return this.phone_number;
    }

    /**
     * This sets the user's phone number to a new value
     * @param phone_number
     *  The new phone number to assign to the user
     */
    public void setPhoneNumber(String phone_number){
        this.phone_number = phone_number;
    }

    /**
     * This returns the user's URI of profile_pic
     * @return
     *  Returns URI of user's current profile_pic
     */
    public String getProfilePic(){ return this.profile_pic;    }

    /**
     * This sets the profile_pic to a new value
     * @param profile_pic
     *  The new profile_pic value of user
     */
    public void setProfilePic(String profile_pic){
        this.profile_pic = profile_pic;
    }

    /**
     * This clears profile pic URI to null
     */
    public void clearProfilePic() {
        this.profile_pic = null;
    }

    /**
     * Gets the current arraylist of notifications
     * @return
     *  returns arraylist of user's notifications
     */
    public ArrayList<Notification> getPendingNotifications(){   return pendingNotifications;    }

    /**
     * Set pendingNotifications to a new ArrayList of notifications
     * @param notifications
     *  The new arrayList of notifications to assign to pendingNotifications
     */
    public void setPendingNotifications(ArrayList<Notification> notifications) {
        this.pendingNotifications = notifications;
    }

    /**
     * Adds notification to pendingNotifications
     * @param notification
     *  Notification to insert into user's notification list
     */
    public void addNotification(Notification notification) {
        pendingNotifications.add(notification);
    }

    /**
     * Delete notification from pendingNotifications
     * @param notification
     *  The notification to delete
     * @return
     *  Returns true if notification was found inside pendingNotifications and deleted, false otherwise
     */
    public Boolean deleteNotification(Notification notification) {
        if (pendingNotifications.contains(notification)){
            pendingNotifications.remove(notification);
            return true;
        }
        return false;
    }

    /**
     * Gets all of the user's currently applied events
     * @return
     *  Returns the event ids for events the user has applied for
     */
    public ArrayList<String> getApplications(){ return applications; }

    /**
     * Sets a new value to applications
     * @param applications
     *  The new ArrayList to assign to applications
     */
    public void setApplications(ArrayList<String> applications) {
        this.applications = applications;
    }

    /**
     * Gets all of the event ids for the events the user has won the lottery (but not accepted/declined)
     * @return
     *  Returns the event ids for events the user won lottery for
     */
    public ArrayList<String> getAccepted(){ return accepted; }

    /**
     * Sets a new value to accepted
     * @param accepted
     *  The new ArrayList to assign to accepted
     */
    public void setAccepted(ArrayList<String> accepted) {
        this.accepted = accepted;
    }

    /**
     * Gets all of the events the user has enrolled in
     * @return
     *  Returns the event ids for events the user has enrolled in
     */
    public ArrayList<String> getEnrolled(){ return enrolled; }

    /**
     * Sets a new value to enrolled
     * @param enrolled
     *  The new ArrayList to assign to enrolled
     */
    public void setEnrolled(ArrayList<String> enrolled) {
        this.enrolled = enrolled;
    }

    /**
     * Converts user to map format (which is used to store user's in Firebase)
     * @return
     */
    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("full_name", name);
        map.put("user_id", getUserID());
        return map;
    }
}
