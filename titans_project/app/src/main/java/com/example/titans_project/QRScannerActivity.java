package com.example.titans_project;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.mlkit.vision.barcode.BarcodeScannerOptions;
import com.google.mlkit.vision.barcode.BarcodeScanning;
import com.google.mlkit.vision.barcode.common.Barcode;
import com.google.mlkit.vision.common.InputImage;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.io.IOException;
import java.util.List;

public class QRScannerActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST_CODE = 100;
    private ImageView qrImageView;
    private Button scanButton, chooseImageButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr_scanner);

        // 初始化按钮和图片视图
        qrImageView = findViewById(R.id.qrImageView);
        scanButton = findViewById(R.id.scanButton);
        chooseImageButton = findViewById(R.id.chooseImageButton);

        // 设置按钮点击事件
        scanButton.setOnClickListener(v -> startCameraScan());
        chooseImageButton.setOnClickListener(v -> openGalleryForImage());
    }

    /**
     * 启动相机扫描二维码
     */
    private void startCameraScan() {
        new IntentIntegrator(this)
                .setDesiredBarcodeFormats(IntentIntegrator.QR_CODE)
                .setPrompt("Scan a QR code")
                .setCameraId(0) // 使用后置摄像头
                .setBeepEnabled(true)
                .setBarcodeImageEnabled(true)
                .initiateScan();
    }

    /**
     * 打开图库选择图片
     */
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
            qrImageView.setImageURI(imageUri);  // 显示选中的图片
            decodeQRCodeFromImage(imageUri);    // 解析二维码
        } else {
            // 处理相机扫描结果
            IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
            if (result != null && result.getContents() != null) {
                Toast.makeText(this, "Scanned from camera: " + result.getContents(), Toast.LENGTH_LONG).show();
            } else {
                super.onActivityResult(requestCode, resultCode, data);
            }
        }
    }

    /**
     * 使用 ML Kit 解析本地图片中的二维码
     */
    private void decodeQRCodeFromImage(Uri imageUri) {
        try {
            InputImage image = InputImage.fromFilePath(this, imageUri);
            BarcodeScannerOptions options = new BarcodeScannerOptions.Builder()
                    .setBarcodeFormats(Barcode.FORMAT_QR_CODE)
                    .build();

            BarcodeScanning.getClient(options)
                    .process(image)
                    .addOnSuccessListener(this::handleBarcodeResult)
                    .addOnFailureListener(e -> Toast.makeText(this, "Failed to scan QR code from image", Toast.LENGTH_SHORT).show());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 处理从图像中解析的二维码结果
     */
    private void handleBarcodeResult(List<Barcode> barcodes) {
        if (!barcodes.isEmpty()) {
            for (Barcode barcode : barcodes) {
                String rawValue = barcode.getRawValue();
                Toast.makeText(this, "Scanned from image: " + rawValue, Toast.LENGTH_LONG).show();
                break; // 只处理第一个二维码
            }
        } else {
            Toast.makeText(this, "No QR code found in image", Toast.LENGTH_SHORT).show();
        }
    }
}
