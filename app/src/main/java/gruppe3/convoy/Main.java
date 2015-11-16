package gruppe3.convoy;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.FrameLayout;


public class Main extends AppCompatActivity {

    FrameLayout frame;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        frame = (FrameLayout) findViewById(R.id.knapFrame);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.knapFrame, new MainButtonsFragment())
                .commit();
    }

}
