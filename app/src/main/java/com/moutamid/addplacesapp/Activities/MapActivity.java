package com.moutamid.addplacesapp.Activities;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.moutamid.addplacesapp.Helper.Config;
import com.moutamid.addplacesapp.R;

public class MapActivity extends FragmentActivity implements OnMapReadyCallback {
    private GoogleMap mMap;
    private FusedLocationProviderClient fusedLocationClient;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(this, location -> {
                        if (location != null) {
                            LatLng currentLocation = new LatLng(location.getLatitude(), location.getLongitude());
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 15));
                        } else {
                            Toast.makeText(this, "Unable to get current location", Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
        }

        mMap.setOnMapLongClickListener(this::showCoordinatesDialog);
    }

    private void showCoordinatesDialog(LatLng latLng) {
        Config.lat = latLng.latitude;
        Config.lng = latLng.longitude;
        String message = "Latitude: " + latLng.latitude + "\nLongitude: " + latLng.longitude;
        new AlertDialog.Builder(this)
                .setTitle("Selected Coordinates")
                .setMessage(message)
                .setPositiveButton("Next", (dialog, which) -> finish())
                .setNegativeButton("Cancel", null)
                .show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    mMap.setMyLocationEnabled(true);
                    fusedLocationClient.getLastLocation()
                            .addOnSuccessListener(this, location -> {
                                if (location != null) {
                                    LatLng currentLocation = new LatLng(location.getLatitude(), location.getLongitude());
                                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 15));
                                } else {
                                    Toast.makeText(this, "Unable to get current location", Toast.LENGTH_SHORT).show();
                                }
                            });
                }
            } else {
                Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
