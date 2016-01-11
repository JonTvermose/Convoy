package gruppe3.convoy;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.BounceInterpolator;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterItem;
import com.google.maps.android.clustering.ClusterManager;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import gruppe3.convoy.functionality.AddSpot;
import gruppe3.convoy.functionality.ClusterMaker;
import gruppe3.convoy.functionality.HttpConnection;
import gruppe3.convoy.functionality.PathJSONParser;
import gruppe3.convoy.functionality.SingleTon;
import gruppe3.convoy.functionality.Spot;

/*
 Klassen er udviklet af Jon Tvermose Nielsen
 */
public class GMapsFragment extends Fragment implements OnMapReadyCallback, View.OnClickListener {

    private GoogleMap gMap;
    private Spot spot; // Det spot der er klikket på
    private View view;
    private ImageView goButton, zoomLocation, addLocation, homeButton;
    private AddSpot addSpot;
    private ClusterManager<ClusterMaker> mClusterManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        view = inflater.inflate(R.layout.fragment_gmap, container, false);

        //Knap til at zoome ind på nuværende lokation
        zoomLocation = (ImageView) view.findViewById(R.id.zoomLocation);
        zoomLocation.setOnClickListener(this);

        // Knap til at tilføje lokation
        addLocation = (ImageView) view.findViewById(R.id.imageAddLoc);
        addLocation.setOnClickListener(this);

        // Knap til startsiden
        homeButton = (ImageView) view.findViewById(R.id.imageHome);
        homeButton.setOnClickListener(this);

        //Knap til at starte vejvisning til et valgt POI
        goButton = (ImageView) view.findViewById(R.id.goButton);
        goButton.setVisibility(View.GONE); // Skjuler knappen indtil den kan bruges
        goButton.setOnClickListener(this);

