package metropolia.edu.jukebox;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import com.spotify.sdk.android.player.Config;
import com.spotify.sdk.android.player.ConnectionStateCallback;
import com.spotify.sdk.android.player.Player;
import com.spotify.sdk.android.player.PlayerNotificationCallback;
import com.spotify.sdk.android.player.PlayerState;
import com.spotify.sdk.android.player.Spotify;

import metropolia.edu.jukebox.queue.QueueFragment;
import metropolia.edu.jukebox.queue.QueueList;

public final class Playback implements PlayerNotificationCallback,
        ConnectionStateCallback,
        Player.InitializationObserver,
        Runnable {

    private static final String TAG = "Playback";
    private final Context context;
    private final Player player;
    private final Config config;
    private boolean isPlay = false;
    private int duration = 1000; // Default time: 1 sek
    final QueueList queueList;

    public Playback(Context context ) {
        this.context = context;
        this.queueList = QueueList.getInstance();
        this.config = new Config(this.context, MainActivity.TOKEN, LoginActivity.CLIENT_ID);
        this.player = Spotify.getPlayer(this.config, this, this);
    }

    @Override
    public void run(){
        try {
            while (true) {
                Thread.sleep(1000);
                if ( isPlay == false ) {
                    if ( queueList.getTrackList().size() > 0 ) {
                        this.player.addConnectionStateCallback(Playback.this);
                        this.player.addPlayerNotificationCallback(Playback.this);
                        this.player.play("spotify:track:" + queueList.getTrackList().get(0).getId());
                        this.queueList.deleteTrack();
                        Thread.sleep(500);
                        this.isPlay = true;
                    }
                }
            }
        }catch (InterruptedException e){
            e.printStackTrace();
        }
    }

    @Override
    public void onInitialized(Player player) {
        Log.d(TAG, "Player initialized");
    }

    @Override
    public void onError(Throwable throwable) {
        Log.e(TAG, "Could not initialize player: " + throwable.getMessage());
    }

    @Override
    public void onPlaybackEvent(EventType eventType, PlayerState playerState) {
        Log.d(TAG, "Playback event received: " + eventType.name());
        switch(eventType.name()){
            case "TRACK_CHANGED":
                Log.d(TAG, "TRACK_CHANGED");
                duration = playerState.durationInMs;
                break;
            case "TRACK_END":
                Log.d(TAG, "TRACK_END");
                isPlay = false;
                break;
            case "PAUSE":
                isPlay = false;
                break;
        }
    }

    @Override
    public void onPlaybackError(ErrorType errorType, String s) {
        Log.d(TAG, "Playback error received: " + errorType.name());
    }

    @Override
    public void onLoggedIn() {
        Log.d(TAG, "User logged in");
    }

    @Override
    public void onLoggedOut() {
        Log.d(TAG, "User logged out");
    }

    @Override
    public void onLoginFailed(Throwable throwable) {
        Log.d(TAG, "Login failed"+ throwable.getMessage());
    }

    @Override
    public void onTemporaryError() {
        Log.d(TAG, "Temporary error occurred");
    }

    @Override
    public void onConnectionMessage(String s) {
        Log.d(TAG, "Received connection message: " + s);
    }
}
