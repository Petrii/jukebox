package metropolia.edu.jukebox;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.spotify.sdk.android.authentication.AuthenticationClient;
import com.spotify.sdk.android.authentication.AuthenticationRequest;
import com.spotify.sdk.android.authentication.AuthenticationResponse;

import java.util.concurrent.TimeUnit;

public class LoginActivity extends Activity {

    private static final String CLIENT_ID = "5edab87c1536471aab90d32d5c528875";
    private static final String REDIRECT_URI = "lbjukebox://callback";
    private static final int REQUEST_CODE = 1337;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String token = CredentialsHandler.getToken(this);
        // Check if logged in Spotify account
        if( token == null ){
            setContentView(R.layout.activity_login);
        }else{
            startMainActivity();
        }
    }

    public void onLoginButtonClicked(View v){
        AuthenticationRequest.Builder builder =
                new AuthenticationRequest.Builder(CLIENT_ID, AuthenticationResponse.Type.TOKEN, REDIRECT_URI);
        builder.setScopes(new String[]{"playlist-read"}); // Read your publicly available information
        AuthenticationRequest request = builder.build();

        AuthenticationClient.openLoginActivity(this, REQUEST_CODE, request);
    }

    public void onJoinButtonClicked(View v){
        startMainActivity();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        // Check if result comes from the correct activity
        if (requestCode == REQUEST_CODE) {
            AuthenticationResponse response = AuthenticationClient.getResponse(resultCode, intent);
            switch (response.getType()) {
                // Response was successful and contains auth token
                case TOKEN:
                    Log.d("Got token", ""+response.getAccessToken());
                    CredentialsHandler.setToken(this, response.getAccessToken(), response.getExpiresIn(), TimeUnit.SECONDS);
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
    /*
    *
    * @params String token
    */
    private void startMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

}
