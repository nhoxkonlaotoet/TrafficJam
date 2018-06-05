package com.example.administrator.demo.models;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

/**
 * Created by Administrator on 05/05/2018.
 */
@IgnoreExtraProperties
public class MyLatlng {
    public double latitude;
    public double longitude;
    public MyLatlng()
    {

    }

    public MyLatlng(LatLng latlng)
    {
        this.latitude=latlng.latitude;
        this.longitude = latlng.longitude;

    }
    @Exclude
    public LatLng getLatlng()
    {
        return new LatLng(latitude,longitude);
    }
}
