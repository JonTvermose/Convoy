package gruppe3.convoy;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import gruppe3.convoy.functionality.MyLocation;


public class Main extends FragmentActivity {

    public static String dest,maxSpeed,roadTrain;
    public static int timer,minutter;
    public static Boolean food=false,
                    wc=false,
                    bed=false,
                    bath=false,
                    fuel=false,
                    adblue=false;
    public static MyLocation myLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        myLocation = new MyLocation();
        myLocation.startLocationService(this); // Starter stedbestemmelse

        setContentView(R.layout.activity_main);
        getSupportFragmentManager()
                        .beginTransaction()
                        .add(R.id.MainFragment, new AppFragment())
                        .setTransitionStyle(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                        .commit();
    }

    @Override
         protected void onStop(){
        myLocation.stopLocationUpdates(); // Stopper opdateringen fra GPS/Network
        super.onStop();
    }

    @Override
    protected void onStart(){
        myLocation.onResume(); // Start opdatering fra GPS
        super.onStart();
    }
}
