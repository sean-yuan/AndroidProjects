package com.seanyuan.virtualhumidor3.models;

import com.google.firebase.database.IgnoreExtraProperties;

// [START blog_user_class]
@IgnoreExtraProperties
public class User {

    public String username;
    public String email;
    public String pictureID;

    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public User(String username, String email, String pictureID) {
        this.username = username;
        this.email = email;
        this.pictureID = pictureID;
    }

}
// [END blog_user_class]
