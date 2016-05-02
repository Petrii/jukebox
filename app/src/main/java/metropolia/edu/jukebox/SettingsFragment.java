package metropolia.edu.jukebox;

import android.os.Bundle;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;


public class SettingsFragment extends PreferenceFragmentCompat{
    private Button hostButton;

    public static final String TAG = "Settings";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onCreatePreferences(Bundle bundle, String s) {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view;
        if(MainActivity.isHost){
            view = inflater.inflate(R.layout.fragment_share, container, false);
        }else{
            view = inflater.inflate(R.layout.fragment_join, container, false);
        }
        return view;
    }
}
