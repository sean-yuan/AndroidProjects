package com.seanyuan.virtualhumidor;

import android.graphics.Bitmap;

/**
 * Created by seanyuan on 10/7/16.
 */

public class FeedItem {
    private String title;
    private String length;
    private String gauge;
    private String location;
    private String notes;
    private Bitmap thumbnail;
    private String type;
    private String ratingValue;
    private String price;
    private String quantity;
    private String ownerID;

    public String getCigarID() {
        return cigarID;
    }

    public void setCigarID(String cigarID) {
        this.cigarID = cigarID;
    }

    public String getOwnerID() {
        return ownerID;
    }

    public void setOwnerID(String ownerID) {
        this.ownerID = ownerID;
    }

    private String cigarID;

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getLength() {
        return length;
    }

    public void setLength(String length) {
        this.length = length;
    }

    public String getGauge() {
        return gauge;
    }

    public void setGauge(String gauge) {
        this.gauge = gauge;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getRatingValue() {
        return ratingValue;
    }

    public void setRatingValue(String ratingValue) {
        this.ratingValue = ratingValue;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Bitmap getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(Bitmap thumbnail) {
        this.thumbnail = thumbnail;
    }
}
