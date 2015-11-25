package gruppe3.convoy.functionality;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by Jon on 25/11/2015.
 */
public class Spot {
    private LatLng pos;
    private boolean adblue;
    private boolean food;
    private boolean bath;
    private boolean bed;
    private boolean wc;
    private boolean fuel;
    private boolean roadtrain;
    private String desc;

    public Spot(String desc, boolean adblue, boolean food, boolean bath, boolean bed, boolean wc, boolean fuel, boolean roadtrain, LatLng pos){
        this.desc = desc;
        this.setAdblue(adblue);
        this.setFood(food);
        this.setBath(bath);
        this.setBed(bed);
        this.setWc(wc);
        this.setFuel(fuel);
        this.setRoadtrain(roadtrain);
        this.setPos(pos);
    }

    public String getDesc(){
        return desc;
    }

    public void setDesc(String desc){
        this.desc = desc;
    }

    public LatLng getPos() {
        return pos;
    }

    public void setPos(LatLng pos) {
        this.pos = pos;
    }

    public boolean isAdblue() {
        return adblue;
    }

    public void setAdblue(boolean adblue) {
        this.adblue = adblue;
    }

    public boolean isFood() {
        return food;
    }

    public void setFood(boolean food) {
        this.food = food;
    }

    public boolean isBath() {
        return bath;
    }

    public void setBath(boolean bath) {
        this.bath = bath;
    }

    public boolean isBed() {
        return bed;
    }

    public void setBed(boolean bed) {
        this.bed = bed;
    }

    public boolean isWc() {
        return wc;
    }

    public void setWc(boolean wc) {
        this.wc = wc;
    }

    public boolean isFuel() {
        return fuel;
    }

    public void setFuel(boolean fuel) {
        this.fuel = fuel;
    }

    public boolean isRoadtrain() {
        return roadtrain;
    }

    public void setRoadtrain(boolean roadtrain) {
        this.roadtrain = roadtrain;
    }
}
