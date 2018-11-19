package slicksoala.wheretoapp;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

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

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private Place currPlace;
    private int k;
    private ArrayList<Place> route;
    private ArrayList<Place> places;
    private String transit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        Intent intent = getIntent();
        route = (ArrayList<Place>) intent.getSerializableExtra("route");
        places = (ArrayList<Place>) intent.getSerializableExtra("places");
        transit = intent.getStringExtra("transit");
        k = route.size() - 2;
        currPlace = route.get(0);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        assert mapFragment != null;
        mapFragment.getMapAsync(this);
    }

    public void onRerollClick(View view) {
        ArrayList<Place> kList = new ArrayList<>();
        kList.add(currPlace);

        Collections.shuffle(places);

        int i = 0;
        int pos = 0;
        while (i < Math.min(k, places.size()) && pos < places.size()) {
            if (places.get(pos).getUserPref() >= 0.5) {
                Log.d("place", places.get(pos).getName());
                kList.add(places.get(pos));
                i++;
            }
            pos++;
        }

        OptRoutingTask dmtask = new OptRoutingTask();
        dmtask.execute(kList);
    }

    public void onStartJourney(View view) {
        /*Uri.Builder dirBuilder = new Uri.Builder()
                .scheme("https")
                .authority("www.google.com")
                .appendPath("dir")
                .appendPath("")
                .appendQueryParameter("api", "1")
                .appendQueryParameter("destination", route.get(1).getLatitude()
                        + "," + route.get(1).getLongitude());
        Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                dirBuilder.build());*/
        StringBuilder waypoints = new StringBuilder(route.get(1).getLatitude() + ","
                + route.get(1).getLongitude());
        if (route.size() > 3) {
            for (int i = 2; i < route.size() - 1; i++) {
                waypoints.append("|").append(route.get(i).getLatitude()).append(",").append(route.get(i).getLongitude());
            }
        }
        String url = "http://www.google.com/maps/dir/?api=1" +
                "&origin=" + route.get(0).getLatitude() + ","
                + route.get(0).getLongitude();
            url += "&waypoints=" + waypoints;
        url += "&destination=" + route.get(0).getLatitude()+","
                + route.get(0).getLongitude();
        url += "&travelmode=" + transit;
        Intent intent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(url));
        intent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");
        startActivity(intent);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        googleMap.getUiSettings().setZoomControlsEnabled(true);

        LatLng[] latLngs = new LatLng[route.size()];

        LatLngBounds.Builder builder = new LatLngBounds.Builder();

        latLngs[0] = new LatLng(route.get(0).getLatitude(), route.get(0).getLongitude());
        googleMap.addMarker(new MarkerOptions().position(latLngs[0]).title(route.get(0).getName()));

        for (int i = 1; i < route.size(); i++) {
            double lat = route.get(i).getLatitude();
            double lng = route.get(i).getLongitude();
            String title = route.get(i).getName();
            latLngs[i] = new LatLng(lat, lng);
            googleMap.addMarker(new MarkerOptions().position(latLngs[i]).title(title));
            PolylineOptions edge = new PolylineOptions().add(latLngs[i], latLngs[i - 1]).width(5).color(Color.BLUE);
            builder.include(latLngs[i]);
            googleMap.addPolyline(edge);
        }

        LatLngBounds bounds = builder.build();
        int padding = 200;
        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
        googleMap.animateCamera(cu);

        googleMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {

            @Override
            public View getInfoWindow(Marker arg0) {
                return null;
            }

            @Override
            public View getInfoContents(Marker marker) {

                Context mContext = getApplicationContext();

                LinearLayout info = new LinearLayout(mContext);
                info.setOrientation(LinearLayout.VERTICAL);

                TextView title = new TextView(mContext);
                title.setTextColor(Color.BLACK);
                title.setGravity(Gravity.CENTER);
                title.setTypeface(null, Typeface.BOLD);
                title.setText(marker.getTitle());

                TextView snippet = new TextView(mContext);
                snippet.setTextColor(Color.GRAY);
                snippet.setText(marker.getSnippet());
                snippet.setGravity(Gravity.CENTER);

                info.addView(title);
                info.addView(snippet);

                return info;
            }
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
            Intent intentSplash = new Intent(MapsActivity.this, SplashActivity.class);
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
            mapsIntent.putExtra("places", places);
            startActivity(mapsIntent);
            return null;
        }
    }
}
