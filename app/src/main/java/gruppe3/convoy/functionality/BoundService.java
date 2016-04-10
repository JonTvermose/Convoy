package gruppe3.convoy.functionality;

import android.app.Service;
import android.content.Intent;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by Christian on 14-01-2016.
 * Updated by Jon on 09-04-2016
 */
public class BoundService extends Service{
    // Binder given to clients
    private final IBinder mBinder = new LocalBinder();
    private static Object obj;
    private static String filnavn;
    private static Set<Spot> spotsLokal = new HashSet<>();

    private final String CONVOYSPOTSURL = "http://convoy.com/spots"; // TODO - hvor ligger REST serveren?

    public void uploadSpot(final Spot newSpot) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                HashMap<String, String> params = new HashMap<>();
                params.put("adBlue", Boolean.toString(newSpot.isAdblue()));
                params.put("roadTrain", Boolean.toString(newSpot.isRoadtrain()));
                params.put("wc", Boolean.toString(newSpot.isWc()));
                params.put("food", Boolean.toString(newSpot.isFood()));
                params.put("bath", Boolean.toString(newSpot.isBath()));
                params.put("bed", Boolean.toString(newSpot.isBed()));
                params.put("fuel", Boolean.toString(newSpot.isFuel()));
                params.put("deleted", Boolean.toString(newSpot.isDeleted()));
                params.put("longitude", newSpot.getLng());
                params.put("latitude", newSpot.getLat());
                params.put("name", newSpot.getDesc());
                // ... etc
                // TODO - Tilføj alle nødvendige parametre med korrekte keys

