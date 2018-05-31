package com.example.administrator.demo.fragments;


import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
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
import android.widget.Toast;

import com.example.administrator.demo.models.Camera;
import com.example.administrator.demo.models.DirectionFinder;
import com.example.administrator.demo.models.DirectionFinderListener;
import com.example.administrator.demo.R;
import com.example.administrator.demo.models.DownLoadImageTask;
import com.example.administrator.demo.models.MyLatlng;
import com.example.administrator.demo.models.Point;
import com.example.administrator.demo.models.Route;

import com.google.android.gms.location.LocationListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.GoogleMap.OnMyLocationClickListener;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MapFragment extends Fragment implements
        OnMyLocationClickListener,
        OnMapReadyCallback,DirectionFinderListener{

    CountDownTimer cdt;
    private List<Marker> originMarkers = new ArrayList<>();
    private List<Marker> destinationMarkers = new ArrayList<>();
    private List<Polyline> polylinePaths = new ArrayList<>();
    private List<Point> trafficjams = new ArrayList<>();
    private ProgressDialog progressDialog;
    boolean origin=false,destination=false;
    String latlngOrigin,latlngDestination;
    private GoogleMap mMap;
    ImageButton imgbtnChangeMapType,imgbtnDirect,imgbtnSearch,imgbtnSendTrafficJam;
    Button btnHideDirection,btnStart;
    EditText txtSearch,txtOrigin,txtDestination;
    ImageView imgv;
    LatLng myLocation = new LatLng(10.851038, 106.771995);

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

    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        imgv = getActivity().findViewById(R.id.imageView);


        imgbtnChangeMapType = getActivity().findViewById(R.id.imgbtnChangeMapType);
        imgbtnDirect= getActivity().findViewById(R.id.imgbtnDirect);
        btnStart = getActivity().findViewById(R.id.btnStart);
        btnHideDirection = getActivity().findViewById(R.id.btnHideDirection);
        txtSearch = getActivity().findViewById(R.id.txtSearch);
        txtOrigin = getActivity().findViewById(R.id.txtorigin);
        txtDestination = getActivity().findViewById(R.id.txtdestination);
        imgbtnSearch = getActivity().findViewById(R.id.imgbtnSearch);
        imgbtnSendTrafficJam = getActivity().findViewById(R.id.imgbtnSendTrafficJam);

        String android_id = Settings.Secure.getString(getContext().getContentResolver(),
                Settings.Secure.ANDROID_ID);
        Toast.makeText(getActivity(), android_id, Toast.LENGTH_LONG).show();
        txtOrigin.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {

                   origin=b;
            }
        });
        txtDestination.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {

                    destination=b;
            }
        });
        imgbtnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mMap.clear();
                LatLng latLng = getLocationFromAddress(getContext(),txtSearch.getText().toString());
                if(latLng==null) {
                    Toast.makeText(getActivity(), "Không tìm thấy", Toast.LENGTH_SHORT).show();
                    return;
                }
                Toast.makeText(getActivity(),latLng.toString() ,Toast.LENGTH_SHORT).show();
               mMap.addMarker(new MarkerOptions().position(latLng).title(getAddress(latLng)));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
            }
        });
        //
        imgbtnChangeMapType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (imgbtnChangeMapType.getTag().equals(R.string.MODE_NORMAL)) {
                    Toast.makeText(getActivity(), "my style", Toast.LENGTH_SHORT).show();
                    imgbtnChangeMapType.setTag(R.string.MODE_MY_STYLE);
                    imgbtnChangeMapType.setImageResource(R.drawable.ic_satellite_station);
                    mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                    try {
                        boolean success = mMap.setMapStyle(
                                MapStyleOptions.loadRawResourceStyle(
                                        getActivity(), R.raw.mystyle_json));

                        if (!success) {
                            Log.e("", "Style parsing failed.");
                        }
                    } catch (Resources.NotFoundException e) {
                        Log.e("", "Can't find style. Error: ", e);
                    }
                } else if (imgbtnChangeMapType.getTag().equals(R.string.MODE_MY_STYLE)) {
                    Toast.makeText(getActivity(), "satellite", Toast.LENGTH_SHORT).show();
                    imgbtnChangeMapType.setTag(R.string.MODE_SATELLITE);
                    imgbtnChangeMapType.setImageResource(R.drawable.ic_satellite_station);
                    mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);

                } else if (imgbtnChangeMapType.getTag().equals(R.string.MODE_SATELLITE)) {
                    Toast.makeText(getActivity(), "hybrid", Toast.LENGTH_SHORT).show();
                    imgbtnChangeMapType.setTag(R.string.MODE_HYBRID);
                    imgbtnChangeMapType.setImageResource(R.drawable.ic_map);
                    mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);

                } else if (imgbtnChangeMapType.getTag().equals(R.string.MODE_HYBRID)) {
                    Toast.makeText(getActivity(), "terrain", Toast.LENGTH_SHORT).show();
                    imgbtnChangeMapType.setTag(R.string.MODE_TERRAIN);
                    imgbtnChangeMapType.setImageResource(R.drawable.ic_map);
                    mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);

                } else if (imgbtnChangeMapType.getTag().equals(R.string.MODE_TERRAIN)) {
                    Toast.makeText(getActivity(), "none", Toast.LENGTH_SHORT).show();
                    imgbtnChangeMapType.setTag(R.string.MODE_NONE);
                    imgbtnChangeMapType.setImageResource(R.drawable.ic_map);
                    mMap.setMapType(GoogleMap.MAP_TYPE_NONE);

                } else if (imgbtnChangeMapType.getTag().equals(R.string.MODE_NONE)) {
                    Toast.makeText(getActivity(), "normal", Toast.LENGTH_SHORT).show();
                    imgbtnChangeMapType.setTag(R.string.MODE_NORMAL);
                    imgbtnChangeMapType.setImageResource(R.drawable.ic_map);
                    mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                    mMap.setMapStyle(null);

                }
            }
        });
        imgbtnDirect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LinearLayout layoutDirection = getActivity().findViewById(R.id.layoutDirection);
                LinearLayout layoutSearchBar = getActivity().findViewById(R.id.layoutSearchBar);
                layoutDirection.setVisibility(View.VISIBLE);
                layoutSearchBar.setVisibility(View.GONE);
                origin=true;
                destination=true;
            }
        });
        btnHideDirection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LinearLayout layoutDirection = getActivity().findViewById(R.id.layoutDirection);
                LinearLayout layoutSearchBar = getActivity().findViewById(R.id.layoutSearchBar);
                layoutDirection.setVisibility(View.GONE);
                layoutSearchBar.setVisibility(View.VISIBLE);
            }
        });
        // tìm đường
        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String origin,destination;
                if(latlngOrigin == null)
                    origin  = txtOrigin.getText().toString();
                else
                    origin=latlngOrigin;
                if(latlngDestination ==null)
                    destination=txtDestination.getText().toString();
                else
                    destination = latlngDestination;

                sendRequest(origin,destination);
                mMap.clear();
                btnHideDirection.callOnClick();
                txtOrigin.setText("");
                txtDestination.setText("");
                latlngOrigin=null;
                latlngDestination=null;
            }
        });

        imgbtnSendTrafficJam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendTrafficJam(myLocation);
            }
        });

    }//end onViewCreated

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMaxZoomPreference(25);
        mMap.setMinZoomPreference(5);
        LatLng hcmute = new LatLng(10.850986, 106.772017);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                new LatLng(10.850986, 106.772017), 18));
      //  mMap.addMarker(new MarkerOptions().position(hcmute).title("Marker in HCMUTE"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(hcmute));
        imgbtnChangeMapType.setTag(R.string.MODE_NORMAL);
        enableMyLocation();
        showAllTrafficJams();
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {

                if (origin) {
                    mMap.clear();
                    mMap.addMarker(new MarkerOptions().position(latLng).title(getAddress(latLng)));
                    txtOrigin.setText(getAddress(latLng));
                    latlngOrigin = latLng.latitude + "," + latLng.longitude;
                    Toast.makeText(getActivity(), latlngOrigin, Toast.LENGTH_SHORT).show();
                    origin = false;
                } else if (destination) {
                    mMap.addMarker(new MarkerOptions().position(latLng).title(getAddress(latLng)));
                    txtDestination.setText(getAddress(latLng));
                    destination = false;
                    latlngDestination = latLng.latitude + "," + latLng.longitude;
                    Toast.makeText(getActivity(), latlngDestination, Toast.LENGTH_SHORT).show();

                } else {

   //                     mMap.clear();
                    mMap.addMarker(new MarkerOptions().position(latLng).title(getAddress(latLng)));
                    for(Point p:trafficjams)
                    {
                        if(Math.sqrt( Math.pow((latLng.latitude-p.location.latitude),2)+Math.pow((latLng.longitude-p.location.longitude),2))*110952 <= p.level*15)
                        {
                            Toast.makeText(getActivity(), "kẹt xe: "+p.id, Toast.LENGTH_SHORT).show();

                        }
                    }
//                    txtSearch.setText(getAddress(latLng));
                }


            }
        });
        setMarkerClick();
        //final LocationManager manager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        LocationListener locationListenerFunc = new LocationListener() {

            @Override
            public void onLocationChanged(Location location) {
                Toast.makeText(getActivity(),"asdasdasdasdasd", Toast.LENGTH_LONG).show();
            }
        };
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("points");
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                showAllTrafficJams();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        showAllCameras();
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

            for(Address addr : address)
            {
                Address location = addr;
                p1 = new LatLng(location.getLatitude(), location.getLongitude() );
                return p1;
            }
        } catch (IOException ex) {

            ex.printStackTrace();
        }

        return null;
    }

    public String getAddress(LatLng latLng)
    {
        Geocoder geocoder;

        List<Address> addresses ;
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
            String result= address+", "+city+", "+state+", "+country;
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

        }
            else
           {
                // Access to the location has been granted to the app.
                Toast.makeText(getActivity(), "enable location", Toast.LENGTH_SHORT).show();
                mMap.setMyLocationEnabled(true);
            }

    }

    @Override
    public void onMyLocationClick(@NonNull Location location) {
        //Toast.makeText(getActivity(), "Current location:\n" + location, Toast.LENGTH_LONG).show();
       txtSearch.setText( getAddress(new LatLng(location.getLatitude(),location.getLongitude())));
    }

    private void sendRequest(String origin, String destination) {
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

        if (originMarkers != null) {
            for (Marker marker : originMarkers) {
                marker.remove();
            }
        }

        if (destinationMarkers != null) {
            for (Marker marker : destinationMarkers) {
                marker.remove();
            }
        }

        if (polylinePaths != null) {
            for (Polyline polyline:polylinePaths ) {
                polyline.remove();
            }
        }
    }

    @Override
    public void onDirectionFinderSuccess(List<Route> routes) {
        progressDialog.dismiss();
        polylinePaths = new ArrayList<>();
        originMarkers = new ArrayList<>();
        destinationMarkers = new ArrayList<>();

        for (Route route : routes) {
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(route.startLocation, 16));

            originMarkers.add(mMap.addMarker(new MarkerOptions()
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_origin))
                    .title(route.startAddress)
                    .position(route.startLocation)));
            destinationMarkers.add(mMap.addMarker(new MarkerOptions()
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_destination))
                    .title(route.endAddress)
                    .position(route.endLocation)));

            PolylineOptions polylineOptions = new PolylineOptions().
                    geodesic(true).
                    color(Color.parseColor("#74ccfc")).
                    width(15);
            for (int i = 0; i < route.points.size(); i++)
                polylineOptions.add(route.points.get(i));

            polylinePaths.add(mMap.addPolyline(polylineOptions));
        }
    }

    public void sendTrafficJam(final LatLng location)
    {

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("points");
        String id = myRef.push().getKey();
        //myRef.child(id).setValue(new User(id, "hiep", "hùng hiệp", "123456", "male"));
        myRef.child(id).setValue(new Point(id,new MyLatlng(myLocation),2));

    }

    public void showAllTrafficJams()
    {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("points");
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<CircleOptions> circleOptions= new ArrayList<>();
                trafficjams.clear();
                for (DataSnapshot Snapshot1 : dataSnapshot.getChildren()) {

                //    Toast.makeText(getActivity(), Snapshot1.getValue().toString(), Toast.LENGTH_SHORT).show();
                    Point p = Snapshot1.getValue(Point.class);
                    //u.id = Snapshot1.getKey();
                    trafficjams.add(p);
                    CircleOptions circleOption = new CircleOptions()
                            .center(p.location.getLatlng())
                            .radius(p.level*15)
                            .strokeColor(Color.WHITE)
                            .strokeWidth(1)
                            .fillColor(Color.argb(60,255, 0, 0));
                    circleOptions.add(circleOption);

                }
                for( CircleOptions circleOption : circleOptions)
                {
                    mMap.addCircle(circleOption);
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("errrrorrrrrrrrrrrrrrr",databaseError.toString() );
            }


        });
    }
    public void showAllCameras()
    {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("cameras");
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<Camera> cameras= new ArrayList<>();
                trafficjams.clear();
                for (DataSnapshot Snapshot1 : dataSnapshot.getChildren()) {
                    Camera c = Snapshot1.getValue(Camera.class);
                    mMap.addMarker(new MarkerOptions()
                                .title(c.name)
                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_camera) )
                                .position(c.location.getLatlng()))
                            .setTag(c.id);
                }

            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("errrrorrrrrrrrrrrrrrr",databaseError.toString() );
            }


        });
    }
    void setMarkerClick()
    {

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(final Marker marker) {
                final Marker m = marker;
                if(cdt!=null)
                    cdt.cancel();
                 cdt= new CountDownTimer(900000000, 15000) {
                    public void onTick(long millisUntilFinished) {
                        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
                        final String imgURL = "http://giaothong.hochiminhcity.gov.vn/render/ImageHandler.ashx?id="+m.getTag()+"&t="+timestamp.getTime()+".png";
                        new DownLoadImageTask(imgv).execute(imgURL);

                        Toast.makeText(getContext(), timestamp.getTime()+"", Toast.LENGTH_SHORT).show();
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

}