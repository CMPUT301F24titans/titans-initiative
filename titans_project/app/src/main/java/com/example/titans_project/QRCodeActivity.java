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

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.io.OutputStream;

public class QRCodeActivity extends AppCompatActivity {

    private ImageView qrCodeImageView;
    private FirebaseFirestore db;
    private CollectionReference eventsRef;
    private Button saveToLocalButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr_code);

        // 初始化 Firebase 和视图
        db = FirebaseFirestore.getInstance();
        eventsRef = db.collection("events");
        qrCodeImageView = findViewById(R.id.qrCodeImageView);
        saveToLocalButton = findViewById(R.id.saveToLocalButton);
        ImageButton backButton = findViewById(R.id.backButton);

        // 设置返回按钮
        backButton.setOnClickListener(v -> finish());

        // 设置保存按钮
        saveToLocalButton.setOnClickListener(v -> saveImageToGallery());

        // 从数据库获取数据并生成二维码
        fetchEventDataAndGenerateQRCode();
    }

    private void fetchEventDataAndGenerateQRCode() {
        // 假设 document ID 是 "fakeEventTitle"，可以根据需要更换成其他 Event 名称
        eventsRef.document("fakeEventTitle")
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        // 获取 Event ID 字段
                        String eventId = documentSnapshot.getString("Event ID");
                        if (eventId != null) {
                            // 使用 Event ID 生成二维码
                            generateQRCode(eventId);
                        } else {
                            Toast.makeText(this, "Event ID not found", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(this, "Event data not found", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Failed to load event data", Toast.LENGTH_SHORT).show());
    }

    private void generateQRCode(String text) {
        try {
            // 使用 ZXing 库生成二维码
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            BitMatrix bitMatrix = barcodeEncoder.encode(text, BarcodeFormat.QR_CODE, 300, 300);
            Bitmap bitmap = Bitmap.createBitmap(300, 300, Bitmap.Config.RGB_565);

            for (int x = 0; x < 300; x++) {
                for (int y = 0; y < 300; y++) {
                    bitmap.setPixel(x, y, bitMatrix.get(x, y) ? android.graphics.Color.BLACK : android.graphics.Color.WHITE);
                }
            }

            // 将生成的二维码设置到 ImageView 中
            qrCodeImageView.setImageBitmap(bitmap);
        } catch (WriterException e) {
            e.printStackTrace();
            Toast.makeText(this, "Failed to generate QR Code", Toast.LENGTH_SHORT).show();
        }
    }

    private void saveImageToGallery() {
        // 检查 ImageView 中是否有二维码图片
        if (qrCodeImageView.getDrawable() == null) {
            Toast.makeText(this, "没有二维码可以保存", Toast.LENGTH_SHORT).show();
            return;
        }

        // 将 ImageView 中的 drawable 转换为 Bitmap
        Bitmap bitmap = ((BitmapDrawable) qrCodeImageView.getDrawable()).getBitmap();

        // 调用方法保存二维码到图库
        saveBitmapToGallery(this, bitmap);
    }

    private void saveBitmapToGallery(Context context, Bitmap bitmap) {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.DISPLAY_NAME, "QRCode_" + System.currentTimeMillis() + ".jpg");
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");

        // 设置保存路径为“Pictures/QR Codes”
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            values.put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/QR Codes");
        }

        try (OutputStream outputStream = context.getContentResolver().openOutputStream(
                context.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values))) {
            if (outputStream != null) {
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
                Toast.makeText(context, "二维码已保存到本地图库", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(context, "保存二维码失败", Toast.LENGTH_SHORT).show();
        }
    }
}
