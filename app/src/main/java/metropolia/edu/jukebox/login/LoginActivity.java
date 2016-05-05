package metropolia.edu.jukebox.login;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.spotify.sdk.android.authentication.AuthenticationClient;
import com.spotify.sdk.android.authentication.AuthenticationRequest;
import com.spotify.sdk.android.authentication.AuthenticationResponse;

import java.util.concurrent.TimeUnit;

import metropolia.edu.jukebox.MainActivity;
import metropolia.edu.jukebox.R;

public class LoginActivity extends Activity {

    public static final String CLIENT_ID = "5edab87c1536471aab90d32d5c528875";
    private static final String REDIRECT_URI = "lbjukebox://callback";
    private static final int REQUEST_CODE = 48567;
    private boolean isHost = false;
    private String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        token = CredentialsHandler.getToken(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    /** If token is null or i.e. user isn't logged in Spotify. Then open Spotify API login form.
     *  Else go to the MainActivity
     *
     * @param v
     */
    public void onLoginButtonClicked(View v){
        if( token == null ){
            final AuthenticationRequest.Builder builder =
                    new AuthenticationRequest.Builder(CLIENT_ID, AuthenticationResponse.Type.TOKEN, REDIRECT_URI);
            builder.setScopes(new String[]{"playlist-read"}); // Read your publicly available information
            final AuthenticationRequest request = builder.build();
            AuthenticationClient.openLoginActivity(this, REQUEST_CODE, request);
        } else {
            isHost = true;
            startMainActivity();
        }
    }

    /**
     * @param v
     */
    public void onJoinButtonClicked(View v){
        isHost = false;
        startMainActivity();
    }

    /**
     * Spotify login API activity result.
     * If request code is TOKEN, login is successful. Save login token to CredentialsHandler.
     *
     * @param requestCode
     * @param resultCode
     * @param intent
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        // Check if result comes from the correct activity
        if (requestCode == REQUEST_CODE) {
            final AuthenticationResponse response = AuthenticationClient.getResponse(resultCode, intent);
            switch (response.getType()) {
                // Response was successful and contains auth token
                case TOKEN:
                    Log.d("Got token", ""+response.getAccessToken());
                    CredentialsHandler.setToken(this, response.getAccessToken(), response.getExpiresIn(), TimeUnit.SECONDS);
                    isHost = true;
                    startMainActivity();
                    break;
                // Auth flow returned an error
                case ERROR:
                    Log.d("Auth error",""+response.getError());
                    break;
                // Most likely auth flow was cancelled
                default:
                    Log.d("Auth result",""+response.getType());
            }
        }
    }
    /**
    *
    * @params String token
    */
    private void startMainActivity() {
        final Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("isHost", isHost);
        startActivity(intent);
        finish();
    }

    /**
     * This save instance is under the development
     *
     * @param outState
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("isHost", isHost);
    }

    /**
     * This save instance is under the development
     *
     * @param savedInstanceState
     */
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        this.isHost = savedInstanceState.getBoolean("isHost");
    }
}
