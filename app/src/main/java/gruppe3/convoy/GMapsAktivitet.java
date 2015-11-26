package gruppe3.convoy;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gmaps_aktivitet);

        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        locationListener = new MyLocation();
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 5, locationListener); // Mindste tid mellem update = 1 sek, minmumsdistance = 5 meter
        // Find sidste kendte lokation
        String locationProvider = LocationManager.NETWORK_PROVIDER; // Or use LocationManager.GPS_PROVIDER
        lastKnownLocation = locationManager.getLastKnownLocation(locationProvider);

        // TESTKODE
        if(lastKnownLocation==null){
            lastKnownLocation = new Location("");
        }
        lastKnownLocation.setLatitude(55.4);
        lastKnownLocation.setLongitude(0.4);

        try {
            if (googleMap == null) {
                MapFragment m = ((MapFragment) getFragmentManager().
                        findFragmentById(R.id.map));
                m.getMapAsync(this);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
        // Gør det muligt at finde nuværende position og ændre maptype
        googleMap.setMyLocationEnabled(true);
        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);


        if (locationListener.getLocation() != null) {
            LatLng cPos = new LatLng(locationListener.getLocation().getLatitude(), locationListener.getLocation().getLongitude());
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(cPos, 12));
        } else {
            LatLng cPos = new LatLng(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude());
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(cPos, 12), 2000, null);
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
                            Toast.makeText(GMapsAktivitet.this, "Closing dialog", Toast.LENGTH_SHORT).show();
                            dialog.hide();
                        }
                    });
                    dialog.show();
                } catch (Exception e){
                    // TO DO : Fejlhåndtering
                    // Hvad skal der ske hvis man klikker på en marker som vi ikke kan identificere?
                }

                return true;
            }
        });
        locationListener.setMap(googleMap, this);
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
