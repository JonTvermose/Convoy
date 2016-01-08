package gruppe3.convoy.functionality;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.util.Log;
import android.view.View;

import java.util.List;
import java.util.Locale;

import gruppe3.convoy.Main;

/**
 * Created by Jon on 08/01/2016.
 */
public class AddSpot implements View.OnClickListener {

    public Boolean food=false, wc=false, bed=false, bath=false, fuel=false, adblue=false, roadTrain = false;
    public Location loc;
    private String addressTxt;

    public AddSpot(Location location, Context context) {
        this.loc = location;
        try {
            Geocoder geo = new Geocoder(context.getApplicationContext(), Locale.getDefault());
            List<Address> addresses = geo.getFromLocation(loc.getLatitude(), loc.getLongitude(), 1);
            if (addresses.isEmpty()) {
                // TO DO
            }
            else {
                if (addresses.size() > 0) {
                    addressTxt = addresses.get(0).getFeatureName() + ", " + addresses.get(0).getLocality() +", " + addresses.get(0).getAdminArea() + ", " + addresses.get(0).getCountryName();
                    Log.d("Kort", "Address:- " + addresses.get(0).getFeatureName() + addresses.get(0).getAdminArea() + addresses.get(0).getLocality());
                }
            }
        }
        catch (Exception e) {
            addressTxt = "No address found";
            e.printStackTrace(); // getFromLocation() may sometimes fail
        }
    }


    @Override
    public void onClick(View v) {


    }

    public String getAddressTxt(){
        return addressTxt;
    }
}
