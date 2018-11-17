package slicksoala.wheretoapp;

import android.os.Parcelable;

import java.io.Serializable;

public class Place implements Serializable {
    private String name;
    private String category;
    private String rating;
    private String opennow;
    private double userPref = 0.5;
    private double latitude,longitude;
    public Place()
    {
        this.name="";
        this.category="";
        this.rating="";
        this.opennow="";
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getCategory() {
        return category;
    }
    public void setCategory(String category) {
        this.category = category;
    }
    public String getRating() {
        return rating;
    }
    public void setRating(String rating) {
        this.rating = rating;
    }
    public String getOpen() {
        return opennow;
    }
    public void setOpenNow(String open) {
        this.opennow = open;
    }
    public void setLatLng(double lat,double lon)
    {
        this.latitude=lat;
        this.longitude=lon;
    }


    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public double getUserPref() {
        return userPref;
    }

    public void setUserPref(double userPref) {
        this.userPref = userPref;
    }
}
