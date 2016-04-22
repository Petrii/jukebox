package metropolia.edu.jukebox;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

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
    private final Context context;
    private final GoogleApiClient googleApiClient;
    private boolean isConnected;
    private String remoteHostEndpoint;
    private List<String> remotePeerEndpoints = new ArrayList<>();
    private final QueueList queueList;
    private final ParcelableUtil parcelableUtil;

    public Connection(Context context) {
        this.context = context;

        googleApiClient = new GoogleApiClient.Builder(this.context)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Nearby.CONNECTIONS_API)
                .build();

        queueList = QueueList.getInstance();
        parcelableUtil = new ParcelableUtil();
    }

    public void sendQueueListToClients() {
        byte[] payload = parcelableUtil.marshall(queueList);

        Nearby.Connections.sendReliableMessage(googleApiClient, remotePeerEndpoints, payload);
    }

    public void sendTrackToHost(Track track) {
        byte[] payload = parcelableUtil.marshall(track);

        Nearby.Connections.sendReliableMessage(googleApiClient, remoteHostEndpoint, payload);
    }

    public void discover() {
        if (!isConnectedToNetwork()) {
            return;
        }

        MainActivity.isHost = false;
        String serviceId = context.getString(R.string.service_id);
        long CONNECTION_TIME_OUT = 10000L;

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

    @Override
    public void onEndpointFound(String endpointId,
                                final String deviceId,
                                final String serviceId,
                                String endpointName) {

        byte[] payload = null;

        Log.d(TAG, "onEndpointFound");

        try {
            Nearby.Connections.sendConnectionRequest(googleApiClient, deviceId, endpointId, payload, new Connections.ConnectionResponseCallback() {
                @Override
                public void onConnectionResponse(String endpointId, Status status, byte[] bytes) {
                    if (status.isSuccess()) {
                        Log.d(TAG, "Connected to: "+ endpointId);
                        Nearby.Connections.stopDiscovery(googleApiClient, serviceId);
                        remoteHostEndpoint = endpointId;

                        if (!MainActivity.isHost) {
                            isConnected = true;
                        }
                    } else {
                        Log.d(TAG, "Connection failed to host: "+ endpointId);
                        Log.d(TAG, "Status code: "+ status.getStatus().getStatusCode());
                        Log.d(TAG, "Device Id: "+ deviceId);

                        if (!MainActivity.isHost) {
                            isConnected = false;
                        }
                    }
                }
            }, this);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void advertise() {
        if (!isConnectedToNetwork()) {
            return;
        }

        Log.d(TAG, "Trying to advertise.");

        MainActivity.isHost = true;
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

    @Override
    public void onConnectionRequest(final String remoteEndpointId,
                                    final String remoteDeviceId,
                                    final String remoteEndpointName,
                                    byte[] payload) {
        if (MainActivity.isHost) {
            byte[] myPayload = null;
            Nearby.Connections.acceptConnectionRequest(googleApiClient, remoteEndpointId, myPayload, this).setResultCallback(new ResultCallback<Status>() {
                @Override
                public void onResult(Status status) {
                    if (status.isSuccess()) {
                        Log.d(TAG, "New client connected remoteEndpointName: "+ remoteEndpointName);
                        Log.d(TAG, "New client connected remoteDeviceId: "+ remoteDeviceId);
                        Log.d(TAG, "New client connected remoteEndpointId: "+ remoteEndpointId);

                        if (!remotePeerEndpoints.contains(remoteEndpointId)) {
                            remotePeerEndpoints.add(remoteEndpointId);
                        }

                        sendQueueListToClients();

                    } else {
                        Log.d(TAG, "Failed connecting to: "+ remoteEndpointName);
                    }
                }
            });
        } else {
            Nearby.Connections.rejectConnectionRequest(googleApiClient, remoteEndpointId);
        }
    }

    private boolean isConnectedToNetwork() {
        ConnectivityManager connManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo info = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
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

        if (MainActivity.isHost) {
            Log.d(TAG, "Disconnecting host.");
            Nearby.Connections.stopAdvertising(googleApiClient);
            Nearby.Connections.stopAllEndpoints(googleApiClient);
            MainActivity.isHost = false;
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
        MainActivity.UserID = Nearby.Connections.getLocalEndpointId(googleApiClient);
        if(MainActivity.isHost){
            advertise();
        }else{
            discover();
        }
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

        if (MainActivity.isHost) {
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
}
