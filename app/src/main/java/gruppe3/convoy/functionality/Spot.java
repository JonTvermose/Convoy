package gruppe3.convoy.functionality;

import com.google.android.gms.maps.model.LatLng;
import com.parse.ParseObject;

import java.io.Serializable;

/**
 * Created by Jon on 25/11/2015.
 */
public class Spot implements Serializable {

    private String lat;
    private String lng;
    private boolean adblue;
    private boolean food;
    private boolean bath;
    private boolean bed;
    private boolean wc;
    private boolean fuel;
    private boolean roadtrain;
    private String desc;
    private String createdAt;

    public Spot(String desc, boolean adblue, boolean food, boolean bath, boolean bed, boolean wc, boolean fuel, boolean roadtrain, String createdAt, String lat, String lng) {

        this.desc = desc;
        this.setAdblue(adblue);
        this.setFood(food);
        this.setBath(bath);
        this.setBed(bed);
        this.setWc(wc);
        this.setFuel(fuel);
        this.setRoadtrain(roadtrain);
        this.createdAt = createdAt;
        this.setLat(lat);
        this.setLng(lng);
    }

    public Spot(String desc, boolean adblue, boolean food, boolean bath, boolean bed, boolean wc, boolean fuel, boolean roadtrain, String lat, String lng) {

        this.desc = desc;
        this.setAdblue(adblue);
        this.setFood(food);
        this.setBath(bath);
        this.setBed(bed);
        this.setWc(wc);
        this.setFuel(fuel);
        this.setRoadtrain(roadtrain);
        this.createdAt = createdAt;
        this.setLat(lat);
        this.setLng(lng);
    }

    public String getCreatedAt() { return createdAt; }

    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

    public String getDesc(){
        return desc;
    }

    public void setDesc(String desc){
        this.desc = desc;
    }

    public String getLat() { return lat; }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLng() {
        return lng;
    }

    public void setLng(String lng) {
        this.lng = lng;
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

    public void pushToDB(){
        ParseObject spotObject = new ParseObject("Spots");
        spotObject.put("desc", desc);
        spotObject.put("adblue", adblue);
        spotObject.put("food", food);
        spotObject.put("bath", bath);
        spotObject.put("bed", bed);
        spotObject.put("wc", wc);
        spotObject.put("fuel", fuel);
        spotObject.put("roadtrain", roadtrain);
        spotObject.put("posLat", lat);
        spotObject.put("posLng", lng);
        spotObject.put("createdAt", createdAt);
        spotObject.saveInBackground();
    }
}
