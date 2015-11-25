package gruppe3.convoy.functionality;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;

/**
 * Created by Jon on 25/11/2015.
 */
public class MyLocation implements LocationListener {

    private Location current;
    private GoogleMap map;
    private Context context;
    private boolean adjustedCamera;
    private final int ZOOM = 12;

    public MyLocation(){
        adjustedCamera = false;
    }

    @Override
    public void onLocationChanged(Location location) {
        this.current = location;
        Log.d("Location", "Current Location updated: " + location.getLatitude() + " : " + location.getLongitude());
        if (!adjustedCamera){
            adjustedCamera = true;
            updateMapView();
            Toast.makeText(context, "Location updated", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    public Location getLocation(){
        return current;
    }

    public void setMap(GoogleMap googleMap, Context context) {
        this.map = googleMap;
        this.context = context;
    }

    public void updateMapView(){
        LatLng cPos = new LatLng(current.getLatitude(), current.getLongitude());
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(cPos, ZOOM), 2000, null);
    }
}
