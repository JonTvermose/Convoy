package gruppe3.convoy;


import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.Switch;


/**
 * A simple {@link Fragment} subclass.
 */
public class AdvancedFragment extends Fragment {

    public AdvancedFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rod = inflater.inflate(R.layout.fragment_advanced, container, false);
        EditText dest = (EditText) rod.findViewById(R.id.destination_editText);
        NumberPicker timer = (NumberPicker) rod.findViewById(R.id.timer_numberPicker);
        NumberPicker minutter = (NumberPicker) rod.findViewById(R.id.minutter_numberPicker);
        EditText maxSpeed = (EditText) rod.findViewById(R.id.maxSpeed_editText);
        Switch roadTrain = (Switch) rod.findViewById(R.id.roadTrain_switch);
        return rod;
    }

}
