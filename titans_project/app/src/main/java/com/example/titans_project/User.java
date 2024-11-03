package com.example.titans_project;


/**
 * This is a class that defines a user
 */
public class User {

    private String name;
    private String email;
    private String phone_number = "";  // Optional, empty by default

    /**
     * This creates a user object if only a name and email are provided
     * @param name
     *  The user's full name
     * @param email
     *  The user's email address in, the general form of xxxxx@domain.ca
     */
    public User(String name, String email){
        this.name = name;
        this.email = email;
    }

    /**
     * This creates a user object if ALL of name, email, and phone number are provided
     * @param name
     *  The user's full name
     * @param email
     *  The user's email address, in the general form of xxxxx@domain.ca
     * @param phone_number
     *  The user's phone number, in the general form of (123) 456-7891
     */
    public User(String name, String email, String phone_number){
        this.name = name;
        this.email = email;
        this.phone_number = phone_number;
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
