package gruppe3.convoy.functionality;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.Random;

import gruppe3.convoy.Main;

/**
 * Created by Jon on 25/11/2015.
 */
public class BackendSimulator {

    private ArrayList<Spot> markers;

    public BackendSimulator(){
        markers = new ArrayList<Spot>();
        addTestData();
    }

    /**
     * Simulerer testdata:
     * 250 parkeringspladser placeret i europa
     */
    private void addTestData(){
        Random r = new Random();

        for (int i=0; i < 500; i++){
            LatLng pos = new LatLng(r.nextDouble()*12+42, r.nextDouble()*28+5);
//            markers.add(new Spot("Testspot"+i, r.nextBoolean(), r.nextBoolean(), r.nextBoolean(), r.nextBoolean(), r.nextBoolean(), r.nextBoolean(), r.nextBoolean(), pos));
            Spot plet = new Spot("Testspot"+i, r.nextBoolean(), r.nextBoolean(), r.nextBoolean(), r.nextBoolean(), r.nextBoolean(), r.nextBoolean(), r.nextBoolean(), pos);
            if(plet.isAdblue()== Main.adblue && plet.isBath()== Main.bath && plet.isBed()== Main.bed && plet.isWc()== Main.wc && plet.isFood()== Main.food && plet.isFuel()== Main.fuel){
                markers.add(plet);
            }
        }
    }

    public ArrayList<Spot> getMarkers(){
        return markers;
    }

    public ArrayList<Spot> getMarkers(LatLng pos){
        // DO something
        return markers;
    }

    public ArrayList<Spot> getMarkers(LatLng pos, boolean[] choices){
        // DO something
        return markers;
    }




}
