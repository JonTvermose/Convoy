package gruppe3.convoy.functionality;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import gruppe3.convoy.Main;

/**
 * Created by Jon on 08/01/2016.
 */
public class AddSpot {

    public Boolean food=false, wc=false, bed=false, bath=false, fuel=false, adblue=false, roadTrain = false;
    public Location loc;
    private String addressTxt;
    private Context context;

    public AddSpot(Location location, final Context context) {
        this.context=context;
        this.loc = location;
        new AsyncTask<Void, Void, List<Address>>(){
            @Override
            protected List<Address> doInBackground(Void... params) {
                try {
                    Log.d("Kort" , "Forsøger at finde adresse");
                    Geocoder geo = new Geocoder(AddSpot.this.context.getApplicationContext(), Locale.getDefault());
                    return geo.getFromLocation(loc.getLatitude(), loc.getLongitude(), 1);
                }
                catch (IOException e) {
                    addressTxt = "Error.";
                    Log.d("Kort" , "IO Exception");
                    e.printStackTrace(); // getFromLocation() may sometimes fail
                }
                return null;
            }

            @Override
            protected void onPostExecute(List<Address> addresses){
                if (addresses==null){
                    // TO DO - fejlhåndtering hvis der ikke modtages adresse fra GEOcoder server
                    return;
                }
                if (addresses.isEmpty()) {
                    addressTxt = "No address found";
                }
                else {
                    if (addresses.size() > 0) {
                        addressTxt = addresses.get(0).getFeatureName() + ", " + addresses.get(0).getLocality() +", " + addresses.get(0).getAdminArea() + ", " + addresses.get(0).getCountryName();
                        Log.d("Kort", "Address:- " + addresses.get(0).getFeatureName() + addresses.get(0).getAdminArea() + addresses.get(0).getLocality());
                        addressTxt = addressTxt.replace("null,", ""); // Fjerner stygge grimme null-ord med komma....
                        addressTxt = addressTxt.replace("null", ""); // Fjerner stygge grimme null-ord....
                    }
                }
            }
        }.execute();
    }

    public String getAddressTxt(){
        return addressTxt;
    }
}
