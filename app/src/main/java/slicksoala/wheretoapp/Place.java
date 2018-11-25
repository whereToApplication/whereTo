package slicksoala.wheretoapp;

import java.io.Serializable;
import java.util.ArrayList;

public class Place implements Serializable {
    private String name;
    private String category;
    private ArrayList<String> categories;
    private String rating;
    private double userPref;
    private double latitude,longitude;
    public Place()
    {
        this.name="";
        this.category="";
        this.rating="";
        this.categories = new ArrayList<String>();
        this.userPref = 0.5;
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
    public void setLatLng(double lat,double lon) {
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
    public void addCategory(String category) {
        this.categories.add(category);
    }
    public ArrayList<String> getCategories() {
        return categories;
    }
}
