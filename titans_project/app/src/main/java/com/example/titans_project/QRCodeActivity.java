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

public class QRCodeActivity extends AppCompatActivity {

    private ImageView qrCodeImageView;
    private Button saveToLocalButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_qr_code);

        qrCodeImageView = findViewById(R.id.qrCodeImageView);
        saveToLocalButton = findViewById(R.id.saveToLocalButton);
        ImageButton backButton = findViewById(R.id.backButton);

        // Set up the back button
        backButton.setOnClickListener(v -> finish());

        // Set up the save button
        saveToLocalButton.setOnClickListener(v -> saveImageToGallery());

        // Get the Event ID from the intent and generate the QR code
        String eventId = getIntent().getStringExtra("eventID");
        if (eventId != null) {
            generateQRCode(eventId);
        } else {
            Toast.makeText(this, "Event ID is missing", Toast.LENGTH_SHORT).show();
        }
    }

    private void generateQRCode(String text) {
        try {
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            BitMatrix bitMatrix = barcodeEncoder.encode(text, BarcodeFormat.QR_CODE, 300, 300);
            Bitmap bitmap = Bitmap.createBitmap(300, 300, Bitmap.Config.RGB_565);

            for (int x = 0; x < 300; x++) {
                for (int y = 0; y < 300; y++) {
                    bitmap.setPixel(x, y, bitMatrix.get(x, y) ? android.graphics.Color.BLACK : android.graphics.Color.WHITE);
                }
            }

            qrCodeImageView.setImageBitmap(bitmap);
        } catch (WriterException e) {
            e.printStackTrace();
            Toast.makeText(this, "Failed to generate QR Code", Toast.LENGTH_SHORT).show();
        }
    }

    private void saveImageToGallery() {
        if (qrCodeImageView.getDrawable() == null) {
            Toast.makeText(this, "No QR Code to save", Toast.LENGTH_SHORT).show();
            return;
        }

        Bitmap bitmap = ((BitmapDrawable) qrCodeImageView.getDrawable()).getBitmap();
        saveBitmapToGallery(this, bitmap);
    }

    private void saveBitmapToGallery(Context context, Bitmap bitmap) {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.DISPLAY_NAME, "QRCode_" + System.currentTimeMillis() + ".jpg");
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            values.put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/QR Codes");
        }

        try (OutputStream outputStream = context.getContentResolver().openOutputStream(
                context.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values))) {
            if (outputStream != null) {
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
                Toast.makeText(context, "QR Code saved to local gallery", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(context, "Failed to save the QR code", Toast.LENGTH_SHORT).show();
        }
    }
}
