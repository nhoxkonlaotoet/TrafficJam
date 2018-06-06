package com.example.administrator.demo.fragments;


import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Point;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.administrator.demo.IFragmentManager;
import com.example.administrator.demo.activities.MapsActivity;
import com.example.administrator.demo.models.Announce;
import com.example.administrator.demo.models.Camera;
import com.example.administrator.demo.models.DirectionFinder;
import com.example.administrator.demo.models.DirectionFinderListener;
import com.example.administrator.demo.R;
import com.example.administrator.demo.models.DownLoadImageTask;
import com.example.administrator.demo.models.Route;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.location.places.ui.SupportPlaceAutocompleteFragment;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static android.content.ContentValues.TAG;

public class MapFragment extends Fragment implements
        OnMapReadyCallback, DirectionFinderListener, IFragmentManager {


    CountDownTimer cdt;
    MarkerOptions originMarker;
    MarkerOptions destinationMarker;
    List<PolylineOptions> polylinePaths = new ArrayList<>();

    List<Camera> cameras = new ArrayList<>();
    List<Announce> trafficjams = new ArrayList<>();

    ProgressDialog progressDialog;
    boolean origin = false, destination = false;
    String latlngOrigin, latlngDestination;
    private GoogleMap mMap;
    ImageButton imgbtnDirection, imgbtnSearch;
    Button btnCloseCamera,btnCancelDirection; // ,btnHideDirection, btnStart;
  //  EditText txtSearch, txtOrigin, txtDestination;
    ImageView imgvCamera;
    RelativeLayout layoutCamera;
    Location myLocation;
    PlaceAutocompleteFragment autocompleteOriginFragment, autocompleteDestinationFragment;
    Marker searchMarker;
    LinearLayout layoutDirection;

    public MapFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_map, container, false);
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        return view;
    }

    void mapping() {
        imgvCamera = getActivity().findViewById(R.id.imgvCamera);
        imgbtnDirection = getActivity().findViewById(R.id.imgbtnDirect);
      //  btnStart = getActivity().findViewById(R.id.btnStart);
      //  btnHideDirection = getActivity().findViewById(R.id.btnHideDirection);
      //  txtSearch = getActivity().findViewById(R.id.txtSearch);
    //    txtOrigin = getActivity().findViewById(R.id.txtorigin);
     //   txtDestination = getActivity().findViewById(R.id.txtdestination);
    //    imgbtnSearch = getActivity().findViewById(R.id.imgbtnSearch);
        btnCancelDirection=getActivity().findViewById(R.id.btnCancleDirection);
        layoutDirection = getActivity().findViewById(R.id.layoutDirection);
        layoutCamera = getActivity().findViewById(R.id.layoutCamera);
        btnCloseCamera = getActivity().findViewById(R.id.btnCloseCamera);
        autocompleteOriginFragment =
                (PlaceAutocompleteFragment) getActivity().getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment_origin);

        autocompleteDestinationFragment =
                (PlaceAutocompleteFragment) getActivity().getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment_destination);
    }

    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        mapping();
        layoutCamera.setVisibility(View.GONE);
        layoutDirection.setVisibility(View.GONE);
        autocompleteOriginFragment.setHint("Tìm kiếm");
        setPlaceSelectedOrigin();
        autocompleteDestinationFragment.setHint("Chọn điểm đến");
        setPlaceSelectedDestination();

//        txtOrigin.setOnFocusChangeListener(new View.OnFocusChangeListener() {
//            @Override
//            public void onFocusChange(View view, boolean b) {
//
//                origin = b;
//            }
//        });
//        txtDestination.setOnFocusChangeListener(new View.OnFocusChangeListener() {
//            @Override
//            public void onFocusChange(View view, boolean b) {
//
//                destination = b;
//            }
//        });


