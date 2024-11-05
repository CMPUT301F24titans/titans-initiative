package CMPUT301Project;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

public class AdminControlPanelActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_control_panel);

        // Button for Event Management
        findViewById(R.id.btnEventManagement).setOnClickListener(view -> 
            startActivity(new Intent(this, EventManagementActivity.class))
        );

        // Button for Profile Management
        findViewById(R.id.btnProfileManagement).setOnClickListener(view -> 
            startActivity(new Intent(this, ProfileManagementActivity.class))
        );

        // Button for Image Management
        findViewById(R.id.btnImageManagement).setOnClickListener(view -> 
            startActivity(new Intent(this, ImageManagementActivity.class))
        );

        // Button for Notification Management
        findViewById(R.id.btnNotificationManagement).setOnClickListener(view -> 
            startActivity(new Intent(this, NotificationManagementActivity.class))
        );

        // Button for QR Code Management
        findViewById(R.id.btnQRCodeManagement).setOnClickListener(view -> 
            startActivity(new Intent(this, QRCodeManagementActivity.class))
        );

        // Button for Facility Management (optional, based on your requirement)
        findViewById(R.id.btnFacilityManagement).setOnClickListener(view -> 
            startActivity(new Intent(this, FacilityManagementActivity.class))
        );
    }
}

