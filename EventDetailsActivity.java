//Event detail
public class EventDetailsActivity extends AppCompatActivity {

    private TextView eventName, eventDate, eventLocation;
    private Button editButton, deleteButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_details);

        eventName = findViewById(R.id.eventName);
        eventDate = findViewById(R.id.eventDate);
        eventLocation = findViewById(R.id.eventLocation);
        editButton = findViewById(R.id.editEventButton);
        deleteButton = findViewById(R.id.deleteEventButton);

        // Fetch and display event details here
        // Example:
        eventName.setText("Sample Event");
        eventDate.setText("2024-10-30");
        eventLocation.setText("Sample Location");

        editButton.setOnClickListener(view -> {
            // Code to edit the event
        });

        deleteButton.setOnClickListener(view -> {
            // Code to delete the event
        });
    }
}
//event_detail.xml
<LinearLayout
    xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:orientation="vertical"
    android:padding="16dp">

    <TextView
        android:id="@+id/eventName"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="Event Name"
        android:textSize="18sp"/>

    <TextView
        android:id="@+id/eventDate"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="Event Date"
        android:paddingTop="8dp"/>

    <TextView
        android:id="@+id/eventLocation"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="Event Location"
        android:paddingTop="8dp"/>

    <Button
        android:id="@+id/editEventButton"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:text="Edit Event"/>

    <Button
        android:id="@+id/deleteEventButton"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:text="Delete Event"/>
</LinearLayout>
//Profile Detail
public class ProfileDetailsActivity extends AppCompatActivity {

    private TextView profileName, profileEmail, profilePhone;
    private Button editButton, deleteButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_details);

        profileName = findViewById(R.id.profileName);
        profileEmail = findViewById(R.id.profileEmail);
        profilePhone = findViewById(R.id.profilePhone);
        editButton = findViewById(R.id.editProfileButton);
        deleteButton = findViewById(R.id.deleteProfileButton);

        // Load profile data
        profileName.setText("John Doe");
        profileEmail.setText("johndoe@example.com");
        profilePhone.setText("123-456-7890");

        editButton.setOnClickListener(view -> {
            // Code to edit profile
        });

        deleteButton.setOnClickListener(view -> {
            // Code to delete profile
        });
    }
}
//profile_detail.xml
<LinearLayout
    xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:orientation="vertical"
    android:padding="16dp">

    <TextView
        android:id="@+id/profileName"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="Name"
        android:textSize="18sp"/>

    <TextView
        android:id="@+id/profileEmail"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="Email"
        android:paddingTop="8dp"/>

    <TextView
        android:id="@+id/profilePhone"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="Phone"
        android:paddingTop="8dp"/>

    <Button
        android:id="@+id/editProfileButton"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:text="Edit Profile"/>

    <Button
        android:id="@+id/deleteProfileButton"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:text="Delete Profile"/>
</LinearLayout>
//New Notification
public class CreateNotificationActivity extends AppCompatActivity {

    private EditText notificationTitle, notificationMessage;
    private Button sendButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_notification);

        notificationTitle = findViewById(R.id.notificationTitle);
        notificationMessage = findViewById(R.id.notificationMessage);
        sendButton = findViewById(R.id.sendNotificationButton);

        sendButton.setOnClickListener(view -> {
            // Code to send notification
        });
    }
}
//new_notification.xml
<LinearLayout
    xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:orientation="vertical"
    android:padding="16dp">

    <EditText
        android:id="@+id/notificationTitle"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:hint="Notification Title"/>

    <EditText
        android:id="@+id/notificationMessage"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:hint="Notification Message"
        android:paddingTop="8dp"/>

    <Button
        android:id="@+id/sendNotificationButton"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:text="Send Notification"/>
</LinearLayout>
//Scheduled Notification
public class ScheduledNotificationActivity extends AppCompatActivity {

    private TextView notificationTitle, notificationDate, notificationMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scheduled_notification);

        notificationTitle = findViewById(R.id.scheduledNotificationTitle);
        notificationDate = findViewById(R.id.scheduledNotificationDate);
        notificationMessage = findViewById(R.id.scheduledNotificationMessage);

        // Load scheduled notification data here
        notificationTitle.setText("Sample Notification");
        notificationDate.setText("2024-11-05 10:00 AM");
        notificationMessage.setText("This is a scheduled notification.");
    }
}
//scheduled_notification.xml
<LinearLayout
    xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:orientation="vertical"
    android:padding="16dp">

    <TextView
        android:id="@+id/scheduledNotificationTitle"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="Notification Title"
        android:textSize="18sp"/>

    <TextView
        android:id="@+id/scheduledNotificationDate"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="Scheduled Date"
        android:paddingTop="8dp"/>

    <TextView
        android:id="@+id/scheduledNotificationMessage"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="Notification Message"
        android:paddingTop="8dp"/>
</LinearLayout>
//Facility Profile
public class FacilityProfileActivity extends AppCompatActivity {

