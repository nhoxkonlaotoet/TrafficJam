package com.example.administrator.demo.models;

import com.google.firebase.database.IgnoreExtraProperties;

/**
 * Created by Administrator on 05/05/2018.
 */
@IgnoreExtraProperties
public class User {
    public String id;
    public Boolean isActive;
    public User()
    {

    }
    public User(String id,  Boolean isActive)
    {
        this.id=id;
        this.isActive= isActive;
    }

}
