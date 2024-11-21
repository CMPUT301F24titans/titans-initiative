package com.example.titans_project;


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


    /**
     * This creates a user object if ALL of name, email, phone number, facility, and notifications are provided
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
}
