package com.example.administrator.demo.models;

import com.google.android.gms.maps.model.LatLng;
import java.util.List;
/**
 * Created by Administrator on 23/04/2018.
 */


public class Route {
    public Distance distance;
    public Duration duration;
    public String endAddress;
    public LatLng endLocation;
    public String startAddress;
    public LatLng startLocation;

    public List<LatLng> points;
}