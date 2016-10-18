package com.seanyuan.virtualhumidor;

import java.util.List;

/**
 * Created by seanyuan on 9/30/16.
 */

public class User {

    public String userName;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public List<String> getHumidor() {
        return humidor;
    }

    public void setHumidor(List<String> humidor) {
        this.humidor = humidor;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String email;
    public List<String> humidor;

    public User() {

    }

    public User(String userName, String email, List<String> humidor) {
        this.userName = userName;
        this.email = email;
        this.humidor = humidor;
    }

}
