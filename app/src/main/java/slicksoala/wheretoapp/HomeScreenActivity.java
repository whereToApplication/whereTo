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
        } else {
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

            int maxRad = travelType.getAvgSpeed() * availableTime/4;

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
        }
    }
}
