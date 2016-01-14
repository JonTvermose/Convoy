package gruppe3.convoy;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * Created by Christian on 23-11-2015.
 */

public class MainFragmentPagerAdapter extends FragmentPagerAdapter {
    final int PAGE_COUNT = 3;
    private String[] tabTitles;
    private Context context;
    private Fragment mainButton, advanced, settings;
    private Fragment[] pageTitles;


    public MainFragmentPagerAdapter(FragmentManager fm, Context context) {
        super(fm);
        this.context = context;
        mainButton = new MainButtonsFragment();
        advanced = new AdvancedFragment();
        settings = new SettingsFragment();
        pageTitles = new Fragment[]{mainButton, advanced, settings};
        tabTitles = new String[]{"Preferences", "Destination", "Settings"};
    }

    @Override
    public int getCount() {
        return PAGE_COUNT;
    }

    @Override
    public Fragment getItem(int position) {
        return pageTitles[position];
    }

    @Override
    public CharSequence getPageTitle(int position) {
        // Generate title based on item position
        return tabTitles[position];
    }

    public Fragment getMainFrag(){
        return pageTitles[0];
    }
}

