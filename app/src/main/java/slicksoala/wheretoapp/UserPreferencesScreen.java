package slicksoala.wheretoapp;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;

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
import java.util.Collections;
import java.util.LinkedList;

public class UserPreferencesScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_preferences_screen);
        ListView preferencesListView = findViewById(R.id.prefList);
        ImageView cont = findViewById(R.id.contBtn);

        Intent intent = getIntent();
        Bundle extra = intent.getBundleExtra("extra");
        ArrayList<Place> places = (ArrayList<Place>) extra.getSerializable("preferences");
        PreferencesListAdapter prefAdapter = new PreferencesListAdapter(
                this, places);
        preferencesListView.setAdapter(prefAdapter);

        cont.setOnClickListener(v -> {
            Place currPlace = (Place) extra.getSerializable("currPlace");
            int k = intent.getIntExtra("k", 0);

            ArrayList<Place> kList = new ArrayList<>();
            kList.add(currPlace);

            Collections.shuffle(places);

            int i = 0;
            int pos = 0;
            while (i < Math.max(k, places.size()) && pos < places.size()) {
                if (places.get(pos).getUserPref() >= 0.5) {
                    Log.d("place", places.get(pos).getName());
                    kList.add(places.get(pos));
                    i++;
                }
                pos++;
            }

            OptRoutingTask dmtask = new OptRoutingTask();
            dmtask.execute(kList);
        });
    }

    class OptRoutingTask extends AsyncTask<ArrayList<Place>, Void, Void> {
        final String DISTMATRIX_BASE = "https://maps.googleapis.com/maps/api/distancematrix/json?units=metric";
        final String ORIG_PAR = "&origins=";
        final String DEST_PAR = "&destinations=";
        final String KEY_PAR = "&key=";
        final String API_KEY = "AIzaSyAr-MIu6A-LmtXGsm94fDfIjICLguluajQ";
        @Override
        protected Void doInBackground(ArrayList<Place>... al) {
            Intent intentSplash = new Intent(UserPreferencesScreen.this, SplashActivity.class);
            startActivity(intentSplash);
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
            /*String route = "";
            for (Place p : placesRoute) {
                route += "-> " + p.getName();
            }
            System.out.println("ROUTE: " + route);*/

            Intent mapsIntent = new Intent(getApplicationContext(), MapsActivity.class);
            mapsIntent.putExtra("route", placesRoute);
            startActivity(mapsIntent);
            return null;
        }
    }
}
