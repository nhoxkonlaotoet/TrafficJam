package com.example.administrator.demo.models;

import com.google.firebase.database.IgnoreExtraProperties;

/**
 * Created by Administrator on 05/05/2018.
 */
@IgnoreExtraProperties
public class User {
    public String id;
    public String deviceId;
    public String fullName;
    public String gender;
    public User()
    {

    }
    public User(String id,  String deviceId, String fullName, String gender)
    {
        this.id=id;
        this.deviceId= deviceId;
        this.fullName=fullName;

        this.gender=gender;
    }

}
