package com.example.administrator.demo.activities;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;


import com.example.administrator.demo.R;
import com.example.administrator.demo.fragments.AnnounceTrafficJamFragment;
import com.example.administrator.demo.fragments.MapFragment;
import com.example.administrator.demo.models.Camera;
import com.example.administrator.demo.models.MyLatlng;
import com.example.administrator.demo.models.Point;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.OnDisconnect;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MapsActivity extends AppCompatActivity
        implements  GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks, LocationListener {
    private List<Point> trafficjams = new ArrayList<>();

    GoogleApiClient googleApiClient;
    Location myLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        Bundle bundle = getIntent().getExtras();
        String action = bundle.getString("action");

        googleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addApi(LocationServices.API)
                .build();
        googleApiClient.connect();
//        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
//        mapFragment.getMapAsync(this);
        if(action.equals("viewmap")) {
            FragmentManager manager = getSupportFragmentManager();
            MapFragment fragment = new MapFragment();
            manager.beginTransaction().replace(R.id.container_main, fragment, fragment.getTag()).commit();
        }
        else if(action.equals("announce"))
        {
            FragmentManager manager = getSupportFragmentManager();
            AnnounceTrafficJamFragment fragment = new AnnounceTrafficJamFragment();
            manager.beginTransaction().replace(R.id.container_main, fragment, fragment.getTag()).commit();
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        startLocationServices();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onLocationChanged(Location location) {
        Toast.makeText(this, "changed: " + location.toString(), Toast.LENGTH_SHORT).show();
        myLocation = location;


    }

    @Override
    protected void onStop() {
        if (googleApiClient != null)
            if (googleApiClient.isConnected())
                googleApiClient.disconnect();
     //   LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, this);
        Toast.makeText(this, "asdasdasdsad", Toast.LENGTH_SHORT).show();
        super.onStop();


    }

    public void startLocationServices() {
        Toast.makeText(this, "start service", Toast.LENGTH_LONG).show();
        LocationRequest request = LocationRequest.create().setPriority(LocationRequest.PRIORITY_LOW_POWER);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, request, this);

    }

}

