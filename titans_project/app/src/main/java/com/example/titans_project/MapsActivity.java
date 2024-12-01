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


public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_maps);

        db = FirebaseFirestore.getInstance();

        Button returnButton = findViewById(R.id.returnButton);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        } else {
            // Log or handle the error if the map fragment is missing
            throw new IllegalStateException("Map fragment not found!");
        }

        returnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;

        String eventID = getIntent().getStringExtra("eventID");
        if (eventID == null || eventID.isEmpty()) {
            Toast.makeText(this, "Event ID not provided!", Toast.LENGTH_SHORT).show();
            return;
        }

        db.collection("events").document(eventID).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        ArrayList<HashMap<String, Double>> locations =
                                (ArrayList<HashMap<String, Double>>) documentSnapshot.get("locations");

                        if (locations != null && !locations.isEmpty()) {
                            for (HashMap<String, Double> location : locations) {
                                Double latitude = location.get("latitude");
                                Double longitude = location.get("longitude");

                                if (latitude != null && longitude != null) {
                                    LatLng latLng = new LatLng(latitude, longitude);
                                    mMap.addMarker(new MarkerOptions().position(latLng).title("Event Location"));
                                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10));
                                } else {
                                    Log.e("MapsActivity", "Invalid latitude/longitude in Firestore data");
                                }
                            }
                        } else {
                            Log.w("MapsActivity", "No locations found for eventID: " + eventID);
                        }
                    } else {
                        Log.w("MapsActivity", "No document found for eventID: " + eventID);
                    }
                })
                .addOnFailureListener(e -> Log.e("MapsActivity", "Firestore fetch error: ", e));
        }
    }
