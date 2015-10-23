package com.pontonsoft.keepincontact.activities;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.LocationManager;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.LocationSource;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.pontonsoft.keepincontact.R;
import com.pontonsoft.keepincontact.model.Contact;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MapContactActivity extends FragmentActivity implements
        OnMapReadyCallback {

    private GoogleMap mMap;
    private Contact contact;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_contact);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
                SupportMapFragment mapFragment = (SupportMapFragment)
                getSupportFragmentManager()
                        .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        this.contact =
                (Contact)getIntent().getSerializableExtra("contact");
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move
     the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user
     will be prompted to install
     * it inside the SupportMapFragment. This method will only be
     triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap)
    {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new
                MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        this.mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);

        Geocoder coder = new Geocoder(MapContactActivity.this,
                Locale.getDefault());
        try {
            List<Address> addresses =
                    coder.getFromLocationName(contact.getAdresse(), 5);
            Address address = addresses.get(0);
            LatLng ll = new
                    LatLng(address.getLatitude(),address.getLongitude());
            this.mMap.addMarker(new MarkerOptions().position(ll));
            CameraUpdate cu = CameraUpdateFactory.newLatLngZoom(ll,15);
            this.mMap.moveCamera(cu);
        } catch (IOException ie) {
            ie.printStackTrace();
        }
    }
}