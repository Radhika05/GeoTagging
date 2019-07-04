package com.radhika.geotagging.Models;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity(tableName = "maps")
public class Maps implements Serializable {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private String address;
    private Double latitude;
    private Double longitude;
    private String image;

    public Maps(String address, Double latitude, Double longitude, String image) {
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
        this.image = image;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
