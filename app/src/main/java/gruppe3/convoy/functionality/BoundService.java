package gruppe3.convoy.functionality;

import android.app.Service;
import android.content.Intent;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
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
import java.net.UnknownHostException;
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
    private static String filnavn;
    private static ArrayList<Spot> spotsLokal = new ArrayList<>();

    private final String CONVOYSPOTSURL = "http://10.16.227.23:8080/ConvoyServer/webresources/convoy"; // TODO - hvor ligger REST serveren?

    public void uploadSpot(final Spot newSpot) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Gson gson = new Gson();
                String json = gson.toJson(newSpot);
                String postUrl = BoundService.this.CONVOYSPOTSURL + "/create";
                Log.d("DATA", "URL: " + postUrl + ", Opretter spot: " + json);
                BoundService.this.performPostCall(postUrl , json);
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

    /**
     * Gemmer et objekt på telefonen som en binær fil. Hvis filnavnet eksisterer i forvejen forsøges denne slettet
     * @param obj Objekt der skal gemmes på telefonen
     * @param filnavn Filnavn der gemmes i.
     */
    public void gem(final Object obj, final String filnavn) {
        final String fileName = this.getFilesDir() + "/"+filnavn+".ser";
        new Thread(new Runnable() {
            public void run() {
                // Først slettes den eksisterende fil (hvis den findes)
                File fil = new File(fileName);
                if (fil.delete()) {
                    System.out.println("Slettet fil fra telefon.");
                } else {
                    System.out.println("Fil kunne ikke slettes fra telefon. Måske findes den ikke?");
                }
                try {
                    // Dernæst skrives en ny kopi af filen der indeholder de opdaterede ændringer.
                    FileOutputStream datastream = new FileOutputStream(fileName);
                    ObjectOutputStream objektstream = new ObjectOutputStream(datastream);
                    objektstream.writeObject(obj);
                    objektstream.close();
                    System.out.println("Skrevet fil til telefonen");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    // Sender POST (create) til REST server.
    // Fungerer muligvis også med PUT (create or update). "conn.setRequestMethod("POST");" skal således ændres til "PUT".
    private String performPostCall(String requestURL, String json) {
        URL url;
        String response = "";
        try {
            url = new URL(requestURL);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Accept", "application/json");
            conn.connect();

            OutputStream os = conn.getOutputStream();
            BufferedWriter writer = new BufferedWriter(
                    new OutputStreamWriter(os, "UTF-8"));
            writer.write(json);

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
                Log.d("DATA", "Fejlkode: " + responseCode);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return response;
    }

    /**
     * Indlæs gemt data fra telefonen, samt hent opdaterede spots fra serveren.
     * @param filename
     */
    public void hent(String filename) {
        filnavn=filename;
        final String fileName = this.getFilesDir() + "/"+filnavn+".ser";

        // Kode til at fjerne legacy-fil fra tidligere versioner
        if(SingleTon.lastUpdated == -1){ // Hvis lastUpdated ikke findes i telefonen, slettes det lokale data
            File fil = new File(fileName);
            if (fil.delete()) {
                System.out.println("Slettet fil fra telefon.");
                SingleTon.lastUpdated = 0;
            } else {
                System.out.println("Fil kunne ikke slettes fra telefon. Måske findes den ikke?");
            }
        }

        new Thread(new Runnable() {
            public void run() {
                try{
                    FileInputStream datastream = new FileInputStream(fileName);
                    ObjectInputStream objektstream = new ObjectInputStream(datastream);
                    spotsLokal = (ArrayList<Spot>) objektstream.readObject();
                    objektstream.close();
                } catch (IOException | ClassNotFoundException e){
                    System.out.println("File not found");
                    spotsLokal = null;
                }
                SingleTon.hentetLokal=true;
                if(SingleTon.isConnected){
                    if(spotsLokal==null){
                        System.out.println("Internet, men ingen lokal data");
                        hentFraDb(0); // Tiden 0 sendes idet alle spots ønskes hentet fra serveren
                    } else {
                        System.out.println("Internet og lokal data");
                        System.out.println("antal spots før: " + spotsLokal.size());
                        hentFraDb(SingleTon.lastUpdated); // Her sendes hvornår spots sidst er blevet opdateret på klienten!
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
                        URL url = new URL(BoundService.this.CONVOYSPOTSURL + "/get_last/" + SingleTon.lastUpdated);
                        System.out.println("URL: " + url.toString()); // DEBUG
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
                } catch (Exception e){
                    Log.d("Error reading url", e.toString());
                    e.printStackTrace();
                }

                Log.d("DATA" , data);

                Gson gson = new Gson();
                SpotsContainer spotsContainer = gson.fromJson(data, SpotsContainer.class);
                Log.d("DATA", spotsContainer.toString());

                // Opdater SingleTon.lastUpdated med server-værdi for hvornår svaret er sendt! Værdien skal findes i JSON-objektet
                SingleTon.lastUpdated = spotsContainer.getLastUpdated();

                if(SingleTon.spots==null) { // Hvis klienten er tom, tilføjes alle spots
                    SingleTon.spots = spotsContainer.getSpots();
                } else { // Ellers opdateres klientens spots med de ændringer der er foretaget, og eventuelle nye spots tilføjes
                    ArrayList<Spot> updSpots = spotsContainer.getSpots();
                    for(Spot updSpot : updSpots) {
                        boolean updated = false;
                        for(int i=0; i<SingleTon.spots.size(); i++){
                            if(SingleTon.spots.get(i).getId() == updSpot.getId()) { // Hvis updSpot allerede findes i klienten, opdateres klienten med det nye spot
                                SingleTon.spots.remove(i);
                                SingleTon.spots.add(updSpot);
                                updated = true;
                                i = SingleTon.spots.size(); // Vi stopper med at lede efter det spot vi lige har opdateret
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

