package slicksoala.wheretoapp;

import android.graphics.Color;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.getUiSettings().setZoomControlsEnabled(true);

        ArrayList<Place> route = (ArrayList<Place>) getIntent().getSerializableExtra("route");

        LatLng[] latLngs = new LatLng[route.size()];

        latLngs[0] = new LatLng(route.get(0).getLatitude(), route.get(0).getLongitude());
        mMap.addMarker(new MarkerOptions().position(latLngs[0]).title(route.get(0).getName()));

        double avlat = 0, avlng = 0;
        for (int i = 1; i < route.size(); i++) {
            double lat = route.get(i).getLatitude();
            avlat += lat;
            double lng = route.get(i).getLongitude();
            avlng += lng;
            String title = route.get(i).getName();
            latLngs[i] = new LatLng(lat, lng);
            mMap.addMarker(new MarkerOptions().position(latLngs[i]).title(title));
            PolylineOptions edge = new PolylineOptions().add(latLngs[i], latLngs[i - 1]).width(5).color(Color.BLUE);
            mMap.addPolyline(edge);
        }
        avlat /= route.size();
        avlng /= route.size();
        /*mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(avlat, avlng)));
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(new LatLng(avlat, avlng))      // Sets the center of the map to Mountain View
                .zoom(16)                   // Sets the zoom
                .bearing(90)                // Sets the orientation of the camera to east
                .tilt(30)                   // Sets the tilt of the camera to 30 degrees
                .build();                   // Creates a CameraPosition from the builder
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));*/
        mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(avlat, avlng)));
    }
}
