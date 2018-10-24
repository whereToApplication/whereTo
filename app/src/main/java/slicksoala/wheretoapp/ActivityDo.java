package slicksoala.wheretoapp;

public enum ActivityDo {
    SIGHTSEE("Sights"),
    EAT("Food"),
    WANDER("Roam");

    private String val;

    ActivityDo(String val) { this.val = val;}
    public String toString() { return this.val;}
}
