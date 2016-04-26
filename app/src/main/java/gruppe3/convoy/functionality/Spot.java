package gruppe3.convoy.functionality;

import java.io.Serializable;

/**
 * Created by Jon on 25/11/2015.
 */
public class Spot implements Serializable {

    private int id;
    private long lastUpdated;
    private boolean deleted;

    private String latitude;
    private String longitude;
    private boolean addblue;
    private boolean food;
    private boolean bath;
    private boolean bed;
    private boolean wc;
    private boolean fuel;
    private boolean roadtrain;
    private String name;

    public Spot(String desc, boolean adblue, boolean food, boolean bath, boolean bed, boolean wc, boolean fuel, boolean roadtrain, String lat, String lng) {
        this.name = desc;
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
        this.addblue = addBlue;
        this.food = food;
        this.wc = wc;
        this.bed = bed;
        this.bath = bath;
        this.roadtrain = roadtrain;
        this.longitude = Double.toString(longitude);
        this.latitude = Double.toString(latitude);
        this.name = desc;
    }

    public String getDesc(){
        return name;
    }

    public void setDesc(String desc){
        this.name = desc;
    }

    public String getLat() { return latitude; }

    public void setLat(String lat) {
        this.latitude = lat;
    }

    public String getLng() {
        return longitude;
    }

    public void setLng(String lng) {
        this.longitude = lng;
    }

    public boolean isAdblue() {
        return addblue;
    }

    public void setAdblue(boolean adblue) {
        this.addblue = adblue;
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