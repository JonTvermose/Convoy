package gruppe3.convoy.functionality;

import android.app.Dialog;
import android.graphics.Color;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
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

    public ParserTask(Dialog dialog) {
        this.dialog = dialog;
    }

    public ParserTask(){}

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
            GMapsFragment.polyLineOptions = new PolylineOptions();

            List<HashMap<String, String>> path = routes.get(i);

            for (int j = 0; j < path.size(); j++) {
                HashMap<String, String> point = path.get(j);

                double lat = Double.parseDouble(point.get("lat"));
                double lng = Double.parseDouble(point.get("lng"));
                LatLng position = new LatLng(lat, lng);

                points.add(position);
            }

            GMapsFragment.polyLineOptions.addAll(points);
            GMapsFragment.polyLineOptions.width(6); // Tykkelse på stregerne
            GMapsFragment.polyLineOptions.color(Color.BLUE); // Farve på stregerne
            }
        if (dialog==null){
            Log.d("Hviletid", "Tilføjer hviletidsmarker og zoomer map.");
            String dur = "Approximated position in ";
            int[] endDur = parser.splitToComponentTimes();
            if(endDur[0] != 0){
                dur = dur + endDur[0] + " hour ";
                dur = dur + endDur[1] + " m";
            } else {
                dur = endDur[1] + " mins";
            }
            GMapsFragment.gMap.addMarker(new MarkerOptions().
                    position(SingleTon.destPos).
                    title(dur)
                    .icon(BitmapDescriptorFactory.defaultMarker(210f))).showInfoWindow(); // Destinationsmarkeren har en anden farve en normale markers
            GMapsFragment.gMap.animateCamera(CameraUpdateFactory.newLatLngZoom(SingleTon.destPos, 10));
            GMapsFragment.poly = GMapsFragment.gMap.addPolyline(GMapsFragment.polyLineOptions);
        }
    }
}
