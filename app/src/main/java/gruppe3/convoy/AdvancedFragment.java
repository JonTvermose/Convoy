package gruppe3.convoy;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.Switch;

import gruppe3.convoy.functionality.SingleTon;


/**
 * A simple {@link Fragment} subclass.
 */
public class AdvancedFragment extends Fragment {

    public static EditText dest;
    public static NumberPicker timer,minutter;
    public static Switch roadTrain;
    public static String[] hours,mins;

    public AdvancedFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rod = inflater.inflate(R.layout.fragment_advanced, container, false);
        dest = (EditText) rod.findViewById(R.id.destination_editText);

        timer = (NumberPicker) rod.findViewById(R.id.timer_numberPicker);
        hours = new String[24];
        for(int i=0; i<hours.length; i++)
            hours[i] = Integer.toString(i);
        timer.setMinValue(0);
        timer.setMaxValue(23);
        timer.setWrapSelectorWheel(true);
        timer.setDisplayedValues(hours);
        timer.setValue(0);

        minutter = (NumberPicker) rod.findViewById(R.id.minutter_numberPicker);
        mins = new String[12];
        for(int i=0; i<mins.length; i++)
            mins[i] = Integer.toString(i*5);
        minutter.setMinValue(0);
        minutter.setMaxValue(11);
        minutter.setWrapSelectorWheel(true);
        minutter.setDisplayedValues(mins);
        minutter.setValue(0);

        roadTrain = (Switch) rod.findViewById(R.id.roadTrain_switch);
        roadTrain.setChecked(SingleTon.roadTrain);
        return rod;
    }

}
