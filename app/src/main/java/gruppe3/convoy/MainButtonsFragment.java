package gruppe3.convoy;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import gruppe3.convoy.functionality.SingleTon;


public class MainButtonsFragment extends Fragment {

    private ImageView food,wc,bed,bath,fuel,adblue;

    public static MainButtonsFragment newInstance(String param1, String param2) {
        MainButtonsFragment fragment = new MainButtonsFragment();
        return fragment;
    }

    public MainButtonsFragment() {// Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {super.onCreate(savedInstanceState);}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View knapper;
        if(SingleTon.nightMode){
            knapper = inflater.inflate(R.layout.fragment_main_buttons, container, false);
        } else {
            knapper = inflater.inflate(R.layout.fragment_main_buttons_night, container, false);
        }

        food = (ImageView) knapper.findViewById(R.id.imageFood);

        if(SingleTon.food){
            food.setTag(R.drawable.food_t_check);
            food.setImageResource(R.drawable.food_t_check);
        } else {
            food.setTag(R.drawable.food_t);
            food.setImageResource(R.drawable.food_t);
        }
        food.setSelected(SingleTon.food);
        food.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ((Integer) food.getTag() == R.drawable.food_t) {
                    food.setImageResource(R.drawable.food_t_check);
                    food.setTag(R.drawable.food_t_check);
                    food.setSelected(true);
                    SingleTon.food = true;
                    Log.d("Error", SingleTon.food.toString());
                } else {
                    food.setImageResource(R.drawable.food_t);
                    food.setTag(R.drawable.food_t);
                    food.setSelected(false);
                    SingleTon.food = false;
                }
            }
        });

        wc = (ImageView) knapper.findViewById(R.id.imageWc);
        if(SingleTon.wc){
            wc.setTag(R.drawable.wc_t_check);
            wc.setImageResource(R.drawable.wc_t_check);
        } else {
            wc.setTag(R.drawable.wc_t);
            wc.setImageResource(R.drawable.wc_t);
        }
        wc.setSelected(SingleTon.wc);
        wc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ((Integer) wc.getTag() == R.drawable.wc_t) {
                    wc.setImageResource(R.drawable.wc_t_check);
                    wc.setTag(R.drawable.wc_t_check);
                    wc.setSelected(true);
                    SingleTon.wc = true;
                } else {
                    wc.setImageResource(R.drawable.wc_t);
                    wc.setTag(R.drawable.wc_t);
                    wc.setSelected(false);
                    SingleTon.wc = false;
                }
            }
        });

        bed = (ImageView) knapper.findViewById(R.id.imageBed);
        if(SingleTon.bed){
            bed.setTag(R.drawable.bed_t_check);
            bed.setImageResource(R.drawable.bed_t_check);
        } else {
            bed.setTag(R.drawable.bed_t);
            bed.setImageResource(R.drawable.bed_t);
        }
        bed.setSelected(SingleTon.bed);
        bed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bed.isSelected() == false) {
                    bed.setImageResource(R.drawable.bed_t_check);
                    bed.setTag(R.drawable.bed_t_check);
                    bed.setSelected(true);
                    SingleTon.bed = true;
                } else {
                    bed.setImageResource(R.drawable.bed_t);
                    bed.setTag(R.drawable.bed_t);
                    bed.setSelected(false);
                    SingleTon.bed = false;
                }
            }
        });

        bath = (ImageView) knapper.findViewById(R.id.imageBath);
        if(SingleTon.bath){
            bath.setTag(R.drawable.bath_t_check);
            bath.setImageResource(R.drawable.bath_t_check);
        } else {
            bath.setTag(R.drawable.bath_t);
            bath.setImageResource(R.drawable.bath_t);
        }
        bath.setSelected(SingleTon.bath);
        bath.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ((Integer) bath.getTag() == R.drawable.bath_t) {
                    bath.setImageResource(R.drawable.bath_t_check);
                    bath.setTag(R.drawable.bath_t_check);
                    bath.setSelected(true);
                    SingleTon.bath=true;
                } else {
                    bath.setImageResource(R.drawable.bath_t);
                    bath.setTag(R.drawable.bath_t);
                    bath.setSelected(false);
                    SingleTon.bath=false;
                }
            }
        });

        fuel = (ImageView) knapper.findViewById(R.id.imageFuel);
        if(SingleTon.fuel){
            fuel.setTag(R.drawable.fuel_t_check);
            fuel.setImageResource(R.drawable.fuel_t_check);
        } else {
            fuel.setTag(R.drawable.fuel_t);
            fuel.setImageResource(R.drawable.fuel_t);
        }
        fuel.setSelected(SingleTon.fuel);
        fuel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ((Integer) fuel.getTag() == R.drawable.fuel_t) {
                    fuel.setImageResource(R.drawable.fuel_t_check);
                    fuel.setTag(R.drawable.fuel_t_check);
                    fuel.setSelected(true);
                    SingleTon.fuel=true;
                } else {
                    fuel.setImageResource(R.drawable.fuel_t);
                    fuel.setTag(R.drawable.fuel_t);
                    fuel.setSelected(false);
                    SingleTon.fuel=false;
                }
            }
        });

        adblue = (ImageView) knapper.findViewById(R.id.imageAdblue);
        if(SingleTon.adblue){
            adblue.setTag(R.drawable.adblue_t_check);
            adblue.setImageResource(R.drawable.adblue_t_check);
        } else {
            adblue.setTag(R.drawable.adblue_t);
            adblue.setImageResource(R.drawable.adblue_t);
        }
        adblue.setSelected(SingleTon.adblue);
        adblue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ((Integer) adblue.getTag() == R.drawable.adblue_t) {
                    adblue.setImageResource(R.drawable.adblue_t_check);
                    adblue.setTag(R.drawable.adblue_t_check);
                    adblue.setSelected(true);
                    SingleTon.adblue=true;
                } else {
                    adblue.setImageResource(R.drawable.adblue_t);
                    adblue.setTag(R.drawable.adblue_t);
                    adblue.setSelected(false);
                    SingleTon.adblue=false;
                }
            }
        });

        return knapper;
    }
}
