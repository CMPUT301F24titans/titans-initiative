package com.example.titans_project;

public class User {

    private String name;
    private String email;
    private String phone_number = "";  // Optional, empty by default

    public User(String name, String email){
        this.name = name;
        this.email = email;
    }

    public User(String name, String email, String phone_number){
        this.name = name;
        this.email = email;
        this.phone_number = phone_number;
    }

    public String getName(){
        return this.name;
    }

    public void setName(String name){
        this.name = name;
    }

    public String getEmail(){
        return this.email;
    }

    public void setEmail(String email){
        this.email = email;
    }

    public String getPhoneNumber(){
        return this.phone_number;
    }

    public void setPhoneNumber(){
        this.phone_number = phone_number;
    }
}
