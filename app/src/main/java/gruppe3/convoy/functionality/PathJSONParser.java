package gruppe3.convoy.functionality;

/**
 * Klassen er inspireret af http://javapapers.com/android/draw-path-on-google-maps-android-api/ og udbygget af Jon Tvermose Nielsen
 * Klassens formål er at oversætte et JSON object fra Google Directions API
 */

import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PathJSONParser {

    private String dist, dur, startAdress, endAdress;
    private int seconds, endTime, tempTime;
    private double time = 0;
    private boolean restMode = false, found = false;

    public PathJSONParser(boolean restMode){
        this.restMode = restMode;
        seconds = SingleTon.timer * 60 * 60 + SingleTon.minutter * 60;
        double newSec = seconds/SingleTon.speedSetting;
        seconds = (int) newSec;
        if(restMode){
            SingleTon.restPos = SingleTon.destPos;
        }
    }


    public List<List<HashMap<String, String>>> parse(JSONObject jObject) {
        List<List<HashMap<String, String>>> routes = new ArrayList<List<HashMap<String, String>>>();
        JSONArray jRoutes = null;
        JSONArray jLegs = null;
        JSONArray jSteps = null;
        try {
            jRoutes = jObject.getJSONArray("routes");
            /** Traversing all routes */
            for (int i = 0; i < jRoutes.length(); i++) {
                jLegs = ((JSONObject) jRoutes.get(i)).getJSONArray("legs");
                List<HashMap<String, String>> path = new ArrayList<HashMap<String, String>>();

                // Finder den samlede distance og tid
                dist = (String) ((JSONObject) ((JSONObject) jLegs.get(0)).get("distance")).get("text");
                double duration = (int) ((JSONObject) ((JSONObject) jLegs.get(0)).get("duration")).get("value")*SingleTon.speedSetting;
                int[] endDur = splitToComponentTimes(duration);
                if(endDur[0] != 0){
                    dur = endDur[0] + " hour ";
                    dur = dur + endDur[1] + " m";
                } else {
                    dur = endDur[1] + " mins";
                }
                startAdress = (String) ((JSONObject) jLegs.get(0)).get("start_address");
                endAdress = (String) ((JSONObject) jLegs.get(0)).get("end_address");
                Log.d("Rute", "Samlet Distance: " + dist);
                Log.d("Rute", "Samlet Tid: " + dur);
                Log.d("Rute", "Start adresse: " + startAdress);
                Log.d("Rute", "Slut adresse: " + endAdress);

                /** Traversing all legs */
                for (int j = 0; j < jLegs.length(); j++) {
                    jSteps = ((JSONObject) jLegs.get(j)).getJSONArray("steps");
                    Log.d("Rute", "Antal Steps: " + jSteps.length());

                    /** Traversing all steps */
                    for (int k = 0; k < jSteps.length(); k++) {
                        // Hviletidssøgning
                        time = time + (int) ((JSONObject) ((JSONObject) jSteps.get(k)).get("duration")).get("value")*SingleTon.speedSetting;
                        Log.d("Hviletid" , "Time er: " + time + ", seconds er: " + seconds);
                        if (time >= seconds + 1200 && restMode){
                            if(!found){
                                double lat = (double) ((JSONObject) ((JSONObject) jSteps.get(k-1)).get("end_location")).get("lat");
                                double lng = (double) ((JSONObject) ((JSONObject) jSteps.get(k-1)).get("end_location")).get("lng");
                                LatLng rest = new LatLng(lat, lng);
                                SingleTon.restPos = rest;
                                // Hvis den beregnede tid er større end hviletiden stopper vi ruten der.
                                Log.d("Hviletid" , "Ruten er færdigberegnet!");
                                Log.d("Hviletid" , "Hviletidspositionen er: " + SingleTon.restPos.latitude + ", " + SingleTon.restPos.longitude);
                                found = true;
                            }
                        } else {
                            String polyline = "";
                            polyline = (String) ((JSONObject) ((JSONObject) jSteps
                                    .get(k)).get("polyline")).get("points");
                            List<LatLng> list = decodePoly(polyline);

                            /** Traversing all points */
                            for (int l = 0; l < list.size(); l++) {
                                HashMap<String, String> hm = new HashMap<String, String>();
                                hm.put("lat",
                                        Double.toString(((LatLng) list.get(l)).latitude));
                                hm.put("lng",
                                        Double.toString(((LatLng) list.get(l)).longitude));
                                path.add(hm);
                            }
                        }
                        if(!found){
                            endTime = (int) time;
                        }
                    }
                    routes.add(path);
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
        }
        return routes;
    }

    public int[] splitToComponentTimes(double longVal)
    {
        int hours = (int) longVal / 3600;
        int remainder = (int) longVal - hours * 3600;
        int mins = remainder / 60;
        remainder = remainder - mins * 60;
        int secs = remainder;

        int[] ints = {hours , mins , secs};
        return ints;
    }

    public int[] splitToComponentTimes()
    {
        int hours = endTime / 3600;
        int remainder = endTime - hours * 3600;
        int mins = remainder / 60;
        remainder = remainder - mins * 60;
        int secs = remainder;

        int[] ints = {hours , mins , secs};
        return ints;
    }

    /**
     * Method Courtesy :
     * jeffreysambells.com/2010/05/27
     * /decoding-polylines-from-google-maps-direction-api-with-java
     * */
    private List<LatLng> decodePoly(String encoded) {

        List<LatLng> poly = new ArrayList<LatLng>();
        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;

        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            LatLng p = new LatLng((((double) lat / 1E5)),
                    (((double) lng / 1E5)));
            poly.add(p);
        }
        return poly;
    }

    public String getDist(){ return dist; }
    public String getDur(){ return dur; }
    public String getStartAdress(){ return startAdress; }
    public String getEndAdress(){ return endAdress; }
}
