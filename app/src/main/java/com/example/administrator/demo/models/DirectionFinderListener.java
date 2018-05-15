package com.example.administrator.demo.models;

import java.util.List;

/**
 * Created by Administrator on 23/04/2018.
 */

public interface  DirectionFinderListener {
    void onDirectionFinderStart();
    void onDirectionFinderSuccess(List<Route> route);
}
