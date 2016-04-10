package gruppe3.convoy.functionality;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Jon on 10/04/2016.
 */
public class SpotsJSONParser {


    public ArrayList<Spot> parse(JSONArray jArray) {
        ArrayList<Spot> spots = new ArrayList<>();

        try {
            JSONObject jo;
            JSONArray ja = jArray;
            for (int i = 0; i < ja.length(); i++) {
                jo = ja.getJSONObject(i);
                Spot spot = new Spot(jo.getInt("id"), jo.getBoolean("addBlue"), jo.getBoolean("food"), jo.getBoolean("wc"),
                        jo.getBoolean("bed"), jo.getBoolean("bath"), jo.getBoolean("roadtrain"), jo.getDouble("longitude"), jo.getDouble("latitude"),
                        jo.getString("name"), jo.getInt("lastUpdated"), jo.getBoolean("deleted"));
                spots.add(spot);
            }
        } catch (JSONException e) {
            e.printStackTrace();
            System.err.println("Fejl i JSONArray parsing.");
            return null;
        }
        return spots;
    }
}






