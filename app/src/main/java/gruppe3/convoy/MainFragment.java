package gruppe3.convoy;


import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import gruppe3.convoy.functionality.SingleTon;


/**
 * A simple {@link Fragment} subclass.
 */
public class MainFragment extends Fragment {

    private ViewPager mPager;
    private PagerAdapter mPagerAdapter;
    public static Button search;
    private boolean session = false;

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

        // Search-knappen's onClickListener
        search = (Button) rod.findViewById(R.id.searchButton);
        if(!session){
            search.setEnabled(false);
            search.setText(SingleTon.searchTxt1);
            search.setTextSize(TypedValue.COMPLEX_UNIT_SP, 30);
        }

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                session = true;
                SingleTon.roadTrain = AdvancedFragment.roadTrain.isChecked();
                SingleTon.timer = AdvancedFragment.timer.getValue();
                SingleTon.minutter = AdvancedFragment.minutter.getValue();
                SingleTon.dest = AdvancedFragment.dest.getText().toString();

                getFragmentManager()
                        .beginTransaction()
                        .replace(R.id.MainFragment, new GMapsFragment())
                        .setTransitionStyle(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                        .addToBackStack(null)
                        .commit();
            }
        });
        return rod;
    }
}
