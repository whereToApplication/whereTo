package slicksoala.wheretoapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionButton;
import com.skyfishjy.library.RippleBackground;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.concurrent.ExecutionException;

public class Welcome extends AppCompatActivity implements View.OnClickListener{

    final String radiusW = "1000";
    final String radiusD = "16093";
    final String radiusT = "48280";

    private ActivityDo activitySelect = null;
    private TravelType travelTypeSelect = null;
    private String currLat, currLong;
    private String rad = "0";
    ArrayList<Place> gplacesList, fsplacesList, yplacesList;
    ArrayList<Place> placesList;
    HashMap<String, Place> places;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        RippleBackground rippleBackground = (RippleBackground) findViewById(R.id.content);
        rippleBackground.startRippleAnimation();

        ImageView goBtn = (ImageView) findViewById(R.id.centerImage);
        FloatingActionButton walkBtn = (FloatingActionButton) findViewById(R.id.walkFAM);
        FloatingActionButton driveBtn = (FloatingActionButton) findViewById(R.id.driveFAM);
        FloatingActionButton travBtn = (FloatingActionButton) findViewById(R.id.travelFAM);
        FloatingActionButton ptgBtn = (FloatingActionButton) findViewById(R.id.ptgACT);
        FloatingActionButton ttdBtn = (FloatingActionButton) findViewById(R.id.ttdACT);
        FloatingActionButton steBtn = (FloatingActionButton) findViewById(R.id.steACT);

        goBtn.setOnClickListener(this);
        walkBtn.setOnClickListener(this);
        driveBtn.setOnClickListener(this);
        travBtn.setOnClickListener(this);
        ptgBtn.setOnClickListener(this);
        ttdBtn.setOnClickListener(this);
        steBtn.setOnClickListener(this);
    }

    public void goAction() throws ExecutionException, InterruptedException {
        /*if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(Welcome.this, "First enable LOCATION ACCESS in settings.", Toast.LENGTH_LONG).show();
            return;
        }*/
        /*LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        currLat = Double.toString(location.getLatitude());
        currLong = Double.toString(location.getLongitude());*/
        currLat = "33.78508547";
        currLong = "-84.3879824";

        if (travelTypeSelect == TravelType.WALK)
            rad = radiusW;
        else if (travelTypeSelect == TravelType.DRIVE)
            rad = radiusD;
        else
            rad = radiusT;

        GooglePlacesListTask gtask = new GooglePlacesListTask();
        gplacesList = gtask.execute(currLat, currLong, rad, activitySelect.toString()).get();
        Place placeToGo = (Place) gplacesList.get(new Random().nextInt(gplacesList.size()));
        System.out.println("GOOGLE PLACE NAME:" + placeToGo.getName());

        FoursquarePlacesListTask fstask = new FoursquarePlacesListTask();
        fsplacesList = fstask.execute(currLat, currLong, rad, activitySelect.toString()).get();
        Place placeToGo2 = (Place) fsplacesList.get(new Random().nextInt(fsplacesList.size()));
        System.out.println("FOURSQUARE PLACE NAME:" + placeToGo2.getName());

        YelpFusionPlacesListTask ytask = new YelpFusionPlacesListTask();
        yplacesList = ytask.execute(currLat, currLong, rad, activitySelect.toString()).get();
        Place placeToGo3 = (Place) yplacesList.get(new Random().nextInt(yplacesList.size()));
        System.out.println("YELP FUSION PLACE NAME:" + placeToGo3.getName());

        /*GooglePlacesListTask gtask = new GooglePlacesListTask();
        placesList = gtask.execute(currLat, currLong, rad, activitySelect.toString()).get();

        FoursquarePlacesListTask fstask = new FoursquarePlacesListTask();
        placesList.addAll(fstask.execute(currLat, currLong, rad, activitySelect.toString()).get());

        YelpFusionPlacesListTask ytask = new YelpFusionPlacesListTask();
        placesList.addAll(ytask.execute(currLat, currLong, rad, activitySelect.toString()).get());

        for (Place p : placesList) {
            if (places.containsKey(p.getName())) {
                if (!p.getRating().equals("")) {
                    places.put(p.getName(), p);
                }
            } else {
                places.put(p.getName(), p);
            }
        }
        System.out.println(places.size());*/
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id){
            case R.id.centerImage:
                if (activitySelect == null && travelTypeSelect == null) {
                    Toast.makeText(Welcome.this, "Please select an activity and range!", Toast.LENGTH_LONG).show();
                } else if (activitySelect == null) {
                    Toast.makeText(Welcome.this, "Please select an activity!", Toast.LENGTH_LONG).show();
                } else if (travelTypeSelect == null) {
                    Toast.makeText(Welcome.this, "Please select a range!", Toast.LENGTH_LONG).show();
                } else {
                    try {
                        goAction();
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                break;
            case R.id.walkFAM:
                Toast.makeText(Welcome.this, TravelType.WALK.toString(), Toast.LENGTH_SHORT).show();
                travelTypeSelect = TravelType.WALK;
                break;
            case R.id.driveFAM:
                Toast.makeText(Welcome.this, TravelType.DRIVE.toString(), Toast.LENGTH_SHORT).show();
                travelTypeSelect = TravelType.DRIVE;
                break;
            case R.id.ptgACT:
                Toast.makeText(Welcome.this, ActivityDo.SIGHTSEE.toString(), Toast.LENGTH_SHORT).show();
                activitySelect = ActivityDo.SIGHTSEE;
                break;
            case R.id.ttdACT:
                Toast.makeText(Welcome.this, ActivityDo.WANDER.toString(), Toast.LENGTH_SHORT).show();
                activitySelect = ActivityDo.WANDER;;
                break;
            case R.id.steACT:
                Toast.makeText(Welcome.this, ActivityDo.EAT.toString(), Toast.LENGTH_SHORT).show();
                activitySelect = ActivityDo.EAT;
                break;
        }
    }

}
