package gruppe3.convoy;


import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import java.util.ArrayList;

import gruppe3.convoy.functionality.SingleTon;
import gruppe3.convoy.functionality.Spot;


/**
 * A simple {@link Fragment} subclass.
 */
public class MainFragment extends Fragment {

    private ViewPager mPager;
    private PagerAdapter mPagerAdapter;
    private boolean session = false;
//    public static ProgressDialog progressDialog;

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

//        LinearLayout bFrag = (LinearLayout) rod.findViewById(R.id.mainBottomFragment);

        getFragmentManager()
                .beginTransaction()
                .add(R.id.mainBottomFragment, new SearchButtonFragment())
                .setTransitionStyle(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .commit();


        return rod;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        AdvancedFragment.autocompleteFragment.onActivityResult(requestCode, resultCode, data);
    }
}
