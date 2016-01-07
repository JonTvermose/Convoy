package gruppe3.convoy;

import android.app.Activity;
import android.app.Dialog;
import android.app.FragmentManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import gruppe3.convoy.functionality.HttpConnection;
import gruppe3.convoy.functionality.PathJSONParser;
import gruppe3.convoy.functionality.SingleTon;
import gruppe3.convoy.functionality.Spot;

public class GMapsAktivitet extends Fragment implements OnMapReadyCallback {

    public static GoogleMap gMap;
    private final int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private Spot spot;
    private View view;
    private ImageView goButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        view = inflater.inflate(R.layout.activity_gmaps_aktivitet, container, false);
        goButton = (ImageView) view.findViewById(R.id.goButton);
        goButton.setVisibility(View.GONE); // Skjuler knappen indtil den kan bruges
        goButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(spot!=null) {
                    Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                            Uri.parse("http://maps.google.com/maps?&daddr="
                                    + spot.getPos().latitude + ","
                                    + spot.getPos().longitude));
                    intent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");
                    startActivity(intent);
                } else {
                    Toast.makeText(getActivity(), "You must choose where to go!", Toast.LENGTH_LONG).show(); // Safety-text, bør ikke rammes
                }
            }
        });

    getMap();

//        if (ContextCompat.checkSelfPermission(this,
//                Manifest.permission.ACCESS_FINE_LOCATION)
//                != PackageManager.PERMISSION_GRANTED) {
//            // Hvis vi ikke har permissions skal vi bede om permission
//            Log.d("Access", "Mangler adgang til ACCESS_FINE_LOCATION");
//
//            // Should we show an explanation?
//            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
//                    Manifest.permission.ACCESS_FINE_LOCATION)) {
//                // TODO
//                // Show an expanation to the user *asynchronously* -- don't block
//                // this thread waiting for the user's response! After the user
//                // sees the explanation, try again to request the permission.
//
//            } else {
//
//                // No explanation needed, we can request the permission.
//
//                ActivityCompat.requestPermissions(this,
//                        new String[]{Manifest.permission.READ_CONTACTS},
//                        MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
//
//                // MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION is an
//                // app-defined int constant. The callback method gets the
//                // result of the request.
//            }
//        } else {
//            // Hvis vi har tilladelse i orden startes maps bare
//            Log.d("Access", "ACCESS_FINE_LOCATION er ok");
//            getMap();
//        }
        return view;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    getMap(); // Hent google maps kort m.m. Appen fortsætter

                } else {
                    // TODO - permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }
            default: {
                // TODO - der er sket en fejl
            }
        }
    }

    private void getMap(){
        try {
            if (gMap == null) {
                SupportMapFragment m = ((SupportMapFragment) getChildFragmentManager().
                        findFragmentById(R.id.map));
                        Log.d("Kort", "Forsøger at hente nyt kort - asynkront");
                if(m==null) {
                    Log.d("Kort", "SupportMapFragment m er null, kunne ikke finde map");
                }
                m.getMapAsync(this);
            } else{
                Log.d("Kort", "Kort eksisterer allerede");
                onMapReady(gMap);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.d("Kort", "GMapsAktivitet.onMapReady() er kaldt *****");
        this.gMap = googleMap;

        // Gør det muligt at finde nuværende position og ændre maptype
        gMap.setMyLocationEnabled(true);
        gMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        LatLng cPos = new LatLng(SingleTon.myLocation.getLocation().getLatitude(), SingleTon.myLocation.getLocation().getLongitude());
        gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(cPos, 12));

        // Tilføjer markers til Google Maps
        int id = 0;
        for (Spot spot : SingleTon.spots) {
            Marker mark = gMap.addMarker(new MarkerOptions().position(spot.getPos()).title(spot.getDesc()));
            mark.setDraggable(false);
            mark.setSnippet(Integer.toString(id)); // Tilføj unikt ID til marker, svarende til indekset for det pågældende spot i listen over spots
            id++;
        }
        Log.d("Kort", "Tilføjet " + id + " markers(spots) til kortet");

        // Clicklistener til markers. Når man klikker på en marker åbnes en Dialog-boks
        gMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            Polyline poly = null;
            PolylineOptions polyLineOptions = null;
            TextView distance, title ;
            Button route;

            @Override
            public boolean onMarkerClick(Marker marker) {
                goButton.setVisibility(View.GONE); // Siker at GO-knappen forsvinder hvis man har trykket på et andet spot før dette
                final Dialog dialog = new Dialog(getActivity());
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.fragment_spot); // XML-layout til Dialog-boksen

                // Fjern rute fra kort hvis der eksisterer et i forvejen
                if(poly!=null){
                    poly.remove();
                }

                try {
                    spot = SingleTon.spots.get(Integer.valueOf(marker.getSnippet())); //getSpot(marker.getTitle());

                    /* Henter rute til POI */
                    Location startLoc = SingleTon.myLocation.getLocation();
                    String url = GMapsAktivitet.this.getMapsApiDirectionsUrl(startLoc, GMapsAktivitet.this.spot.getPos());
                    ReadTask downloadTask = new ReadTask();
                    downloadTask.execute(url);

                    title = (TextView) dialog.findViewById(R.id.title_TextView);
                    title.setText("");

                    ImageView adblue = (ImageView) dialog.findViewById(R.id.adblue_imageView);
                    ImageView bed = (ImageView) dialog.findViewById(R.id.bed_imageView);
                    ImageView bath = (ImageView) dialog.findViewById(R.id.bath_imageView);
                    ImageView food = (ImageView) dialog.findViewById(R.id.food_imageView);
                    ImageView fuel = (ImageView) dialog.findViewById(R.id.fuel_imageView);
                    ImageView wc = (ImageView) dialog.findViewById(R.id.wc_imageView);
                    distance = (TextView) dialog.findViewById(R.id.distance_textView);

                    // Sæt billederne afhængig af hvilken service der er tilgængelig på det pågældende spot
                    if (spot.isAdblue()) {
                        adblue.setImageResource(R.drawable.adblue_t_check);
                    } else {
                        adblue.setImageResource(R.drawable.adblue_t);
                    }
                    if (spot.isBath()) {
                        bath.setImageResource(R.drawable.bath_t_check);
                    } else {
                        bath.setImageResource(R.drawable.bath_t);
                    }
                    if (spot.isBed()) {
                        bed.setImageResource(R.drawable.bed_t_check);
                    } else {
                        bed.setImageResource(R.drawable.bed_t);
                    }
                    if (spot.isFood()) {
                        food.setImageResource(R.drawable.food_t_check);
                    } else {
                        food.setImageResource(R.drawable.food_t);
                    }
                    if (spot.isFuel()) {
                        fuel.setImageResource(R.drawable.fuel_t_check);
                    } else {
                        fuel.setImageResource(R.drawable.fuel_t);
                    }
                    if (spot.isWc()) {
                        wc.setImageResource(R.drawable.wc_t_check);
                    } else {
                        wc.setImageResource(R.drawable.wc_t);
                    }

                    // Clicklistener til "FIND ROUTE"-knappen
                    route = (Button) dialog.findViewById(R.id.findRoute_button);
                    route.setEnabled(false);
                    route.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Toast.makeText(getActivity(), "Drawing route.", Toast.LENGTH_SHORT).show();
                            if(polyLineOptions==null){ // Kan fjernes så længe knappen er deaktiveret indtil ruten er modtaget og parset
                                Toast.makeText(getActivity(), "Route not ready!", Toast.LENGTH_LONG).show();
                            } else {
                                poly = GMapsAktivitet.this.gMap.addPolyline(polyLineOptions);
                                dialog.hide();
                                goButton.setVisibility(View.VISIBLE); // Viser GO-knappen
                            }
                        }
                    });

                    // Clicklistener til "Luk"-knappen (man kan også bare klikke udenfor Dialog-boksen)
                    ImageView close = (ImageView) dialog.findViewById(R.id.close_imageView);
                    close.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.hide();
                        }
                    });
                    dialog.show();
                } catch (Exception e) {
                    // TO DO : Fejlhåndtering
                    // Hvad skal der ske hvis man klikker på en marker som vi ikke kan identificere?
                    Toast.makeText(getActivity(), "Could not find matching POI: " + marker.getTitle(), Toast.LENGTH_LONG).show();
                }

                return true;
            }

            class ReadTask extends AsyncTask<String, Void, String> {
                @Override
                protected String doInBackground(String... url) {
                    String data = "";
                    try {
                        HttpConnection http = new HttpConnection();
                        data = http.readUrl(url[0]);
                    } catch (Exception e) {
                        Log.d("Background Task", e.toString());
                    }
                    return data;
                }

                @Override
                protected void onPostExecute(String result) {
                    super.onPostExecute(result);
                    new ParserTask().execute(result);
                }

                class ParserTask extends
                        AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {
                    private PathJSONParser parser;

                    @Override
                    protected List<List<HashMap<String, String>>> doInBackground(
                            String... jsonData) {

                        JSONObject jObject;
                        List<List<HashMap<String, String>>> routes = null;

                        try {
                            jObject = new JSONObject(jsonData[0]);
                            parser = new PathJSONParser();
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
                        distAndTime = distAndTime.replace("mins", "m");
                        distAndTime = distAndTime.replace(",", ".");
                        distance.setText(distAndTime);
                        title.setText(parser.getEndAdress()); // TO DO - her mangler noget logik for hvis teksten bliver for lang eller "Unnamed road" er en del af den


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

                        route.setEnabled(true); // Gør "Find Route"-knappen tilgængelig
                    }
                }
            }
        });
    }

    /**
     * Kreerer en Google Directions API URL ud fra en start lokation og en slutposition.
     * @param start Start lokation (latitude og longitude)
     * @param dest Slut lokation (latiture og longitude)
     * @return URL til Google Directions API der returnerer et JSON object med ruten mellem de to punkter
     */
    private String getMapsApiDirectionsUrl(Location start, LatLng dest) {
        String locations =
                "origin=" + start.getLatitude() + "," + start.getLongitude()
                        + "&"
                        + "destination=" + dest.latitude + "," + dest.longitude;
        // String key = "key=" + "AIzaSyCZSGpLIQ6JUmEJsj8TexBJMdrVZ-mwu40"; // TO DO - bør nok gemmes eller hentes fra andet sted?
        String mode = "mode=" + "driving"; // Dette kan udelades (er default for Google Directions)
        String units = "units=" + "metric"; // Eventuelt variabel baseret på indstillinger i appen

        // Google Directions bruger som standard hastighedsgrænser for biler. Vha. traffic_model forsøger vi at nedsætte hastigheden
        // så den er mere realistisk for en lastbil der skal følge andre hastighedsgrænser (80 eller 90 km/t på motorveje)
        String trafficModel = "traffic_model=" + "pessimistic";
        String departTime = "departure_time=" + "now";

        String params = locations + "&" + mode + "&" + trafficModel + "&" + units + "&" + departTime; // + "&" + key;
        String output = "json";
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + params;

        Log.d("Rute", "Påbegynder at finde rute");
        Log.d("Rute", "Fra: " + start.getLatitude() + "," + start.getLongitude());
        Log.d("Rute", "Til: " + dest.latitude + "," + dest.longitude);
        Log.d("Rute", "Url: " + url);
        return url;
    }

}