                String postUrl = BoundService.this.CONVOYSPOTSURL + "/ADDSPOT";// TODO - hvad er den korrekte POST url?
                BoundService.this.performPostCall(postUrl , params);
            }
        }).start();
    }

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
                    System.out.println("Skrevet til telefonen");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    // Sender POST (create) til REST server.
    // Fungerer muligvis også med PUT (create or update). "conn.setRequestMethod("POST");" skal således ændres til "PUT".
    /*
    Testet og tjekket. Fungerer med eksemplet:
    HashMap<String, String> params = new HashMap<>();
        params.put("id", "99");
        params.put("userId", "1");
        params.put("title", "This is the title");
        params.put("body", "This is the body");
        String url = "http://localhost:8080";
        String url2 = "https://httpbin.org/post";
        String response = performPostCall(url2, params);
        String[] lines = response.split(",");
        System.out.println("**** Udskriver svar: *****");
        for (String line : lines) {
            System.out.println(line + ",");
        }
        System.out.println("**** Svar slut ****");
     */
    private String performPostCall(String requestURL, HashMap<String, String> postDataParams) {
        URL url;
        String response = "";
        try {
            url = new URL(requestURL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setDoInput(true);
            conn.setDoOutput(true);

            OutputStream os = conn.getOutputStream();
            BufferedWriter writer = new BufferedWriter(
                    new OutputStreamWriter(os, "UTF-8"));
            writer.write(getPostDataString(postDataParams));

            writer.flush();
            writer.close();
            os.close();
            int responseCode=conn.getResponseCode();

            if (responseCode == HttpsURLConnection.HTTP_OK) {
                String line;
                BufferedReader br=new BufferedReader(new InputStreamReader(conn.getInputStream()));
                while ((line=br.readLine()) != null) {
                    response+=line;
                }
            }
            else {
                response="";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return response;
    }

    private String getPostDataString(HashMap<String, String> params) throws UnsupportedEncodingException {
        StringBuilder result = new StringBuilder();
        boolean first = true;
        for(Map.Entry<String, String> entry : params.entrySet()){
            if (first){
                first = false;
            } else {
                result.append("&");
            }
            result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
        }
        return result.toString();
    }

    /**
     * Indlæs gemt data fra telefonen, samt hent opdaterede spots fra serveren.
     * @param filename
     */
    public void hent(String filename) {
        filnavn=filename;
        final String fileName = this.getFilesDir() + "/"+filnavn+".ser";
        System.out.println(fileName);
        new Thread(new Runnable() {
            public void run() {
                try{
                    FileInputStream datastream = new FileInputStream(fileName);
                    ObjectInputStream objektstream = new ObjectInputStream(datastream);
                    spotsLokal = (Set<Spot>) objektstream.readObject();
                    objektstream.close();
                } catch (IOException | ClassNotFoundException e){
                    System.out.println("File not found");
                    spotsLokal = null;
                }
                SingleTon.hentetLokal=true;
                if(SingleTon.isConnected){
                    if(spotsLokal==null){
                        System.out.println("Internet, men ingen lokal data");
                        hentFraDb(0);
                    } else {
                        System.out.println("Internet og lokal data");
                        if(spotsLokal != null){
                            System.out.println("antal spots før: " + spotsLokal.size());
                        } else {
                            System.out.println("ingen spots på telefonen");
                        }
                        hentFraDb(spotsLokal.size()); // TODO - Her skal sendes hvornår spots sidst er blevet opdateret!
                    }
                } else {
                    System.out.println("Hverken internet eller lokal data");
                    SingleTon.spots=null;
                    SingleTon.dataLoadDone=true;
                }
            }
        }).start();
    }


    /**
     * Hent spots fra REST server
     * @param lastUpdated hvornår er klienten sidst blevet opdateret - i ms siden 01.01.1970
     */
    private void hentFraDb(final long lastUpdated){
        // Hent spots fra REST server asynkront
        new Thread(new Runnable() {
            @Override
            public void run() {
                String data = "";
                InputStream iStream = null;
                HttpURLConnection urlConnection = null;
                try {
                    try {
                        Uri.Builder uri = new Uri.Builder()
                                .scheme("https")
                                .authority(BoundService.this.CONVOYSPOTSURL)
                                .appendPath("lastUpdated")
                                .appendQueryParameter("lastUpdated", Long.toString(lastUpdated));

                        URL url = new URL(uri.build().toString());
                        urlConnection = (HttpURLConnection) url.openConnection();
                        urlConnection.connect();
                        iStream = urlConnection.getInputStream();
                        BufferedReader br = new BufferedReader(new InputStreamReader(iStream));
                        StringBuffer sb = new StringBuffer();
                        String line = "";
                        while ((line = br.readLine()) != null) {
                            sb.append(line);
                        }
                        data = sb.toString();
                        br.close();
                    } catch (Exception e) {
                        Log.d("Exception reading url", e.toString());
                    } finally {
                        iStream.close();
                        urlConnection.disconnect();
                    }
                } catch (IOException e){
                    Log.d("Error closing url", e.toString());
                }

                JSONArray spotsListe = null;
                try {
                    spotsListe = new JSONArray(data);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if(SingleTon.spots==null) { // Hvis klienten er tom, tilføjes alle spots
                    SingleTon.spots = new SpotsJSONParser().parse(spotsListe); // Parser JSON til en ArrayList<Spot>
                } else {
                    ArrayList<Spot> updSpots = new SpotsJSONParser().parse(spotsListe);
                    for(Spot updSpot : updSpots) {
                        boolean updated = false;
                        for(int i=0; i<SingleTon.spots.size(); i++){
                            if(SingleTon.spots.get(i).getId() == updSpot.getId()) { // Hvis updSpot allerede findes i klienten, opdateres klienten med det nye spot
                                SingleTon.spots.remove(i);
                                SingleTon.spots.add(updSpot);
                                updated = true;
                                i = SingleTon.spots.size();
                            }
                        }
                        if(!updated) {
                            SingleTon.spots.add(updSpot); // Hvis updSpot ikke findes i klienten tilføjes det
                        }
                    }
                }
                SingleTon.hentetDb=true;
                SingleTon.dataLoadDone = true;
                // Gem spots lokalt på telefonen
                gem(SingleTon.spots, filnavn);
                System.out.println("antal spots efter: " + SingleTon.spots.size());
            }
        }).start();
    }
}

