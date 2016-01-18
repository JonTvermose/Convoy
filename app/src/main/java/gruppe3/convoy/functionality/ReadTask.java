package gruppe3.convoy.functionality;

import android.app.Dialog;
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import gruppe3.convoy.GMapsFragment;

/**
 * Created by Jon on 13/01/2016.
 */
public class ReadTask extends AsyncTask<String, Void, String> {

    private Dialog dialog;
    private GoogleMap gMap;
    private Polyline poly;
    private PolylineOptions polyLineOptions;
    private GMapsFragment gMapsFragment;

    public ReadTask(Dialog dialog, GoogleMap gMap, Polyline poly, PolylineOptions polylineOptions, GMapsFragment gMapsFragment){
        this.gMap=gMap;
        this.poly=poly;
        this.polyLineOptions=polylineOptions;
        this.dialog = dialog;
        this.gMapsFragment =gMapsFragment;
    }

    public ReadTask(GoogleMap gMap, Polyline poly, PolylineOptions polylineOptions, GMapsFragment gMapsFragment){
        this.gMap=gMap;
        this.poly=poly;
        this.polyLineOptions=polylineOptions;
        this.gMapsFragment =gMapsFragment;
    }

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
        if(dialog!=null){
            super.onPostExecute(result);
            new ParserTask(dialog, gMap, poly, polyLineOptions, gMapsFragment).execute(result);
        } else {
            super.onPostExecute(result); // TO DO - klassen kan benyttes til andre ting
            new ParserTask(gMap, poly, polyLineOptions, gMapsFragment).execute(result);
        }
    }
}
