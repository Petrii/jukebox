package metropolia.edu.jukebox.share;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import metropolia.edu.jukebox.MainActivity;
import metropolia.edu.jukebox.R;

/**
 * Created by petri on 15.5.2016.
 */
public class RootFragment extends Fragment {
    private static final String TAG = "RootFragment";
    private Fragment fragment;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_share_root, container, false);
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        Log.d(TAG, ""+MainActivity.isActive);
        if (MainActivity.isActive) {
            fragment = new ShareFragment();
        } else {
            fragment = new LoginFragment();
        }
        transaction.replace(R.id.root_frame, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
        return view;
    }
}
