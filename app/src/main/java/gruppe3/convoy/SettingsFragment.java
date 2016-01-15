package gruppe3.convoy;


import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.Switch;

import gruppe3.convoy.functionality.SingleTon;


/**
 * A simple {@link Fragment} subclass.
 */
public class SettingsFragment extends Fragment implements CompoundButton.OnCheckedChangeListener, SeekBar.OnSeekBarChangeListener {

    private Switch roadTrain, saveData, nightMode, powerSaving;
    private SeekBar speedBar;

    public SettingsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rod;
        if(SingleTon.nightMode){
            rod = inflater.inflate(R.layout.fragment_settings_night, container, false);
        } else {
            rod = inflater.inflate(R.layout.fragment_settings, container, false);
        }

        roadTrain = (Switch) rod.findViewById(R.id.roadTrain_switch);
        roadTrain.setChecked(SingleTon.roadTrain);
        roadTrain.setOnCheckedChangeListener(this);

        saveData = (Switch) rod.findViewById(R.id.savedata_switch);
        saveData.setChecked(SingleTon.saveData);
        saveData.setOnCheckedChangeListener(this);

        nightMode = (Switch) rod.findViewById(R.id.nightmode_switch);
        nightMode.setChecked(SingleTon.nightMode);
        nightMode.setOnCheckedChangeListener(this);
        nightMode.setEnabled(false);

        powerSaving = (Switch) rod.findViewById(R.id.powermode_switch);
        powerSaving.setChecked(SingleTon.powerSaving);
        powerSaving.setOnCheckedChangeListener(this);
        powerSaving.setEnabled(false);

        speedBar = (SeekBar) rod.findViewById(R.id.speedBar_seekbar);
        speedBar.setProgress(convertSpeed(SingleTon.speedSetting));
        speedBar.setOnSeekBarChangeListener(this);

        final Handler h = new Handler();
        h.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (SingleTon.dataLoadDone) {
                    nightMode.setEnabled(true);
                    powerSaving.setEnabled(true);
                } else {
                    h.postDelayed(this, 100);
                }
            }
        }, 100);

        return rod;
    }

    // Konverterer tal fra 1.0 til 1.5 til 100-0
    private int convertSpeed(double setting){
        double retur = 100 - (setting-1)*200;
        Log.d("Setting" , "SpeedBar sat til: " + (int) retur);
        return (int) retur;
    }

    // Konverterer tal fra 0 til 100 til 1.5-1.0
    private double convertSpeed(int speed){
        double retur = 1.5 - speed * 0.005;
        Log.d("Setting" , "SingleTon.speedSetting sat til: " + retur);
        return retur;
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

        if(buttonView==roadTrain){
            Log.d("Advanced", "RoadTrain er sat til: " + isChecked);
            SingleTon.roadTrain = isChecked;

        } else if (buttonView==saveData){
            Log.d("Advanced", "SaveData er sat til: " + isChecked);
            SingleTon.saveData = isChecked;

        } else if (buttonView==nightMode){
            // TO DO : Reload siden/appen med nyt farveskema
            Log.d("Advanced", "NightMode er sat til: " + isChecked);
            SingleTon.nightMode = isChecked;

            // Genskaber appen i den nye mode
            SingleTon.switchMode=true;
            getActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .setCustomAnimations(R.animator.fade_in, R.animator.fade_out)
                    .replace(R.id.MainFragment, new MainFragment())
                    .commit();

        } else if (buttonView==powerSaving) {
            Log.d("Advanced", "Powersaving er sat til: " + isChecked);
            SingleTon.powerSaving = isChecked;
            if(isChecked){
                SingleTon.sensorManager.unregisterListener((SensorEventListener) getActivity()); // Stopper sensor lytning
                Log.d("Sensor", "Stopper sensorlytter");
            } else if (SingleTon.accelerometer!= null && SingleTon.dataLoadDone){
                SingleTon.sensorManager.registerListener((SensorEventListener) getActivity(), SingleTon.accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
                Log.d("Sensor","Starter sensorlytter");
            }
            // Genstarter GPS-servicen med mindre/større opdateringsfrekvens afhængig af SingleTon.powerSaving
            SingleTon.myLocation.stopLocationUpdates();
            SingleTon.myLocation.startLocationService(getActivity());
        }
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (seekBar == speedBar){
            SingleTon.speedSetting = convertSpeed(progress);
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
    }
}
