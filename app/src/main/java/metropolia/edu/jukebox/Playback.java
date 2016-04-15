package metropolia.edu.jukebox;

import android.content.Context;
import android.util.Log;
import com.spotify.sdk.android.player.Config;
import com.spotify.sdk.android.player.ConnectionStateCallback;
import com.spotify.sdk.android.player.Player;
import com.spotify.sdk.android.player.PlayerNotificationCallback;
import com.spotify.sdk.android.player.PlayerState;
import com.spotify.sdk.android.player.Spotify;

import java.util.List;

import metropolia.edu.jukebox.queue.Track;

public class Playback implements PlayerNotificationCallback, ConnectionStateCallback, Player.InitializationObserver {

    private Context context;
    private Player player;

    public Playback(Context context) {
        this.context = context;

        Config config = new Config(this.context, MainActivity.TOKEN, LoginActivity.CLIENT_ID);
        player = Spotify.getPlayer(config, this, this);
    }

    public void playFirstInQueue(List<Track> queue) {
        Log.d("Playback", "First in queue:"+ queue.get(0).getId());

        this.player.addConnectionStateCallback(Playback.this);
        this.player.addPlayerNotificationCallback(Playback.this);
        this.player.play("spotify:track:"+ queue.get(0).getId());
    }

    @Override
    public void onInitialized(Player player) {
        Log.d("Playback", "Player initialized");
    }

    @Override
    public void onError(Throwable throwable) {
        Log.e("Playback", "Could not initialize player: " + throwable.getMessage());
    }

    @Override
    public void onPlaybackEvent(EventType eventType, PlayerState playerState) {
        Log.d("Playback", "Playback event received: " + eventType.name());
    }

    @Override
    public void onPlaybackError(ErrorType errorType, String s) {
        Log.d("Playback", "Playback error received: " + errorType.name());
    }

    @Override
    public void onLoggedIn() {
        Log.d("Playback", "User logged in");
    }

    @Override
    public void onLoggedOut() {
        Log.d("Playback", "User logged out");
    }

    @Override
    public void onLoginFailed(Throwable throwable) {
        Log.d("Playback", "Login failed"+ throwable.getMessage());
    }

    @Override
    public void onTemporaryError() {
        Log.d("Playback", "Temporary error occurred");
    }

    @Override
    public void onConnectionMessage(String s) {
        Log.d("Playback", "Received connection message: " + s);
    }
}