//        imgbtnSearch.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                LatLng latLng = getLocationFromAddress(getContext(), txtSearch.getText().toString());
//                if (latLng == null) {
//                    Toast.makeText(getActivity(), "Không tìm thấy", Toast.LENGTH_SHORT).show();
//                    return;
//                }
//                Toast.makeText(getActivity(), latLng.toString(), Toast.LENGTH_SHORT).show();
//                mMap.addMarker(new MarkerOptions().position(latLng).title(getAddress(latLng)));
//                mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
//            }
//        });


     //   setButtonStartClick();
        setButtonDirectionClick();
        //setButtonHideDirectionClick();
        setButtonCancelDirection();
        setButtonCloseCameraClick();

    }//end onViewCreated
    void setButtonCancelDirection()
    {
        btnCancelDirection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                origin=false;
                destination=false;
                originMarker=null;
                destinationMarker=null;
                layoutDirection.setVisibility(View.GONE);
                autocompleteOriginFragment.setHint("Tìm kiếm");
            }
        });
    }
    void setPlaceSelectedOrigin() {
        autocompleteOriginFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                // TODO: Get info about the selected place.
                if (origin) {
                    Log.i(TAG, "Place: " + place.getName());
                    originMarker = new MarkerOptions()
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_dot_inside_a_circle))
                            .position(place.getLatLng());
                    mMap.addMarker(originMarker);
                    origin = false;
                    Log.e("origin_: ", "origin: " + origin + " destination: " + destination);
                } else {
                    if(searchMarker!=null)
                        searchMarker.remove();
                    searchMarker = mMap.addMarker(new MarkerOptions().position(place.getLatLng()));
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(place.getLatLng(), 18));
                }
            }

            @Override
            public void onError(Status status) {
                // TODO: Handle the error.
                Log.i(TAG, "An error occurred: " + status);
            }
        });
    }

    void setPlaceSelectedDestination() {
        autocompleteDestinationFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                // TODO: Get info about the selected place.
                Log.i(TAG, "Place: " + place.getName());
                destinationMarker = new MarkerOptions()
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_dot))
                        .position(place.getLatLng());
                destination = false;
                if (!origin) {

                    sendDirectionRequest(originMarker.getPosition().latitude + "," + originMarker.getPosition().longitude,
                            destinationMarker.getPosition().latitude + "," + destinationMarker.getPosition().longitude);
                }
            }

            @Override
            public void onError(Status status) {
                // TODO: Handle the error.
                Log.i(TAG, "An error occurred: " + status);
            }
        });
    }

//    void setButtonStartClick() {
//        // tìm đường
//        btnStart.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//                String origin, destination;
//                if (latlngOrigin == null)
//                    origin = txtOrigin.getText().toString();
//                else
//                    origin = latlngOrigin;
//                if (latlngDestination == null)
//                    destination = txtDestination.getText().toString();
//                else
//                    destination = latlngDestination;
//
//                sendDirectionRequest(origin, destination);
//                btnHideDirection.callOnClick();
//                txtOrigin.setText("");
//                txtDestination.setText("");
//                latlngOrigin = null;
//                latlngDestination = null;
//            }
//        });
//    }

    void setButtonDirectionClick() {
        imgbtnDirection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //  Log.e("start direction: ","true" );
//                LinearLayout layoutDirection = getActivity().findViewById(R.id.layoutDirection);
//                LinearLayout layoutSearchBar = getActivity().findViewById(R.id.layoutSearchBar);
//                layoutDirection.setVisibility(View.VISIBLE);
//                layoutSearchBar.setVisibility(View.GONE);
                originMarker=null;
                destinationMarker=null;
                polylinePaths.clear();
                autocompleteOriginFragment.setText("");
                autocompleteOriginFragment.setHint("Chọn điểm xuất phát");
                autocompleteDestinationFragment.setText("");
                origin = true;
                destination = true;
                layoutDirection.setVisibility(View.VISIBLE);

            }
        });
    }

