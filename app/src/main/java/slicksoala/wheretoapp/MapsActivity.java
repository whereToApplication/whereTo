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
import java.util.ArrayList;
import java.util.Collections;

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

    @Override
    public void onBackPressed() {
        Intent backHomeIntent = new Intent(this, HomeScreenActivity.class);
        startActivity(backHomeIntent);
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

        Intent routing = new Intent(this, RoutingSplash.class);
        routing.putExtra("places", places);
        routing.putExtra("kList", kList);
        routing.putExtra("transit", transit);
        startActivity(routing);
    }

    public void onStartJourney(View view) {
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
}
