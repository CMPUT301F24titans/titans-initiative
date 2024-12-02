package com.example.titans_project;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Activity for displaying event locations on a Google Map.
 * The locations are retrieved from Firebase Firestore and marked on the map.
 * Users can press a button to return to the previous screen.
 */
public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;         // Google Map object for displaying the map and markers
    private FirebaseFirestore db;   // Firestore instance to fetch event data

    /**
     * Called when the activity is created. Initializes the UI and the map.
     * Retrieves event data from Firestore and sets up the map.
     *
     * @param savedInstanceState The saved state of the activity, if any.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_maps);

        // Initialize Firebase Firestore instance
        db = FirebaseFirestore.getInstance();

        // Find the return button and set its click listener
        Button returnButton = findViewById(R.id.returnButton);

        // Obtain the SupportMapFragment and notify when the map is ready to be used
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        } else {
            // Throw an error if the map fragment is not found
            throw new IllegalStateException("Map fragment not found!");
        }

        // Set up the return button to close the activity when clicked
        returnButton.setOnClickListener(view -> finish());
    }

    /**
     * This method is called when the map is ready to be used. It retrieves event data
     * from Firestore and adds markers to the map for each location associated with the event.
     *
     * @param googleMap The GoogleMap object that has been initialized.
     */
    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;

        // Get the event ID passed through the intent
        String eventID = getIntent().getStringExtra("eventID");

        // Check if the eventID is null or empty
        if (eventID == null || eventID.isEmpty()) {
            Toast.makeText(this, "Event ID not provided!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Fetch event data from Firestore based on the eventID
        db.collection("events").document(eventID).get()
                .addOnSuccessListener(documentSnapshot -> {
                    // Check if the document exists in Firestore
                    if (documentSnapshot.exists()) {
                        // Retrieve the locations from the event document
                        ArrayList<HashMap<String, Double>> locations =
                                (ArrayList<HashMap<String, Double>>) documentSnapshot.get("locations");

                        // Check if locations are available and add markers to the map
                        if (locations != null && !locations.isEmpty()) {
                            for (HashMap<String, Double> location : locations) {
                                Double latitude = location.get("latitude");
                                Double longitude = location.get("longitude");

                                // Add marker to the map if latitude and longitude are valid
                                if (latitude != null && longitude != null) {
                                    LatLng latLng = new LatLng(latitude, longitude);
                                    mMap.addMarker(new MarkerOptions().position(latLng).title("Event Location"));
                                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10));
                                } else {
                                    // Log an error if latitude or longitude is invalid
                                    Log.e("MapsActivity", "Invalid latitude/longitude in Firestore data");
                                }
                            }
                        } else {
                            // Log a warning if no locations are found for the event
                            Log.w("MapsActivity", "No locations found for eventID: " + eventID);
                        }
                    } else {
                        // Log a warning if the event document is not found in Firestore
                        Log.w("MapsActivity", "No document found for eventID: " + eventID);
                    }
                })
                .addOnFailureListener(e -> {
                    // Log an error if the Firestore fetch fails
                    Log.e("MapsActivity", "Firestore fetch error: ", e);
                });
    }
}