//    void setButtonHideDirectionClick() {
//        btnHideDirection.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                LinearLayout layoutDirection = getActivity().findViewById(R.id.layoutDirection);
//                LinearLayout layoutSearchBar = getActivity().findViewById(R.id.layoutSearchBar);
//                layoutDirection.setVisibility(View.GONE);
//                layoutSearchBar.setVisibility(View.VISIBLE);
//            }
//        });
//    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMaxZoomPreference(25);
        mMap.setMinZoomPreference(5);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                new LatLng(10.850986, 106.772017), 18));

        enableMyLocation();

        setTrafficJamsListener();
        setPolylineClick();
        setMapClick();
        setMarkerClick();
        if (getActivity().getIntent().getExtras().getString("action").equals("viewcamera"))
            showAllCameras();
    }

    void setPolylineClick() {
        mMap.setOnPolylineClickListener(new GoogleMap.OnPolylineClickListener() {
            @Override
            public void onPolylineClick(Polyline polyline) {
                if (polyline.getColor() == getResources().getColor(R.color.selected_way))
                    return;

                boolean ignore;
                PolylineOptions moveToLast = new PolylineOptions();
                int index = -1;
                for (PolylineOptions polylineOptions : polylinePaths) {
                    if (polylineOptions.getColor() != getResources().getColor(R.color.red_way))
                        polylineOptions.color(getResources().getColor(R.color.normal_way));

                    if (polylineOptions.getPoints().size() == polyline.getPoints().size()) {
                        ignore = false;
                        int n = polyline.getPoints().size();

                        for (int i = 1; i < n - 1; i++) {
                            if (!polyline.getPoints().get(i).equals(polylineOptions.getPoints().get(i))) {
                                ignore = true;
                                break;
                            }
                        }
                        if (!ignore) {
                            Log.e("selected ", "true");
                            if (polylineOptions.getColor() != getResources().getColor(R.color.red_way))
                                polylineOptions.color(getResources().getColor(R.color.selected_way));
                            moveToLast = polylineOptions;
                            index = polylinePaths.indexOf(polylineOptions);
                        }
                    }
                }
                if (moveToLast.getPoints().size() != 0 && index != -1) {
                    polylinePaths.remove(index);
                    polylinePaths.add(moveToLast);
                }
                mapRefresh();
            }
        });
    }

    // tìm kiếm
    public LatLng getLocationFromAddress(Context context, String strAddress) {

        Geocoder coder = new Geocoder(context);
        List<Address> address;
        LatLng p1 = null;

        try {
            // May throw an IOException
            address = coder.getFromLocationName(strAddress, 5);
            if (address == null) {
                return null;
            }

            for (Address addr : address) {
                Address location = addr;
                p1 = new LatLng(location.getLatitude(), location.getLongitude());
                return p1;
            }
        } catch (IOException ex) {

            ex.printStackTrace();
        }
        return null;
    }

    public String getAddress(LatLng latLng) {
        Geocoder geocoder;

        List<Address> addresses;
        geocoder = new Geocoder(getActivity(), Locale.getDefault());

        try {
            addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
            String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
            String city = addresses.get(0).getLocality();
            String state = addresses.get(0).getAdminArea();
            String country = addresses.get(0).getCountryName();
            String postalCode = addresses.get(0).getPostalCode();
            String knownName = addresses.get(0).getFeatureName(); // số nhà
            //    Toast.makeText(getActivity(), address+", "+city+", "+state+", "+country+", "+postalCode+", "+knownName, Toast.LENGTH_SHORT).show();
            String result = address + ", " + city + ", " + state + ", " + country;
            return result;

        } catch (IOException e) {
            e.printStackTrace();

        }
        return null;
    }

    // bật vị trí của tôi
    public void enableMyLocation() {

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            }
            if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
            }
            if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.INTERNET)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.INTERNET}, 1);
            }

            // Access to the location has been granted to the app.
            Toast.makeText(getActivity(), "enable location", Toast.LENGTH_SHORT).show();
            mMap.setMyLocationEnabled(true);

        } else {
            // Access to the location has been granted to the app.
            Toast.makeText(getActivity(), "enable location", Toast.LENGTH_SHORT).show();
            mMap.setMyLocationEnabled(true);
        }

    }

    private void sendDirectionRequest(String origin, String destination) {
        if (origin.isEmpty()) {
            Toast.makeText(getActivity(), "Vui lòng chọn điểm xuất phát", Toast.LENGTH_SHORT).show();
            return;
        }
        if (destination.isEmpty()) {
            Toast.makeText(getActivity(), "Vui lòng chọn điểm đến", Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            new DirectionFinder(this, origin, destination).execute();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDirectionFinderStart() {
        progressDialog = ProgressDialog.show(getActivity(), "",
                "Chờ xíu", true);

//        if (originMarker != null) {
//            originMarker = null;
//        }
//        if (destinationMarker != null) {
//            destinationMarker = null;
//        }
        if (polylinePaths != null) {
            polylinePaths.clear();
        }
        mapRefresh();
    }

    @Override
    public void onDirectionFinderSuccess(List<Route> routes) {
        progressDialog.dismiss();
//        polylinePaths.clear();
//        originMarker = null;
//        destinationMarker = null;
        boolean ignore;

        for (Route route : routes) {
            ignore = false;
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(route.startLocation, 16));

//            originMarker = new MarkerOptions()
//                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_origin))
//                    .title(route.startAddress)
//                    .position(route.startLocation);
//            destinationMarker = new MarkerOptions()
//                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_destination))
//                    .title(route.endAddress)
//                    .position(route.endLocation);

            PolylineOptions polylineOptions = new PolylineOptions().
                    geodesic(true).
                    color(getResources().getColor(R.color.normal_way)).
                    width(10);

            for (int i = 0; i < route.points.size(); i++) {
                if (!ignore) {
                    if (isInTrafficJam(route.points.get(i)) ||
                            (i > 0 && isInTrafficJam(new LatLng((route.points.get(i - 1).latitude + route.points.get(i).latitude) / 2,
                                    (route.points.get(i - 1).longitude + route.points.get(i).longitude) / 2)))) {
                        polylineOptions.color(getResources().getColor(R.color.red_way));
                        Log.e("Direction ", route + "");
                        ignore = true;
                    }
                }
                polylineOptions.add(route.points.get(i));
            }

            polylinePaths.add(polylineOptions);
            mapRefresh();
        }

    }

    public void setTrafficJamsListener() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference myRef = database.getReference("announces");
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
//                if (trafficjams.size()==0)
//                    return;
                trafficjams.clear();
                for (DataSnapshot Snapshot1 : dataSnapshot.getChildren()) {
                    Announce p = Snapshot1.getValue(Announce.class);
                    trafficjams.add(p);
                }
                if (myLocation != null)
                    if (isInTrafficJam(new LatLng(myLocation.getLatitude(), myLocation.getLongitude())))
                        Toast.makeText(getActivity(), "Bạn đang bị kẹt xe", Toast.LENGTH_SHORT).show();
                mapRefresh();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void showAllTrafficJams() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("announces");
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                trafficjams.clear();
                for (DataSnapshot Snapshot1 : dataSnapshot.getChildren()) {
                    Announce p = Snapshot1.getValue(Announce.class);
                    trafficjams.add(p);
                }
                mapRefresh();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("errrrorrrrrrrrrrrrrrr", databaseError.toString());
            }


        });

    }

    void mapRefresh() {
        mMap.clear();
        for (Camera c : cameras) {
            mMap.addMarker(new MarkerOptions()
                    .title(c.name)
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_camera))
                    .position(c.location.getLatlng()))
                    .setTag(c.id);
        }
        for (Announce a : trafficjams) {
            CircleOptions circleOption = new CircleOptions()
                    .center(a.location.getLatlng())
                    .radius(a.level * 15)
                    .strokeColor(Color.WHITE)
                    .strokeWidth(1)
                    .fillColor(Color.argb(60, 255, 0, 0));
            mMap.addCircle(circleOption);
        }

        for (PolylineOptions polylineOptions : polylinePaths) {

            Polyline line = mMap.addPolyline(polylineOptions);
            line.setClickable(true);
        }
        if (originMarker != null) {
            mMap.addMarker(originMarker);
            if (destinationMarker != null)
                mMap.addMarker(destinationMarker);
        }

    }

    public void showAllCameras() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("cameras");
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                cameras.clear();
                Log.e("camera count", dataSnapshot.getChildrenCount() + "");
                for (DataSnapshot Snapshot1 : dataSnapshot.getChildren()) {
                    Camera c = Snapshot1.getValue(Camera.class);
                    cameras.add(c);
                }
                mapRefresh();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("errrrorrrrrrrrrrrrrrr", databaseError.toString());
            }


        });
    }

    void setMapClick() {
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                Log.e("onMapClick: ", "origin: " + origin + " destination: " + destination);
                if (origin) {
                    originMarker = new MarkerOptions()
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_dot_inside_a_circle))
                            .position(latLng);
                    mMap.addMarker(originMarker);
                  //  txtOrigin.setText(getAddress(latLng));
                    latlngOrigin = latLng.latitude + "," + latLng.longitude;
                    Toast.makeText(getActivity(), latlngOrigin, Toast.LENGTH_SHORT).show();
                    origin = false;
                    autocompleteOriginFragment.setText(getAddress(latLng));

                } else if (destination) {
                    destinationMarker = new MarkerOptions()
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_dot))
                            .position(latLng);
                    mMap.addMarker(destinationMarker);
                  //  txtDestination.setText(getAddress(latLng));
                    destination = false;
                    latlngDestination = latLng.latitude + "," + latLng.longitude;
                    Toast.makeText(getActivity(), latlngDestination, Toast.LENGTH_SHORT).show();
                    autocompleteDestinationFragment.setText(getAddress(latLng));
                    sendDirectionRequest(originMarker.getPosition().latitude + "," + originMarker.getPosition().longitude,
                            destinationMarker.getPosition().latitude + "," + destinationMarker.getPosition().longitude);
                }

