package gruppe3.convoy.functionality;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

/**
 * Created by Jon on 09/01/2016.
 */
public class ClusterMaker implements ClusterItem {

    private final LatLng mPosition;
    private String snippet;

    public ClusterMaker(LatLng mPosition){
        this.mPosition = mPosition;
    }

    @Override
    public LatLng getPosition() {
        return mPosition;
    }

    public String getSnippet(){
        return snippet;
    }

    public void setSnippet(String snippet){
        this.snippet=snippet;
    }
}
