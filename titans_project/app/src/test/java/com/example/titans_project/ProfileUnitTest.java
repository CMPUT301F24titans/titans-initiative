package com.example.titans_project;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import com.google.firebase.firestore.FirebaseFirestore;

import org.junit.Test;

public class ProfileViewUnitTest {

    @Test
    public void testGenerateInitials() {
        String name = "John Doe";
        String initials = ProfileView.getInitials(name);
        assertEquals("JD", initials);
    }

    @Test
    public void testGenerateInitialsWithSpaces() {
        String name = "  Alice   Wonderland  ";
        String initials = ProfileView.getInitials(name);
        assertEquals("AW", initials);
    }

    @Test
    public void testValidateEmail() {
        String validEmail = "example@example.com";
        String invalidEmail = "invalid-email";

        assertTrue(validEmail.matches("^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$"));
        assertFalse(invalidEmail.matches("^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$"));
    }




