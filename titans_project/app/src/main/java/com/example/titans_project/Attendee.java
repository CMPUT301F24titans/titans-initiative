package com.example.titans_project;

public class Attendee {
    private String name;
    private String userID;
    private String email;

    public Attendee(String name, String userID, String email) {
        this.name = name;
        this.userID = userID;
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public String getUserId() {
        return userID;
    }

    public String getEmail() {
        return email;
    }

    public String getUserID() {
        return userID;
    }
}