//                Log.e("so duong ", polylinePaths.size()+"");
//                for(Polyline polyline : polylinePaths)
//                {
//                    Log.e("so diem",polyline.getPoints().size()+"" );
//                    for(int i=1;i<polyline.getPoints().size();i++)
//                    {
//                        if(isLine(polyline.getPoints().get(i-1),polyline.getPoints().get(i),latLng,15))
//                            Log.e( "aaaaaaaaaaa", polyline.getPoints().get(i).toString());
//                    }
//                }
//                try {//stop load camera image
//                    cdt.cancel();
//                } catch (Exception e) {
//                }
            }
        });
    }

    boolean isLine(LatLng p1, LatLng p2, LatLng e, float width) {
        double a = ((double) (p1.latitude - p2.latitude)) / ((double) (p1.longitude - p2.longitude));
        double b = p1.latitude - a * p1.longitude;
        Log.e("onMapClick", a + " " + b);
        if (p1.longitude < p2.longitude) {
            if (e.longitude < p1.longitude || e.longitude > p2.longitude)
                return false;
        } else if (e.longitude < p2.longitude || e.longitude > p1.longitude)
            return false;
        if (((double) (a * e.longitude + b - e.latitude) * 3600 * 30.82) < width && ((double) (a * e.longitude + b - e.latitude) * 3600 * 30.82) > -width) {
            Log.e("isLine: ", "a=" + a + "x=" + e.longitude + "b=" + b + "y=" + e.latitude + " ====" + ((a * e.longitude + b - e.latitude) * 3600 * 30.82) + "");
            return true;
        }
        return false;
    }

    void setButtonCloseCameraClick() {
        btnCloseCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                layoutCamera.setVisibility(View.INVISIBLE);
                if (cdt != null)
                    cdt.cancel();
            }
        });
    }

    void setMarkerClick() {
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(final Marker marker) {
                final Marker m = marker;
                if (cdt != null)
                    cdt.cancel();
                layoutCamera.setVisibility(View.VISIBLE);
                imgvCamera.setImageBitmap(null);
                imgvCamera.setBackground(getResources().getDrawable(R.drawable.loading));
                cdt = new CountDownTimer(900000000, 15000) {
                    public void onTick(long millisUntilFinished) {
                        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
                        final String imgURL = "http://giaothong.hochiminhcity.gov.vn/render/ImageHandler.ashx?id=" + m.getTag() + "&t=" + timestamp.getTime() + ".png";
                        new DownLoadImageTask(imgvCamera).execute(imgURL);

                        Toast.makeText(getContext(), timestamp.getTime() + "", Toast.LENGTH_SHORT).show();
                    }

                    public void onFinish() {
                        cdt.start();
                    }

                };

                cdt.start();

                return false;
            }
        });
    }

    @Override
    public void onStop() {
        super.onStop();
        try {
            cdt.cancel();
        } catch (Exception e) {
        }

    }

    public boolean isInTrafficJam(LatLng latlng) {


        if (trafficjams.size() != 0)
            for (Announce a : trafficjams) {
                Double x = Math.sqrt(Math.pow(latlng.latitude - a.location.latitude, 2)
                        + Math.pow(latlng.longitude - a.location.longitude, 2));
                if (x * 30.82 * 3600 <= a.level * 15) {
                    return true;
                }
            }
        return false;
    }

    @Override
    public void onDataChanged(Object data) {
        try {
            myLocation = (Location) data;
        } catch (Exception e) {
        }
    }
}