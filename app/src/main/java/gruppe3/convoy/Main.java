package gruppe3.convoy;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.HorizontalScrollView;

import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.List;


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

    /**
     * The pager widget, which handles animation and allows swiping horizontally to access previous
     * and next wizard steps.
     */
    private ViewPager mPager;

    /**
     * The pager adapter, which provides the pages to the view pager widget.
     */
    private PagerAdapter mPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Parse.enableLocalDatastore(this);

        Parse.initialize(this);

//        ParseObject testObject = new ParseObject("TestObject");
//        testObject.put("foo", "Yaaaas");
//        testObject.saveInBackground();

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
}
