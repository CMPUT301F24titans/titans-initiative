package com.example.titans_project;

import org.junit.Test;
import static org.junit.Assert.*;

public class EventUnitTest {

    @Test
    public void testEventConstructor() {
        Event event = new Event("1", "Swimming Class", "Pool", "2025-01-01", "2025-02-01", "Learn swimming", "organizer123", null, 20, true);

        assertEquals("Swimming Class", event.getName());
        assertEquals("Pool", event.getFacilityName());
        assertEquals("2025-01-01", event.getCreatedDate());
        assertEquals("2025-02-01", event.getEventDate());
        assertEquals("Learn swimming", event.getDescription());
        assertEquals("organizer123", event.getOrganizerID());
        assertNull(event.getPicture());
        assertEquals(20, (int) event.getApplicantLimit());
        assertTrue(event.getGeolocation());
    }

    @Test
    public void testSetEventDetails() {
        Event event = new Event();
        event.setEventID("1");
        event.setName("Cooking Class");
        event.setFacilityName("Kitchen");
        event.setCreated_date("2025-01-01");
        event.setEvent_date("2025-02-01");
        event.setDescription("Learn cooking");

        assertEquals("1", event.getEventID());
        assertEquals("Cooking Class", event.getName());
        assertEquals("Kitchen", event.getFacilityName());
        assertEquals("2025-01-01", event.getCreatedDate());
        assertEquals("2025-02-01", event.getEventDate());
        assertEquals("Learn cooking", event.getDescription());
    }

    @Test
    public void testIsValidEvent() {
        Event event = new Event("1", "Yoga Class", "Yoga Studio", "2025-01-01", "2025-02-01", "Relax and stretch", "organizer123", null, 20, true);
        assertTrue(event.isValid());
    }

    @Test
    public void testInvalidEvent() {
        Event event = new Event("", "", "", "", "", "", "", null, null, null);
        assertFalse(event.isValid());
    }





