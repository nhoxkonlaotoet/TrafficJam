package com.example.administrator.demo.models;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 05/05/2018.
 */
@IgnoreExtraProperties
public class Announce {
    public String id;
    public MyLatlng location;
    public Integer level;
    public String deviceId;
    public Timestamp time;
    public List image;
    public Announce()
    {

    }
    public Announce(String id, MyLatlng location, Integer level, String deviceId, Timestamp time , List image )
    {
        this.id=id;
        this.location=location;
        this.level=level;
        this.deviceId=deviceId;
        this.time=time;
        this.image=image;
    }

}
