package gruppe3.convoy;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * Created by Christian on 23-11-2015.
 */

public class MainFragmentPagerAdapter extends FragmentPagerAdapter {
    final int PAGE_COUNT = 2;
    private String tabTitles[] = new String[] { "Preferences", "Destination settings"};
    private Context context;
    private Fragment mainButton = new MainButtonsFragment(),advanced = new AdvancedFragment();
    private Fragment[] pageTitles = new Fragment[]{mainButton,advanced};


    public MainFragmentPagerAdapter(FragmentManager fm, Context context) {
        super(fm);
        this.context = context;


    }

    @Override
    public int getCount() {
        return PAGE_COUNT;
    }

    @Override
    public Fragment getItem(int position) {
//        switch (position) {
//            case 0:
//                return new MainButtonsFragment();
//            case 1:
//                return new AdvancedFragment();
//        }
//        return null;
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

