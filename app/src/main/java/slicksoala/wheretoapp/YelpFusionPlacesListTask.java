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

public class YelpFusionPlacesListTask extends AsyncTask<String, Void, ArrayList<Place>> {
    //https://api.yelp.com/v3/businesses/search?term=food&latitude=33.78508547&longitude=-84.3879824&radius=1000
    final String YELP_API_BASE = "https://api.yelp.com/v3/businesses/search?";
    final String API_KEY = "IRT-fzwU1f8luW7wcdWJ5wSzmTOWoJuYKAOMZJtlv-D6s-MVhzGwu7MLn77_A2NWUohglYO_WZhBgejDmHINDKSSP-jzSKFoa_DeL3TdYGrezK1TFeYaHLagsmvLW3Yx";
    final String TER_PAR = "term=";
    final String LAT_PAR = "&latitude=";
    final String LON_PAR = "&longitude=";
    final String RAD_PAR = "&radius=";

    String currLat, currLong;
    String rad, activitySelect;
    String placeType;

    final String placeTypePTG = "arts";
    final String placeTypeTTD = "arts";
    final String placeTypeSTE = "food";

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

        String urlString = YELP_API_BASE + TER_PAR + placeType + LAT_PAR + currLat + LON_PAR + currLong + RAD_PAR + rad;

        URL url;
        HttpURLConnection urlConnection = null;
        String resultString = "";
        JSONObject jsonObject;
        ArrayList<Place> parseList = new ArrayList();

        try {
            url = new URL(urlString);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestProperty("Authorization","Bearer " + API_KEY);

            InputStream in = urlConnection.getInputStream();
            InputStreamReader reader = new InputStreamReader(in);

            int data = reader.read();
            while (data != -1) {
                char current = (char) data;
                resultString += current;
                data = reader.read();
            }
            jsonObject = new JSONObject(resultString);
            if (jsonObject.has("businesses")) {
                JSONArray jsonArray = jsonObject.getJSONArray("businesses");
                for (int i = 0; i < jsonArray.length(); i++) {
                    Place poi = new Place();
                    if (jsonArray.getJSONObject(i).has("name")) {
                        poi.setName(jsonArray.getJSONObject(i).optString("name"));
                        poi.setRating(jsonArray.getJSONObject(i).optString("rating"));

                        if (jsonArray.getJSONObject(i).has("coordinates"))
                        {
                            poi.setLatLng(Double.parseDouble(jsonArray.getJSONObject(i).getJSONObject("coordinates").optString("latitude")),
                                    Double.parseDouble(jsonArray.getJSONObject(i).getJSONObject("coordinates").optString("longitude")));
                        }
                    }
                    parseList.add(poi);
                }
            }
        } catch (MalformedURLException e) {
            System.out.print(e + "IOException");
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