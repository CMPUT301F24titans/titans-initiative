package com.example.titans_project;

import android.content.Context;
import android.graphics.Bitmap;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class QRCodeUnitTest {

    private QRCodeActivity qrCodeActivity;

    @Before
    public void setUp() {
        qrCodeActivity = new QRCodeActivity();
    }

    @Test
    public void testGenerateQRCodeSuccess() {
        String sampleEventID = "event123";
        try {
            Bitmap qrCodeBitmap = qrCodeActivity.generateQRCode(sampleEventID);
            assertNotNull(qrCodeBitmap);
        } catch (Exception e) {
            fail("QR Code generation failed: " + e.getMessage());
        }
    }

    @Test
    public void testGenerateQRCodeFailure() {
        String invalidEventID = null; // Simulate invalid input
        try {
            Bitmap qrCodeBitmap = qrCodeActivity.generateQRCode(invalidEventID);
            assertNull(qrCodeBitmap);
        } catch (Exception e) {
            assertTrue(e instanceof IllegalArgumentException);
        }
    }
}









