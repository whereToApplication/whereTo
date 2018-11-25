package slicksoala.wheretoapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;

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
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

import info.hoang8f.android.segmented.SegmentedGroup;

public class UserPreferencesActivity extends AppCompatActivity {
    private ArrayList<Integer> selectedPrefs;
    private ArrayList<Place> places;
    private String transit;
    SharedPreferences preferences;
    SharedPreferences.Editor editor;
    double avgPref = 0;
    Place currPlace;
    int k;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_preferences_screen);
        ListView preferencesListView = findViewById(R.id.prefList);
        ImageView cont = findViewById(R.id.contBtn);

        Intent intent = getIntent();
        Bundle extra = intent.getBundleExtra("extra");
        places = (ArrayList<Place>) extra.getSerializable("places");
        currPlace = (Place) extra.getSerializable("currPlace");
        k = intent.getIntExtra("k", 0);
        transit = intent.getStringExtra("transit");
        selectedPrefs = new ArrayList<>(places.size());
        for (int i = 0; i < places.size(); i++) {
            selectedPrefs.add(-1);
        }

        preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        editor = preferences.edit();
        HashSet<String> categories = new HashSet<>();
        for (Place place : places) {
            ArrayList<String> placeCategories = place.getCategories();
            for (String cat : placeCategories) {
                if (!preferences.contains(cat))
                    categories.add(cat);
            }
        }
        if (categories.size() == 0) {
            doKRouting();
        }
        List<String> cats = new ArrayList<String>(categories);
        PreferenceListAdapter prefAdapter = new PreferenceListAdapter(
                this, cats);
        preferencesListView.setAdapter(prefAdapter);

        cont.setOnClickListener(v -> {
            doKRouting();
        });
    }

    public void doKRouting() {
        ArrayList<Place> kList = new ArrayList<>();
        kList.add(currPlace);
        updateUserPref();
        Collections.shuffle(places);

        int i = 0;
        int pos = 0;
        while (i < Math.min(k, places.size()) && pos < places.size()) {
            if (places.get(pos).getUserPref() >= avgPref) {
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

    public void updateUserPref() {
        for (Place place : places) {
            ArrayList<String> placeCategories = place.getCategories();
            int prefScore = 0;
            for (String cat : placeCategories) {
                prefScore += preferences.getInt(cat, 0);
            }
            place.setUserPref(prefScore);
            avgPref += prefScore;
        }
        avgPref /= places.size();
    }

    @Override
    public void onBackPressed() {
        Intent backHomeIntent = new Intent(this, HomeScreenActivity.class);
        startActivity(backHomeIntent);
    }

    class PreferenceListAdapter extends ArrayAdapter<String>{
        private final Context mContext;
        private final int mResource;
        private Place place;
        private String category;
        private int position;

        PreferenceListAdapter(@NonNull Context context, List<String> objects) {
            super(context, R.layout.layout_preferenceitem, objects);
            mContext = context;
            mResource = R.layout.layout_preferenceitem;
        }
        @NonNull
        @Override
        public View getView(int position, View convertView, @NonNull ViewGroup parent) {
            this.position = position;
            this.category = getItem(position);
            String qformat1 = "Do you like ";
            String qformat2 = "?";
            LayoutInflater inflater = LayoutInflater.from(mContext);
            convertView = inflater.inflate(mResource, parent, false);

            TextView prefText = convertView.findViewById(R.id.prefqTxt);
            prefText.setText(qformat1 + category + qformat2);

            RadioButton yesBtn = convertView.findViewById(R.id.yesBtn);
            yesBtn.setOnClickListener(v -> {
                selectedPrefs.set(position, 0);
                editor.putInt(category, 1);
                editor.apply();
            });
            RadioButton noBtn = convertView.findViewById(R.id.noBtn);
            noBtn.setOnClickListener(v -> {
                selectedPrefs.set(position, 1);
                editor.putInt(category, -1);
                editor.apply();
            });

            SegmentedGroup ynGroup = convertView.findViewById(R.id.prefGroup);
            if (selectedPrefs.get(position) != -1)
                ((RadioButton)ynGroup.getChildAt(selectedPrefs.get(position))).setChecked(true);
            return convertView;
        }

    }
}