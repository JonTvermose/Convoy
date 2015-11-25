package gruppe3.convoy;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import gruppe3.convoy.functionality.BackendSimulator;
import gruppe3.convoy.functionality.MyLocation;
import gruppe3.convoy.functionality.Spot;

public class GMapsAktivitet extends Activity implements OnMapReadyCallback {

    private GoogleMap googleMap;
    private Location lastKnownLocation;
    private MyLocation locationListener;

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


        if(locationListener.getLocation() != null){
            LatLng cPos = new LatLng(locationListener.getLocation().getLatitude(), locationListener.getLocation().getLongitude());
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(cPos, 12));
        } else {
            LatLng cPos = new LatLng(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude());
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(cPos, 12));
            Toast.makeText(this, "Unable to fetch the current location. Using last know location", Toast.LENGTH_SHORT).show();
        }

        Marker mark1 = googleMap.addMarker(new MarkerOptions().position(new LatLng(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude())).title("Sidst kendte sted"));

        BackendSimulator backend = new BackendSimulator();
        for (Spot spot : backend.getMarkers()){
            Marker mark = googleMap.addMarker(new MarkerOptions().position(spot.getPos()).title(spot.getDesc()));
            mark.setDraggable(false);
        }

        locationListener.setMap(googleMap);

    }

}
