package gruppe3.convoy.functionality;

import android.app.Dialog;
import android.graphics.Color;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import gruppe3.convoy.GMapsFragment;
import gruppe3.convoy.R;

/**
 * Created by Jon on 13/01/2016.
 */
public class ParserTask extends
        AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {

    private PathJSONParser parser;
    private Dialog dialog;
    private GoogleMap gMap;
    private Polyline poly;
    private PolylineOptions polyLineOptions;
    private GMapsFragment gMapsFragment;

    public ParserTask(Dialog dialog, GoogleMap gMap, Polyline poly, PolylineOptions polylineOptions, GMapsFragment gMapsFragment) {
        this.dialog = dialog;
        this.gMap=gMap;
        this.poly=poly;
        this.polyLineOptions=polylineOptions;
        this.gMapsFragment = gMapsFragment;
    }

    public ParserTask(GoogleMap gMap, Polyline poly, PolylineOptions polylineOptions, GMapsFragment gMapsFragment){
        this.gMap=gMap;
        this.poly=poly;
        this.polyLineOptions=polylineOptions;
        this.gMapsFragment = gMapsFragment;
    }

    @Override
    protected List<List<HashMap<String, String>>> doInBackground(
            String... jsonData) {

        JSONObject jObject;
        List<List<HashMap<String, String>>> routes = null;

        try {
            jObject = new JSONObject(jsonData[0]);
            if(dialog!=null){
                parser = new PathJSONParser(false);
            } else {
                parser = new PathJSONParser(true);
            }
            routes = parser.parse(jObject);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return routes;
    }

    @Override
    protected void onPostExecute(List<List<HashMap<String, String>>> routes) {
        ArrayList<LatLng> points = null;

        // Opdaterer afstand, tid og adresse/overskrift på popuppen.
        String distAndTime = parser.getDist() + " | " + parser.getDur();
        distAndTime = distAndTime.replace("hours", "h");
        if (distAndTime.contains("h")) {
            distAndTime = distAndTime.replace("mins", "m");
        }
        distAndTime = distAndTime.replace(",", ".");
        if (dialog!=null){
            TextView distance = (TextView) dialog.findViewById(R.id.distance_textView);
            distance.setText(distAndTime);
            TextView title = (TextView) dialog.findViewById(R.id.title_TextView);
            title.setText(parser.getEndAdress()); // TO DO - her mangler noget logik for hvis teksten bliver for lang eller "Unnamed road" er en del af den
            Button route = (Button) dialog.findViewById(R.id.findRoute_button);
            route.setEnabled(true); // Gør "Find Route"-knappen tilgængelig
        }

        // traversing through routes
        for (int i = 0; i < routes.size(); i++) {
            points = new ArrayList<LatLng>();
            polyLineOptions = new PolylineOptions();

            List<HashMap<String, String>> path = routes.get(i);

            for (int j = 0; j < path.size(); j++) {
                HashMap<String, String> point = path.get(j);

                double lat = Double.parseDouble(point.get("lat"));
                double lng = Double.parseDouble(point.get("lng"));
                LatLng position = new LatLng(lat, lng);

                points.add(position);
            }

            polyLineOptions.addAll(points);
            polyLineOptions.width(6); // Tykkelse på stregerne
            polyLineOptions.color(Color.BLUE); // Farve på stregerne
            }
        if (dialog==null){
            addRestMarker();
        }
        gMapsFragment.setPolyLineOptions(polyLineOptions);
    }

    // Tilføjer en hviletidsmarker samt tegner rute på kortet
    private void addRestMarker(){
        Log.d("Hviletid", "Tilføjer hviletidsmarker og zoomer map.");
        String dur = "Approximated position in ";
        int[] endDur = parser.splitToComponentTimes();
        if(endDur[0] != 0){
            dur = dur + endDur[0] + " hour ";
            dur = dur + endDur[1] + " m";
        } else {
            dur = endDur[1] + " mins";
        }
        Log.d("Parser" , dur);
        gMap.addMarker(new MarkerOptions().
                position(SingleTon.destPos).
                title(dur)
                .icon(BitmapDescriptorFactory.defaultMarker(210f))).showInfoWindow(); // Destinationsmarkeren har en anden farve en normale markers
        gMap.animateCamera(CameraUpdateFactory.newLatLngZoom(SingleTon.destPos, 10));
//        poly = gMap.addPolyline(polyLineOptions);
    }
}
