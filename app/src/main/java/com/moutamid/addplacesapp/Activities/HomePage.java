package com.moutamid.addplacesapp.Activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.fxn.stash.Stash;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.moutamid.addplacesapp.Helper.Config;
import com.moutamid.addplacesapp.R;
import com.moutamid.addplacesapp.model.LocationModel;

public class HomePage extends AppCompatActivity implements OnMapReadyCallback {

    private static final String TAG = "HomePage";
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;

    private GoogleMap mMap;
    private DatabaseReference dbRef; // Reference for Realtime Database
    private FusedLocationProviderClient fusedLocationClient;
    Button btnAdd;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);
        btnAdd = findViewById(R.id.btnAdd);
       Config.checkApp(HomePage.this);
        // Obtain the SupportMapFragment and set this activity as the callback listener
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map_fragment);
        mapFragment.getMapAsync(this);

        // Initialize Firebase Realtime Database
        dbRef = FirebaseDatabase.getInstance().getReference().child("AddPlacesApp")
                .child("Places");

        // Initialize FusedLocationProviderClient
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(HomePage.this, AddPlacesActivity.class));
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Check and request location permission
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
        } else {
            mMap.setMyLocationEnabled(true);
            moveToCurrentLocation(); // Move to current location
        }

        fetchPlacesData(); // Call function to fetch place data
    }

    private void fetchPlacesData() {
        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                    LocationModel place = childSnapshot.getValue(LocationModel.class);
                    if (place != null) {
                        addMarkerToMap(place);

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w(TAG, "Error getting data: ", error.toException());
            }
        });
    }

    private void addMarkerToMap(LocationModel place) {
        LatLng latLng = new LatLng(Double.parseDouble(place.getLat()), Double.parseDouble(place.getLng()));
        Marker marker = mMap.addMarker(new MarkerOptions().position(latLng).title(place.getName()));

        // Set marker click listener
        marker.setTag(place);
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                LocationModel place = (LocationModel) marker.getTag();
                showPlaceDetails(place);  // Call method to show details
                return true;  // Consume click event
            }
        });
    }

    private void showPlaceDetails(LocationModel place) {
        Stash.put("currentLocationModel", place);
        startActivity(new Intent(this, DetailsActivity.class));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    mMap.setMyLocationEnabled(true);
                    moveToCurrentLocation(); // Move to current location after permission is granted
                }
            } else {
                Toast.makeText(this, "Location permission is required to use this feature.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void moveToCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.getLastLocation().addOnSuccessListener(this, location -> {
                if (location != null) {
                    LatLng currentLatLng = new LatLng(location.getLatitude(), location.getLongitude());
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 13));
                    Config.current_lat = location.getLatitude();
                    Config.current_lng = location.getLongitude();
                }
            });
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
//        onMapReady(mMap);
    }
}
