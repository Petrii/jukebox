package metropolia.edu.jukebox;

import android.content.Context;
import android.content.Intent;
import android.net.nsd.NsdServiceInfo;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.os.Handler;

/**
 * Created by petri on 30.3.2016.
 */
public class QueueFragment extends ListFragment {

    WifiLocalServiceHelper mNsdHelper;
    private TextView mStatusView;
    private Handler mUpdateHandler;
    public static final String TAG = "Queue List";

    QueueConnection mQueueConnection;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_queue, container, false);
        return view;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
                mUpdateHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                String newTrack = msg.getData().getString("msg");
                addNewTrack(newTrack);
            }
        };

        mQueueConnection = new QueueConnection(mUpdateHandler);

        mNsdHelper = new WifiLocalServiceHelper(this.getContext());
        mNsdHelper.initializeNsd();
    }
    public static Intent createIntent(Context context){
        return new Intent(context, QueueFragment.class);
    }

    public void clickAdvertise(View v){
        if(mQueueConnection.getLocalPort() > -1){
            mNsdHelper.registerService(mQueueConnection.getLocalPort());
        }else{
            Log.d(TAG, "ServerSocket isn't bound.");
        }
    }

    public void clickDiscover(View v){
        mNsdHelper.discoverServices();
    }

    public void clickConnect(View v){
        NsdServiceInfo service = mNsdHelper.getmServiceInfo();
        if(service != null){
            Log.d(TAG, "Connecting...");
            mQueueConnection.connectToHost(service.getHost(), service.getPort());
        }else{
            Log.d(TAG, "No service to connect to!");
        }
    }

    public void clickSend(View v){
        //EditText selectedTrack = (EditText)findViewById(R.id)
    }

    public void addNewTrack(String line){
        mStatusView.append("\n" + line);
    }

    @Override
    public void onPause() {
        if(mNsdHelper != null){
            mNsdHelper.stopDiscovery();
        }
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        if(mNsdHelper != null){
            mNsdHelper.discoverServices();
        }
    }

    @Override
    public void onDestroy() {
        mNsdHelper.tearDown();
        mQueueConnection.tearDown();
        super.onDestroy();
    }
}
