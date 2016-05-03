package metropolia.edu.jukebox;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.nearby.Nearby;
import com.google.android.gms.nearby.connection.AppIdentifier;
import com.google.android.gms.nearby.connection.AppMetadata;
import com.google.android.gms.nearby.connection.Connections;

import java.util.ArrayList;
import java.util.List;

import metropolia.edu.jukebox.queue.ParcelableUtil;
import metropolia.edu.jukebox.queue.QueueList;
import metropolia.edu.jukebox.queue.Track;

public class Connection implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        Connections.ConnectionRequestListener,
        Connections.MessageListener,
        Connections.EndpointDiscoveryListener {

    private static final String TAG = "ConnectionTAG";
    private final QueueList queueList;
    private final ParcelableUtil parcelableUtil;
    private final Context context;
    private final Activity activity;
    private final GoogleApiClient googleApiClient;

    private boolean isConnected;
    private boolean mIsHost = false;
    private String clientAuthCode = null;
    private String remoteHostEndpoint = null;
    private List<String> remotePeerEndpoints = new ArrayList<>();


    public Connection(Context context, Activity activity, boolean isHost) {
        this.activity = activity;
        this.context = context;
        this.mIsHost = isHost;

        googleApiClient = new GoogleApiClient.Builder(this.context)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Nearby.CONNECTIONS_API)
                .build();

        queueList = QueueList.getInstance();
        parcelableUtil = new ParcelableUtil();
    }

    public void sendQueueListToClients() {
        if(!remotePeerEndpoints.isEmpty()){
            final byte[] payload = parcelableUtil.marshall(queueList);
            Nearby.Connections.sendReliableMessage(googleApiClient, remotePeerEndpoints, payload);
        }
    }

    public boolean sendTrackToHost(Track track) {
        if(googleApiClient.isConnected()) {
            if (remoteHostEndpoint != null) {
                final byte[] payload = parcelableUtil.marshall(track);
                Nearby.Connections.sendReliableMessage(googleApiClient, remoteHostEndpoint, payload);
                return true;
            }
            return false;
        }else{
            return false;
        }
    }

    public void discover(String clientAuth) {
        if (!isConnectedToNetwork()) {
           connect();
        }
        this.clientAuthCode = clientAuth;
        final String serviceId = context.getString(R.string.service_id);
        long CONNECTION_TIME_OUT = 1000L;

        Nearby.Connections.startDiscovery(googleApiClient, serviceId, CONNECTION_TIME_OUT, this).setResultCallback(new ResultCallback<Status>() {
            @Override
            public void onResult(Status status) {
                if (status.isSuccess()) {
                    Log.d(TAG, "Discovering.");
                } else {
                    Log.d(TAG, "Discovering failed: " + status.getStatus().getStatusCode());
                }
            }
        });
    }

    /**
     * Client
     *
     * @param endpointId
     * @param deviceId
     * @param serviceId
     * @param endpointName
     */
    @Override
    public void onEndpointFound(String endpointId,
                                final String deviceId,
                                final String serviceId,
                                String endpointName) {

        byte[] payload = clientAuthCode.getBytes();
        try {
            Nearby.Connections.sendConnectionRequest(googleApiClient, deviceId, endpointId, payload, new Connections.ConnectionResponseCallback() {
                @Override
                public void onConnectionResponse(String endpointId, Status status, byte[] bytes) {
                    if (status.isSuccess()) {
                        Nearby.Connections.stopDiscovery(googleApiClient, serviceId);
                        remoteHostEndpoint = endpointId;
                        showToast("Connected to host");
                        isConnected = true;
                    } else {
                        showToast("Connection failed to host!");
                        isConnected = false;
                    }
                }
            }, this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void advertise(String clientAuth) {
        if (!isConnectedToNetwork()) {
            return;
        }
        clientAuthCode = clientAuth;
        // The advertising timeout is set to run indefinitely
        long CONNECTION_TIME_OUT = 0L;

        List<AppIdentifier> appIdentifierList = new ArrayList<>();
        appIdentifierList.add(new AppIdentifier(context.getPackageName()));
        AppMetadata appMetadata = new AppMetadata(appIdentifierList);

        Nearby.Connections.startAdvertising(googleApiClient, null, appMetadata, CONNECTION_TIME_OUT, this).setResultCallback(new ResultCallback<Connections.StartAdvertisingResult>() {
            @Override
            public void onResult(Connections.StartAdvertisingResult result) {
                if (result.getStatus().isSuccess()) {
                    Log.d(TAG, "Advertising.");
                } else {
                    Log.d(TAG, "Advertising failed: "+ result.getStatus().getStatusCode());
                }
            }
        });
    }

    /**
     * Server
     *
     * @param remoteEndpointId
     * @param remoteDeviceId
     * @param remoteEndpointName
     * @param payload
     */
    @Override
    public void onConnectionRequest(final String remoteEndpointId,
                                    final String remoteDeviceId,
                                    final String remoteEndpointName,
                                    byte[] payload) {
        final byte[] myPayload = null;
        final String code = new String(payload);
        if (mIsHost && code.equals(clientAuthCode)) {
            Nearby.Connections.acceptConnectionRequest(googleApiClient, remoteEndpointId, myPayload, this).setResultCallback(new ResultCallback<Status>() {
                @Override
                public void onResult(Status status) {
                    if (status.isSuccess()) {
                        if (!remotePeerEndpoints.contains(remoteEndpointId)) {
                            remotePeerEndpoints.add(remoteEndpointId);
                            Log.d(TAG, "New connection and client is added to list");
                        }
                        sendQueueListToClients();
                    } else {
                        Log.d(TAG, "Failed connecting to: "+ remoteEndpointName);
                        Nearby.Connections.rejectConnectionRequest(googleApiClient, remoteEndpointId);
                    }
                }
            });
        } else {
            Nearby.Connections.rejectConnectionRequest(googleApiClient, remoteEndpointId);
        }
    }

    private boolean isConnectedToNetwork() {
        final ConnectivityManager connManager =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        final NetworkInfo info = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (info != null && info.isConnectedOrConnecting()) {
            return true;
        }

        Log.d(TAG, "Not connected to network");
        return false;
    }

    public void connect() {
        if(!googleApiClient.isConnected()) {
            Log.d(TAG, "Connecting GoogleApiClient.");
            googleApiClient.connect();
        }
    }

    public void disconnect() {
        if (!isConnectedToNetwork()) {
            return;
        }

        /*
        if (googleApiClient != null && googleApiClient.isConnected()) {
            Log.d(TAG, "Disconnecting GoogleApiClient");
            googleApiClient.disconnect();
        }*/

        if (mIsHost) {
            Log.d(TAG, "Disconnecting host.");
            Nearby.Connections.stopAdvertising(googleApiClient);
            Nearby.Connections.stopAllEndpoints(googleApiClient);
            remotePeerEndpoints.clear();
        } else {
            Log.d(TAG, "Disconnecting client.");
            if (!isConnected || TextUtils.isEmpty(remoteHostEndpoint)) {
                Nearby.Connections.stopDiscovery(googleApiClient, context.getString(R.string.service_id));
                return;
            }
            Nearby.Connections.disconnectFromEndpoint(googleApiClient, remoteHostEndpoint);
            remoteHostEndpoint = null;
        }
        isConnected = false;
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.d(TAG, "onConnected");
        MainActivity.UserID = Nearby.Connections.getLocalDeviceId(googleApiClient);
        if(mIsHost){
            advertise("1");
        }/*else{
            discover();
        }*/
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.d(TAG, "onConnectionSuspended");
    }

    @Override
    public void onEndpointLost(String s) {
        Log.d(TAG, "onEndpointLost");
    }

    @Override
    public void onMessageReceived(String s, byte[] bytes, boolean b) {
        Log.d(TAG, "onMessageReceived");

        if (mIsHost) {
            queueList.updateQueueList(new Track(parcelableUtil.unmarshall(bytes)));
            sendQueueListToClients();
        } else {
            new QueueList(parcelableUtil.unmarshall(bytes));
        }
        MainActivity.updateUI = true;
    }

    @Override
    public void onDisconnected(String s) {
        Log.d(TAG, "onDisconnected");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d(TAG, "onConnectionFailed");
    }

    protected void showToast(String message) {
        Toast.makeText(activity, message, Toast.LENGTH_LONG).show();
    }
}
