package gruppe3.convoy;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.animation.BounceInterpolator;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterManager;
import com.parse.ParseObject;

import org.w3c.dom.Text;

import java.util.ArrayList;

import gruppe3.convoy.functionality.AddSpot;
import gruppe3.convoy.functionality.ClusterMaker;
import gruppe3.convoy.functionality.ReadTask;
import gruppe3.convoy.functionality.SingleTon;
import gruppe3.convoy.functionality.Spot;

/*
 Klassen er udviklet af Jon Tvermose Nielsen
 */
public class GMapsFragment extends AppCompatActivity implements OnMapReadyCallback, View.OnClickListener, ClusterManager.OnClusterItemClickListener<ClusterMaker> {

    private Spot spot; // Det spot der er klikket på
    private ImageView goButton, zoomLocation, addLocation, homeButton;
    private ClusterManager<ClusterMaker> mClusterManager;
    private Marker destMark;
    private Dialog addDialog;
    private Dialog dialog;
    private GoogleMap gMap;
    private Polyline poly = null;
    private PolylineOptions polyLineOptions = null;
    private ConnectivityManager cm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(savedInstanceState==null){
            SearchButtonFragment.progressDialog.setMessage("Found " + SingleTon.searchedSpots.size() + " spots. Retrieving map...");
            if (SingleTon.nightMode){
                setContentView(R.layout.fragment_gmap_night);
            } else {
                setContentView(R.layout.fragment_gmap);
            }

            cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

            //Knap til at zoome ind på nuværende lokation
            zoomLocation = (ImageView) findViewById(R.id.zoomLocation);
            zoomLocation.setOnClickListener(this);

            // Knap til at tilføje lokation
            addLocation = (ImageView) findViewById(R.id.imageAddLoc);
            addLocation.setOnClickListener(this);

            // Knap til startsiden
            homeButton = (ImageView) findViewById(R.id.imageHome);
            homeButton.setOnClickListener(this);

            //Knap til at starte vejvisning til et valgt POI
            goButton = (ImageView) findViewById(R.id.goButton);
            goButton.setVisibility(View.GONE); // Skjuler knappen indtil den kan bruges
            goButton.setOnClickListener(this);

        }
        getMap();
    }

    // Initierer at hente kortet (asynkront)
    private void getMap(){
        try {
            if (gMap == null) {
                try {
                    SupportMapFragment m = ((SupportMapFragment) getSupportFragmentManager().
                            findFragmentById(R.id.map));
                    Log.d("Kort", "Forsøger at hente nyt kort - asynkront");
                    m.getMapAsync(this);
                } catch (NullPointerException e){
                    Log.d("Kort", "SupportMapFragment m er null, kunne ikke finde map");
                    Toast.makeText(this, "An error occurred, could not fetch map. Sorry", Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                    finish();
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
        SearchButtonFragment.search.setEnabled(true); // Genaktiverer Search-knappen
        Log.d("Kort", "GMapsAktivitet.onMapReady() er kaldt");
        this.gMap = googleMap;

        // Gør det muligt at finde nuværende position og ændre maptype
        gMap.setMyLocationEnabled(true);
        gMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        // Hvis brugeren har indtastet en destination, tilføjes denne på kortet, og kameraet bevæges til positionen
        if(SingleTon.hasDest){
            Location startLoc = SingleTon.myLocation.getLocation();
            if(SingleTon.minutter==0 && SingleTon.timer==0){
                // Der udføres en "normal" destinationssøgning, dvs. kameraet zoomes til destinationens lokation
                Log.i("Kort", getMapsApiDirectionsUrl(startLoc, SingleTon.destPos));
                destMark = gMap.addMarker(new MarkerOptions().
                        position(SingleTon.destPos).
                        title(SingleTon.destAdress)
                        .icon(BitmapDescriptorFactory.defaultMarker(210f))); // Destinationsmarkeren har en anden farve en normale markers
                destMark.showInfoWindow();
                gMap.animateCamera(CameraUpdateFactory.newLatLngZoom(SingleTon.destPos, 12));
            } else {
                Log.d("Kort" , "Der udføres hviletidssøgning. Timer: " + SingleTon.timer + ", Minutter: " + SingleTon.minutter);
                // Der udføres en hviletidssøgning, der beregnes hvor brugeren vil være på det givne tidspunkt, tegnes og zoomes til det pågældende sted
                new ReadTask(gMap, poly, polyLineOptions, this).execute(getMapsApiDirectionsUrl(startLoc, SingleTon.destPos));
            }
        } else {
            // Kortet startes "normalt", der zoomes til nuværende lokation
            LatLng cPos = new LatLng(SingleTon.myLocation.getLocation().getLatitude(), SingleTon.myLocation.getLocation().getLongitude());
            gMap.animateCamera(CameraUpdateFactory.newLatLngZoom(cPos, 12));
        }

        // Tilføjer markers/clusters til kortet samt tilhørende clicklisteners
        mClusterManager = new ClusterManager<ClusterMaker>(this, gMap);
        gMap.setOnMarkerClickListener(mClusterManager);
        gMap.setOnCameraChangeListener(mClusterManager);
        if(SingleTon.searchedSpots!=null){
            int id = 0;
            for (Spot spot : SingleTon.searchedSpots) {
                ClusterMaker mark = new ClusterMaker(new LatLng(Double.valueOf(spot.getLat()), Double.valueOf(spot.getLng())));
                mark.setSnippet(Integer.toString(id)); // Tilføj unikt ID til marker, svarende til indekset for det pågældende spot i listen over spots
                mClusterManager.addItem(mark);
                id++;
            }
            Log.d("Kort", "Tilføjet " + id + " markers(spots) til kortet");
        } else {
            Toast.makeText(this, "No destinations was found. Try to widen your search.", Toast.LENGTH_LONG).show();
            Log.d("Error", "Der er ingen spots at vise på kortet");
        }

        // Clicklistener til når der trykkes på kortet og holdes nede i lang tid
        gMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                Log.d("Kort", "Der klikkes med et langt tryk på kortet: " + latLng.latitude + ", " + latLng.longitude);
                // Der zoomes hvis man er zoomet for langt ud
                if (gMap.getCameraPosition().zoom < 14.5f) {
                    Log.d("Kort", "Der zoomes. Nuværende zoom: " + gMap.getCameraPosition().zoom);
                    gMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
                    Toast.makeText(GMapsFragment.this, "Zoom not close enough.\n\nPress again to add location.", Toast.LENGTH_LONG).show();
                } else {
                    // Ellers kan man tilføje et spot der hvor man har klikket
                    Log.d("Kort", "Der tilføjes et spot");
                    Vibrator vibe = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                    vibe.vibrate(50); // Vibrate for x milliseconds
                    Location loc = new Location("");
                    loc.setLatitude(latLng.latitude);
                    loc.setLongitude(latLng.longitude);
                    addLocation(new AddSpot(loc, GMapsFragment.this));
                }
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
        mClusterManager.setOnClusterItemClickListener(this);

        SearchButtonFragment.progressDialog.dismiss(); // Luk ProgressDialog der "bygger kort"
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
        String mode = "mode=" + "driving"; // Dette kan udelades (er default for Google Directions)
        String units = "units=" + "metric"; // Eventuelt variabel baseret på indstillinger i appen

        // Google Directions bruger som standard hastighedsgrænser for biler. Vha. traffic_model forsøger vi at nedsætte hastigheden
        // så den er mere realistisk for en lastbil der skal følge andre hastighedsgrænser (80 eller 90 km/t på motorveje)
        // !! Google Directions API giver pt. samme resultat uanset hvilken mode der vælges - derfor ganges den modtagne tid med en faktor
        String trafficModel = "traffic_model=" + "pessimistic";
        String departTime = "departure_time=" + "now";

        String params = locations + "&" + mode + "&" + trafficModel + "&" + units + "&" + departTime; // + "&" + key; //
        String output = "json";
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + params;

        Log.d("Rute", "Bygger url til google api");
        Log.d("Rute", "Fra: " + start.getLatitude() + "," + start.getLongitude());
        Log.d("Rute", "Til: " + dest.latitude + "," + dest.longitude);
        Log.d("Rute", "Url: " + url);
        return url;
    }

    @Override
    public void onClick(View v) {
        if (v == zoomLocation) {
            Log.d("Kort", "Der klikkes på Zoomknap");
            // Der zoomes til nuværende lokation
            if (gMap != null) {
                LatLng cPos = new LatLng(SingleTon.myLocation.getLocation().getLatitude(), SingleTon.myLocation.getLocation().getLongitude());
                gMap.animateCamera(CameraUpdateFactory.newLatLngZoom(cPos, 12), 1000, null);
                Log.d("Kort", "Der zoomes til: " + cPos.latitude + ", " + cPos.longitude);
            } else {
                Log.d("Kort", "Der blev forsøgt zoomet til lokation, men gMap er null");
            }
        } else if (v == goButton) {
            Log.d("Kort", "Der klikkes på GO-knap");
            // Der viderestilles til google maps kørselsvejledning. Slutsted medsendes (startsted er nuværende lokation når den udelades)
            if (spot != null) {
                Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                        Uri.parse("http://maps.google.com/maps?&daddr="
                                + spot.getLat() + ","
                                + spot.getLng()));
                intent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");
                startActivity(intent);
            } else {
                Toast.makeText(this, "You must choose where to go!", Toast.LENGTH_LONG).show(); // Safety-text, bør ikke rammes
            }
        } else if (v == homeButton){
            finish(); // Aktiviteten lukkes
        } else if (v == addLocation) {
            addLocation(new AddSpot(SingleTon.myLocation.getLocation(), this));
        }
    }

    // Animerer en marker på Google Maps med en "drop-pin-effekt"
    // Tyv stjålet fra https://guides.codepath.com/android/Google-Maps-API-v2-Usage#falling-pin-animation og udbygget
    private void dropPinEffect(final Marker marker) {
        final android.os.Handler handler = new android.os.Handler();
        final long start = SystemClock.uptimeMillis();
        final long duration = 1500;
        final android.view.animation.Interpolator interpolator = new BounceInterpolator(); // Use the bounce interpolator
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
                    // Når animation er færdig fjernes standard Google Maps marker, og Google Maps Util Cluster gentegnes
                    marker.remove();
                    mClusterManager.cluster();
                    // "Beløn" brugeren for at tilføje en lokation med en kort vibration
                    Vibrator vibe = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                    vibe.vibrate(200); // Vibrate for x milliseconds
                    Toast.makeText(GMapsFragment.this, "Location added", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public boolean onClusterItemClick(ClusterMaker marker) {
        Log.d("Kort", "Der klikkes på en ClusterMarker: " + marker.getPosition().latitude + ", " + marker.getPosition().longitude);
        goButton.setVisibility(View.GONE); // Siker at GO-knappen forsvinder hvis man har trykket på et andet spot før dette

        // Tjek for internetforbindelse
        if (cm.getActiveNetworkInfo() == null){
            Log.d("Error", "Ingen internetforbindelse.");
            Toast.makeText(this, "You have no internet connection!", Toast.LENGTH_SHORT).show();
            return false;
        }

        // Byg eller genbrug dialog - forhindrer memoryleak!!
        if(dialog==null){
            dialog = new Dialog(this);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        }

        // Udpak afhængig af day/night mode
        if (SingleTon.nightMode) {
            dialog.setContentView(R.layout.dialog_spot_night);
        } else {
            dialog.setContentView(R.layout.dialog_spot); // XML-layout til Dialog-boksen
        }

        // Fjern rute fra kort hvis der eksisterer et i forvejen
        if (poly != null) {
            poly.remove();
        }

        try {
            spot = SingleTon.searchedSpots.get(Integer.valueOf(marker.getSnippet())); // TO DO - kan optimeres (meget)

            /*
            Henter rute til POI
            1. Find startlokation
            2. Byg URL til Google Directions API
            3. Kontakt Google Directions API og modtag JSON
            4. Oversæt JSON, gem de "vigtige ting", byg en rute
            5. Opdater Dialogboksen med adresse, afstand og tid
            */
            Location startLoc = SingleTon.myLocation.getLocation();
            String url = GMapsFragment.this.getMapsApiDirectionsUrl(startLoc,  new LatLng(Double.valueOf(spot.getLat()),Double.valueOf(spot.getLng())));
            ReadTask downloadTask = new ReadTask(dialog, gMap, poly, polyLineOptions, this);
            downloadTask.execute(url);

            ImageView adblue = (ImageView) dialog.findViewById(R.id.adblue_imageView);
            ImageView bed = (ImageView) dialog.findViewById(R.id.bed_imageView);
            ImageView bath = (ImageView) dialog.findViewById(R.id.bath_imageView);
            ImageView food = (ImageView) dialog.findViewById(R.id.food_imageView);
            ImageView fuel = (ImageView) dialog.findViewById(R.id.fuel_imageView);
            ImageView wc = (ImageView) dialog.findViewById(R.id.wc_imageView);
            ImageView roadTrain = (ImageView) dialog.findViewById(R.id.roadTrain_img);
            TextView name = (TextView) dialog.findViewById(R.id.inputLocName);

            // Forkort navnet på stedet hvis det er for langt
            String nameTxt = spot.getDesc();
            if (nameTxt.length() > 20){
                nameTxt = nameTxt.substring(0,17) + "...";
            }
            name.setText(nameTxt);

            // Sæt billederne afhængig af hvilken service der er tilgængelig på det pågældende spot
            if (spot.isRoadtrain()) {
                roadTrain.setVisibility(View.VISIBLE);
                roadTrain.setSelected(false);
            } else {
                roadTrain.setVisibility(View.GONE); // Vi viser intet billede hvis der ikke er roadtrain
            }
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

            // Clicklistener til "FIND ROUTE"-knappen. Her tegnes ruten på kortet.
            Button route = (Button) dialog.findViewById(R.id.findRoute_button);
            route.setEnabled(false);
            route.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(GMapsFragment.this, "Drawing route.", Toast.LENGTH_SHORT).show();
                    poly = gMap.addPolyline(polyLineOptions); // Tegn ruten på kortet
                    dialog.hide();
                    goButton.setVisibility(View.VISIBLE); // Viser GO-knappen
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
            Toast.makeText(this, "Could not find id: " + marker.getSnippet(), Toast.LENGTH_LONG).show();
            Log.d("Kort", "Kunne ikke finde det rigtige spot. SingleTon.searchedSpots størrelse: " + SingleTon.searchedSpots.size() + " , der søges efter spot nr: " + marker.getSnippet());
            e.printStackTrace();
        }

        return true;
    }

    @Override
    protected void onStop(){
        gMap.setMyLocationEnabled(false); // Stopper GPS-forbrug når map er åbnet og appen ikke er synlig
        super.onStop();
    }

    @Override
    protected void onResume(){
        if(gMap!=null){
            gMap.setMyLocationEnabled(true); // Genstarter GPS-opdateringer når map er åbnet og appen er synlig
        }
        super.onResume();
    }

    public void setPolyLineOptions(PolylineOptions poly){
        this.polyLineOptions = poly;
    }

    public void drawLines(){
        poly = gMap.addPolyline(polyLineOptions); // Tegn ruten på kortet
    }

    private void addLocation(final AddSpot addSpot){
        Log.d("Kort", "Der klikkes på tilføj sted-knap");
        // Tjek for internetforbindelse
        if (cm.getActiveNetworkInfo() == null){
            Log.d("Error", "Ingen internetforbindelse på AddSpot knap.");
            Toast.makeText(this, "You have no internet connection!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Dialogboks til at tilføje et lokation laves eller genbruges
        if(addDialog==null){
            addDialog = new Dialog(this);
            addDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        }

        // Udpakker layout baseret på day/night mode
        if(SingleTon.nightMode){
            addDialog.setContentView(R.layout.dialog_addlocation_night);
        } else {
            addDialog.setContentView(R.layout.dialog_addlocation); // XML-layout til Dialog-boksen
        }

        // Indtastningsfelt til navn på lokation
        final EditText addLocName = (EditText) addDialog.findViewById(R.id.inputLocName);

        // Luk knappen
        final ImageView close = (ImageView) addDialog.findViewById(R.id.close_addLocation);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addDialog.hide();
                addDialog=null;
            }
        });

        /*
        Herunder følger ImageViews der symboliserer hvilke faciliteter der kan tilføjes.
        Når man klikker på billedet skiftes billedet og boolean sættes så værdi kan aflæses senere
         */
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

        final ImageView roadTrain = (ImageView) addDialog.findViewById(R.id.roadTrain_img);
        roadTrain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!addSpot.roadTrain) {
                    addSpot.roadTrain = true;
                    roadTrain.setImageResource(R.drawable.roadtrain_txt_noback_check);
                    roadTrain.setSelected(true);
                } else {
                    addSpot.roadTrain = false;
                    roadTrain.setImageResource(R.drawable.roadtrain_txt_noback);
                    roadTrain.setSelected(false);
                }
            }
        });

        addDialog.show();

        // Adressen findes asynkront i AddSpot klassen. Der ventes i alt 50 * 100 ms på svar fra GEO-koderen i AddSpot.java
        final TextView address = (TextView) addDialog.findViewById(R.id.loc_addressTxt);
        final Handler h = new Handler();
        h.postDelayed(new Runnable() {
            int p = 0;
            @Override
            public void run() {
                if (addSpot.getAddressTxt() != null) {
                    address.setText(addSpot.getAddressTxt()); // Opdaterer adressefeltet med adressen
                } else if (p<50) {
                    // Prøv igen om 100 ms - vi venter højest 5 sekunder på svar.
                    h.postDelayed(this, 100);
                }
                p++;
            }
        }, 50);

        /*
        Opret lokation knappen
         */
        Button createLocation = (Button) addDialog.findViewById(R.id.createLocButton);
        createLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addDialog.hide(); // Skjul dialogboksen

                // Sikrer os mod nullpointers
                if (SingleTon.searchedSpots == null) {
                    SingleTon.searchedSpots = new ArrayList<Spot>();
                }

                // Tilføjer spot til google map kortet så vi kan animere det
                LatLng latLng = new LatLng(addSpot.loc.getLatitude(), addSpot.loc.getLongitude());
                Marker mark = gMap.addMarker(new MarkerOptions().
                        position(latLng).
                        title(addSpot.getAddressTxt()));
                mark.setSnippet(Integer.toString(SingleTon.searchedSpots.size())); // Set snippet så vi kan fremsøge spot når der klikkes på det

                // tilføj spottet til clusterManageren
                ClusterMaker clustMark = new ClusterMaker(latLng);
                clustMark.setSnippet(mark.getSnippet());
                mClusterManager.addItem(clustMark);

                // Start animationen af markeren
                dropPinEffect(mark);

                // Sikrer det brugerudfyldte navn giver mening
                String name = addLocName.getText().toString();
                if (name.equals("") || name.toUpperCase().equals("INPUT LOCATION NAME")){
                    Log.d("Kort" , "Doven bruger - Dummy name givet til tilføjet lokation.");
                    name = "Truck Stop"; // Generisk navn hvis brugeren er doven
                }
                // Tilføjer spot til den hentede liste af spots, så det har samme funktionalitet som alle andre spots
                Spot newSpot = new Spot(name, addSpot.adblue, addSpot.food, addSpot.bath, addSpot.bed, addSpot.wc, addSpot.fuel, addSpot.roadTrain, String.valueOf(latLng.latitude), String.valueOf(latLng.longitude));
                SingleTon.searchedSpots.add(newSpot);

                // Uploader data til Parse.com
                ParseObject parseSpot = new ParseObject("Spots1");
                parseSpot.put("adblue", newSpot.isAdblue());
                parseSpot.put("bath", newSpot.isBath());
                parseSpot.put("bed", newSpot.isBed());
                parseSpot.put("food", newSpot.isFood());
                parseSpot.put("fuel", newSpot.isFuel());
                parseSpot.put("roadtrain", newSpot.isRoadtrain());
                parseSpot.put("wc", newSpot.isWc());
                parseSpot.put("desc", newSpot.getDesc());
                parseSpot.put("posLat", newSpot.getLat());
                parseSpot.put("posLng", newSpot.getLng());
                parseSpot.saveEventually();
            }
        });
    }
}