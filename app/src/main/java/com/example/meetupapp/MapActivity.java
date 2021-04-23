package com.example.meetupapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.meetupapp.Fragments.ChooseLocationFragment;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {
    private MapView mapView;
    private GoogleMap gMap;
    private EditText searchBar;
    private Button searchButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        searchBar = findViewById(R.id.searchBar);
        searchButton = findViewById(R.id.searchButton);
        mapView = findViewById(R.id.mapView);

        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        gMap = googleMap;

        LatLng currLocation = new LatLng(40.730610,-73.935242);
        googleMap.addMarker(new MarkerOptions().position(currLocation).title("Your location"));
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currLocation, 15));
        mapView.onResume();

    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState, @NonNull PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        mapView.onSaveInstanceState(outState);
    }

    //search button code
    public void search(View view) {
        Geocoder geocoder = new Geocoder(this);
        List<Address> possibleAddresses = null;
        String location = searchBar.getText().toString();
        String officialAddress = "";

        if(location != null){
            try {
                possibleAddresses = geocoder.getFromLocationName(location, 5);
            } catch (IOException locationNotFound){
                locationNotFound.printStackTrace();
            }
        }

        Address address = possibleAddresses.get(0);
        LatLng currAddress = new LatLng(address.getLatitude(), address.getLongitude());
        gMap.addMarker(new MarkerOptions().position(currAddress).title(location));
        gMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currAddress, 17));

        officialAddress = address.getAddressLine(0);
        searchBar.setText(officialAddress);
    }

    //Button used to confirm the address
    public void confirmAddress(View view){
        String location = searchBar.getText().toString();

        if (!location.matches("")) {
            new AlertDialog.Builder(this)
                    .setTitle("Verify Information")
                    .setMessage("Is this location correct?\n\n" + "Location: " + searchBar.getText().toString())
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //Send the information back to the activity
                            Intent goBackToFragment = new Intent(getApplicationContext(), ChooseLocationFragment.class);
                            goBackToFragment.putExtra("location", location);
                            setResult(RESULT_OK,goBackToFragment);
                            finish();
                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    }).create().show();
        } else {
            new AlertDialog.Builder(this)
                    .setMessage("Please enter a location")
                    .setPositiveButton("Continue", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    }).create().show();
        }
    }
}