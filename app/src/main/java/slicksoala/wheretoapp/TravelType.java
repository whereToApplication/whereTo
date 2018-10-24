package slicksoala.wheretoapp;

public enum TravelType {
    WALK("Walk"),
    DRIVE("Drive");

    private String val;

    TravelType(String val) { this.val = val;}
    public String toString() { return this.val;}
}
