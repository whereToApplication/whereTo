package slicksoala.wheretoapp;

public enum Radius {
    NEARBY("Nearby", "1000"),
    CITYWIDE("City Wide", "16093");

    private String type;
    private String rad;

    Radius(String type, String rad) {
        this.type = type;
        this.rad = rad;
    }

    public String toString() {
        return this.type;
    }

    public String getVal() {return this.rad;}
}

