package slicksoala.wheretoapp;

public enum Range {
    WALK("Walk"),
    DRIVE("Drive"),
    TRAVEL("Public Transport");

    private String val;

    Range(String val) { this.val = val;}
    public String toString() { return this.val;}
}
