package metropolia.edu.jukebox;

import android.os.Bundle;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.app.Activity;
import android.net.nsd.NsdServiceInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.nearby.Nearby;

import metropolia.edu.jukebox.queue.QueueList;

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
        View view = inflater.inflate(R.layout.fragment_settings, container, false);
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
