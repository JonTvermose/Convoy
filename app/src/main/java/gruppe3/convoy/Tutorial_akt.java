package gruppe3.convoy;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;


public class Tutorial_akt extends FragmentActivity implements View.OnClickListener {
    private Button back,skipAll, next;
    private TextView text;
    private int page = 1;
    private String hovedskaerm = "Welcome to Convoy! This is your home screen! Here you can select your truck stop preferences.";
    private String destination = "If you need to go somewhere, this page will help you search for truck stops in the area near your destination. " +
            "When a destination has been selected, it is also possible to select a pause-timer for a truck spot near you, when you have to take your break.";
    private String settings1 = "In the settings menu it is possible to check whether or not you are driving with a road-train above 25m. You can also switch night mode on for a more pleasing color scheme during nighttime. " +
            "If you become tired of setting your preferences in the main screen, you can also save your preferences in this tab.";
    private String settings2 = "If your phone lower on power you can switch on the power-saving mode. " +
            "You can also set your average speed to better suit your vehicle of choice for a more accurate time calculation on the map.";
    private String map = "Here is your overview. It shows every truck spot in the world! It is possible to add your own truck spot at your location by pressing the icon in the upper right corner " +
            "or by pressing and holding on a selected area on the map if you want to add a spot somewhere else. ";
    ImageView img = (ImageView) findViewById(R.id.tutorialImage);


    protected void onCreateView(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutorial);

        next = (Button) findViewById(R.id.next);
        back = (Button) findViewById(R.id.back);
        skipAll = (Button) findViewById(R.id.skipAll);
        text = (TextView) findViewById(R.id.desc);

        img.setImageResource(R.drawable.hovedskaerm1);
        text.setText(hovedskaerm);
        back.setVisibility(View.GONE);
    }


    @Override
    public void onClick(View v) {
        if ( v == next) {
            if (page < 6)
            ++page;
        } else if (v == back) {
           if (page > 1)
            --page;
        } else if (v == skipAll) {
            page = 6;
        }
        switch (page) {
            case 1: img.setImageResource(R.drawable.hovedskaerm1);
                    text.setText(hovedskaerm);
                    back.setVisibility(View.GONE);
                break;
            case 2: img.setImageResource(R.drawable.destination1);
                    text.setText(destination);
                break;
            case 3: img.setImageResource(R.drawable.settings1);
                text.setText(settings1);
                break;
            case 4: img.setImageResource(R.drawable.settings1);
                text.setText(settings2);
                break;
            case 5: img.setImageResource(R.drawable.map1);
                text.setText(map);
                next.setText("Finish");
                break;
            case 6: break;
        }

    }
}
