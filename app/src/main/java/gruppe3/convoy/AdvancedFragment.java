package gruppe3.convoy;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.Toast;

import com.google.android.gms.location.places.Place;

import gruppe3.convoy.functionality.SingleTon;


/**
 * A simple {@link Fragment} subclass.
 */
public class AdvancedFragment extends Fragment implements NumberPicker.OnValueChangeListener, View.OnClickListener{

    private static NumberPicker timer;
    private static NumberPicker minutter;
    private String[] hours,mins;
    private View rod;
    private static LinearLayout line;

//    public static SupportPlaceAutocompleteFragment autocompleteFragment;
    private Place place;

    public AdvancedFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        System.out.println("Advanced onCreate");

        try {
            if(SingleTon.nightMode){
                rod = inflater.inflate(R.layout.fragment_advanced_night, container, false);
            } else {
                rod = inflater.inflate(R.layout.fragment_advanced, container, false);
            }
        } catch (InflateException e){
            System.out.println("Advanced catch");
            return rod;
        }

        getFragmentManager()
                .beginTransaction()
                .add(R.id.autocompleteholder, new AutoCompleteFragment())
                .commit();

        timer = (NumberPicker) rod.findViewById(R.id.timer_numberPicker);
        hours = new String[24];
        for(int i =0; i <= 9; i++)
            hours[i] = "0" + Integer.toString(i);
        for(int i=10; i<hours.length; i++)
            hours[i] = Integer.toString(i);
        timer.setMinValue(0);
        timer.setMaxValue(23);
        timer.setWrapSelectorWheel(true);
        timer.setDisplayedValues(hours);
        timer.setValue(SingleTon.timer);
        timer.setOnValueChangedListener(this);

        minutter = (NumberPicker) rod.findViewById(R.id.minutter_numberPicker);
        mins = new String[12];
        mins[0] = "00";
        mins[1] = "05";
        for(int i=2; i<mins.length; i++)
            mins[i] = Integer.toString(i * 5);
        minutter.setMinValue(0);
        minutter.setMaxValue(11);
        minutter.setWrapSelectorWheel(true);
        minutter.setDisplayedValues(mins);
        minutter.setValue(SingleTon.minutter/5);
        minutter.setOnValueChangedListener(this);

        line = (LinearLayout) rod.findViewById(R.id.linje2);
        line.setOnClickListener(this);

        if(!SingleTon.hasDest | place == null){
            Log.d("Advanced" , "Destination nulstilles");
            SingleTon.minutter = 0;
            SingleTon.timer = 0;
            SingleTon.hasDest = false;
            place = null;
            disableNumberPicker(); // Låser numberPicker indtil der er indtastet en gyldig destination
        } else {
            Log.d("Advanced", "Destination nulstilles ikke");
//            this.onPlaceSelected(place);
            enableNumberPicker();
        }

        return rod;
    }

    // Skriver værdien af numberpicker til SingleTon
    @Override
    public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
        if(picker==minutter){
            SingleTon.minutter = Integer.valueOf(mins[newVal]);
            Log.d("Advanced", "Minutter er sat til: " + mins[newVal]);
        } else if (picker == timer){
            SingleTon.timer = Integer.valueOf(hours[newVal]);
            Log.d("Advanced", "Timer er sat til: " + hours[newVal]);
        }
        if(SingleTon.minutter!=0 | SingleTon.timer!=0){
            String srcTxt = "Find Truck Stop in ";
            if(SingleTon.timer!=0){
                SearchButtonFragment.search.setText(srcTxt + translate(SingleTon.timer) + ":" + translate(SingleTon.minutter));
            } else {
                SearchButtonFragment.search.setText(srcTxt + translate(SingleTon.minutter) + " mins");
            }
        } else {
            SearchButtonFragment.search.setText("Find Truck Stop near destination");
        }
    }

    private String translate(int i){
        if(i<=9)
            return "0" + Integer.toString(i);
        return Integer.toString(i);
    }

    // Aktiver scroll-hjulet med antal tid tilbage
    public static void enableNumberPicker(){
        line.setAlpha(1);
        timer.setEnabled(true);
        minutter.setEnabled(true);
        if(SingleTon.nightMode){
            line.setBackgroundResource(R.drawable.knap_night_bg3); // Fjerner onClick effekt
        } else {
            line.setBackgroundResource(R.drawable.knap_blaa_bg3); // Fjerner onClick effekt
        }

    }

    // Deaktiver scroll-hjulet med antal tid tilbage
    public static void disableNumberPicker(){
        line.setAlpha(0.5f);
        timer.setValue(SingleTon.timer);
        minutter.setValue(SingleTon.minutter/5);
        timer.setEnabled(false);
        minutter.setEnabled(false);
        if(SingleTon.nightMode){
            line.setBackgroundResource(R.drawable.knap_night_bg2); // Fjerner onClick effekt
        } else {
            line.setBackgroundResource(R.drawable.knap_blaa_bg2); // Tilføjer onClick effekt
        }
    }

    @Override
    public void onClick(View v) {
        Log.d("Advanced", "Der klikkes");
        // Viser en toast når der klikkes på numberpickeren og der ikke er en destination
        if(v==line){
            Log.d("Advanced", "Der klikkes på linearLayout");
            if(minutter.isEnabled()){
                // do nothing
            } else {
                Toast.makeText(getActivity(), "Destination required", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
