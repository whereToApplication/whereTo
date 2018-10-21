package slicksoala.wheretoapp;

public enum ActivityDo {
    PLACESTOGO("Sigthseeing"),
    STUFFTOEAT("Food"),
    THINGSTODO("What To Do");

    private String val;

    ActivityDo(String val) { this.val = val;}
    public String toString() { return this.val;}
}
