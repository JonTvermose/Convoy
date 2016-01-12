package gruppe3.convoy;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.Switch;
import android.widget.TextView;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.location.places.ui.SupportPlaceAutocompleteFragment;
import com.google.android.gms.maps.model.LatLng;

import gruppe3.convoy.functionality.SingleTon;


/**
 * A simple {@link Fragment} subclass.
 */
public class AdvancedFragment extends Fragment implements NumberPicker.OnValueChangeListener, CompoundButton.OnCheckedChangeListener {

    private NumberPicker timer,minutter;
    private Switch roadTrain;
    private String[] hours,mins;
    private View rod;
    private LinearLayout line;

    public static SupportPlaceAutocompleteFragment autocompleteFragment;

    public AdvancedFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rod = inflater.inflate(R.layout.fragment_advanced, container, false);
        autocompleteFragment = (SupportPlaceAutocompleteFragment)
                getChildFragmentManager().findFragmentById(R.id.autocomplete);
        autocompleteFragment.getView().setVisibility(View.INVISIBLE);

        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                // TODO: Get info about the selected place.
                Log.i("Advanced", "Id: " + place.getId());
                Log.i("Advanced", "Name: " + place.getName());
                Log.i("Advanced", "Address: " + place.getAddress());
                Log.i("Advanced", "Pos: " + place.getLatLng());
                SingleTon.hasDest = true;
                SingleTon.destPos = new LatLng(place.getLatLng().latitude, place.getLatLng().longitude);
                SingleTon.destAdress = place.getName().toString();

                Log.i("Advanced", "var hasDest: " + SingleTon.hasDest);
                Log.i("Advanced", "var destPos: " + SingleTon.destPos.toString());
                ((TextView) rod.findViewById(R.id.destHead)).setText(place.getAddress());

                // "Aktiver" scroll-hjulet med antal tid tilbage
                line.setAlpha(1);
                timer.setEnabled(true);
                minutter.setEnabled(true);
            }

            @Override
            public void onError(Status status) {
                // TODO: Handle the error.
                Log.i("Advanced", "An error occurred: " + status);
            }
        });

        timer = (NumberPicker) rod.findViewById(R.id.timer_numberPicker);
        hours = new String[24];
        for(int i=0; i<hours.length; i++)
            hours[i] = Integer.toString(i);
        timer.setMinValue(0);
        timer.setMaxValue(23);
        timer.setWrapSelectorWheel(true);
        timer.setDisplayedValues(hours);
        timer.setValue(SingleTon.timer);
        timer.setEnabled(false); // Låser funktionen indtil der er indtastet en gyldig destination
        timer.setOnValueChangedListener(this);

        minutter = (NumberPicker) rod.findViewById(R.id.minutter_numberPicker);
        mins = new String[12];
        for(int i=0; i<mins.length; i++)
            mins[i] = Integer.toString(i * 5);
        minutter.setMinValue(0);
        minutter.setMaxValue(11);
        minutter.setWrapSelectorWheel(true);
        minutter.setDisplayedValues(mins);
        minutter.setValue(SingleTon.minutter);
        minutter.setEnabled(false); // Låser funktionen indtil der er indtastet en gyldig destination
        minutter.setOnValueChangedListener(this);

        line = (LinearLayout) rod.findViewById(R.id.linje2);
        line.setAlpha(0.5f);

        roadTrain = (Switch) rod.findViewById(R.id.roadTrain_switch);
        roadTrain.setChecked(SingleTon.roadTrain);
        roadTrain.setOnCheckedChangeListener(this);
        return rod;
    }

    @Override
    public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
        if(picker==minutter){
            SingleTon.minutter = Integer.valueOf(mins[newVal]);
            Log.d("Advanced", "Minutter er sat til: " + mins[newVal]);
        } else if (picker == timer){
            SingleTon.timer = Integer.valueOf(hours[newVal]);
            Log.d("Advanced", "Timer er sat til: " + hours[newVal]);
        }
    }

    @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        Log.d("Advanced", "RoadTrain er sat til: " + isChecked);
        SingleTon.roadTrain = isChecked;
    }
}
