package gruppe3.convoy;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.Switch;
import android.widget.TextView;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.location.places.ui.SupportPlaceAutocompleteFragment;
import com.google.android.gms.maps.model.LatLng;

import gruppe3.convoy.functionality.SingleTon;


/**
 * A simple {@link Fragment} subclass.
 */
public class AdvancedFragment extends Fragment implements PlaceSelectionListener {

    public static EditText dest;
    public static NumberPicker timer,minutter;
    public static Switch roadTrain;
    public static String[] hours,mins;
    static SupportPlaceAutocompleteFragment autocompleteFragment;


    public AdvancedFragment() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rod = inflater.inflate(R.layout.fragment_advanced, container, false);
//        dest = (EditText) rod.findViewById(R.id.destination_editText);
//        dest.setText(SingleTon.dest);


        // Retrieve the PlaceAutocompleteFragment.
        autocompleteFragment = (SupportPlaceAutocompleteFragment)
                getChildFragmentManager().findFragmentById(R.id.autocomplete);
        autocompleteFragment.getView().setVisibility(View.INVISIBLE);

        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                // TODO: Get info about the selected place.
                Log.i("auto", "Id: " + place.getId());
                Log.i("auto", "Name: " + place.getName());
                Log.i("auto", "Address: " + place.getAddress());
                Log.i("auto", "Pos: " + place.getLatLng());
                SingleTon.hasDest = true;
                SingleTon.destPos = new LatLng(place.getLatLng().latitude, place.getLatLng().longitude);

                Log.i("auto", "var hasDest: " + SingleTon.hasDest);
                Log.i("auto", "var destPos: " + SingleTon.destPos.toString());
                ((TextView) rod.findViewById(R.id.destHead)).setText(place.getAddress());
            }

            @Override
            public void onError(Status status) {
                // TODO: Handle the error.
                Log.i("auto", "An error occurred: " + status);

            }
        });


//        autocompleteFragment.setOnPlaceSelectedListener(this);


        timer = (NumberPicker) rod.findViewById(R.id.timer_numberPicker);
        hours = new String[24];
        for(int i=0; i<hours.length; i++)
            hours[i] = Integer.toString(i);
        timer.setMinValue(0);
        timer.setMaxValue(23);
        timer.setWrapSelectorWheel(true);
        timer.setDisplayedValues(hours);
        timer.setValue(SingleTon.timer);

        minutter = (NumberPicker) rod.findViewById(R.id.minutter_numberPicker);
        mins = new String[12];
        for(int i=0; i<mins.length; i++)
            mins[i] = Integer.toString(i*5);
        minutter.setMinValue(0);
        minutter.setMaxValue(11);
        minutter.setWrapSelectorWheel(true);
        minutter.setDisplayedValues(mins);
        minutter.setValue(SingleTon.minutter);

        roadTrain = (Switch) rod.findViewById(R.id.roadTrain_switch);
        roadTrain.setChecked(SingleTon.roadTrain);
        return rod;
    }


    @Override
    public void onPlaceSelected(Place place) {
        // TODO: Get info about the selected place.
        Log.i("auto", "Place: " + place.getName());
    }

    @Override
    public void onError(Status status) {
        // TODO: Handle the error.
        Log.i("auto", "An error occurred: " + status);
    }


}
