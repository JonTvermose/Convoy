package gruppe3.convoy.functionality;

import android.app.Dialog;
import android.os.AsyncTask;
import android.util.Log;

/**
 * Created by Jon on 13/01/2016.
 */
public class ReadTask extends AsyncTask<String, Void, String> {

    private Dialog dialog;
    public ReadTask(Dialog dialog) {
        this.dialog = dialog;
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
            new ParserTask(dialog).execute(result);
        } else {
            // TO DO - klassen kan benyttes til andre ting
        }
    }
}
