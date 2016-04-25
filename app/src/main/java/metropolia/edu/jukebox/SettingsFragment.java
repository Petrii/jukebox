package metropolia.edu.jukebox;

import android.os.Bundle;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;


public class SettingsFragment extends PreferenceFragmentCompat implements View.OnClickListener {
    private Button hostButton;
    private Button clientButton;

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
        final View view = inflater.inflate(R.layout.fragment_settings, container, false);
        final LinearLayout shareLayout = (LinearLayout)view.findViewById(R.id.share_invite_layout);
        final LinearLayout joinLayout = (LinearLayout)view.findViewById(R.id.join_party_layout);
        if(MainActivity.isHost){
            shareLayout.setVisibility(View.VISIBLE);
            joinLayout.setVisibility(View.GONE);
        }else{
            joinLayout.setVisibility(View.VISIBLE);
            shareLayout.setVisibility(View.GONE);
        }
        Button send = (Button) view.findViewById(R.id.clickSend);
        send.setOnClickListener(this);
        hostButton = (Button) view.findViewById(R.id.hostButton);
        hostButton.setOnClickListener(this);
        clientButton = (Button) view.findViewById(R.id.clientButton);
        clientButton.setOnClickListener(this);
        return view;
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.hostButton:
                ((MainActivity)getActivity()).advertise();
                break;
            case R.id.clientButton:
                ((MainActivity)getActivity()).discover();
                break;
            case R.id.clickSend:

                break;
        }
    }
}
