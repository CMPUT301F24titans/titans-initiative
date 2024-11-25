package com.example.titans_project;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
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

import java.util.List;

public class QRScannerActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST_CODE = 100;
    private ImageView qrImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr_scanner);

        qrImageView = findViewById(R.id.qrImageView);

        findViewById(R.id.scan_button).setOnClickListener(v -> startCameraScan());
        findViewById(R.id.chooseImageButton).setOnClickListener(v -> openGalleryForImage());
    }

    private void startCameraScan() {
        new IntentIntegrator(this)
                .setDesiredBarcodeFormats(IntentIntegrator.QR_CODE)
                .setPrompt("Scan a QR code")
                .setCameraId(0)
                .setBeepEnabled(true)
                .setBarcodeImageEnabled(true)
                .initiateScan();
    }

    private void openGalleryForImage() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            Uri imageUri = data.getData();
            qrImageView.setImageURI(imageUri);
            decodeQRCodeFromImage(imageUri);
        } else {
            IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
            if (result != null && result.getContents() != null) {
                handleScannedQRCode(result.getContents());
            } else {
                Toast.makeText(this, "No QR code found", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void decodeQRCodeFromImage(Uri imageUri) {
        try {
            InputImage image = InputImage.fromFilePath(this, imageUri);
            BarcodeScannerOptions options = new BarcodeScannerOptions.Builder()
                    .setBarcodeFormats(Barcode.FORMAT_QR_CODE)
                    .build();

            BarcodeScanning.getClient(options)
                    .process(image)
                    .addOnSuccessListener(barcodes -> {
                        if (!barcodes.isEmpty()) {
                            handleScannedQRCode(barcodes.get(0).getRawValue());
                        } else {
                            Toast.makeText(this, "No QR code found in image", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(e -> Toast.makeText(this, "Failed to scan QR code from image", Toast.LENGTH_SHORT).show());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void handleScannedQRCode(String eventId) {
        if (eventId != null && !eventId.isEmpty()) {
            Intent intent = new Intent(QRScannerActivity.this, ScannedEventDetailActivity.class);
            intent.putExtra("eventID", eventId);
            startActivity(intent);
        } else {
            Toast.makeText(this, "Invalid QR code content", Toast.LENGTH_SHORT).show();
        }
    }
}
