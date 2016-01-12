package gruppe3.convoy;


import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import gruppe3.convoy.functionality.MyLocation;
import gruppe3.convoy.functionality.SingleTon;


/**
 * A simple {@link Fragment} subclass.
 */
public class MainFragment extends Fragment {

    private ViewPager mPager;
    private PagerAdapter mPagerAdapter;
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

        getFragmentManager()
                .beginTransaction()
                .add(R.id.mainBottomFragment, new ProgressFragment())
                .commit();

        SingleTon.myLocation = new MyLocation();
        SingleTon.myLocation.startLocationService(getActivity()); // Starter stedbestemmelse

        // Følgende styrer animationen for ProgressBar på startskærmen når der loades data
        final Handler h = new Handler();
        h.postDelayed(new Runnable() {
            int p = 10; // Tælles løbende op til 50
            int d = 50; // Når location er opdateret sættes progress til 50%
            @Override
            public void run() {
                if (MyLocation.POSUPDATED) {
                    // Hvis positionen er opdateret: opdater progressbar og start datahentning
                    if (d<90){
                        ProgressFragment.progressBarTxt.setText(SingleTon.searchTxt2);
                        ProgressFragment.progressBar.setProgress(d);
                        d++;
                        d++;
                    }
                    if (SingleTon.dataLoadDone){
                        ProgressFragment.progressBar.setProgress(100);
                        getChildFragmentManager().findFragmentById(R.id.pager).getChildFragmentManager().findFragmentById(R.id.autocomplete).getView().setVisibility(View.VISIBLE);
                        getFragmentManager()
                                .beginTransaction()
                                .setCustomAnimations(R.animator.fade_in, R.animator.fade_out)
                                .replace(R.id.mainBottomFragment, new SearchButtonFragment())
                                .commit();
                    } else {
                        h.postDelayed(this, 100);
                    }
                } else {
                    // Vent yderligere 100 ms indtil positionen er opdateret og opdater progressbar
                    h.postDelayed(this, 100);
                    p++;
                    if (p <= 50) {
                        ProgressFragment.progressBar.setProgress(p);
                    }
                }
            }
        }, 100);

        return rod;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        AdvancedFragment.autocompleteFragment.onActivityResult(requestCode, resultCode, data);
    }

}
