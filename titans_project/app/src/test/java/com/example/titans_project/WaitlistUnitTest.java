package com.example.titans_project;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class WaitlistUnitTest {

    private Waitlist waitlist;
    private List<User> mockUsers;

    @Before
    public void setUp() {
        mockUsers = new ArrayList<>();
        mockUsers.add(new User("user1", "User One", "user1@example.com", "123-456-7890"));
        mockUsers.add(new User("user2", "User Two", "user2@example.com", "123-456-7890"));
        waitlist = new Waitlist("Event 1", mockUsers, "Organizer 1");
    }
    @Test
    public void testGetEvent() {
        assertEquals("Event 1", waitlist.getEvent());
    }

    @Test
    public void testSetEvent() {
        waitlist.setEvent("New Event");
        assertEquals("New Event", waitlist.getEvent());
    }
    @Test
    public void testSetWaitlist() {
        List<User> newUsers = new ArrayList<>();
        newUsers.add(new User("user3", "User Three", "user3@example.com", "123-456-7890"));
        waitlist.setWaitlist(newUsers);
        assertEquals(newUsers, waitlist.getWaitlist());
    }
}




