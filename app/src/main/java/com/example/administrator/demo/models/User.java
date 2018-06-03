package com.example.administrator.demo.models;

import com.google.firebase.database.IgnoreExtraProperties;

/**
 * Created by Administrator on 05/05/2018.
 */
@IgnoreExtraProperties
public class User {
    public String id;
    public double rate;
    public Boolean isActive;
    public User()
    {

    }
    public User(String id, double rate, Boolean isActive)
    {
        this.id=id;
        this.rate = rate;
        this.isActive= isActive;
    }

}
