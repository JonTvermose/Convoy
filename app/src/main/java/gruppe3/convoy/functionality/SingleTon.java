package gruppe3.convoy.functionality;

import android.app.Application;
import android.location.LocationListener;
import android.location.LocationManager;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

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


    @Override
    public void onCreate(){
        super.onCreate();
        System.out.println("SingleTon OnCreate");
        Parse.initialize(this);
        System.out.println("Parse initialiseret");
        if(spots==null){
            System.out.println("Spots er null");
            // Asynkront kald til DB
            ParseQuery<ParseObject> query2 = ParseQuery.getQuery("Spots");
            query2.findInBackground(new FindCallback<ParseObject>() {
                public void done(List<ParseObject> spotList, ParseException e) {
                    if (e == null) {
                        spots = new ArrayList<Spot>();
                        for (int i = 0; spotList.size() > i; i++) {

                            LatLng pos = new LatLng(spotList.get(i).getDouble("posLat"), spotList.get(i).getDouble("posLng"));
                            spots.add(new Spot(
                                    spotList.get(i).getString("desc"),
                                    spotList.get(i).getBoolean("adblue"),
                                    spotList.get(i).getBoolean("food"),
                                    spotList.get(i).getBoolean("bath"),
                                    spotList.get(i).getBoolean("bed"),
                                    spotList.get(i).getBoolean("wc"),
                                    spotList.get(i).getBoolean("fuel"),
                                    spotList.get(i).getBoolean("roadtrain"),
                                    pos
                            ));

                        }
                        System.out.println("Done with spots!");
                    } else {
                        Log.d("score", "Error: " + e.getMessage());
                    }
                }
            });
        }
        if(locationManager==null){
            // Lav en locationmanager
        }
        if(locationListener==null){
            // Lav en locationlistener
        }
    }
}
