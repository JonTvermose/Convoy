package gruppe3.convoy.functionality;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Date;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by Christian on 14-01-2016.
 */
public class BoundService extends Service{
    // Binder given to clients
    private final IBinder mBinder = new LocalBinder();
    private static ArrayList<Spot> spotsParse = new ArrayList();
    private static Object obj;

    /**
     * Class used for the client Binder.  Because we know this service always
     * runs in the same process as its clients, we don't need to deal with IPC.
     */
    public class LocalBinder extends Binder {
        BoundService getService() {
            // Return this instance of LocalService so clients can call public methods
            return BoundService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        System.out.println("onBind");
        return mBinder;
    }

    public void gem(final Object obj, final String filnavn) {
        final String fileName = this.getFilesDir() + "/"+filnavn+".ser";
        new Thread(new Runnable() {
            public void run() {
                try {
                    FileOutputStream datastream = new FileOutputStream(fileName);
                    ObjectOutputStream objektstream = new ObjectOutputStream(datastream);
                    objektstream.writeObject(obj);
                    objektstream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public void hent(String filnavn) {
        System.out.println(1);
        final String fileName = this.getFilesDir() + "/"+filnavn+".ser";
        System.out.println(fileName);
        new Thread(new Runnable() {
            public void run() {
                System.out.println(3);
                try {
                    System.out.println(4);
                    FileInputStream datastream = null;
                    System.out.println(4.5);
                    try{
                        datastream = new FileInputStream(fileName);
                        ObjectInputStream objektstream = new ObjectInputStream(datastream);
                        SingleTon.spotsLokal = (ArrayList<Spot>) objektstream.readObject();
                        objektstream.close();
                    } catch (FileNotFoundException e){
                        System.out.println("File not found");
                        SingleTon.spotsLokal = null;
                    }
                    SingleTon.hentetLokal=true;
                    if(SingleTon.spotsLokal==null){
                        System.out.println("spotsLokal = null");
                        hentFraDb(null);
                    } else {
                            System.out.println("else");
                            hentFraDb(SingleTon.spotsLokal.get(SingleTon.spotsLokal.size()-1).getCreatedAt());

                    }
                } catch (IOException | ClassNotFoundException e) {
                    SingleTon.spotsLokal=null;
                }
            }
        }).start();
    }

    public void hentFraDb(String created){
        System.out.println("spotsParse");
        // Asynkront kald til DB
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Spots1");
        query.setLimit(1000);

        if(created!=null){
            System.out.println(created);
            System.out.println("skip sat til: "+spotsParse.size());
            query.setSkip(spotsParse.size());
        }
        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> spotList, ParseException e) {
                Log.d("Data", "e = " + e);
                int spotsSize = spotList.size();
                if (e == null) {
                    for (int i = 0; spotsSize > i; i++) {
                        //LatLng pos = new LatLng(Double.valueOf(spotList.get(i).getString("posLat")), Double.valueOf(spotList.get(i).getString("posLng")));
                        spotsParse.add(new Spot(
                                spotList.get(i).getString("desc"),
                                spotList.get(i).getBoolean("adblue"),
                                spotList.get(i).getBoolean("food"),
                                spotList.get(i).getBoolean("bath"),
                                spotList.get(i).getBoolean("bed"),
                                spotList.get(i).getBoolean("wc"),
                                spotList.get(i).getBoolean("fuel"),
                                spotList.get(i).getBoolean("roadtrain"),
                                spotList.get(i).getCreatedAt().toString(),
                                spotList.get(i).getString("posLat"),
                                spotList.get(i).getString("posLng")
                        ));
                    }
                    Log.d("Data", "Done with spots!");
                    System.out.println("Size of Spots = " + spotsParse.size());
                    System.out.println("spotsParse" + spotsParse.get(spotsParse.size() - 1).getCreatedAt());

                    if(spotsSize==1000){
                        System.out.println("antal spots hentet fra DB: "+spotsParse.size());
                        hentFraDb(spotsParse.get(spotsParse.size() - 1).getCreatedAt());
                    } else {

//                    SingleTon.spotsDb = spotsParse;
                        SingleTon.searchedSpots = spotsParse;
                        SingleTon.hentetDb=true;
                        SingleTon.dataLoadDone = true;
                    }
                } else {
                    Log.d("score", "Error: " + e.getMessage());
                }
            }
        });
    }

}