        getMap();
        return view;
    }

    // Initierer at hente kortet (asynkront)
    private void getMap(){
        try {
            if (gMap == null) {
//                MainFragment.progressDialog.setMessage("Retrieving Map...");
                try {
                    SupportMapFragment m = ((SupportMapFragment) getChildFragmentManager().
                            findFragmentById(R.id.map));
                    Log.d("Kort", "Forsøger at hente nyt kort - asynkront");
                    m.getMapAsync(this);
                } catch (NullPointerException e){
                    Log.d("Kort", "SupportMapFragment m er null, kunne ikke finde map");
                    Toast.makeText(getActivity(), "An error occurred, could not fetch map. Sorry", Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                    getFragmentManager().popBackStack();
                }
            } else{
                Log.d("Kort", "Kort eksisterer allerede");
                onMapReady(gMap);
            }
        }
        catch (Exception e) {
            Log.d("Kort", "Der skete en fejl. Se log stackTrace");
            e.printStackTrace();
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.d("Kort", "GMapsAktivitet.onMapReady() er kaldt");
        this.gMap = googleMap;

        // Gør det muligt at finde nuværende position og ændre maptype
        gMap.setMyLocationEnabled(true);
        gMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        LatLng cPos = new LatLng(SingleTon.myLocation.getLocation().getLatitude(), SingleTon.myLocation.getLocation().getLongitude());
        gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(cPos, 12));

        // Tilføjer markers til Google Maps
        mClusterManager = new ClusterManager<ClusterMaker>(getActivity(), gMap);
        gMap.setOnMarkerClickListener(mClusterManager);
        gMap.setOnCameraChangeListener(mClusterManager);
        if(SingleTon.searchedSpots!=null){
            int id = 0;
            for (Spot spot : SingleTon.searchedSpots) {
                ClusterMaker mark = new ClusterMaker(new LatLng(spot.getPos().latitude, spot.getPos().longitude));
                mark.setSnippet(Integer.toString(id)); // Tilføj unikt ID til marker, svarende til indekset for det pågældende spot i listen over spots
                mClusterManager.addItem(mark);
                id++;
            }
            Log.d("Kort", "Tilføjet " + id + " markers(spots) til kortet");
        } else {
            Toast.makeText(getActivity(), "No destinations was found. Try to widen your search.", Toast.LENGTH_LONG).show();
            Log.d("Error", "Der er ingen spots at vise på kortet");
        }

        // Clicklistener til når der trykkes på kortet og holdes nede i lang tid
        gMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                Location loc = new Location("");
                loc.setLatitude(latLng.latitude);
                loc.setLongitude(latLng.longitude);
                addSpot = new AddSpot(loc, getActivity());
                GMapsFragment.this.onClick(addLocation); // Genbruger onClick-metoden
            }
        });

        // ClickHandler til når der klikkes på en Cluster af markers. Der zoomes og kortet centreres ved cluster
        mClusterManager.setOnClusterClickListener(new ClusterManager.OnClusterClickListener<ClusterMaker>() {
            @Override
            public boolean onClusterClick(Cluster<ClusterMaker> cluster) {
                Log.d("Kort", "Der er klikket på Cluster med pos: " + cluster.getPosition().latitude + ", " + cluster.getPosition().longitude);
                Log.d("Kort", "Zoomlevel er: " + gMap.getCameraPosition().zoom + ". Der zoomes ind til zoom +1");
                gMap.animateCamera(CameraUpdateFactory.newLatLngZoom(cluster.getPosition(), gMap.getCameraPosition().zoom + 1));
                return true;
            }
        });

        // Clicklistener til markers. Når man klikker på en marker åbnes en Dialog-boks
        mClusterManager.setOnClusterItemClickListener(new ClusterManager.OnClusterItemClickListener<ClusterMaker>() {
            Polyline poly = null;
            PolylineOptions polyLineOptions = null;
            TextView distance, title;
            Button route;

            @Override
            public boolean onClusterItemClick(ClusterMaker marker) {
                goButton.setVisibility(View.GONE); // Siker at GO-knappen forsvinder hvis man har trykket på et andet spot før dette
                final Dialog dialog = new Dialog(getActivity());
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.dialog_spot); // XML-layout til Dialog-boksen

                // Fjern rute fra kort hvis der eksisterer et i forvejen
                if (poly != null) {
                    poly.remove();
                }

                try {
                    spot = SingleTon.searchedSpots.get(Integer.valueOf(marker.getSnippet()));

                    /* Henter rute til POI */
                    Location startLoc = SingleTon.myLocation.getLocation();
                    String url = GMapsFragment.this.getMapsApiDirectionsUrl(startLoc, GMapsFragment.this.spot.getPos());
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
                        adblue.setSelected(false);
                    } else {
                        adblue.setImageResource(R.drawable.adblue_t);
                        adblue.setSelected(true);
                    }
                    if (spot.isBath()) {
                        bath.setImageResource(R.drawable.bath_t_check);
                        bath.setSelected(false);
                    } else {
                        bath.setImageResource(R.drawable.bath_t);
                        bath.setSelected(true);
                    }
                    if (spot.isBed()) {
                        bed.setImageResource(R.drawable.bed_t_check);
                        bed.setSelected(false);
                    } else {
                        bed.setImageResource(R.drawable.bed_t);
                        bed.setSelected(true);
                    }
                    if (spot.isFood()) {
                        food.setImageResource(R.drawable.food_t_check);
                        food.setSelected(false);
                    } else {
                        food.setImageResource(R.drawable.food_t);
                        food.setSelected(true);
                    }
                    if (spot.isFuel()) {
                        fuel.setImageResource(R.drawable.fuel_t_check);
                        fuel.setSelected(false);
                    } else {
                        fuel.setImageResource(R.drawable.fuel_t);
                        fuel.setSelected(true);
                    }
                    if (spot.isWc()) {
                        wc.setImageResource(R.drawable.wc_t_check);
                        wc.setSelected(false);
                    } else {
                        wc.setImageResource(R.drawable.wc_t);
                        wc.setSelected(true);
                    }

                    // Clicklistener til "FIND ROUTE"-knappen
                    route = (Button) dialog.findViewById(R.id.findRoute_button);
                    route.setEnabled(false);
                    route.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Toast.makeText(getActivity(), "Drawing route.", Toast.LENGTH_SHORT).show();
                            if (polyLineOptions == null) { // Kan fjernes så længe knappen er deaktiveret indtil ruten er modtaget og parset
                                Toast.makeText(getActivity(), "Route not ready!", Toast.LENGTH_LONG).show();
                            } else {
                                poly = GMapsFragment.this.gMap.addPolyline(polyLineOptions);
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
                    Toast.makeText(getActivity(), "Could not find id: " + marker.getSnippet(), Toast.LENGTH_LONG).show();
                    Log.d("Kort", "Kunne ikke finde det rigtige spot. SingleTon.searchedSpots størrelse: " + SingleTon.searchedSpots.size() + " , der søges efter spot nr: " + marker.getSnippet());
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
                        if (distAndTime.contains("h")) {
                            distAndTime = distAndTime.replace("mins", "m");
                        }
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
//        MainFragment.progressDialog.dismiss();
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

    @Override
    public void onClick(View v) {
        if (v == zoomLocation) {
            Log.d("Kort", "Der klikkes på Zoomknap");
            if (gMap != null) {
                LatLng cPos = new LatLng(SingleTon.myLocation.getLocation().getLatitude(), SingleTon.myLocation.getLocation().getLongitude());
                gMap.animateCamera(CameraUpdateFactory.newLatLngZoom(cPos, 12), 1000, null);
                Log.d("Kort", "Der zoomes til: " + cPos.latitude + ", " + cPos.longitude);
            } else {
                Log.d("Kort", "Der blev forsøgt zoomet til lokation, men gMap er null");
            }
        } else if (v == goButton) {
            Log.d("Kort", "Der klikkes på GO-knap");
            if (spot != null) {
                Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                        Uri.parse("http://maps.google.com/maps?&daddr="
                                + spot.getPos().latitude + ","
                                + spot.getPos().longitude));
                intent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");
                startActivity(intent);
            } else {
                Toast.makeText(getActivity(), "You must choose where to go!", Toast.LENGTH_LONG).show(); // Safety-text, bør ikke rammes
            }
        } else if (v == homeButton){
            getFragmentManager().popBackStack();
        } else if (v == addLocation) {
            Log.d("Kort", "Der klikkes på tilføj sted-knap");
            // Hvis der er klikket på knappen er addSpot = null og dermed skal nuværende lokation bruges
            if (addSpot == null) {
                addSpot = new AddSpot(SingleTon.myLocation.getLocation(), getActivity());
            }

            final Dialog addDialog = new Dialog(getActivity());
            addDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            addDialog.setContentView(R.layout.dialog_addlocation); // XML-layout til Dialog-boksen

            final ImageView adblue = (ImageView) addDialog.findViewById(R.id.adblue_img);
            adblue.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!addSpot.adblue) {
                        addSpot.adblue = true;
                        adblue.setImageResource(R.drawable.adblue_t_check);
                        adblue.setSelected(true);
                    } else {
                        addSpot.adblue = false;
                        adblue.setImageResource(R.drawable.adblue_t);
                        adblue.setSelected(false);
                    }
                }
            });
            final ImageView bed = (ImageView) addDialog.findViewById(R.id.bed_img);
            bed.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!addSpot.bed) {
                        addSpot.bed = true;
                        bed.setImageResource(R.drawable.bed_t_check);
                        bed.setSelected(true);
                    } else {
                        addSpot.bed = false;
                        bed.setImageResource(R.drawable.bed_t);
                        bed.setSelected(false);
                    }
                }
            });
            final ImageView bath = (ImageView) addDialog.findViewById(R.id.bath_img);
            bath.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!addSpot.bath) {
                        addSpot.bath = true;
                        bath.setImageResource(R.drawable.bath_t_check);
                        bath.setSelected(true);
                    } else {
                        addSpot.bath = false;
                        bath.setImageResource(R.drawable.bath_t);
                        bath.setSelected(false);
                    }
                }
            });
            final ImageView food = (ImageView) addDialog.findViewById(R.id.food_img);
            food.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!addSpot.food) {
                        addSpot.food = true;
                        food.setImageResource(R.drawable.food_t_check);
                        food.setSelected(true);
                    } else {
                        addSpot.food = false;
                        food.setImageResource(R.drawable.food_t);
                        food.setSelected(false);
                    }
                }
            });
            final ImageView fuel = (ImageView) addDialog.findViewById(R.id.fuel_img);
            fuel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!addSpot.fuel) {
                        addSpot.fuel = true;
                        fuel.setImageResource(R.drawable.fuel_t_check);
                        fuel.setSelected(true);
                    } else {
                        addSpot.fuel = false;
                        fuel.setImageResource(R.drawable.fuel_t);
                        fuel.setSelected(false);
                    }
                }
            });
            final ImageView wc = (ImageView) addDialog.findViewById(R.id.wc_img);
            wc.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!addSpot.wc) {
                        addSpot.wc = true;
                        wc.setImageResource(R.drawable.wc_t_check);
                        wc.setSelected(true);
                    } else {
                        addSpot.wc = false;
                        wc.setImageResource(R.drawable.wc_t);
                        wc.setSelected(false);
                    }
                }
            });

            Switch roadTrain = (Switch) addDialog.findViewById(R.id.loc_switch);
            roadTrain.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    addSpot.roadTrain = isChecked;
                }
            });

            addDialog.show();

            TextView address = (TextView) addDialog.findViewById(R.id.loc_addressTxt);
            address.setText(addSpot.getAddressTxt()); // Opdaterer adressefeltet med adressen

            Button createLocation = (Button) addDialog.findViewById(R.id.createLocButton);
            createLocation.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // TO DO - hent data fra addSpot og send det asynkront til parse.com
                    addDialog.hide();
                    // Tilføjer spot til google map kortet så vi kan animere det
                    LatLng latLng = new LatLng(addSpot.loc.getLatitude(), addSpot.loc.getLongitude());
                    Marker mark = gMap.addMarker(new MarkerOptions().
                            position(latLng).
                            title(addSpot.getAddressTxt()));
