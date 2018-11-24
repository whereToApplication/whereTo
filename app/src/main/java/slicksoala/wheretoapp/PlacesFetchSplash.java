package slicksoala.wheretoapp;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.skyfishjy.library.RippleBackground;

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

public class PlacesFetchSplash extends AppCompatActivity {

    int k;
    String act;
    String transit;
    FoodSelectTask ftask;
    SightSelectTask stask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.screen_splash);
        RippleBackground rippleBackground = (RippleBackground) findViewById(R.id.ripple);
        rippleBackground.startRippleAnimation();

        Intent intent = getIntent();
        act = intent.getStringExtra("activity");
        k = intent.getIntExtra("k", 1);
        String currLat = intent.getStringExtra("latitude");
        String currLong = intent.getStringExtra("longitude");
        String maxRad = intent.getStringExtra("range");
        transit = intent.getStringExtra("transit");


        if (act.equals("Food")) {
            ftask = new FoodSelectTask();
            ftask.execute(currLat, currLong, maxRad, act);
            return;
        } else {
            stask = new SightSelectTask();
            stask.execute(currLat, currLong, maxRad, act);
            return;
        }
    }

    @Override
    public void onBackPressed() {
        if (act.equals("Food")) {
            ftask.cancel(true);
        } else {
            stask.cancel(true);
        }
        Intent backHomeIntent = new Intent(this, HomeScreenActivity.class);
        startActivity(backHomeIntent);
    }


    class FoodSelectTask extends AsyncTask<String, Void, Void> {
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

        boolean results;
        ArrayList<Place> parseList;
        Place currPlace;

        @Override
        protected Void doInBackground(String... params) {

            currLat = params[0];
            currLong = params[1];
            rad = params[2];
            activitySelect = params[3];
            switch (activitySelect) {
                case "Sights":
                    placeType = placeTypePTG;
                    break;
                case "Roam":
                    placeType = placeTypeTTD;
                    break;
                default:
                    placeType = placeTypeSTE;
                    break;
            }

            currPlace = new Place();
            currPlace.setName("Your Location");
            currPlace.setLatLng(Double.parseDouble(currLat), Double.parseDouble(currLong));

            String urlString = YELP_API_BASE + TER_PAR + placeType + LAT_PAR + currLat + LON_PAR + currLong + RAD_PAR + rad;

            URL url;
            HttpURLConnection urlConnection = null;
            StringBuilder resultString = new StringBuilder();
            JSONObject jsonObject;
            parseList = new ArrayList();

            try {
                url = new URL(urlString);
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestProperty("Authorization","Bearer " + API_KEY);

                InputStream in = urlConnection.getInputStream();
                InputStreamReader reader = new InputStreamReader(in);

                int data = reader.read();
                while (data != -1) {
                    char current = (char) data;
                    resultString.append(current);
                    data = reader.read();
                }
                jsonObject = new JSONObject(resultString.toString());
                if (jsonObject.has("businesses")) {
                    JSONArray itemsArray = jsonObject.getJSONArray("businesses");
                    if (itemsArray.length() == 0) {
                        results = false;
                    } else {
                        results = true;
                        for (int i = 0; i < itemsArray.length(); i++) {
                            Place poi = new Place();
                            if (itemsArray.getJSONObject(i).has("name")) {
                                poi.setName(itemsArray.getJSONObject(i).optString("name"));
                                poi.setRating(itemsArray.getJSONObject(i).optString("rating"));
                                if (itemsArray.getJSONObject(i).has("categories")) {
                                    StringBuilder cat = new StringBuilder();
                                    JSONArray catArray = itemsArray.getJSONObject(i).getJSONArray("categories");
                                    if (catArray.length() == 1) {
                                        cat.append(catArray.getJSONObject(0).optString("title"));
                                    }
                                    else if (catArray.length() == 2) {
                                        cat.append(catArray.getJSONObject(0).optString("title")).append(" ");
                                        cat.append("and ").append(catArray.getJSONObject(1).optString("title"));
                                    } else {
                                        for (int j = 0; j < catArray.length() - 1; j++) {
                                            cat.append(catArray.getJSONObject(j).optString("title")).append(", ");
                                        }
                                        cat.append("and ").append(catArray.getJSONObject(catArray.length() - 1).optString("title"));
                                    }
                                    poi.setCategory(cat.toString());
                                }
                                if (itemsArray.getJSONObject(i).has("coordinates"))
                                {
                                    poi.setLatLng(Double.parseDouble(itemsArray.getJSONObject(i).getJSONObject("coordinates").optString("latitude")),
                                            Double.parseDouble(itemsArray.getJSONObject(i).getJSONObject("coordinates").optString("longitude")));
                                }
                            }
                            parseList.add(poi);
                        }
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
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (results) {
                Intent intentPref = new Intent(PlacesFetchSplash.this, UserPreferencesActivity.class);
                Bundle extra = new Bundle();
                extra.putSerializable("preferences", parseList);
                extra.putSerializable("currPlace", currPlace);
                intentPref.putExtra("extra", extra);
                intentPref.putExtra("k", k);
                intentPref.putExtra("transit", transit);
                startActivity(intentPref);
            } else {
                Toast.makeText(PlacesFetchSplash.this, "Sorry! Couldn't find any places near you! :(",
                        Toast.LENGTH_LONG).show();
                Intent homeIntent = new Intent(PlacesFetchSplash.this, HomeScreenActivity.class);
                startActivity(homeIntent);
            }
        }
    }

    class SightSelectTask extends AsyncTask<String, Void, Void> {

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
        final String CAT_PAR = "&categoryId=";
        String currLat, currLong;
        String rad, activitySelect;
        String placeType;

        final String placeTypePTG = "507c8c4091d498d9fc8c67a9,4bf58dd8d48988d181941735,4bf58dd8d48988d1e2931735,4fceea171983d5d06c3e9823,56aa371be4b08b9a8d5734db";
        final String placeTypeTTD = "4d4b7104d754a06370d81259";
        final String placeTypeSTE = "4d4b7105d754a06374d81259";

        boolean results;
        ArrayList<Place> parseList;
        Place currPlace;

        @Override
        protected Void doInBackground(String... params) {

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
            String urlString = FOURSQ_API_BASE + CIC_PAR + CLIENT_ID + CSC_PAR + CLIENT_SECRET + VER_PAR + LIM_PAR + LOC_PAR + currLat + "," + currLong + RAD_PAR +
                    rad + CAT_PAR + placeType;

            currPlace = new Place();
            currPlace.setName("Your Location");
            currPlace.setLatLng(Double.parseDouble(currLat), Double.parseDouble(currLong));

            URL url;
            HttpURLConnection urlConnection = null;
            StringBuilder resultString = new StringBuilder();
            JSONObject jsonObject;
            parseList = new ArrayList();

            try {
                url = new URL(urlString);
                urlConnection = (HttpURLConnection) url.openConnection();

                InputStream in = urlConnection.getInputStream();
                InputStreamReader reader = new InputStreamReader(in);

                int data = reader.read();
                while (data != -1) {
                    char current = (char) data;
                    resultString.append(current);
                    data = reader.read();
                }
                jsonObject = new JSONObject(resultString.toString());
                if (jsonObject.has("response")) {
                    if (jsonObject.getJSONObject("response").has("groups")) {
                        JSONArray groupsArray = jsonObject.getJSONObject("response").getJSONArray("groups");
                        if (groupsArray.getJSONObject(0).has("items")) {
                            JSONArray itemsArray = groupsArray.getJSONObject(0).getJSONArray("items");
                            if (itemsArray.length() == 0) {
                                results = false;
                            } else {
                                results = true;
                                for (int i = 0; i < itemsArray.length(); i++) {
                                    Place poi = new Place();
                                    if (itemsArray.getJSONObject(i).has("venue")) {
                                        poi.setName(itemsArray.getJSONObject(i).getJSONObject("venue").optString("name"));
                                        if (itemsArray.getJSONObject(i).getJSONObject("venue").has("location")){
                                            Double lat = Double.parseDouble(itemsArray.getJSONObject(i).getJSONObject("venue").
                                                    getJSONObject("location").optString("lat"));
                                            Double lng = Double.parseDouble(itemsArray.getJSONObject(i).getJSONObject("venue").
                                                    getJSONObject("location").optString("lng"));
                                            poi.setLatLng(lat,lng);
                                            poi.setRating("3");

                                            if (itemsArray.getJSONObject(i).getJSONObject("venue").has("categories")) {
                                                StringBuilder cat = new StringBuilder();
                                                JSONArray catArray = itemsArray.getJSONObject(i).getJSONObject("venue").getJSONArray("categories");
                                                if (catArray.length() == 1) {
                                                    cat.append(catArray.getJSONObject(0).optString("pluralName"));
                                                }
                                                else if (catArray.length() == 2) {
                                                    cat.append(catArray.getJSONObject(0).optString("pluralName")).append(" ");
                                                    cat.append("and ").append(catArray.getJSONObject(1).optString("pluralName"));
                                                } else {
                                                    for (int j = 0; j < catArray.length() - 1; j++) {
                                                        cat.append(catArray.getJSONObject(j).optString("pluralName")).append(", ");
                                                    }
                                                    cat.append("and ").append(catArray.getJSONObject(catArray.length() - 1).optString("title"));
                                                }
                                                poi.setCategory(cat.toString());
                                            }
                                        }
                                    }
                                    parseList.add(poi);
                                }
                            }
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (results) {
                Intent intentPref = new Intent(PlacesFetchSplash.this, UserPreferencesActivity.class);
                Bundle extra = new Bundle();
                extra.putSerializable("preferences", parseList);
                extra.putSerializable("currPlace", currPlace);
                intentPref.putExtra("extra", extra);
                intentPref.putExtra("k", k);
                intentPref.putExtra("transit", transit);
                startActivity(intentPref);
            } else {
                Toast.makeText(PlacesFetchSplash.this, "Sorry! Couldn't find any places near you! :(",
                        Toast.LENGTH_LONG).show();
                Intent homeIntent = new Intent(PlacesFetchSplash.this, HomeScreenActivity.class);
                startActivity(homeIntent);
            }
        }
    }
}
