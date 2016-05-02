package metropolia.edu.jukebox;

import android.app.Activity;
import android.content.Context;
import android.content.IntentSender;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.nearby.Nearby;
import com.google.android.gms.nearby.connection.Connections;
import com.google.android.gms.nearby.messages.Message;
import com.google.android.gms.nearby.messages.MessageListener;
import com.google.android.gms.nearby.messages.Strategy;
import com.google.android.gms.nearby.messages.SubscribeCallback;
import com.google.android.gms.nearby.messages.SubscribeOptions;

/**
 * Created by petri on 1.5.2016.
 */
public class Beacon implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        Connections.MessageListener {
    private static final String TAG = "Beacon";
    private final Context context;
    private final Activity activity;
    GoogleApiClient mGoogleApiClient;
    // Create a new message listener.
    private MessageListener mMessageListener;

    private static final int REQUEST_RESOLVE_ERROR = 100;
    private static final int REQUEST_PERMISSION = 42;

    public Beacon(Context context, Activity activity){
        this.context = context;
        this.activity = activity;
        mGoogleApiClient = new GoogleApiClient.Builder(this.context)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Nearby.MESSAGES_API)
                .build();
        mGoogleApiClient.connect();

        mMessageListener = new MessageListener() {
            @Override
            public void onFound(Message message) {
                // Do something with the message.
                Log.i(TAG, "Message found: " + message);
                Log.i(TAG, "Message string: " + new String(message.getContent()));
                Log.i(TAG, "Message namespaced type: " + message.getNamespace() +
                        "/" + message.getType());
            }

            // Called when a message is no longer detectable nearby.
            public void onLost(Message message) {
                // Take appropriate action here (update UI, etc.)
            }
        };
    }

    public void subscribe(){
        SubscribeOptions options = new SubscribeOptions.Builder()
                .setStrategy(Strategy.BLE_ONLY)
                .setCallback(new SubscribeCallback() {
                    @Override
                    public void onExpired() {
                        Log.i(TAG, "No longer subscribing.");
                    }
                }).build();

        Nearby.Messages.subscribe(mGoogleApiClient, mMessageListener, options)
                .setResultCallback(new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        if (status.isSuccess()) {
                            Log.i(TAG, "Subscribed successfully."+status.getStatusMessage());
                        } else {
                            Log.i(TAG, "Could not subscribe."+ status.getStatus());
                            // Check whether consent was given;
                            // if not, prompt the user for consent.
                            //handleUnsuccessfulNearbyResult(status);
                        }
                    }
                });
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        //Once connected, we have to check that the user has opted in
        Runnable runOnSuccess = new Runnable() {
            @Override
            public void run() {
                //Subscribe once user permission is verified
                subscribe();
            }
        };
        ResultCallback<Status> callback =
                new ErrorCheckingCallback(runOnSuccess);
        Nearby.Messages.getPermissionStatus(mGoogleApiClient)
                .setResultCallback(callback);
    }

    //ResultCallback triggered when to handle Nearby permissions check
    private class ErrorCheckingCallback implements ResultCallback<Status> {
        private final Runnable runOnSuccess;

        private ErrorCheckingCallback(@Nullable Runnable runOnSuccess) {
            this.runOnSuccess = runOnSuccess;
        }

        @Override
        public void onResult(@NonNull Status status) {
            if (status.isSuccess()) {
                Log.i(TAG, "Permission status succeeded.");
                if (runOnSuccess != null) {
                    runOnSuccess.run();
                }
            } else {
                // Currently, the only resolvable error is that the device is not opted
                // in to Nearby. Starting the resolution displays an opt-in dialog.
                if (status.hasResolution()) {
                    try {
                        status.startResolutionForResult(activity,
                                REQUEST_RESOLVE_ERROR);
                    } catch (IntentSender.SendIntentException e) {
                        showToastAndLog(Log.ERROR, "Request failed with exception: " + e);
                    }
                } else {
                    showToastAndLog(Log.ERROR, "Request failed with : " + status);
                }
            }
        }
    }


    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onMessageReceived(String s, byte[] bytes, boolean b) {

    }

    @Override
    public void onDisconnected(String s) {

    }

    private void showToastAndLog(int logLevel, String message) {
        Toast.makeText(activity, message, Toast.LENGTH_SHORT).show();
        Log.println(logLevel, TAG, message);
    }
}
