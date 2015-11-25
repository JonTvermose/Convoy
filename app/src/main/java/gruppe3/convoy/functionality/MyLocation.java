package gruppe3.convoy.functionality;

import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;

/**
 * Created by Jon on 25/11/2015.
 */
public class MyLocation implements LocationListener {

    private Location current;
    private GoogleMap map;
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

    public void setMap(GoogleMap googleMap) {
        this.map = googleMap;
    }

    public void updateMapView(){
        LatLng cPos = new LatLng(current.getLatitude(), current.getLongitude());
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(cPos, ZOOM));
    }
}
