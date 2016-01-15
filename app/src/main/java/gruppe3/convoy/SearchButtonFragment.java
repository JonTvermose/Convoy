package gruppe3.convoy;


import android.content.Intent;
import android.hardware.SensorEventListener;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.util.ArrayList;

import gruppe3.convoy.functionality.SingleTon;
import gruppe3.convoy.functionality.Spot;


/**
 * A simple {@link Fragment} subclass.
 */
public class SearchButtonFragment extends Fragment {

    public static Button search;

    public SearchButtonFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rod;
        if (SingleTon.nightMode){
            rod = inflater.inflate(R.layout.fragment_search_button_night, container, false);
        } else {
            rod = inflater.inflate(R.layout.fragment_search_button, container, false);
        }

        search = (Button) rod.findViewById(R.id.searchButton);
        Log.d("debug", "Search knap oprettet");
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AsyncTask<Void, Void, String>(){
                    @Override
                    protected String doInBackground(Void... params) {
                        if(SingleTon.spots != null) {
                            // Laver en liste af spots der matcher søgekritierne, baseret på den totale liste af spots.
                            SingleTon.searchedSpots = new ArrayList<Spot>();
                            for (Spot searchedSpot : SingleTon.spots) {
                                boolean mark = true;
                                if (SingleTon.adblue) {
                                    if (!searchedSpot.isAdblue()) {
                                        mark = false;
                                    }
                                }
                                if (SingleTon.food) {
                                    if (!searchedSpot.isFood()) {
                                        mark = false;
                                    }
                                }
                                if (SingleTon.wc) {
                                    if (!searchedSpot.isWc()) {
                                        mark = false;
                                    }
                                }
                                if (SingleTon.bed) {
                                    if (!searchedSpot.isBed()) {
                                        mark = false;
                                    }
                                }
                                if (SingleTon.bath) {
                                    if (!searchedSpot.isBath()) {
                                        mark = false;
                                    }
                                }
                                if (SingleTon.fuel) {
                                    if (!searchedSpot.isFuel()) {
                                        mark = false;
                                    }
                                }
                                if (SingleTon.roadTrain) {
                                    if (!searchedSpot.isRoadtrain()) {
                                        mark = false;
                                    }
                                }
                                if (mark) { // Hvis alle tjek er gået godt, så tilføjes spot til listen over de søgte spots
                                    SingleTon.searchedSpots.add(searchedSpot);
                                }
                            }
                            Log.d("Søgning", "Der blev fundet: " + SingleTon.searchedSpots.size() + " søgeresultater ud af: " + SingleTon.spots.size());
                        }

                        return null;
                    }

                    @Override
                    protected void onPostExecute(String msg) {
                        SingleTon.sensorManager.unregisterListener((SensorEventListener) getActivity()); // Vi afslutter sensorlytter når vi er i mapmode
                        Intent i = new Intent(getActivity(), GMapsFragment.class);
                        getActivity().startActivity(i);
                    }
                }.execute();
            }
        });

        return rod;
    }

}