    private TextView facilityName, facilityAddress, facilityContact;
    private Button editButton, deleteButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_facility_profile);

        facilityName = findViewById(R.id.facilityName);
        facilityAddress = findViewById(R.id.facilityAddress);
        facilityContact = findViewById(R.id.facilityContact);
        editButton = findViewById(R.id.editFacilityButton);
        deleteButton = findViewById(R.id.deleteFacilityButton);

        // Load facility data here
    }
}
//facility_profile.xml
<LinearLayout
    xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:orientation="vertical"
    android:padding="16dp">

    <TextView
        android:id="@+id/facilityName"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="Facility Name"
        android:textSize="18sp"/>

    <TextView
        android:id="@+id/facilityAddress"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="Address"
        android:paddingTop="8dp"/>

    <TextView
        android:id="@+id/facilityContact"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="Contact Info"
        android:paddingTop="8dp"/>

    <Button
        android:id="@+id/editFacilityButton"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:text="Edit Facility"/>

    <Button
        android:id="@+id/deleteFacilityButton"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:text="Delete Facility"/>
</LinearLayout>
//View Notification
public class ViewNotificationActivity extends AppCompatActivity {

    private TextView notificationTitle, notificationDate, notificationMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_notification);

        notificationTitle = findViewById(R.id.notificationTitle);
        notificationDate = findViewById(R.id.notificationDate);
        notificationMessage = findViewById(R.id.notificationMessage);

        // Load notification data here
        notificationTitle.setText("Event Update");
        notificationDate.setText("2024-11-15 09:00 AM");
        notificationMessage.setText("This is a notification regarding the upcoming event.");
    }
}
//view_notification.xml
<LinearLayout
    xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:orientation="vertical"
    android:padding="16dp">

    <TextView
        android:id="@+id/notificationTitle"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="Notification Title"
        android:textSize="18sp"/>

    <TextView
        android:id="@+id/notificationDate"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="Notification Date"
        android:paddingTop="8dp"/>

    <TextView
        android:id="@+id/notificationMessage"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="Notification Message"
        android:paddingTop="8dp"/>
</LinearLayout>
//NewFacility
public class CreateFacilityActivity extends AppCompatActivity {

    private EditText facilityName, facilityAddress, facilityContact;
    private Button createFacilityButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_facility);

        facilityName = findViewById(R.id.facilityName);
        facilityAddress = findViewById(R.id.facilityAddress);
        facilityContact = findViewById(R.id.facilityContact);
        createFacilityButton = findViewById(R.id.createFacilityButton);

        createFacilityButton.setOnClickListener(view -> {
            // Code to create new facility
            String name = facilityName.getText().toString();
            String address = facilityAddress.getText().toString();
            String contact = facilityContact.getText().toString();

            // Logic to create the facility goes here
        });
    }
}
//new_facility.xml
<LinearLayout
    xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:orientation="vertical"
    android:padding="16dp">

    <EditText
        android:id="@+id/facilityName"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:hint="Facility Name"/>

    <EditText
        android:id="@+id/facilityAddress"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:hint="Facility Address"
        android:paddingTop="8dp"/>

    <EditText
        android:id="@+id/facilityContact"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:hint="Facility Contact"
        android:paddingTop="8dp"/>

    <Button
        android:id="@+id/createFacilityButton"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:text="Create Facility"
        android:paddingTop="16dp"/>
</LinearLayout>
//QR Code
public class EventQRCodeActivity extends AppCompatActivity {

    private ImageView qrCodeImageView;
    private Button generateQRCodeButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_qr_code);

        qrCodeImageView = findViewById(R.id.qrCodeImageView);
        generateQRCodeButton = findViewById(R.id.generateQRCodeButton);

        generateQRCodeButton.setOnClickListener(view -> {
            // Code to generate and display QR Code
            // Placeholder code for generating a QR code
            // Bitmap qrCodeBitmap = generateQRCode("Sample Event Data");
            // qrCodeImageView.setImageBitmap(qrCodeBitmap);
        });
    }

    // Placeholder method for generating a QR code
    /*
    private Bitmap generateQRCode(String eventData) {
        try {
            BitMatrix bitMatrix = new MultiFormatWriter().encode(eventData, BarcodeFormat.QR_CODE, 200, 200);
            int width = bitMatrix.getWidth();
            int height = bitMatrix.getHeight();
            Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);

            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    bitmap.setPixel(x, y, bitMatrix.get(x, y) ? Color.BLACK : Color.WHITE);
                }
            }
            return bitmap;
        } catch (WriterException e) {
            e.printStackTrace();
            return null;
        }
    }
    */
}
//qr_code.xml
<LinearLayout
    xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:orientation="vertical"
    android:padding="16dp">

    <ImageView
        android:id="@+id/qrCodeImageView"
        android:layout_width="200dp"
        android:layout_height="200dp"
        android:layout_gravity="center"
        android:contentDescription="QR Code Image"/>

    <Button
        android:id="@+id/generateQRCodeButton"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:text="Generate QR Code"
        android:paddingTop="16dp"/>
</LinearLayout>
=