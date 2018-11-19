package slicksoala.wheretoapp;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import info.hoang8f.android.segmented.SegmentedGroup;

@SuppressWarnings("AssignmentToMethodParameter")
class PreferencesListAdapter extends ArrayAdapter<Place> implements RadioGroup.OnCheckedChangeListener {
    private final Context mContext;
    private final int mResource;
    private Place place;
    private int selected = -1;

    PreferencesListAdapter(@NonNull Context context, List<Place> objects) {
        super(context, R.layout.layout_preferenceitem, objects);
        mContext = context;
        mResource = R.layout.layout_preferenceitem;
    }
    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        place = Objects.requireNonNull(getItem(position));
        String qformat1 = "Do you like ";
        String category = place.getCategory();
        String qformat2 = "?";
        LayoutInflater inflater = LayoutInflater.from(mContext);
        convertView = inflater.inflate(mResource, parent, false);

        TextView prefText = convertView.findViewById(R.id.prefqTxt);
        prefText.setText(qformat1 + category + qformat2);

        RadioButton yesBtn = convertView.findViewById(R.id.yesBtn);
        RadioButton noBtn = convertView.findViewById(R.id.noBtn);

        SegmentedGroup radiusGroup = convertView.findViewById(R.id.prefGroup);
        radiusGroup.setOnCheckedChangeListener(this);
        if (place.getUserPref() > 0.5)
            yesBtn.setChecked(true);
        else if (place.getUserPref() < 0.5)
            noBtn.setChecked(true);

        return convertView;
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (checkedId) {
            case R.id.yesBtn:
                place.setUserPref(1.0);
                break;
            case R.id.noBtn:
                place.setUserPref(0.0);
                break;
        }
    }
}
