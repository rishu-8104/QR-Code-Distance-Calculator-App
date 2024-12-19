package com.example.cameraqr;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.content.pm.PackageManager;
import android.os.Bundle;

import android.content.Intent;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private Button scanButton;
    private FusedLocationProviderClient fusedLocationClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize the FusedLocationProviderClient for obtaining device location
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        // Initialize UI elements and set up click listener
        initViews();
    }

    private void initViews() {
        // Find and assign the scanButton element from the layout
        scanButton = findViewById(R.id.scanButton);

        // Set a click listener for the scanButton
        scanButton.setOnClickListener(this);
    }

    private void updateCurrentLocation() {
        // Check if location permissions are granted
        if (ActivityCompat.checkSelfPermission(
                this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(
                this, android.Manifest.permission.ACCESS_COARSE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED) {

            // If permissions are not granted, request them
            String[] permissions = {
                    android.Manifest.permission.ACCESS_COARSE_LOCATION,
                    android.Manifest.permission.ACCESS_FINE_LOCATION
            };
            ActivityCompat.requestPermissions(this, permissions, 42);
        }

        // Obtain the last known location of the device
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, location -> {
                    if (location != null) {
                        // Get latitude and longitude from the obtained location
                        double latitude = location.getLatitude();
                        double longitude = location.getLongitude();
                    }
                });
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.scanButton) {
            // Start the ScanActivity when the scanButton is clicked
            startActivity(new Intent(MainActivity.this, ScanActivity.class));
        }
    }
}
