package com.example.cameraqr;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

import java.io.IOException;

public class ScanActivity extends AppCompatActivity {
    SurfaceView surfaceView;
    TextView BarcodeValue;
    TextView Distancetext;
    private BarcodeDetector barcodeDetector;
    private CameraSource cameraSource;
    private static final int REQUEST_CAMERA_PERMISSION = 201;
    private static final int REQUEST_LOCATION_PERMISSION = 202;
    private LocationManager locationManager;
    private LocationListener locationListener;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);

        // Find and set up the back button
        Button btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        // Find and initialize the UI elements
        BarcodeValue = findViewById(R.id.BarcodeValue);
        surfaceView = findViewById(R.id.surfaceView);
        Distancetext = findViewById(R.id.Distancetext);

        // Initialize the LocationManager and LocationListener
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                // Handle location updates if needed
            }
        };

        // Initialize barcode detection and camera sources
        initialiseDetectorsAndSources();
    }

    private void initialiseDetectorsAndSources() {
        // Initialize the BarcodeDetector for QR code detection
        barcodeDetector = new BarcodeDetector.Builder(this)
                .setBarcodeFormats(Barcode.QR_CODE)
                .build();

        // Create the CameraSource for scanning
        cameraSource = new CameraSource.Builder(this, barcodeDetector)
                .setRequestedPreviewSize(1920, 1080)
                .setAutoFocusEnabled(true)
                .build();

        // Add a callback for the SurfaceView
        surfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                try {
                    // Check camera permissions and start the camera source
                    if (ActivityCompat.checkSelfPermission(
                            ScanActivity.this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                        if (surfaceView.getHolder() != null) {
                            cameraSource.start(surfaceView.getHolder());
                        } else {
                            Toast.makeText(ScanActivity.this, "Surface is not ready", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        // Request camera permissions if not granted
                        ActivityCompat.requestPermissions(ScanActivity.this, new
                                String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
                // Handle surface changes if needed
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                // Stop the camera source when the surface is destroyed
                cameraSource.stop();
            }
        });

        barcodeDetector.setProcessor(new Detector.Processor<Barcode>() {
            @Override
            public void release() {
                Toast.makeText(getApplicationContext(),
                        "To prevent memory leaks, the barcode scanner has been stopped",
                        Toast.LENGTH_SHORT).show();
            }

            @Override
            public void receiveDetections(Detector.Detections<Barcode> detections) {
                final SparseArray<Barcode> barcodes = detections.getDetectedItems();
                if (barcodes.size() != 0) {
                    final String qrCodeValue = barcodes.valueAt(0).displayValue;

                    BarcodeValue.post(new Runnable() {
                        @Override
                        public void run() {
                            BarcodeValue.setText(qrCodeValue);
                            extractAndDisplayDistance(qrCodeValue);
                        }
                    });
                }
            }

            private void extractAndDisplayDistance(String qrCodeValue) {
                if (qrCodeValue.startsWith("geo:")) {
                    String geoData = qrCodeValue.substring(4);
                    String[] latLng = geoData.split(",");
                    if (latLng.length == 2) {
                        double qrLatitude = Double.parseDouble(latLng[0].trim());
                        double qrLongitude = Double.parseDouble(latLng[1].trim());

                        Location lastKnownLocation = getLastKnownLocation();

                        if (lastKnownLocation != null) {
                            double deviceLatitude = lastKnownLocation.getLatitude();
                            double deviceLongitude = lastKnownLocation.getLongitude();

                            float[] results = new float[1];
                            Location.distanceBetween(
                                    deviceLatitude, deviceLongitude,
                                    qrLatitude, qrLongitude,
                                    results
                            );
                            double distanceInKm = results[0] / 1000.0;

                            Distancetext.setText(String.format("Distance: %.2f km", distanceInKm));
                            BarcodeValue.setText("QR code: " + qrCodeValue);
                        } else {
                            Distancetext.setText("Scan again!");
                            BarcodeValue.setText("QR code: " + qrCodeValue);
                        }
                    }
                } else {
                    // Handle the case where the data is not a valid geo QR code
                    System.out.println("Data Invalid");
                }
            }

            private Location getLastKnownLocation() {
                if (ActivityCompat.checkSelfPermission(
                        ScanActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    locationManager.requestLocationUpdates(
                            LocationManager.GPS_PROVIDER,
                            0,
                            0,
                            locationListener
                    );
                    return locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                } else {
                    // Request location permissions if not granted
                    ActivityCompat.requestPermissions(ScanActivity.this, new
                            String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION_PERMISSION);
                    return null;
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        // Return to the main activity when the back button is pressed
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Release the camera source to free resources
        cameraSource.release();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Reinitialize detectors and sources when the activity resumes
        initialiseDetectorsAndSources();
    }
}
