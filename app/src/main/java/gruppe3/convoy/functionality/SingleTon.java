package gruppe3.convoy.functionality;

import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Handler;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

/**
 * Created by Jon on 06/01/2016.
 */
public class SingleTon extends Application {

    private static SingleTon ourInstance = new SingleTon();
    public static ArrayList<Spot> spots, searchedSpots;
    public static MyLocation myLocation;
    public static int timer,minutter;
    public static final String searchTxt1 = "Finding Location", searchTxt2 = "Connecting to Database", searchTxt2b = "Reading saved Data", searchTxt3 = "Connected. Fetching data";
    public static Boolean food, wc, bed, bath, fuel, adblue, roadTrain = false, dataLoadDone = false, dataLoading = false, nightMode, saveData, switchMode = false, powerSaving = false, session = false;
    public static boolean hasDest;
    public static LatLng destPos, restPos;
    public static String destAdress = "Your destination";
    public static Sensor accelerometer;
    public static SensorManager sensorManager;
    BoundService.LocalBinder mBinder;
    public static BoundService mService;
    public static ServiceConnection mConnection;
    public static boolean mBound = false;
    public static double speedSetting;
    public static boolean hentetLokal=false;
    public static boolean hentetDb=false;
    public static boolean isConnected = true;
    public static long lastUpdated;

    @Override
    public void onCreate(){
        super.onCreate();
        Log.d("Data", "SingleTon OnCreate");

        System.out.println("SingleTon onCreate");
        SingleTon.sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        SingleTon.accelerometer = SingleTon.sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        startBinding();
        Intent intent = new Intent(this, BoundService.class);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        Log.d("Debug", "Preference Manager er startet");
        SingleTon.saveData = prefs.getBoolean("saveData", true);
        SingleTon.lastUpdated = prefs.getLong("lastUpdated", -1);
        if(SingleTon.saveData){
            SingleTon.nightMode = prefs.getBoolean("nightMode", false);
            SingleTon.food = prefs.getBoolean("food", false);
            SingleTon.wc = prefs.getBoolean("wc", false);
            SingleTon.bed = prefs.getBoolean("bed", false);
            SingleTon.bath = prefs.getBoolean("bath", false);
            SingleTon.fuel = prefs.getBoolean("fuel", false);
            SingleTon.adblue = prefs.getBoolean("adblue", false);
            SingleTon.roadTrain = prefs.getBoolean("roadTrain", false);
            SingleTon.powerSaving = prefs.getBoolean("powerSaving", false);
            SingleTon.speedSetting = Double.valueOf(prefs.getString("speedSetting", "1.3"));
        } else {
            SingleTon.nightMode = false;
            SingleTon.food = false;
            SingleTon.wc = false;
            SingleTon.bath = false;
            SingleTon.bed = false;
            SingleTon.fuel = false;
            SingleTon.adblue = false;
            SingleTon.roadTrain = false;
            SingleTon.powerSaving = false;
            SingleTon.speedSetting = 1.3;
        }
        if (SingleTon.myLocation != null){
            SingleTon.myLocation.onResume(); // Start opdatering fra GPS
        }
    }

    public static void fetchData() {
        dataLoading = true;
        hentSpotsLocal("lokaleSpots");
    }

    public void startBinding(){
        /** Defines callbacks for service binding, passed to bindService() */
        mConnection = new ServiceConnection() {

            @Override
            public void onServiceConnected(ComponentName className,
                                           IBinder service) {
                // We've bound to LocalService, cast the IBinder and get LocalService instance
                mBinder = (BoundService.LocalBinder) service;

                mService = mBinder.getService();
                System.out.println("mService"+mService);
                mBound = true;
                System.out.println("mBound"+mBound);
            }

            @Override
            public void onServiceDisconnected(ComponentName arg0) {
                mBound = false;
            }
        };
    }

    public void dropBinding(){
        unbindService(mConnection);
    }

    public static void hentSpotsLocal(final String name){
        final Handler h = new Handler();
        h.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (SingleTon.mBound) {
                    mService.hent(name);
                } else {
                    h.postDelayed(this, 100);
                }
            }
        }, 100);
    }

    public static void gemSpotsLocal(final String name, final ArrayList spotsListe){
        final Handler h = new Handler();
        h.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (SingleTon.mBound) {
                    mService.gem(spotsListe, name);
                } else {
                    h.postDelayed(this, 100);
                }
            }
        }, 100);
    }

    /**
     * Kaldes når brugeren tilføjer et spot. Spottet skal uploades til REST serveren.
     * @param newSpot
     */
    public static void addSpot(final Spot newSpot) {
        final Handler h = new Handler();
        h.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (SingleTon.mBound) {
                    mService.uploadSpot(newSpot);
                } else {
                    h.postDelayed(this, 100);
                }
            }
        }, 100);
    }
}
