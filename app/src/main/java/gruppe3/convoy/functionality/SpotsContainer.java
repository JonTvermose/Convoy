package gruppe3.convoy.functionality;

import java.util.ArrayList;

/**
 *
 * @author Jon
 */
public class SpotsContainer {

    private ArrayList<Spot> spots;
    private long lastUpdated;

    public SpotsContainer(ArrayList<Spot> spots, long lastUpdated){
        this.spots = spots;
        this.lastUpdated = lastUpdated;
    }

    public ArrayList<Spot> getSpots() {
        return spots;
    }

    public void setSpots(ArrayList<Spot> spots) {
        this.spots = spots;
    }

    public long getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(long lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    @Override
    public String toString(){
        String out = "LastUpdated: " + Long.toString(lastUpdated) + ": \n";
        for(Spot spot : this.spots){
            out += "SpotId: " + spot.getId() + ", SpotLat: " + spot.getLat() + ", SpotLong: " + spot.getLat();
        }
        return out;
    }
}

