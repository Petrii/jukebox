package metropolia.edu.jukebox.share;

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

import metropolia.edu.jukebox.MainActivity;

/**
 * Created by petri on 1.5.2016.
 */
public class Beacon implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        Connections.MessageListener {
    private static final int REQUEST_RESOLVE_ERROR = 100;
    private static final String TAG = "Beacon";
    private static final String NAMESPACE = "carbide-cairn-129823";
    private static final String TYPE = "metropolia.edu.jukebox";

    private final Context context;
    private final Activity activity;
    private final GoogleApiClient mGoogleApiClient;

    private boolean mResolvingError = false;

    public Beacon(Context context, Activity activity){
        this.context = context;
        this.activity = activity;
        mGoogleApiClient = new GoogleApiClient.Builder(this.context)
                .addApi(Nearby.MESSAGES_API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        mGoogleApiClient.connect();

        Log.i(TAG, "Trying to publish");
    }

    private MessageListener mMessageListener = new MessageListener() {
        // Called each time a new message is discovered nearby.
        @Override
        public void onFound(Message message) {
            if(message.getNamespace().equals(NAMESPACE) && message.getType().equals(TYPE)){
                MainActivity.jukeboxLoginAuth = new String(message.getContent());
                onStop();
                Log.i(TAG, "Message found and stopping service");
            }
        }
        // Called when the publisher (beacon) is no longer nearby.
        @Override
        public void onLost(Message message) {
            Log.i(TAG, "Lost message: " + message);
        }
    };

    public void onStart(){
        if(!mGoogleApiClient.isConnected()){
            mGoogleApiClient.connect();
        }
    }

    public void onStop(){
        if(mGoogleApiClient.isConnected()){
            Nearby.Messages.unsubscribe(mGoogleApiClient, mMessageListener)
                    .setResultCallback(new ErrorCheckingCallback("unsubscribe()"));
        }
        mGoogleApiClient.disconnect();
    }

    public void publishAndSubscribe(){
        final SubscribeOptions options = new SubscribeOptions.Builder()
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
                            Log.i(TAG, "Subscribed successfully.");
                        } else {
                            Log.i(TAG, "Could not subscribe.");
                            // Check whether consent was given;
                            // if not, prompt the user for consent.
                        }
                    }
                });
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Nearby.Messages.getPermissionStatus(mGoogleApiClient).setResultCallback(
                new ErrorCheckingCallback("getPermissionStatus", new Runnable(){
                    @Override
                    public void run() {
                        publishAndSubscribe();
                    }
                })
        );
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onMessageReceived(String s, byte[] bytes, boolean b) {

    }

    @Override
    public void onDisconnected(String s) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    private class ErrorCheckingCallback implements ResultCallback<Status> {
        private final String method;
        private final Runnable runOnSuccess;

        private ErrorCheckingCallback(String method) {
            this(method, null);
        }

        private ErrorCheckingCallback(String method, @Nullable Runnable runOnSuccess) {
            this.method = method;
            this.runOnSuccess = runOnSuccess;
        }

        @Override
        public void onResult(@NonNull Status status) {
            if (status.isSuccess()) {
                Log.i(TAG, method + " succeeded.");
                if (runOnSuccess != null) {
                    runOnSuccess.run();
                }
            } else {
                // Currently, the only resolvable error is that the device is not opted
                // in to Nearby. Starting the resolution displays an opt-in dialog.
                if (status.hasResolution()) {
                    if (!mResolvingError) {
                        try {
                            status.startResolutionForResult(activity,
                                    REQUEST_RESOLVE_ERROR);
                            mResolvingError = true;
                        } catch (IntentSender.SendIntentException e) {
                            showToastAndLog(Log.ERROR, method + " failed with exception: " + e);
                        }
                    } else {
                        // This will be encountered on initial startup because we do
                        // both publish and subscribe together.  So having a toast while
                        // resolving dialog is in progress is confusing, so just log it.
                        Log.i(TAG, method + " failed with status: " + status
                                + " while resolving error.");
                    }
                } else {
                    showToastAndLog(Log.ERROR, method + " failed with : " + status
                            + " resolving error: " + mResolvingError);
                }
            }
        }
    }

    protected void showToastAndLog(int logLevel, String message) {
        Toast.makeText(activity, message, Toast.LENGTH_SHORT).show();
        Log.println(logLevel, TAG, message);
    }
}
