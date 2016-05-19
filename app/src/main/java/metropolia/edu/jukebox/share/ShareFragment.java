package metropolia.edu.jukebox.share;

import android.os.Bundle;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import metropolia.edu.jukebox.MainActivity;
import metropolia.edu.jukebox.R;


public class ShareFragment extends PreferenceFragmentCompat implements View.OnClickListener{


    private Button joinButton, createButton, button_join;
    private EditText joinCode;
    private TextView shareJoinCode;
    public static final String TAG = "Settings";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MainActivity.isActive = true;
    }

    @Override
    public void onCreatePreferences(Bundle bundle, String s) {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view;
        if(MainActivity.isHost){
            view = inflater.inflate(R.layout.fragment_share, container, false);
            shareJoinCode = (TextView) view.findViewById(R.id.share_join_code);
            if(MainActivity.jukeboxLoginAuth !=null){
                shareJoinCode.setText(MainActivity.jukeboxLoginAuth);
            }
        }else{
            view = inflater.inflate(R.layout.fragment_join, container, false);
            joinCode = (EditText)view.findViewById(R.id.join_code);
            if(MainActivity.jukeboxLoginAuth !=null){
                joinCode.setHint(MainActivity.jukeboxLoginAuth);
                //joinCode.setText(MainActivity.jukeboxLoginAuth);
            }
            joinButton = (Button)view.findViewById(R.id.join_party);
            joinButton.setOnClickListener(this);
        }
        return view;
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.join_party:
                MainActivity.jukeboxLoginAuth = joinCode.getText().toString();
                ((MainActivity) getActivity()).connection.discover(joinCode.getText().toString());
                break;
        }
    }
}
