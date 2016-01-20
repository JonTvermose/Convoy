package gruppe3.convoy;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;


public class Tutorial_akt extends FragmentActivity implements View.OnClickListener, Animation.AnimationListener {
    private Button back,skipAll, next;
    private TextView text, hints;
    private int page = 1;
    private String hovedskaerm = "Welcome to Convoy! This is your home screen! Here you can select your truck stop preferences.";
    private String destination = "If you need to go somewhere, this page will help you search for truck stops in the area near your destination. " +
            "When a destination has been selected, it is also possible to select a pause-timer for a truck spot near you, when you have to take your break.";
    private String settings1 = "In the settings menu you can choose whether or not you are driving with a road-train. Switch night mode on for a more pleasing color scheme during nighttime." +
            "If you become tired of setting your preferences in the main screen, you can save your preferences in this tab aswell.";
    private String settings2 = "If your phone is low on power you can switch on the power-saving mode. " +
            "You can also set your average speed to better suit your vehicle of choice for a more accurate time calculation on the map.";
    private String map = "Here is your overview. It shows every truck spot in the world! It is possible to add your own truck spot at your location by pressing the icon in the upper right corner " +
            "or by pressing and holding on a selected area on the map if you want to add a spot somewhere else. ";
    private ImageView img;
    private int images[] = {R.drawable.hovedskaerm1, R.drawable.destination, R.drawable.settings, R.drawable.settings, R.drawable.map};
    private Animation fadeIn, fadeOut;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutorial);

        if(savedInstanceState==null){
            img = (ImageView) findViewById(R.id.tutorialImage);
            next = (Button) findViewById(R.id.next);
            next.setOnClickListener(this);
            back = (Button) findViewById(R.id.back);
            back.setOnClickListener(this);
            skipAll = (Button) findViewById(R.id.skipAll);
            skipAll.setOnClickListener(this);
            text = (TextView) findViewById(R.id.desc);
            img.setImageResource(R.drawable.hovedskaerm1);
            text.setText(hovedskaerm);
            back.setVisibility(View.GONE);
            hints = (TextView) findViewById(R.id.hints);
            hints.setText("Hint " + page + "/5");

            int fadeDuration = 300;
            fadeIn = new AlphaAnimation(0, 1);
            fadeIn.setInterpolator(new DecelerateInterpolator());
            fadeIn.setDuration(fadeDuration);
            fadeOut = new AlphaAnimation(1, 0);
            fadeOut.setInterpolator(new AccelerateInterpolator());
            fadeOut.setDuration(fadeDuration);
            fadeIn.setAnimationListener(this);
            fadeOut.setAnimationListener(this);
        }
    }

    @Override
    public void onClick(View v) {
        img.startAnimation(fadeOut);
        if ( v == next) {
            if (page < 6)
                page++;
        } else if (v == back) {
            if (page > 1)
                page--;
        } else if (v == skipAll) {
            page = 6;
        }
        switch (page) {
            case 1:
                text.setText(hovedskaerm);
                back.setVisibility(View.GONE);
                hints.setText("Hint " + page + "/5");
                break;
            case 2:
                text.setText(destination);
                back.setVisibility(View.VISIBLE);
                hints.setText("Hint " + page + "/5");
                break;
            case 3:
                text.setText(settings1);
                hints.setText("Hint " + page + "/5");
                break;
            case 4:
                text.setText(settings2);
                next.setText("Next hint");
                hints.setText("Hint " + page + "/5");
                break;
            case 5:
                next.setText("Finish");
                text.setText(map);
                hints.setText("Hint " + page + "/5");
                break;
            case 6:
                finish();
                this.overridePendingTransition(R.animator.fade_in, R.animator.fade_out);
                SharedPreferences.Editor prefs = PreferenceManager.getDefaultSharedPreferences(this).edit();
                prefs.putBoolean("showTutorial", false).apply();
                break;
            default: page = 1;
        }
    }

    @Override
    public void onAnimationStart(Animation animation) {

    }

    @Override
    public void onAnimationEnd(Animation animation) {
        if(animation==fadeOut){
            img.setImageResource(images[page-1]);
            img.startAnimation(fadeIn);
        }
    }

    @Override
    public void onAnimationRepeat(Animation animation) {

    }
}
