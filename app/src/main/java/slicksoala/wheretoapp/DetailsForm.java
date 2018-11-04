package slicksoala.wheretoapp;

import android.content.Intent;
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
    private Button generateButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details_form);
        returnTime = (EditText) findViewById(R.id.returnTime);
        radiusSpinner = findViewById(R.id.radiusSpinner);
        activitySpinner = findViewById(R.id.activitySpinner);
        travelSpinner = findViewById(R.id.travelSpinner);
        paceSpinner = findViewById(R.id.paceSpinner);
        //generateButton = (Button) findViewById(R.id.generate);
        RippleBackground rippleBackground = (RippleBackground) findViewById(R.id.content);
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
        /*generateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), Welcome.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }
        });*/

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
            Toast.makeText(Welcome.this, "First enable LOCATION ACCESS in settings.", Toast.LENGTH_LONG).show();
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
            YelpFusionPlacesListTask yftask = new YelpFusionPlacesListTask();
            masterList = yftask.execute(currLat, currLong, rad, act).get();
        } else {
            FoursquarePlacesListTask fstask = new FoursquarePlacesListTask();
            masterList = fstask.execute(currLat, currLong, rad, act).get();
        }

        kList.add(currPlace);
        for (int i = 0; i < k; i++) {
            kList.add(masterList.get(i));
        }

        GDistanceMatrixTask dmtask = new GDistanceMatrixTask();
        double[][] distMatrix = dmtask.execute(kList).get();

        HeldKarpTSP hk = new HeldKarpTSP();
        System.out.print(Arrays.deepToString(distMatrix));
        double optimalDuration = (double) hk.optimalRoute(distMatrix)[0];
        System.out.println(optimalDuration);

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
        for (int d = 1; d < 20; d++) {
            Toast.makeText(DetailsForm.this, route, Toast.LENGTH_LONG).show();
        }
    }
}
