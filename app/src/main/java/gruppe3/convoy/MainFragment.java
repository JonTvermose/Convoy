package gruppe3.convoy;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import gruppe3.convoy.functionality.MyLocation;
import gruppe3.convoy.functionality.SingleTon;


/**
 * A simple {@link Fragment} subclass.
 */
public class MainFragment extends Fragment {

    private ViewPager mPager;
    private PagerAdapter mPagerAdapter;
    private boolean session = false;

    public MainFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rod;
        if(SingleTon.nightMode){
            rod = inflater.inflate(R.layout.fragment_main_night, container, false);
        } else {
            rod = inflater.inflate(R.layout.fragment_main, container, false);
        }
        // Inflate the layout for this fragment


        System.out.println("MainFrag onCreate");

        // Instantiate a ViewPager and a PagerAdapter.
        mPager = (ViewPager) rod.findViewById(R.id.pager);
        mPagerAdapter = new MainFragmentPagerAdapter(getChildFragmentManager(), getActivity());
        mPager.setAdapter(mPagerAdapter);

        // Give the TabLayout the ViewPager
        TabLayout tabLayout = (TabLayout) rod.findViewById(R.id.sliding_tabs);
        tabLayout.setupWithViewPager(mPager);

        if(SingleTon.switchMode && SingleTon.session){
            // Hvis vi bare skal lave et farveskift
            SingleTon.switchMode=false;
            getFragmentManager()
                    .beginTransaction()
                    .setCustomAnimations(R.anim.fade_in, R.anim.fade_out)
                    .replace(R.id.mainBottomFragment, new SearchButtonFragment())
                    .commit();
        } else {
            if (SingleTon.session) {
                // Hvis appen er i en nuværende session behøver vi ikke se en progressbar og køre opstartsproces
                if (!SingleTon.powerSaving){
                    SingleTon.sensorManager.registerListener((SensorEventListener) getActivity(), SingleTon.accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
                }
                getFragmentManager()
                        .beginTransaction()
                        .setCustomAnimations(R.anim.fade_in, R.anim.fade_out)
                        .add(R.id.mainBottomFragment, new SearchButtonFragment())
                        .commit();
            } else {
                // Appen er lige startet
                // Tjek for internetforbindelse
                ConnectivityManager cm = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
                if (cm.getActiveNetworkInfo() == null) {
                    Log.d("Error", "Ingen internetforbindelse på opstart.");
                    SingleTon.isConnected = false;
                }
                // Hvis appen lige er blevet startet begyndes opstartsproces med en progressbar
                getFragmentManager()
                        .beginTransaction()
                        .add(R.id.mainBottomFragment, new ProgressFragment())
                        .commit();
                SingleTon.myLocation = new MyLocation();
                SingleTon.myLocation.startLocationService(getActivity()); // Starter stedbestemmelse
                Log.d("Stedbestemmelse", "App starter");
                // Følgende styrer animationen for ProgressBar på startskærmen når der loades data
                final Handler h = new Handler();
                h.postDelayed(new Runnable() {
                    int p = 10; // Tælles løbende op til 50
                    int d = 50; // Når location er opdateret sættes progress til 50%

                    @Override
                    public void run() {
                        if (MyLocation.POSUPDATED) {
                            // Hvis positionen er opdateret: opdater progressbar og start datahentning
                            if (d < 90) {
                                if(SingleTon.isConnected){
                                    ProgressFragment.progressBarTxt.setText(SingleTon.searchTxt2);
                                } else {
                                    ProgressFragment.progressBarTxt.setText(SingleTon.searchTxt2b);
                                }
                                ProgressFragment.progressBar.setProgress(d);
                                d++;
                                d++;
                            }
                            if (SingleTon.dataLoadDone) {
                                //Opstartsproces er færdig - start sensorlytter og skift progressbar ud med Search-knap
                                AutoCompleteFragment.autocompleteFragment.getView().setVisibility(View.VISIBLE);
                                SingleTon.session = true;
                                ProgressFragment.progressBar.setProgress(100);
                                if (!SingleTon.powerSaving) {
                                    SingleTon.sensorManager.registerListener((SensorEventListener) getActivity(), SingleTon.accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
                                }
                                if(SingleTon.spots == null || SingleTon.spots.size()==0){
                                    // Hvis spots er null eller tom giver det ingen mening at søge efter noget.
                                    ProgressFragment.progressBarTxt.setText("Error retrieving data. Please restart the app.");
                                    ProgressFragment.progressBarTxt.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            new AlertDialog.Builder(getActivity())
                                                    .setTitle("Error")
                                                    .setMessage("Could not connect to database.\nNo truck stops was found on your phone.\n\nPlease check your internet connection and try again.")
                                                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialog, int which) {
//                                                    getActivity().finish();
                                                            android.os.Process.killProcess(android.os.Process.myPid()); // "Dræb appen.
                                                        }
                                                    })
                                                    .show();
                                        }
                                    });
                                    SingleTon.session = false;
                                    new AlertDialog.Builder(getActivity())
                                            .setTitle("Error")
                                            .setMessage("Could not connect to database.\nNo truck stops was found on your phone.\n\nPlease check your internet connection and try again.")
                                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
//                                                    getActivity().finish();
                                                    android.os.Process.killProcess(android.os.Process.myPid()); // "Dræb appen.
                                                }
                                            })
                                            .show();
                                } else {
                                    getFragmentManager()
                                            .beginTransaction()
                                            .setCustomAnimations(R.anim.fade_in, R.anim.fade_out)
                                            .replace(R.id.mainBottomFragment, new SearchButtonFragment())
                                            .commit();
                                    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
                                    if(prefs.getBoolean("showTutorial", true)){
                                        Intent i = new Intent(getActivity(), Tutorial_akt.class);
                                        getActivity().startActivity(i);
                                        getActivity().overridePendingTransition(R.anim.fade_in2, R.anim.fade_out2);
                                    }
                                }
                            } else {
                                h.postDelayed(this, 100);
                            }
                        } else {
                            // Vent yderligere 100 ms indtil positionen er opdateret og opdater progressbar
                            h.postDelayed(this, 100);
                            p++;
                            if (p <= 50) {
                                ProgressFragment.progressBar.setProgress(p);
                            }
                        }
                    }
                }, 100);
            }

        }
        return rod;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        System.out.println("onActivityResult");
        AutoCompleteFragment.autocompleteFragment.onActivityResult(requestCode, resultCode, data);
    }

}