//                    mark.remove();

                    if(SingleTon.searchedSpots==null){
                        SingleTon.searchedSpots = new ArrayList<Spot>();
                    }
                    mark.setSnippet(Integer.toString(SingleTon.searchedSpots.size()));
                    ClusterMaker clustMark = new ClusterMaker(latLng);
                    clustMark.setSnippet(mark.getSnippet());
                    mClusterManager.addItem(clustMark); // Tilføj marker til clustermanageren

                    GMapsFragment.this.dropPinEffect(mark);

                    // Tilføjer spot til den hentede liste af spots, så det har samme funktionalitet som alle andre spots
                    Spot newSpot = new Spot(addSpot.getAddressTxt(), addSpot.adblue, addSpot.food, addSpot.bath, addSpot.bed, addSpot.wc, addSpot.fuel, addSpot.roadTrain, latLng);
                    SingleTon.searchedSpots.add(newSpot);
                    SingleTon.spots.add(newSpot);
                    Toast.makeText(getActivity(), "Location added", Toast.LENGTH_SHORT).show(); // Muligvis på onSuccess fra parse.com?
                    addSpot = null; // Sikrer vi nulstiller data hvis der tilføjes flere spots i samme session.
                }
            });
        }
    }

    // Animerer en marker på Google Maps med en "drop-pin-effekt"
    // Tyv stjålet fra https://guides.codepath.com/android/Google-Maps-API-v2-Usage#falling-pin-animation og udbygget
    private void dropPinEffect(final Marker marker) {
        // Handler allows us to repeat a code block after a specified delay
        final android.os.Handler handler = new android.os.Handler();
        final long start = SystemClock.uptimeMillis();
        final long duration = 1500;

        // Use the bounce interpolator
        final android.view.animation.Interpolator interpolator = new BounceInterpolator();

        // Animate marker with a bounce updating its position every 15ms
        handler.post(new Runnable() {
            private float alpha = 1;
            @Override
            public void run() {
                long elapsed = SystemClock.uptimeMillis() - start;
                // Calculate t for bounce based on elapsed time
                float t = Math.max(
                        1 - interpolator.getInterpolation((float) elapsed
                                / duration), 0);
                // Set the anchor
                marker.setAnchor(0.5f, 1.0f + 14 * t);

                if (t > 0.0) {
                    // Post this event again 15ms from now.
                    handler.postDelayed(this, 15);
                } else {
                    marker.remove();
                    mClusterManager.cluster();
//                    gMap.animateCamera(CameraUpdateFactory.newLatLng(marker.getPosition()));
                }
            }
        });
    }
}

