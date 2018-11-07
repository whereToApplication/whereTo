package slicksoala.wheretoapp;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
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
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.concurrent.ExecutionException;

public class DetailsForm extends AppCompatActivity {
    private EditText returnTime;
    private Spinner radiusSpinner;
    private Spinner activitySpinner;
    private Spinner travelSpinner;
    private Spinner paceSpinner;
    private RippleBackground rippleBackground;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details_form);
        returnTime = (EditText) findViewById(R.id.returnTime);
        radiusSpinner = findViewById(R.id.radiusSpinner);
        activitySpinner = findViewById(R.id.activitySpinner);
        travelSpinner = findViewById(R.id.travelSpinner);
        paceSpinner = findViewById(R.id.paceSpinner);
        rippleBackground = (RippleBackground) findViewById(R.id.content);
        rippleBackground.startRippleAnimation();
        ImageView go = findViewById(R.id.centerImage);
        go.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    goTo();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

        ArrayAdapter<String> radiusAdapter = new ArrayAdapter(this,android.R.layout.simple_spinner_item, Radius.values());
        radiusAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        radiusSpinner.setAdapter(radiusAdapter);

        ArrayAdapter<String> activityAdapter = new ArrayAdapter(this,android.R.layout.simple_spinner_item, ActivityDo.values());
        activityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        activitySpinner.setAdapter(activityAdapter);

        ArrayAdapter<String> travelAdapter = new ArrayAdapter(this,android.R.layout.simple_spinner_item, TravelType.values());
        travelAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        travelSpinner.setAdapter(travelAdapter);

        ArrayAdapter<String> paceAdapter = new ArrayAdapter(this,android.R.layout.simple_spinner_item, Pace.values());
        paceAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        paceSpinner.setAdapter(paceAdapter);
    }

    public void goTo() throws ExecutionException, InterruptedException {
        /*if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(DetailsForm.this, "First enable LOCATION ACCESS in settings.", Toast.LENGTH_LONG).show();
            return;
        }*/
        /*LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        String currLat = Double.toString(location.getLatitude());
        String currLong = Double.toString(location.getLongitude());*/
        String currLat = "33.78508547";
        String currLong = "-84.3879824";
        Place currPlace = new Place();
        currPlace.setName("Your Location");
        currPlace.setLatLng(Double.parseDouble(currLat), Double.parseDouble(currLong));

        int k = 5;
        ArrayList<Place> masterList;
        ArrayList<Place> kList = new ArrayList<>();

        String ret = returnTime.getText().toString();

        Radius radius = (Radius) radiusSpinner.getSelectedItem();
        String rad = radius.getVal();

        ActivityDo activity = (ActivityDo) activitySpinner.getSelectedItem();
        String act = activity.toString();

        TravelType travelType = (TravelType) travelSpinner.getSelectedItem();
        String tra = travelType.toString();

        Pace pace = (Pace) paceSpinner.getSelectedItem();
        String pac = radius.getVal();

        if (activity == ActivityDo.EAT) {
            FoodSelectTask ftask = new FoodSelectTask();
            ftask.execute(currLat, currLong, rad, act);
        } else {
            SightSelectTask stask = new SightSelectTask();
            stask.execute(currLat, currLong, rad, act);
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

        @Override
        protected Void doInBackground(String... params) {
            Intent intentSplash = new Intent(DetailsForm.this, SplashActivity.class);
            startActivity(intentSplash);

            int k = 5;
            ArrayList<Place> masterList;
            ArrayList<Place> kList = new ArrayList<>();

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

            Place currPlace = new Place();
            currPlace.setName("Your Location");
            currPlace.setLatLng(Double.parseDouble(currLat), Double.parseDouble(currLong));

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
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
            }

            kList.add(currPlace);
            for (int i = 0; i < k; i++) {
                kList.add(parseList.get(i));
            }

            OptimalRoutingTask dmtask = new OptimalRoutingTask();
            dmtask.execute(kList);

            return null;
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
        final String CAT_PAR = "&categoryId";
        String currLat, currLong;
        String rad, activitySelect;
        String placeType;

        final String placeTypePTG = "507c8c4091d498d9fc8c67a9,4bf58dd8d48988d181941735,4bf58dd8d48988d1e2931735,4fceea171983d5d06c3e9823,56aa371be4b08b9a8d5734db";
        final String placeTypeTTD = "4d4b7104d754a06370d81259";
        final String placeTypeSTE = "4d4b7105d754a06374d81259";

        @Override
        protected Void doInBackground(String... params) {
            Intent intentSplash = new Intent(DetailsForm.this, SplashActivity.class);
            startActivity(intentSplash);

            int k = 5;
            ArrayList<Place> masterList;
            ArrayList<Place> kList = new ArrayList<>();

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

            Place currPlace = new Place();
            currPlace.setName("Your Location");
            currPlace.setLatLng(Double.parseDouble(currLat), Double.parseDouble(currLong));

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
                if (jsonObject.has("response")) {
                    if (jsonObject.getJSONObject("response").has("groups")) {
                        JSONArray groupsArray = jsonObject.getJSONObject("response").getJSONArray("groups");
                        if (groupsArray.getJSONObject(0).has("items")) {
                            JSONArray jsonArray = groupsArray.getJSONObject(0).getJSONArray("items");
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
                }

            } catch (MalformedURLException e) {
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
            kList.add(currPlace);
            for (int i = 0; i < k; i++) {
                kList.add(parseList.get(i));
            }

            OptimalRoutingTask dmtask = new OptimalRoutingTask();
            dmtask.execute(kList);

            return null;
        }
    }

    class OptimalRoutingTask extends AsyncTask<ArrayList<Place>, Void, Void> {
        final String DISTMATRIX_BASE = "https://maps.googleapis.com/maps/api/distancematrix/json?units=metric";
        final String ORIG_PAR = "&origins=";
        final String DEST_PAR = "&destinations=";
        final String KEY_PAR = "&key=";
        final String API_KEY = "AIzaSyAr-MIu6A-LmtXGsm94fDfIjICLguluajQ";
        @Override
        protected Void doInBackground(ArrayList<Place>... al) {
            ArrayList<Place> kList = al[0];
            double[][] distMatrix = new double[kList.size()][kList.size()];

            StringBuilder sb = new StringBuilder();
            if (kList != null && kList.size() != 0) {
                sb.append(Double.toString(kList.get(0).getLatitude()));
                sb.append(",");
                sb.append(Double.toString(kList.get(0).getLongitude()));
                for (int i = 1; i < kList.size(); i++) {
                    sb.append("|");
                    sb.append(Double.toString(kList.get(i).getLatitude()));
                    sb.append(",");
                    sb.append(Double.toString(kList.get(i).getLongitude()));
                }
            }
            String origdest = sb.toString();
            String urlString = DISTMATRIX_BASE + ORIG_PAR + origdest + DEST_PAR + origdest + KEY_PAR + API_KEY;

            URL url;
            HttpURLConnection urlConnection = null;
            String resultString = "";

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
                if (jsonObject.has("rows")) {
                    JSONArray rowsArray = jsonObject.getJSONArray("rows");
                    for (int i = 0; i < rowsArray.length(); i++) {
                        if (rowsArray.getJSONObject(i).has("elements")) {
                            JSONArray columnsArray = rowsArray.getJSONObject(i).
                                    getJSONArray("elements");
                            for (int j = 0; j < columnsArray.length(); j++) {
                                if (columnsArray.getJSONObject(j).has("duration")) {
                                    if (columnsArray.getJSONObject(j).getJSONObject("duration").
                                            has("value")) {
                                        int durSeconds = columnsArray.getJSONObject(j).getJSONObject("duration").
                                                getInt("value");
                                        double durMinutes = ((double) durSeconds) / 60;
                                        distMatrix[i][j] = durMinutes;
                                    }
                                }
                            }
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
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
            }

            HeldKarpTSP hk = new HeldKarpTSP();

            LinkedList<Integer> path = (LinkedList<Integer>) hk.optimalRoute(distMatrix)[1];
            ArrayList<Place> placesRoute = new ArrayList<>();
            int i = 0;
            while (i < path.size()) {
                placesRoute.add(kList.get(path.get(i)));
                i++;
            }
            String route = "";
            for (Place p : placesRoute) {
                route += "-> " + p.getName();
            }
            System.out.println("ROUTE: " + route);
            Intent intentBack = new Intent(getApplicationContext(), DetailsForm.class);
            startActivity(intentBack);
            return null;
        }
    }
}
