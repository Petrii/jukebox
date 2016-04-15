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

public class SettingsFragment extends PreferenceFragmentCompat implements View.OnClickListener {
    private TextView mStatusView;

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

        mStatusView = (TextView) view.findViewById(R.id.status);
        Button send = (Button) view.findViewById(R.id.clickSend);
        send.setOnClickListener(this);
        return view;
    }


    @Override
    public void onClick(View v) {
    }



    public TextView getmStatusView() {
        return mStatusView;
    }

    public void addChatLine(String line) {
        mStatusView.append("\n" + line);
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
