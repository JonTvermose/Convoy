package gruppe3.convoy.functionality;

import android.app.Application;
import android.location.LocationListener;
import android.location.LocationManager;

import java.util.ArrayList;

/**
 * Created by Jon on 06/01/2016.
 */
public class SingleTon extends Application {

    private static SingleTon ourInstance = new SingleTon();
    public static ArrayList<Spot> spots;
    public static LocationManager locationManager;
    public static LocationListener locationListener;

    public static SingleTon getInstance() {
        return ourInstance;
    }

    private SingleTon() {
    }

    @Override
    public void onCreate(){
        super.onCreate();
        if(spots==null){
            // Asynkront kald til DB
        }
        if(locationManager==null){
            // Lav en locationmanager
        }
        if(locationListener==null){
            // Lav en locationlistener
        }
    }
}
