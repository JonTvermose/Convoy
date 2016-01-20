package gruppe3.convoy;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
    private static TextView autotekst;
    private static ImageView autoClose;
    private static LinearLayout autoReplace;


    public AutoCompleteFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rod = inflater.inflate(R.layout.fragment_auto_complete, container, false);

        autotekst = (TextView) rod.findViewById(R.id.autoText);
        autoClose = (ImageView) rod.findViewById(R.id.autoClose);
        autoReplace = (LinearLayout) rod.findViewById(R.id.autoReplace);
        autoReplace.setVisibility(View.INVISIBLE);

        System.out.println("Auto onCreate");
        autocompleteFragment = (SupportPlaceAutocompleteFragment) getChildFragmentManager().findFragmentById(R.id.autocomplete);
        autocompleteFragment.setOnPlaceSelectedListener(this);

        if (SingleTon.dataLoadDone){
            autocompleteFragment.getView().setVisibility(View.VISIBLE);
        } else {
            autocompleteFragment.getView().setVisibility(View.INVISIBLE);
        }

        autoClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SingleTon.hasDest=false;
                AdvancedFragment.disableNumberPicker();
                SearchButtonFragment.search.setText("Find Truck Stop");
                getFragmentManager()
                        .beginTransaction()
                        .replace(R.id.autocompleteholder, new AutoCompleteFragment())
                        .commit();
            }
        });
        return rod;
    }

    @Override
    public void onPlaceSelected(Place place) {
        SingleTon.hasDest = true;
        SingleTon.destPos = new LatLng(place.getLatLng().latitude, place.getLatLng().longitude);
        SingleTon.destAdress = place.getName().toString();
        SearchButtonFragment.search.setText("Find Truck Stop near destination"); // Ændrer tekst på search-knap

        autocompleteFragment.getView().setVisibility(View.INVISIBLE);
        autoReplace.setVisibility(View.VISIBLE);

        autotekst.setText(place.getName());
        AdvancedFragment.enableNumberPicker();
    }

    @Override
    public void onError(Status status) {

    }

}
