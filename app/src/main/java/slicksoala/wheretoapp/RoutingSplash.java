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
import java.util.LinkedList;

public class RoutingSplash extends AppCompatActivity {

    private String transit;
    private ArrayList<Place> places;
    private ArrayList<Place> kList;
    OptRoutingTask dmtask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.screen_splash);
        RippleBackground rippleBackground = (RippleBackground) findViewById(R.id.ripple);
        rippleBackground.startRippleAnimation();

        Intent intent = getIntent();
        places = (ArrayList<Place>) intent.getSerializableExtra("places");
        kList = (ArrayList<Place>) intent.getSerializableExtra("kList");
        transit = intent.getStringExtra("transit");

        dmtask = new OptRoutingTask();
        dmtask.execute(kList);
    }

    @Override
    public void onBackPressed() {
        dmtask.cancel(true);
        Intent backHomeIntent = new Intent(this, HomeScreenActivity.class);
        startActivity(backHomeIntent);
    }

    class OptRoutingTask extends AsyncTask<ArrayList<Place>, Void, Void> {
        final String DISTMATRIX_BASE = "https://maps.googleapis.com/maps/api/distancematrix/json?units=metric";
        final String ORIG_PAR = "&origins=";
        final String DEST_PAR = "&destinations=";
        final String KEY_PAR = "&key=";
        final String API_KEY = "AIzaSyAr-MIu6A-LmtXGsm94fDfIjICLguluajQ";
        ArrayList<Place> placesRoute;
        @Override
        protected Void doInBackground(ArrayList<Place>... al) {
            ArrayList<Place> kList = al[0];
            double[][] distMatrix = new double[kList.size()][kList.size()];

            StringBuilder sb = new StringBuilder();
            if (kList.size() != 0) {
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
            StringBuilder resultString = new StringBuilder();

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
                JSONObject jsonObject = new JSONObject(resultString.toString());
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
            placesRoute = new ArrayList<>();
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


            return null;
        }

        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            Intent mapsIntent = new Intent(getApplicationContext(), MapsActivity.class);
            mapsIntent.putExtra("route", placesRoute);
            mapsIntent.putExtra("places", places);
            mapsIntent.putExtra("transit", transit);
            startActivity(mapsIntent);
        }
    }
}
