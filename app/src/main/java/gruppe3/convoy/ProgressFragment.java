package gruppe3.convoy;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;


/**
 * A simple {@link Fragment} subclass.
 */
public class ProgressFragment extends Fragment {

    public static ProgressBar progressBar;
    public static TextView progressBarTxt;

    public ProgressFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rod = inflater.inflate(R.layout.fragment_progress, container, false);

        progressBarTxt = (TextView) rod.findViewById(R.id.progressBarTxt);

        progressBar = (ProgressBar) rod.findViewById(R.id.progressBar);
//        progressBar.setMax(300);
        progressBar.setProgress(0);

        return rod;
    }
}
