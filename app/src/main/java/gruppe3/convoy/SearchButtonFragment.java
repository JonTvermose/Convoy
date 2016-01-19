package gruppe3.convoy;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.hardware.SensorEventListener;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import java.util.ArrayList;

import gruppe3.convoy.functionality.SingleTon;
import gruppe3.convoy.functionality.Spot;


/**
 * A simple {@link Fragment} subclass.
 */
public class SearchButtonFragment extends Fragment {

    public static Button search;
    public static ProgressDialog progressDialog;

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
                search.setEnabled(false); // Deaktiver så der ikke trykkes flere gange i samme "tryk"
                // Tjek for internetforbindelse
                ConnectivityManager cm = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
                if (cm.getActiveNetworkInfo() == null) {
                    Log.d("Error", "Ingen internetforbindelse på søge tryk.");
                    Toast.makeText(getActivity(), "You have no internet connection!", Toast.LENGTH_SHORT).show();
                    return;
                }
                progressDialog = ProgressDialog.show(getActivity(), "Building Map","Finding spots...", true);

                new AsyncTask<Void, Void, String>() {
                    @Override
                    protected String doInBackground(Void... params) {
                        if (SingleTon.spots != null) {
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
                        progressDialog.setMessage("Found " + SingleTon.searchedSpots.size() + " spots. Retrieving map...");

                        return null;
                    }

                    @Override
                    protected void onPostExecute(String msg) {
                        SingleTon.sensorManager.unregisterListener((SensorEventListener) getActivity()); // Vi afslutter sensorlytter når vi er i mapmode
                        Intent i = new Intent(getActivity(), GMapsFragment.class);
                        getActivity().startActivity(i);
                        getActivity().overridePendingTransition(R.animator.fade_in2, R.animator.fade_out2);
                    }
                }.execute();
            }
        });

        return rod;
    }

}
