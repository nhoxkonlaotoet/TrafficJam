package com.example.administrator.demo.activities;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;


import com.example.administrator.demo.IFragmentManager;
import com.example.administrator.demo.R;
import com.example.administrator.demo.fragments.AnnounceTrafficJamFragment;
import com.example.administrator.demo.fragments.MapFragment;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;



public class MapsActivity extends AppCompatActivity
        implements  GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks, LocationListener {
    IFragmentManager interfaceTrans;

    GoogleApiClient googleApiClient;
   public Location myLocation;
    FragmentManager manager = getSupportFragmentManager();
   //init Communicating
    @Override
    public void onAttachFragment(android.support.v4.app.Fragment fragment) {
        super.onAttachFragment(fragment);
        interfaceTrans = (IFragmentManager) fragment;

    }

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

        if(action.equals("viewmap")) {


            MapFragment fragment = new MapFragment();
            manager.beginTransaction().replace(R.id.container_main, fragment, fragment.getTag()).commit();
        }
        else
            if(action.equals("announce"))
        {

            AnnounceTrafficJamFragment fragment = new AnnounceTrafficJamFragment();
            manager.beginTransaction().replace(R.id.container_main, fragment, fragment.getTag()).commit();
        }
        else
            if(action.equals("viewcamera"))
            {

                MapFragment fragment = new MapFragment();
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
    public void onBackPressed() {
        super.onBackPressed();
        Log.e("onBackPressed: ", "true");
        finish();
    }

    @Override
    public void onLocationChanged(Location location) {
        myLocation = location;
        interfaceTrans.onDataChanged(location);
    }

    @Override
    protected void onStop() {
        if (googleApiClient != null)
            if (googleApiClient.isConnected())
                googleApiClient.disconnect();
     //   LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, this);
        super.onStop();


    }

    public void startLocationServices() {
        Log.e( "startLocationServices: ", "true");
        LocationRequest request = LocationRequest.create().setPriority(LocationRequest.PRIORITY_LOW_POWER);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, request, this);

    }


}

