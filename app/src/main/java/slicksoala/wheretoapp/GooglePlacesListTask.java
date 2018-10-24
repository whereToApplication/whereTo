package slicksoala.wheretoapp;


import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class GooglePlacesListTask extends AsyncTask<String, Void, ArrayList<Place>> {
    final String PLACES_API_BASE = "https://maps.googleapis.com/maps/api/place/nearbysearch/json";
    final String API_KEY = "AIzaSyA5ZYJdN-y4AffumiWHX-xUaWDwYS8ZBBU";
    final String LOC_PAR = "?location=";
    final String RAD_PAR = "&radius=";
    final String TYP_PAR = "&types=";
    final String KEY_PAR = "&key=";
    String currLat, currLong;
    String rad, activitySelect;
    String placeType;

    final String placeTypePTG = "amusement_park,aquarium,art_gallery,natural_feature,cafe,casino,library,hindu_temple,museum,park,stadium,zoo";
    final String placeTypeTTD = "bowling_alley,bookstore,gym,shopping_mall,spa,movie_theater,movie_rental";
    final String placeTypeSTE = "bakery,bar,cafe,food,restaurant";


    @Override
    protected ArrayList<Place> doInBackground(String... params) {
        currLat = params[0];
        currLong = params[1];
        rad = params[2];
        activitySelect = params[3];
        if (activitySelect.equals("Sights"))
            placeType = placeTypePTG;
        else if (activitySelect.equals("Roam"))
            placeType = placeTypeTTD;
        else
            placeType = placeTypeSTE;

        String url_base = PLACES_API_BASE + LOC_PAR + currLat + "," + currLong + RAD_PAR +
                rad;
        String apikey_base = KEY_PAR + API_KEY;
        String urlString = url_base + TYP_PAR + placeType + apikey_base;

        URL url;
        HttpURLConnection urlConnection = null;
        String resultString = "";
        ArrayList<Place> parseList = new ArrayList();

        try {
            url = new URL(urlString);
            urlConnection = (HttpURLConnection) url.openConnection();

            InputStream in = urlConnection.getInputStream();
            InputStreamReader reader = new InputStreamReader(in);

            int data = reader.read();
            while (data != -1) {
                char current = (char) data;
                resultString += current;
                data = reader.read();
            }
            JSONObject jsonObject = new JSONObject(resultString);
            if (jsonObject.has("results")) {
                JSONArray jsonArray = jsonObject.getJSONArray("results");
                for (int i = 0; i < jsonArray.length(); i++) {
                    Place poi = new Place();
                    if (jsonArray.getJSONObject(i).has("name")) {
                        poi.setName(jsonArray.getJSONObject(i).optString("name"));
                        poi.setRating(jsonArray.getJSONObject(i).optString("rating", " "));
                        if (jsonArray.getJSONObject(i).has("geometry"))
                        {
                            if (jsonArray.getJSONObject(i).getJSONObject("geometry").has("location"))
                            {
                                if (jsonArray.getJSONObject(i).getJSONObject("geometry").getJSONObject("location").has("lat"))
                                {
                                    poi.setLatLng(Double.parseDouble(jsonArray.getJSONObject(i).getJSONObject("geometry").
                                            getJSONObject("location").getString("lat")), Double.parseDouble(jsonArray.getJSONObject(i).
                                            getJSONObject("geometry").getJSONObject("location").getString("lng")));
                                }
                            }
                        }
                        if (jsonArray.getJSONObject(i).has("types")) {
                            JSONArray typesArray = jsonArray.getJSONObject(i).getJSONArray("types");
                            for (int j = 0; j < typesArray.length(); j++) {
                                poi.setCategory(typesArray.getString(j) + ", " + poi.getCategory());
                            }
                        }
                    }
                    parseList.add(poi);
                }
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList();
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
        return parseList;
    }
}