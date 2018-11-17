package slicksoala.wheretoapp;

public enum TravelType {
    WALK("Walk", 84),
    DRIVE("Drive", 834);

    private String val;
    private int avgSpeed;

    TravelType(String val, int avgSpeed) {
        this.val = val;
        this.avgSpeed = avgSpeed;
    }
    public String toString() { return this.val;}
    public int getAvgSpeed() { return this.avgSpeed;}
}
