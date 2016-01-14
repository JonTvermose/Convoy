package gruppe3.convoy;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Toast;

import gruppe3.convoy.functionality.Serialisering;
import gruppe3.convoy.functionality.SingleTon;


public class Main extends FragmentActivity implements SensorEventListener {

    public static final String PREF_FILE_NAME = "ConvoyPrefs";

    private final int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private long lastShaken = System.currentTimeMillis();

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
                Toast.makeText(this, "Convoy requires access to Location services to function.", Toast.LENGTH_LONG);

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
            SingleTon.sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
            SingleTon.accelerometer = SingleTon.sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

            startApp();
        }
    }

    private void startApp(){
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

        System.out.println("requestCode: " + requestCode);

        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay!
                    startApp(); // Appen fortsætter
                } else {
                    // TODO - permission denied, boo! Disable the
                    Toast.makeText(this, "This app requires access to GPS.", Toast.LENGTH_LONG).show();
                    Log.d("Stedbestemmelse", "Brugeren gav ikke adgang til GPS. App afsluttes.");
                    this.finish();
                }
                return;
            }
            default: {
                // TODO - der er sket en fejl
                Toast.makeText(this, "An error occurred. Sorry... :(", Toast.LENGTH_LONG).show();
                Log.d("Stedbestemmelse", "Main.onRequestPermissionsResult fejlede. RequestKode var: " + requestCode);
            }
        }
    }

    @Override
    protected void onStop(){
        Log.d("Debug" , "Main.onStop() er kaldt. Appen er ikke aktiv");
        SingleTon.myLocation.stopLocationUpdates(); // Stopper opdateringen fra GPS/Network
        SingleTon.sensorManager.unregisterListener(this); // Stopper sensor lytning
        SharedPreferences.Editor prefs = PreferenceManager.getDefaultSharedPreferences(this).edit();
        prefs.putBoolean("saveData", SingleTon.saveData).apply();
        if(SingleTon.saveData){
            prefs.putBoolean("nightMode", SingleTon.nightMode).apply();
            prefs.putBoolean("food", SingleTon.food).apply();
            prefs.putBoolean("wc", SingleTon.wc).apply();
            prefs.putBoolean("bed", SingleTon.bed).apply();
            prefs.putBoolean("bath", SingleTon.bath).apply();
            prefs.putBoolean("fuel", SingleTon.fuel).apply();
            prefs.putBoolean("adblue", SingleTon.adblue).apply();
            prefs.putBoolean("roadTrain", SingleTon.roadTrain).apply();
            prefs.putBoolean("powerSaving", SingleTon.powerSaving).apply();
            prefs.putString("speedSetting", Double.toString(SingleTon.speedSetting)).apply();
        }
        super.onStop();
    }

    @Override
    protected void onResume(){
        Log.d("Debug", "Main.onResume() er kaldt. Appen er aktiv!");
        super.onResume();
        SingleTon.myLocation.startLocationService(this);
        lastShaken = System.currentTimeMillis();
        // Start først sensorlytter hvis vi ikke er igang med at loade data
        if (SingleTon.accelerometer!= null && SingleTon.dataLoadDone && !SingleTon.powerSaving){
            SingleTon.sensorManager.registerListener(this, SingleTon.accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
            Log.d("Sensor", "Starter sensorlytter");
        }
    }

    @Override
    public void onSensorChanged(SensorEvent e) {
        double g=9.80665; // normal tyngdeaccelerationen
        double sum=Math.abs(e.values[0])+Math.abs(e.values[1])+Math.abs(e.values[2]);
        long cTime = System.currentTimeMillis();
        if (sum>3*g && cTime - lastShaken > 2000) {
            lastShaken = cTime;
            if (SingleTon.nightMode){
                SingleTon.nightMode = false;
            } else {
                SingleTon.nightMode = true;
            }

            // Genskaber appen i den nye mode
            Log.d("Sensor", "Skifter day/night mode! " + SingleTon.nightMode);
            SingleTon.switchMode = true;
            getSupportFragmentManager()
                    .beginTransaction()
                    .setCustomAnimations(R.animator.fade_in, R.animator.fade_out)
                    .replace(R.id.MainFragment, new MainFragment())
                    .addToBackStack(null)
                    .commit();
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

}
