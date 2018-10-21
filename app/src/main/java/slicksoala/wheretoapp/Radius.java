package slicksoala.wheretoapp;

public enum Radius {
    NEARBY("Nearby"),
    CITYWIDE("City Wide");

    private String val;

    Radius(String val) {
        this.val = val;
    }

    public String toString() {
        return this.val;
    }
}

