package com.example.titans_project;

import android.content.ContentValues;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.io.OutputStream;

/**
 * This activity is responsible for generating and displaying a QR code for a given event ID.
 * It also allows the user to save the generated QR code to their device's local gallery.
 */
public class QRCodeActivity extends AppCompatActivity {

    private ImageView qrCodeImageView;  // ImageView to display the generated QR code
    private Button saveToLocalButton;   // Button to save the QR code to the local gallery

    /**
     * Initializes the activity. Sets up the UI components and generates the QR code for the event ID.
     *
     * @param savedInstanceState The state of the activity, if it was previously saved.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_qr_code);

        qrCodeImageView = findViewById(R.id.qrCodeImageView);
        saveToLocalButton = findViewById(R.id.saveToLocalButton);
        ImageButton backButton = findViewById(R.id.backButton);

        // Set up the back button to finish the activity when clicked
        backButton.setOnClickListener(v -> finish());

        // Set up the save button to save the QR code to the gallery when clicked
        saveToLocalButton.setOnClickListener(v -> saveImageToGallery());

        // Retrieve the event ID from the intent and generate the QR code
        String eventId = getIntent().getStringExtra("eventID");
        if (eventId != null) {
            generateQRCode(eventId);  // Generate QR code for the given event ID
        } else {
            Toast.makeText(this, "Event ID is missing", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Generates a QR code based on the provided text (event ID) and displays it in the ImageView.
     *
     * @param text The text to encode into the QR code (in this case, the event ID).
     */
    private void generateQRCode(String text) {
        try {
            // Initialize the BarcodeEncoder to generate the QR code
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            // Encode the text into a BitMatrix for a QR code with 300x300 dimensions
            BitMatrix bitMatrix = barcodeEncoder.encode(text, BarcodeFormat.QR_CODE, 300, 300);
            Bitmap bitmap = Bitmap.createBitmap(300, 300, Bitmap.Config.RGB_565);

            // Set the pixels in the Bitmap based on the BitMatrix (black for 1, white for 0)
            for (int x = 0; x < 300; x++) {
                for (int y = 0; y < 300; y++) {
                    bitmap.setPixel(x, y, bitMatrix.get(x, y) ? android.graphics.Color.BLACK : android.graphics.Color.WHITE);
                }
            }

            // Display the generated QR code in the ImageView
            qrCodeImageView.setImageBitmap(bitmap);
        } catch (WriterException e) {
            // Handle QR code generation failure
            e.printStackTrace();
            Toast.makeText(this, "Failed to generate QR Code", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Saves the QR code displayed in the ImageView to the device's local gallery.
     * Displays a Toast message indicating the success or failure of the operation.
     */
    private void saveImageToGallery() {
        // Check if the ImageView contains a drawable (QR code image)
        if (qrCodeImageView.getDrawable() == null) {
            Toast.makeText(this, "No QR Code to save", Toast.LENGTH_SHORT).show();
            return;
        }

        // Convert the drawable to a Bitmap
        Bitmap bitmap = ((BitmapDrawable) qrCodeImageView.getDrawable()).getBitmap();
        // Save the Bitmap to the gallery
        saveBitmapToGallery(this, bitmap);
    }

    /**
     * Saves the provided Bitmap image to the device's gallery.
     *
     * @param context The application context.
     * @param bitmap  The Bitmap image to save.
     */
    private void saveBitmapToGallery(Context context, Bitmap bitmap) {
        // Create ContentValues to define the image metadata (display name, MIME type, etc.)
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.DISPLAY_NAME, "QRCode_" + System.currentTimeMillis() + ".jpg");
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");

        // For Android Q and above, specify the relative path for the image storage location
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            values.put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/QR Codes");
        }

        try (OutputStream outputStream = context.getContentResolver().openOutputStream(
                context.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values))) {

            // If outputStream is available, write the bitmap to the stream
            if (outputStream != null) {
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);  // Compress the bitmap as JPEG
                Toast.makeText(context, "QR Code saved to local gallery", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            // Handle saving failure
            e.printStackTrace();
            Toast.makeText(context, "Failed to save the QR code", Toast.LENGTH_SHORT).show();
        }
    }
}
