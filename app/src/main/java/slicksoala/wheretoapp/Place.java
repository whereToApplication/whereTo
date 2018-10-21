package slicksoala.wheretoapp;

public class Place {
    private String name;
    private String category;
    private String rating;
    private String opennow;
    private String vicinity;
    private double latitude,longitude;
    private double distance;
    public Place()
    {
        this.name="";
        this.category="";
        this.rating="";
        this.opennow="";
        this.vicinity="";
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
    public String getVicinity() {
        return vicinity;
    }
    public void setVicinity(String vicinity) {
        this.vicinity = vicinity;
    }
    public void setLatLng(double lat,double lon)
    {
        this.latitude=lat;
        this.longitude=lon;
    }
    public void setDistance(String dist) {
        this.distance = Double.parseDouble(dist);
    }
}
