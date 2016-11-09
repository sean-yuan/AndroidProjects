package com.seanyuan.cigarautocomplete;

/**
 * Created by seanyuan on 10/26/16.
 */
public class MyObject {

    public String objectName;

    // constructor for adding sample data
    public MyObject(String objectName){
        String result = objectName.replaceAll("\\s+$", "");
        this.objectName = result;
    }

}