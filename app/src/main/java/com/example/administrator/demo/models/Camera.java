package com.example.administrator.demo.models;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class Camera {
    public String id;
    public MyLatlng location;
    public String name;

    public Camera()
    {

    }
    public Camera(String id,  MyLatlng location, String name)
    {
        this.id=id;
        this.location= location;
        this.name=name;
    }

}
