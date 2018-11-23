package slicksoala.wheretoapp;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TimePicker;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import io.fabric.sdk.android.Fabric;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.concurrent.ExecutionException;

import info.hoang8f.android.segmented.SegmentedGroup;

public class HomeScreenActivity extends AppCompatActivity implements RadioGroup.OnCheckedChangeListener {
    TimePicker timePicker;
    private ActivityDo activity;
    private TravelType travelType;
    private String transit;
    private Pace pace;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.activity_homescreen);

        SegmentedGroup activityGroup = findViewById(R.id.activityGroup);
        activityGroup.setOnCheckedChangeListener(this);
        SegmentedGroup travelGroup = findViewById(R.id.travelGroup);
        travelGroup.setOnCheckedChangeListener(this);
        SegmentedGroup paceGroup = findViewById(R.id.paceGroup);
        paceGroup.setOnCheckedChangeListener(this);
        timePicker = findViewById(R.id.timePicker);
        logUser();

        ImageView go = findViewById(R.id.contBtn);
        go.setOnClickListener(view -> {
            try {
                goTo();
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        Button fbBtn = findViewById(R.id.fbBtn);
        fbBtn.setOnClickListener(v -> {
            Intent fintent = new Intent(HomeScreenActivity.this, FeedbackActivity.class);
            startActivity(fintent);
        });
    }

    private void logUser() {
        Crashlytics.setUserIdentifier("12345");
        Crashlytics.setUserEmail("user@whereto.test");
        Crashlytics.setUserName("Test User");
    }


    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (checkedId) {

            //activity
            case R.id.sightsBtn:
                activity = ActivityDo.SIGHTSEE;
                break;
            case R.id.foodBtn:
                activity = ActivityDo.EAT;
                break;
            case R.id.roamBtn:
                activity = ActivityDo.WANDER;
                break;

            //transit
            case R.id.walkBtn:
                travelType = TravelType.WALK;
                transit = "walking";
                break;
            case R.id.driveBtn:
                travelType = TravelType.DRIVE;
                transit = "driving";
                break;
            case R.id.publicBtn:
                travelType = TravelType.DRIVE;
                transit = "driving";
                break;
            case R.id.mixBtn:
                travelType = TravelType.DRIVE;
                transit = "driving";
                break;

            //pace
            case R.id.chillBtn:
                pace = Pace.CHILL;
                break;
            case R.id.moderateBtn:
                pace = Pace.MODERATE;
                break;
            case R.id.fastBtn:
                pace = Pace.FAST;
                break;
        }
    }

    public int getAvailableTime() {
        Calendar now = Calendar.getInstance();
        int currHour = now.get(Calendar.HOUR_OF_DAY);
        int currMin = now.get(Calendar.MINUTE);
        int retHour = timePicker.getHour();
        int retMin = timePicker.getMinute();
        int availableTime;

        if ((currHour > retHour) || ((currHour == retHour) && (currMin > retMin))) {
            availableTime = (24 - currHour) * 60 + (60 - currMin);
            availableTime += retHour * 60 + retMin;
        } else {
            availableTime = (retHour - currHour) * 60 + (retMin - currMin);
        }
        return availableTime;
    }



    private void requestLocationPermission() {
        ActivityCompat.requestPermissions(this, new String[]{
                Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.
                permission.ACCESS_FINE_LOCATION}, 101);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 101:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    try {
                        goWithLocationPerm();
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(this, "failed as location permission was denied",
                            Toast.LENGTH_LONG).show();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    public void goTo() throws ExecutionException, InterruptedException {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            goWithLocationPerm();
        } else {
            requestLocationPermission();
        }
    }

    public void goWithLocationPerm() throws ExecutionException, InterruptedException {
        LocationManager lm = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        assert lm != null;
        Criteria criteria = new Criteria();
        String bestProvider = lm.getBestProvider(criteria, false);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission
                .ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat
                .checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        Location loc = lm.getLastKnownLocation(bestProvider);

        Log.d("LOCATION: ",loc.toString());
        String currLat = Double.toString(loc.getLatitude());
        String currLong = Double.toString(loc.getLongitude());
        /*String currLat = "33.7747968";
        String currLong = "-84.3907072";*/

        if (activity == null || travelType == null || pace == null) {
            Toast.makeText(this, "Make all the selections!", Toast.LENGTH_LONG).show();
            return;
        }
        int availableTime = getAvailableTime();
        if (availableTime == 0) {
            Toast.makeText(this, "Choose a time different from now!", Toast.LENGTH_LONG).show();
            return;
        }

        int maxRad = travelType.getAvgSpeed() * availableTime/2;

        String act = activity.toString();
        if (maxRad > 40000)
            maxRad = 40000;

        int k = getAvailableTime()/(2*pace.getTime());

        if (k == 0) k = 1;
        else if (k > 8) k = 8;

        Intent placesFetch = new Intent(this, PlacesFetchSplash.class);
        placesFetch.putExtra("latitude", currLat);
        placesFetch.putExtra("longitude", currLong);
        placesFetch.putExtra("range", Integer.toString(maxRad));
        placesFetch.putExtra("activity", act);
        placesFetch.putExtra("transit", transit);
        placesFetch.putExtra("k", k);
        startActivity(placesFetch);

        if (activity == ActivityDo.EAT) {
            FoodSelectTask ftask = new FoodSelectTask();
            ftask.execute(currLat, currLong, Integer.toString(maxRad), act);
            return;
        } else {
            SightSelectTask stask = new SightSelectTask();
            stask.execute(currLat, currLong, Integer.toString(maxRad), act);
            return;
        }
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
        int k;

        @Override
        protected Void doInBackground(String... params) {
            Intent intentSplash = new Intent(HomeScreenActivity.this, SplashActivity.class);
            startActivity(intentSplash);

            k = getAvailableTime()/(2*pace.getTime());

            if (k == 0) k = 1;
            else if (k > 8) k = 8;

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
                Intent intentPref = new Intent(HomeScreenActivity.this, UserPreferencesActivity.class);
                Bundle extra = new Bundle();
                extra.putSerializable("preferences", parseList);
                extra.putSerializable("currPlace", currPlace);
                intentPref.putExtra("extra", extra);
                intentPref.putExtra("k", k);
                intentPref.putExtra("transit", transit);
                startActivity(intentPref);
            } else {
                Toast.makeText(HomeScreenActivity.this, "Sorry! Couldn't find any places near you! :(",
                        Toast.LENGTH_LONG).show();
                Intent homeIntent = new Intent(HomeScreenActivity.this, HomeScreenActivity.class);
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
        int k;

        @Override
        protected Void doInBackground(String... params) {
            Intent intentSplash = new Intent(HomeScreenActivity.this, SplashActivity.class);
            startActivity(intentSplash);

            k = getAvailableTime()/(2*pace.getTime());

            if (k == 0) k = 1;
            else if (k > 8) k = 8;

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
                Intent intentPref = new Intent(HomeScreenActivity.this, UserPreferencesActivity.class);
                Bundle extra = new Bundle();
                extra.putSerializable("preferences", parseList);
                extra.putSerializable("currPlace", currPlace);
                intentPref.putExtra("extra", extra);
                intentPref.putExtra("k", k);
                intentPref.putExtra("transit", transit);
                startActivity(intentPref);
            } else {
                Toast.makeText(HomeScreenActivity.this, "Sorry! Couldn't find any places near you! :(",
                        Toast.LENGTH_LONG).show();
                Intent homeIntent = new Intent(HomeScreenActivity.this, HomeScreenActivity.class);
                startActivity(homeIntent);
            }
        }
    }
}