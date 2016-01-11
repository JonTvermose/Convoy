package gruppe3.convoy;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Toast;

import gruppe3.convoy.functionality.MyLocation;
import gruppe3.convoy.functionality.SingleTon;


public class Main extends FragmentActivity {

    public static final String PREF_FILE_NAME = "ConvoyPrefs";
    private final int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Hvis vi ikke har permissions skal vi bede om permission
            Log.d("Access", "Mangler adgang til ACCESS_FINE_LOCATION");

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
                // TODO
                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);

                // MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        } else {
            // Hvis vi har tilladelse i orden startes maps bare
            Log.d("Access", "ACCESS_FINE_LOCATION er ok");
            startApp();
        }

    }

    private void startApp(){
        Log.d("Stedbestemmelse","App starter");

//        SingleTon.myLocation = new MyLocation();
//        SingleTon.myLocation.startLocationService(this); // Starter stedbestemmelse

        setContentView(R.layout.activity_main);
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.MainFragment, new MainFragment())
                .setTransitionStyle(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .commit();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay!
                    startApp(); // Appen fortsætter
                } else {
                    // TODO - permission denied, boo! Disable the
                    Toast.makeText(this, "This app requires access to GPS", Toast.LENGTH_LONG).show();
                    Log.d("Stedbestemmelse", "Brugeren gav ikke adgang til GPS. App afsluttes.");
                    this.finish();
                }
                return;
            }
            default: {
                // TODO - der er sket en fejl
                Toast.makeText(this, "An error occurred. Sorry...", Toast.LENGTH_LONG).show();
                Log.d("Stedbestemmelse", "Main.onRequestPermissionsResult fejlede. RequestKode var: " + requestCode);
            }
        }
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
        if (SingleTon.myLocation != null){
            SingleTon.myLocation.onResume(); // Start opdatering fra GPS
        }
    }
}
