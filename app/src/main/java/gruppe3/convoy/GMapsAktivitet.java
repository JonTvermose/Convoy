package gruppe3.convoy;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

import gruppe3.convoy.functionality.BackendSimulator;
import gruppe3.convoy.functionality.MyLocation;
import gruppe3.convoy.functionality.Spot;

public class GMapsAktivitet extends Activity implements OnMapReadyCallback {

    private GoogleMap googleMap;
    private Location lastKnownLocation;
    private MyLocation locationListener;
    private BackendSimulator backend;
    private ArrayList<Spot> spots;
    private final int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private final int UPDATE_INTERVAL = 1000; // GPS update interval i ms

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gmaps_aktivitet);

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Hvis vi ikke har permissions skal vi bede om permission
            Log.d("Access", "Mangler adgang til ACCESS_FINE_LOCATION");

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
                // TODO
                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_CONTACTS},
                        MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);

                // MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        } else {
            // Hvis vi har tilladelse i orden startes maps bare
            Log.d("Access", "ACCESS_FINE_LOCATION er ok");
            getMap();
        }
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
        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        locationListener = new MyLocation();
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, UPDATE_INTERVAL, 0, locationListener); // Mindste tid mellem update = 0 ms, minmumsdistance = 0 meter
        // Find sidste kendte lokation
        String locationProvider = LocationManager.GPS_PROVIDER; // Or use LocationManager.GPS_PROVIDER
        lastKnownLocation = locationManager.getLastKnownLocation(locationProvider);

        if(lastKnownLocation==null){
            lastKnownLocation = new Location("");
            lastKnownLocation.setLatitude(0);
            lastKnownLocation.setLongitude(0);
        }
         lastKnownLocation.setLatitude(55); // TESTKODE
         lastKnownLocation.setLongitude(12); // TESTKODE

        try {
            if (googleMap == null) {
                MapFragment m = ((MapFragment) getFragmentManager().
                        findFragmentById(R.id.map));
                System.out.println("**** Henter nyt kort *****"); // TESTKODE
                m.getMapAsync(this);
            } else{
                System.out.println("**** Kort eksisterer allerede *****"); // TESTKODE
                onMapReady(googleMap);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
        locationListener.setMap(googleMap, this);
        // Gør det muligt at finde nuværende position og ændre maptype
        googleMap.setMyLocationEnabled(true);
        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);


        if (locationListener.getLocation() != null) {
            LatLng cPos = new LatLng(locationListener.getLocation().getLatitude(), locationListener.getLocation().getLongitude());
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(cPos, 12));
        } else {
            LatLng cPos = new LatLng(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude());
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(cPos, 6), 2000, null);
            Toast.makeText(this, "Unable to fetch the current location. Using last know location", Toast.LENGTH_SHORT).show();
        }

        // TESTKODE - sætter en marker på sidste kendte sted
        // Marker mark1 = googleMap.addMarker(new MarkerOptions().position(new LatLng(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude())).title("Sidst kendte sted"));

        backend = new BackendSimulator();
        spots = backend.getMarkers(); // Hent spots fra serveren

        // Tilføjer markers til Google Maps
        for (Spot spot : spots) {
            Marker mark = googleMap.addMarker(new MarkerOptions().position(spot.getPos()).title(spot.getDesc()));
            mark.setDraggable(false);
        }

        // Clicklistener til markers. Når man klikker på en marker åbnes en Dialog-boks
        googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                final Dialog dialog = new Dialog(GMapsAktivitet.this);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.fragment_spot); // XML-layout til Dialog-boksen
                Spot spot;
                try {
                    spot = getSpot(marker.getTitle());

                    TextView title = (TextView) dialog.findViewById(R.id.title_TextView);
                    title.setText(spot.getDesc());

                    ImageView adblue = (ImageView) dialog.findViewById(R.id.adblue_imageView);
                    ImageView bed = (ImageView) dialog.findViewById(R.id.bed_imageView);
                    ImageView bath = (ImageView) dialog.findViewById(R.id.bath_imageView);
                    ImageView food = (ImageView) dialog.findViewById(R.id.food_imageView);
                    ImageView fuel = (ImageView) dialog.findViewById(R.id.fuel_imageView);
                    ImageView wc = (ImageView) dialog.findViewById(R.id.wc_imageView);

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
                    Button route = (Button) dialog.findViewById(R.id.findRoute_button);
                    route.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Toast.makeText(GMapsAktivitet.this, "Finding route. Please wait...", Toast.LENGTH_LONG).show();
                            dialog.hide(); // TO DO
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
                    Toast.makeText(GMapsAktivitet.this, "Could not find matching POI: " + marker.getTitle(), Toast.LENGTH_LONG).show();
                }

                return true;
            }
        });

    }

    // Finder hvilken spot der er trykket på ud fra en beskrivelsestekst. Skal optimeres!!
    private Spot getSpot(String desc) throws Exception {
        for (Spot spot : spots){
            if(spot.getDesc().equals(desc)){
                return spot;
            }
        }
        Log.d("Error", "Kunne ikke finde spot: " + desc);
        throw new Exception("Error. Could not find: " + desc);
    }

}
