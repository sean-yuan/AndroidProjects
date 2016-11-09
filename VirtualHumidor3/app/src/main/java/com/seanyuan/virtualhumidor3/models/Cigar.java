package com.seanyuan.virtualhumidor3.models;

import android.graphics.Bitmap;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.HashMap;
import java.util.Map;

// [START post_class]
@IgnoreExtraProperties
public class Cigar {

    public String uid;
    public String author;
    public String cigar_name;
    public String cigar_type;
    public String cigar_length;
    public String cigar_gauge;
    public String cigar_notes;
    public String cigar_location;
    public String cigar_price;
    public String cigar_amount;
    public String RatingValuer;
    public String cigarID = uid + cigar_name;
    public Bitmap cigarImage;
    public String imageCode;
    public String ownerPhoto;



    public int starCount = 0;
    public Map<String, Boolean> stars = new HashMap<>();

    public Cigar() {
        // Default constructor required for calls to DataSnapshot.getValue(Cigar.class)
    }

    public Cigar(String ownerPhoto, String uid, String cigar_name, String author, String cigar_type, String cigar_length, String cigar_gauge, String cigar_notes, String cigar_location, String cigar_price, String cigar_amount, String ratingValuer, String cigarID, Bitmap image, String imageCode) {
        this.ownerPhoto = ownerPhoto;
        this.uid = uid;
        this.cigar_name = cigar_name;
        this.author = author;
        this.cigar_type = cigar_type;
        this.cigar_length = cigar_length;
        this.cigar_gauge = cigar_gauge;
        this.cigar_notes = cigar_notes;
        this.cigar_location = cigar_location;
        this.cigar_price = cigar_price;
        this.cigar_amount = cigar_amount;
        RatingValuer = ratingValuer;
        this.cigarID = cigarID;
        this.cigarImage = image;
        this.imageCode = imageCode;
    }

    // [START post_to_map]
    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("uid" , uid);
        result.put("cigar_name" , cigar_name);
        result.put("author" , author);
        result.put("cigar_type" , cigar_type);
        result.put("cigar_length" , cigar_length);
        result.put("cigar_gauge" , cigar_gauge);
        result.put("cigar_notes" , cigar_notes);
        result.put("cigar_location" , cigar_location);
        result.put("cigar_price" , cigar_price);
        result.put("cigar_amount" , cigar_amount);
        result.put("RatingValuer" , RatingValuer);
        result.put("cigarID" , cigarID);
        result.put("starCount", starCount);
        result.put("stars", stars);
        result.put("imageCode", imageCode);
        result.put("ownerPhoto", ownerPhoto);

        return result;
    }
    // [END post_to_map]

}
// [END post_class]
