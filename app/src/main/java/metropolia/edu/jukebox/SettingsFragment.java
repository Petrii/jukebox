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
    NsdHelper mNsdHelper;

    private TextView mStatusView;
    private Handler mUpdateHandler;
    private EditText messageView;

    public static final String TAG = "NsdChat";

    ChatConnection mConnection;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mUpdateHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                String chatLine = msg.getData().getString("msg");
                addChatLine(chatLine);
            }
        };

        mConnection = new ChatConnection(mUpdateHandler);

        mNsdHelper = new NsdHelper(this.getContext());
        mNsdHelper.initializeNsd();
    }

    @Override
    public void onCreatePreferences(Bundle bundle, String s) {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);
        mStatusView = (TextView) view.findViewById(R.id.status);
        Button discover = (Button) view.findViewById(R.id.clickDiscover);
        discover.setOnClickListener(this);
        Button advertise = (Button) view.findViewById(R.id.clickAdvertise);
        advertise.setOnClickListener(this);
        Button connect = (Button) view.findViewById(R.id.clickConnect);
        connect.setOnClickListener(this);
        Button send = (Button) view.findViewById(R.id.clickSend);
        send.setOnClickListener(this);
        messageView = (EditText)view.findViewById(R.id.editText);
        return view;
    }


    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.clickDiscover:
                mNsdHelper.discoverServices();
                break;
            case R.id.clickAdvertise:
                if(mConnection.getLocalPort() > -1) {
                    mNsdHelper.registerService(mConnection.getLocalPort());
                } else {
                    Log.d(TAG, "ServerSocket isn't bound.");
                }
                break;
            case R.id.clickConnect:
                NsdServiceInfo service = mNsdHelper.getChosenServiceInfo();
                if (service != null) {
                    Log.d(TAG, "Connecting.");
                    mConnection.connectToServer(service.getHost(),
                            service.getPort());
                } else {
                    Log.d(TAG, "No service to connect to!");
                }
                break;
            case R.id.clickSend:
                if (messageView != null) {
                    String messageString = messageView.getText().toString();
                    if (!messageString.isEmpty()) {
                        mConnection.sendMessage(messageString);
                    }
                    messageView.setText("");
                }
                break;
        }
    }



    public TextView getmStatusView() {
        return mStatusView;
    }

    public void addChatLine(String line) {
        mStatusView.append("\n" + line);
    }

    @Override
    public void onPause() {
        if (mNsdHelper != null) {
            mNsdHelper.stopDiscovery();
        }
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mNsdHelper != null) {
            mNsdHelper.discoverServices();
        }
    }

    @Override
    public void onDestroy() {
        mNsdHelper.tearDown();
        mConnection.tearDown();
        super.onDestroy();
    }
}
