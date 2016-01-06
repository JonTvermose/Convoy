package gruppe3.convoy;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.maps.model.LatLng;
import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

import gruppe3.convoy.functionality.MyLocation;
import gruppe3.convoy.functionality.Spot;


public class Main extends AppCompatActivity {

    public static String dest,maxSpeed,roadTrain;
    public static int timer,minutter;

    public static Boolean  food=false,
                    wc=false,
                    bed=false,
                    bath=false,
                    fuel=false,
                    adblue=false;
    Button search;
    ArrayList<Spot> spots;

    /**
     * The pager widget, which handles animation and allows swiping horizontally to access previous
     * and next wizard steps.
     */
    private ViewPager mPager;

    /**
     * The pager adapter, which provides the pages to the view pager widget.
     */
    private PagerAdapter mPagerAdapter;

    public static MyLocation myLocation;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        myLocation = new MyLocation();
        myLocation.startLocationService(this); // Starter stedbestemmelse

//        Parse.enableLocalDatastore(this);

//        Parse.initialize(this);

//        ParseObject testObject = new ParseObject("TestObject");
//        testObject.put("foo", "Yaaaas");
//        testObject.saveInBackground();

//        LatLng pos = new LatLng(75.3456,20.4256);
//
//        Spot test = new Spot("München Truck Stop",true ,false ,true ,false ,true ,false ,true, pos);
//
//        test.pushToDB();

        setContentView(R.layout.activity_main);

        search = (Button) findViewById(R.id.searchButton);

        ParseQuery<ParseObject> query = ParseQuery.getQuery("TestObject");
        query.whereExists("foo");
        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> fooList, ParseException e) {
                if (e == null) {
                    for(int i=0;fooList.size()>i;i++){
                        if (fooList.get(i).getString("foo").length()==6){
                            System.out.println(fooList.get(i).getString("foo"));
                            search.setText(fooList.get(i).getString("foo"));
                        }
                    }

                } else {
                    Log.d("score", "Error: " + e.getMessage());
                }
            }
        });

        ParseQuery<ParseObject> query2 = ParseQuery.getQuery("Spots");
//        query2.whereExists("objectId");
        query2.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> spotList, ParseException e) {
                if (e == null) {
                    Main.this.spots = new ArrayList<Spot>();
                    for (int i = 0; spotList.size() > i; i++) {

                        LatLng pos = new LatLng(spotList.get(i).getDouble("posLat"), spotList.get(i).getDouble("posLng"));
                        Main.this.spots.add(new Spot(
                                spotList.get(i).getString("desc"),
                                spotList.get(i).getBoolean("adblue"),
                                spotList.get(i).getBoolean("food"),
                                spotList.get(i).getBoolean("bath"),
                                spotList.get(i).getBoolean("bed"),
                                spotList.get(i).getBoolean("wc"),
                                spotList.get(i).getBoolean("fuel"),
                                spotList.get(i).getBoolean("roadtrain"),
                                pos
                        ));

                    }

                } else {
                    Log.d("score", "Error: " + e.getMessage());
                }
            }
        });

        // Instantiate a ViewPager and a PagerAdapter.
        mPager = (ViewPager) findViewById(R.id.pager);
        mPagerAdapter = new MainFragmentPagerAdapter(getSupportFragmentManager(),Main.this);
        mPager.setAdapter(mPagerAdapter);

        // Give the TabLayout the ViewPager
        TabLayout tabLayout = (TabLayout) findViewById(R.id.sliding_tabs);
        tabLayout.setupWithViewPager(mPager);

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println(spots.get(0).getDesc());
                Intent i = new Intent(Main.this, GMapsAktivitet.class);
//                dest=AdvancedFragment.dest.getText().toString();
                timer = AdvancedFragment.timer.getValue();
                minutter = AdvancedFragment.minutter.getValue();

//                maxSpeed=AdvancedFragment.maxSpeed.getText().toString();
//                roadTrain=AdvancedFragment.roadTrain.get.getText().toString();
                Main.this.startActivity(i);

            }
        });
    }

    @Override
         protected void onStop(){
        myLocation.stopLocationUpdates(); // Stopper opdateringen fra GPS/Network
        super.onStop();
    }

    @Override
    protected void onStart(){
        myLocation.onResume(); // Start opdatering fra GPS
        super.onStart();
    }
}
