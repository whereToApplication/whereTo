package slicksoala.wheretoapp;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

class WhereToPlaces {
    private static final WhereToPlaces ourInstance = new WhereToPlaces();


    static WhereToPlaces getInstance() {
        return ourInstance;
    }
    private ArrayList<Place> masterList;
    private ArrayList<Place> kList;
    private int k = 5;
    private double sig = 0.4;
    private int delta = (int)(Math.random() * 6);

    private WhereToPlaces() {
        this.masterList = new ArrayList<>();
        this.kList = new ArrayList<>();
    }
    public int getK() {
        return k;
    }
    public void setK(int k) {
        this.k = k;
        this.delta = (int)(Math.random() * (k + 1));
    }

    public void setMasterList(ArrayList<Place> masterList) {
        this.masterList = masterList;
    }

    public void setkList(ArrayList<Place> kList) {
        this.kList = kList;
    }

    public ArrayList<Place> getMasterList() {
        return masterList;
    }

    public ArrayList<Place> getkList() {
        return kList;
    }

    public void addtoMasterList(Place place) {
        this.masterList.add(place);
    }

    public void addtoKList(Place place) {
        this.kList.add(place);
    }

    public Place getfromMasterList(int index) {
        return masterList.get(index);
    }
}
