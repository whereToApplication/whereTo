package slicksoala.wheretoapp;

public enum Pace {
    CHILL("Chill"),
    MODERATE("Moderate"),
    FAST("Fast");

    private String val;

    Pace(String val) { this.val = val;}
    public String toString() { return this.val;}
}
