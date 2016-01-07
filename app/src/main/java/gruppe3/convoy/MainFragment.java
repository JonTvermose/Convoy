package gruppe3.convoy;


import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;


/**
 * A simple {@link Fragment} subclass.
 */
public class MainFragment extends Fragment {

    private ViewPager mPager;
    private PagerAdapter mPagerAdapter;

    public MainFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rod = inflater.inflate(R.layout.fragment_main, container, false);

        // Instantiate a ViewPager and a PagerAdapter.
        mPager = (ViewPager) rod.findViewById(R.id.pager);
        mPagerAdapter = new MainFragmentPagerAdapter(getChildFragmentManager(), getActivity());
        mPager.setAdapter(mPagerAdapter);

        // Give the TabLayout the ViewPager
        TabLayout tabLayout = (TabLayout) rod.findViewById(R.id.sliding_tabs);
        tabLayout.setupWithViewPager(mPager);

        Button search = (Button) rod.findViewById(R.id.searchButton);
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent i = new Intent(getActivity(), GMapsAktivitet.class);
//                getActivity().startActivity(i);

                getChildFragmentManager()
                        .beginTransaction()
                        .replace(R.id.StartLayoutFrame, new GMapsAktivitet())
                        .setTransitionStyle(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                        .commit();

//                LatLng dest = new LatLng(49.215094, 8.698491);
//
//                Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
//                        Uri.parse("http://maps.google.com/maps?&daddr="
//                                + dest.latitude +","
//                                + dest.longitude));
//                intent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");
//                startActivity(intent);

            }
        });
        return rod;
    }
}
