package slicksoala.wheretoapp;

public enum Pace {
    CHILL("Chill", 90),
    MODERATE("Moderate", 45),
    FAST("Fast", 15);

    private String val;
    private int time;

    Pace(String val, int time) {
        this.val = val;
        this.time = time;
    }
    public String toString() { return this.val;}
    public int getTime() {return this.time;}
}
