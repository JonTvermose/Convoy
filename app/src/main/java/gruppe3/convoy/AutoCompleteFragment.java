package gruppe3.convoy;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
public class AutoCompleteFragment extends Fragment implements PlaceSelectionListener {

    private View rod;
    public static SupportPlaceAutocompleteFragment autocompleteFragment;
    private Place place;


    public AutoCompleteFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rod = inflater.inflate(R.layout.fragment_auto_complete, container, false);

        System.out.println("Auto onCreate");
        autocompleteFragment = (SupportPlaceAutocompleteFragment)
                getChildFragmentManager().findFragmentById(R.id.autocomplete);
//        System.out.println(autocompleteFragment);
//            autocompleteFragment.getView().setVisibility(View.INVISIBLE); // Skal først være tilgængelig når startup er færdig
            autocompleteFragment.setOnPlaceSelectedListener(this);

        return rod;
    }

    @Override
    public void onPlaceSelected(Place place) {
        Log.i("Advanced", "Id: " + place.getId());
        Log.i("Advanced", "Name: " + place.getName());
        Log.i("Advanced", "Address: " + place.getAddress());
        Log.i("Advanced", "Pos: " + place.getLatLng());
        SingleTon.hasDest = true;
        SingleTon.destPos = new LatLng(place.getLatLng().latitude, place.getLatLng().longitude);
        SingleTon.destAdress = place.getName().toString();
        SearchButtonFragment.search.setText("Find Truck Spot near destination"); // Ændrer tekst på search-knap

        Log.i("Advanced", "var hasDest: " + SingleTon.hasDest);
        Log.i("Advanced", "var destPos: " + SingleTon.destPos.toString());
        if(SingleTon.nightMode){
            System.out.println("NightMode "+getParentFragment().getView().findViewById(R.id.destHead));
            ((TextView) getParentFragment().getView().findViewById(R.id.destHead)).setText(place.getAddress());
        } else {
            System.out.println("DayMode "+getParentFragment().getView().findViewById(R.id.destHead));
            ((TextView) getParentFragment().getView().findViewById(R.id.destHead)).setText(place.getAddress());
        }

//        enableNumberPicker(); // Aktiver numberpicker
    }

    @Override
    public void onError(Status status) {

    }
}
