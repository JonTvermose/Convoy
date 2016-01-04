package gruppe3.convoy;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;


public class MainButtonsFragment extends Fragment {

    ImageView food,wc,bed,bath,fuel,adblue;

    public void resetButtons(){

    }

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
        // Inflate the layout for this fragment

        View knapper = inflater.inflate(R.layout.fragment_main_buttons, container, false);

        food = (ImageView) knapper.findViewById(R.id.imageFood);
        food.setTag(R.drawable.food_t);
        food.setImageResource(R.drawable.food_t);
        food.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ((Integer) food.getTag() == R.drawable.food_t) {
                    food.setImageResource(R.drawable.food_t_check);
                    food.setTag(R.drawable.food_t_check);
                    Main.food=true;
                } else {
                    food.setImageResource(R.drawable.food_t);
                    food.setTag(R.drawable.food_t);
                    Main.food=false;
                }
            }
        });

        wc = (ImageView) knapper.findViewById(R.id.imageWc);
        wc.setTag(R.drawable.wc_t);
        wc.setImageResource(R.drawable.wc_t);
        wc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ((Integer) wc.getTag() == R.drawable.wc_t) {
                    wc.setImageResource(R.drawable.wc_t_check);
                    wc.setTag(R.drawable.wc_t_check);
                    Main.wc=true;
                } else {
                    wc.setImageResource(R.drawable.wc_t);
                    wc.setTag(R.drawable.wc_t);
                    Main.wc=false;
                }
            }
        });

        bed = (ImageView) knapper.findViewById(R.id.imageBed);
        bed.setTag(R.drawable.bed_t);
        bed.setImageResource(R.drawable.bed_t);
        bed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ((Integer) bed.getTag() == R.drawable.bed_t) {
                    bed.setImageResource(R.drawable.bed_t_check);
                    bed.setTag(R.drawable.bed_t_check);
                    Main.bed=true;
                } else {
                    bed.setImageResource(R.drawable.bed_t);
                    bed.setTag(R.drawable.bed_t);
                    Main.bed=false;
                }
            }
        });

        bath = (ImageView) knapper.findViewById(R.id.imageBath);
        bath.setTag(R.drawable.bath_t);
        bath.setImageResource(R.drawable.bath_t);
        bath.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ((Integer) bath.getTag() == R.drawable.bath_t) {
                    bath.setImageResource(R.drawable.bath_t_check);
                    bath.setTag(R.drawable.bath_t_check);
                    Main.bath=true;
                } else {
                    bath.setImageResource(R.drawable.bath_t);
                    bath.setTag(R.drawable.bath_t);
                    Main.bath=false;
                }
            }
        });

        fuel = (ImageView) knapper.findViewById(R.id.imageFuel);
        fuel.setTag(R.drawable.fuel_t);
        fuel.setImageResource(R.drawable.fuel_t);
        fuel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ((Integer) fuel.getTag() == R.drawable.fuel_t) {
                    fuel.setImageResource(R.drawable.fuel_t_check);
                    fuel.setTag(R.drawable.fuel_t_check);
                    Main.fuel=true;
                } else {
                    fuel.setImageResource(R.drawable.fuel_t);
                    fuel.setTag(R.drawable.fuel_t);
                    Main.fuel=false;
                }
            }
        });

        adblue = (ImageView) knapper.findViewById(R.id.imageAdblue);
        adblue.setTag(R.drawable.adblue_t);
        adblue.setImageResource(R.drawable.adblue_t);
        adblue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ((Integer) adblue.getTag() == R.drawable.adblue_t) {
                    adblue.setImageResource(R.drawable.adblue_t_check);
                    adblue.setTag(R.drawable.adblue_t_check);
                    Main.adblue=true;
                } else {
                    adblue.setImageResource(R.drawable.adblue_t);
                    adblue.setTag(R.drawable.adblue_t);
                    Main.adblue=false;
                }
            }
        });

        return knapper;
    }


}
