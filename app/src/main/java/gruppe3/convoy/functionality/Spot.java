package gruppe3.convoy.functionality;

import java.io.Serializable;

/**
 * Created by Jon on 25/11/2015.
 */
public class Spot implements Serializable {

    private int id;
    private long lastUpdated;
    private boolean deleted;

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

    public Spot(String desc, boolean adblue, boolean food, boolean bath, boolean bed, boolean wc, boolean fuel, boolean roadtrain, String lat, String lng) {
        this.desc = desc;
        this.setAdblue(adblue);
        this.setFood(food);
        this.setBath(bath);
        this.setBed(bed);
        this.setWc(wc);
        this.setFuel(fuel);
        this.setRoadtrain(roadtrain);
        this.setLat(lat);
        this.setLng(lng);
    }

    public Spot(int id, boolean addBlue, boolean food, boolean wc, boolean bed, boolean bath, boolean roadtrain, double longitude, double latitude, String desc, int lastUpdated, boolean deleted) {
        this.id = id;
        this.lastUpdated = lastUpdated;
        this.deleted = deleted;
        this.adblue = addBlue;
        this.food = food;
        this.wc = wc;
        this.bed = bed;
        this.bath = bath;
        this.roadtrain = roadtrain;
        this.lng = Double.toString(longitude);
        this.lat = Double.toString(latitude);
        this.desc = desc;
    }

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

    public long getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(long lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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