package metropolia.edu.jukebox.share;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.spotify.sdk.android.authentication.AuthenticationClient;
import com.spotify.sdk.android.authentication.AuthenticationRequest;
import com.spotify.sdk.android.authentication.AuthenticationResponse;

import java.util.concurrent.TimeUnit;

import metropolia.edu.jukebox.MainActivity;
import metropolia.edu.jukebox.R;
import metropolia.edu.jukebox.resources.CredentialsHandler;

/**
 * Created by petri on 12.5.2016.
 */
public class LoginFragment extends PreferenceFragmentCompat implements View.OnClickListener {
    private Button buttonSpotify, buttonJoin;
    public static final String REDIRECT_URI = "lbjukebox://callback";
    public static final int REQUEST_CODE = 17543;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_login, container, false);
        buttonSpotify = (Button)view.findViewById(R.id.button_login);
        buttonSpotify.setOnClickListener(this);
        buttonJoin = (Button)view.findViewById(R.id.button_join);
        buttonJoin.setOnClickListener(this);
        MainActivity.isActive = true;
        return view;
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.button_login:
                if(MainActivity.TOKEN == null) {
                    AuthenticationRequest request =
                            new AuthenticationRequest.Builder(MainActivity.CLIENT_ID, AuthenticationResponse.Type.TOKEN, REDIRECT_URI)
                                    .setScopes(new String[]{"playlist-read", "streaming"}).build(); // Read your publicly available information
                    Intent intent = AuthenticationClient.createLoginActivityIntent(getActivity(), request);
                    startActivityForResult(intent, REQUEST_CODE);
                }else{
                    MainActivity.isHost = true;
                    initShareView();
                }
                break;
            case R.id.button_join:
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.root_frame, new ShareFragment());
                transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                transaction.commit();
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE) {
            AuthenticationResponse response = AuthenticationClient.getResponse(resultCode, data);
            AuthenticationClient.stopLoginActivity(getActivity(), REQUEST_CODE);
            switch (response.getType()) {
                // Response was successful and contains auth token
                case TOKEN:
                    Log.d("Got token", ""+response.getAccessToken());
                    CredentialsHandler.setToken(getContext(), response.getAccessToken(), response.getExpiresIn(), TimeUnit.SECONDS);
                    MainActivity.isHost = true;
                    /*if(playback==null){
                        this.playback = new Playback(this, this);
                    }*/
                    ((MainActivity)getActivity()).connection.advertise();
                    initShareView();
                    break;
                // Auth flow returned an error
                case ERROR:
                    Log.d("Auth error",""+response.getError());
                    break;
                // Most likely auth flow was cancelled
                default:
                    MainActivity.TOKEN = CredentialsHandler.getToken(getContext());
                    Log.d("Auth result",""+response.getError());
                    if(MainActivity.TOKEN != null){
                        Log.d("LoginFragment", "TOKEN: "+MainActivity.TOKEN);
                        MainActivity.isHost = true;
                    }
            }
        }
    }

    private void initShareView(){
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.root_frame, new ShareFragment());
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        transaction.commit();
    }

    @Override
    public void onCreatePreferences(Bundle bundle, String s) {

    }
}
