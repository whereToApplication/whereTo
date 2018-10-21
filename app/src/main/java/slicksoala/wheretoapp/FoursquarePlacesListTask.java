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

public class FoursquarePlacesListTask extends AsyncTask<String, Void, ArrayList<Place>> {

    //https://api.foursquare.com/v2/venues/explore?client_id=SN4XROTU4SNIPGE2B35U2FS34ZE00WESSTX2WO1SHJ3PMOBJ&client_secret=ZPO3E2HYNFKACKF1SCTVZ52ZPDG2H4NWB1AET5YS4M1FYVRD&v=20180323&limit=1&ll=40.7243,-74.0018&query=food
    final String FOURSQ_API_BASE = "https://api.foursquare.com/v2/venues/explore?";
    final String CLIENT_ID = "SN4XROTU4SNIPGE2B35U2FS34ZE00WESSTX2WO1SHJ3PMOBJ";
    final String CLIENT_SECRET = "ZPO3E2HYNFKACKF1SCTVZ52ZPDG2H4NWB1AET5YS4M1FYVRD";
    final String CIC_PAR = "client_id=";
    final String CSC_PAR = "&client_secret=";
    final String VER_PAR = "&v=20180323";
    final String LIM_PAR = "&limit=20";
    final String LOC_PAR = "&ll=";
    final String RAD_PAR = "&radius=";
    final String CAT_PAR = "&categoryId";
    String currLat, currLong;
    String rad, activitySelect;
    String placeType;

    final String placeTypePTG = "507c8c4091d498d9fc8c67a9,4bf58dd8d48988d181941735,4bf58dd8d48988d1e2931735,4fceea171983d5d06c3e9823,56aa371be4b08b9a8d5734db";
    final String placeTypeTTD = "4d4b7104d754a06370d81259";
    final String placeTypeSTE = "4d4b7105d754a06374d81259";

    @Override
    protected ArrayList<Place> doInBackground(String... params) {

        currLat = params[0];
        currLong = params[1];
        rad = params[2];
        activitySelect = params[3];
        if (activitySelect.equals("PLACESTOGO"))
            placeType = placeTypePTG;
        else if (activitySelect.equals("THINGSTODO"))
            placeType = placeTypeTTD;
        else
            placeType = placeTypeSTE;
        String urlString = FOURSQ_API_BASE + CIC_PAR + CLIENT_ID + CSC_PAR + CLIENT_SECRET + VER_PAR + LIM_PAR + LOC_PAR + currLat + "," + currLong + RAD_PAR +
                rad + CAT_PAR + placeType;

        URL url;
        HttpURLConnection urlConnection = null;
        String resultString = "";
        JSONObject jsonObject;
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
            jsonObject = new JSONObject(resultString);
            if (jsonObject.has("groups")) {
                JSONArray groupsArray = jsonObject.getJSONArray("groups");
                if (groupsArray.getJSONObject(0).has("items")) {
                    JSONArray jsonArray = jsonObject.getJSONArray("items");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        Place poi = new Place();
                        if (jsonArray.getJSONObject(i).has("venue")) {
                            poi.setName(jsonArray.getJSONObject(i).getJSONObject("venue").optString("name"));
                            if (jsonArray.getJSONObject(i).getJSONObject("venue").has("location")){
                                Double lat = Double.parseDouble(jsonArray.getJSONObject(i).getJSONObject("venue").
                                        getJSONObject("location").optString("lat"));
                                Double lng = Double.parseDouble(jsonArray.getJSONObject(i).getJSONObject("venue").
                                        getJSONObject("location").optString("lng"));
                                poi.setLatLng(lat,lng);
                                poi.setRating("3");
                            }
                        }
                        if (jsonArray.getJSONObject(i).has("categories")) {
                            poi.setCategory(jsonArray.getJSONObject(i).getJSONObject("categories").
                                    optString("name"));
                        }
                        parseList.add(poi);
                    }
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
