package gruppe3.convoy;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;

import gruppe3.convoy.functionality.MyLocation;
import gruppe3.convoy.functionality.SingleTon;


public class Main extends FragmentActivity {

    public static final String PREF_FILE_NAME = "ConvoyPrefs";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SingleTon.myLocation = new MyLocation();
        SingleTon.myLocation.startLocationService(this); // Starter stedbestemmelse

        setContentView(R.layout.activity_main);
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.MainFragment, new MainFragment())
                .setTransitionStyle(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .commit();
    }

    @Override
    protected void onStop(){
        SingleTon.myLocation.stopLocationUpdates(); // Stopper opdateringen fra GPS/Network
        SharedPreferences.Editor prefs = PreferenceManager.getDefaultSharedPreferences(this).edit();
        prefs.putBoolean("food", SingleTon.food).apply();
        prefs.putBoolean("wc", SingleTon.wc).apply();
        prefs.putBoolean("bed", SingleTon.bed).apply();
        prefs.putBoolean("bath", SingleTon.bath).apply();
        prefs.putBoolean("fuel", SingleTon.fuel).apply();
        prefs.putBoolean("adblue", SingleTon.adblue).apply();
        super.onStop();
    }

    @Override
    protected void onStart(){
        super.onStart();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        Log.d("Error", "Preference Manager er startet");
        SingleTon.food = prefs.getBoolean("food", false);
        SingleTon.wc = prefs.getBoolean("wc", false);
        SingleTon.bed = prefs.getBoolean("bed", false);
        SingleTon.bath = prefs.getBoolean("bath", false);
        SingleTon.fuel = prefs.getBoolean("fuel", false);
        SingleTon.adblue = prefs.getBoolean("adblue", false);
        SingleTon.myLocation.onResume(); // Start opdatering fra GPS
    }
}
