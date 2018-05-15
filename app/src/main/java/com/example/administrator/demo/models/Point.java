package com.example.administrator.demo.models;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 05/05/2018.
 */
@IgnoreExtraProperties
public class Point {
    public String id;
    public MyLatlng location;
    public Integer level;
    public Integer count;
    public Point()
    {

    }
    public Point(String id, MyLatlng location, Integer level)
    {
        this.id=id;
        this.location=location;
        this.level=level;
        count =1;
    }

}
