package com.example.titans_project;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.mlkit.vision.barcode.BarcodeScanning;
import com.google.mlkit.vision.barcode.BarcodeScannerOptions;
import com.google.mlkit.vision.barcode.common.Barcode;
import com.google.mlkit.vision.common.InputImage;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

/**
 * This activity allows the user to scan QR codes either by using the camera or selecting an image from the gallery.
 * It decodes the QR code and directs the user to the event details page if the QR code contains a valid event ID.
 */
public class QRScannerActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST_CODE = 100;  // Request code for selecting an image from the gallery
    private ImageView qrImageView;  // ImageView to display the selected QR code image

    /**
     * Initializes the activity by setting up the UI components and listeners.
     * This method allows the user to either scan a QR code using the camera or choose an image from the gallery.
     *
     * @param savedInstanceState The saved instance state of the activity if it was previously destroyed.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_qr_scanner);

        qrImageView = findViewById(R.id.qrImageView);

        // Set up listeners for the scan button and choose image button
        findViewById(R.id.scan_button).setOnClickListener(v -> startCameraScan());
        findViewById(R.id.chooseImageButton).setOnClickListener(v -> openGalleryForImage());

        // Set up the return button to finish the activity and go back to the previous screen
        findViewById(R.id.button_return).setOnClickListener(view -> finish());
    }

    /**
     * Initiates the QR code scan using the device's camera.
     * This method uses the ZXing library to start scanning for QR codes.
     */
    private void startCameraScan() {
        new IntentIntegrator(this)
                .setDesiredBarcodeFormats(IntentIntegrator.QR_CODE)  // Specify QR code as the desired format
                .setPrompt("Scan a QR code")  // Prompt shown while scanning
                .setCameraId(0)  // Use the rear camera
                .setBeepEnabled(true)  // Enable a beep sound when a QR code is found
                .setBarcodeImageEnabled(true)  // Enable saving barcode image for further processing
                .initiateScan();  // Start scanning
    }

    /**
     * Opens the gallery to allow the user to select an image to scan for a QR code.
     */
    private void openGalleryForImage() {
        Intent intent = new Intent(Intent.ACTION_PICK);  // Create an intent to pick an image from the gallery
        intent.setType("image/*");  // Filter to allow only image files
        startActivityForResult(intent, PICK_IMAGE_REQUEST_CODE);  // Start the activity for result
    }

    /**
     * Handles the result of an activity (either a scanned QR code or a selected image).
     * It processes the QR code and takes appropriate action based on the content.
     *
     * @param requestCode The request code passed to startActivityForResult.
     * @param resultCode  The result code from the activity.
     * @param data        The intent data returned from the activity.
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // If the result is from the image selection activity
        if (requestCode == PICK_IMAGE_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            Uri imageUri = data.getData();  // Get the URI of the selected image
            qrImageView.setImageURI(imageUri);  // Display the selected image in the ImageView
            decodeQRCodeFromImage(imageUri);  // Decode the QR code from the selected image
        } else {
            // If the result is from the QR scanner activity (camera scan)
            IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
            if (result != null && result.getContents() != null) {
                handleScannedQRCode(result.getContents());  // Handle the decoded QR code
            } else {
                Toast.makeText(this, "No QR code found", Toast.LENGTH_SHORT).show();  // Inform the user if no QR code is found
            }
        }
    }

    /**
     * Decodes a QR code from an image selected from the gallery.
     * Uses ML Kit's Barcode Scanning API to extract the QR code's content.
     *
     * @param imageUri The URI of the image to decode.
     */
    private void decodeQRCodeFromImage(Uri imageUri) {
        try {
            // Create an InputImage object from the selected image's URI
            InputImage image = InputImage.fromFilePath(this, imageUri);
            // Set up options to scan for QR codes only
            BarcodeScannerOptions options = new BarcodeScannerOptions.Builder()
                    .setBarcodeFormats(Barcode.FORMAT_QR_CODE)
                    .build();

            // Process the image using the BarcodeScanner from ML Kit
            BarcodeScanning.getClient(options)
                    .process(image)  // Process the image for barcodes
                    .addOnSuccessListener(barcodes -> {
                        if (!barcodes.isEmpty()) {
                            // If QR codes are found, handle the first one
                            handleScannedQRCode(barcodes.get(0).getRawValue());
                        } else {
                            Toast.makeText(this, "No QR code found in image", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(e -> Toast.makeText(this, "Failed to scan QR code from image", Toast.LENGTH_SHORT).show());
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Error processing image", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Handles the decoded QR code content, which is expected to be an event ID.
     * If the QR code contains a valid event ID, it starts a new activity to show the event details.
     *
     * @param eventId The event ID extracted from the QR code.
     */
    private void handleScannedQRCode(String eventId) {
        if (eventId != null && !eventId.isEmpty()) {
            // If the event ID is valid, start the ScannedEventDetailActivity
            Intent intent = new Intent(QRScannerActivity.this, ScannedEventDetailActivity.class);
            intent.putExtra("eventID", eventId);  // Pass the event ID to the next activity
            startActivity(intent);
        } else {
            // If the QR code is invalid, show a toast message
            Toast.makeText(this, "Invalid QR code content", Toast.LENGTH_SHORT).show();
        }
    }
}
