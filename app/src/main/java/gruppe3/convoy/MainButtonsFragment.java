package gruppe3.convoy;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


public class MainButtonsFragment extends Fragment {


    public static MainButtonsFragment newInstance(String param1, String param2) {
        MainButtonsFragment fragment = new MainButtonsFragment();
        return fragment;
    }

    public MainButtonsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        }




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View knapper = inflater.inflate(R.layout.fragment_main_buttons, container, false);
        return knapper;
    }


}
