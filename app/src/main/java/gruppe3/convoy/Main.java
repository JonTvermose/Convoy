package gruppe3.convoy;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.HorizontalScrollView;


public class Main extends AppCompatActivity {

    HorizontalScrollView slide;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //getSupportFragmentManager().beginTransaction()
        //        .replace(R.id.fragFrame, new SliderFragment())
        //        .commit();
    }

}
