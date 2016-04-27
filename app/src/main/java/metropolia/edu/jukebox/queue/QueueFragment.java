package metropolia.edu.jukebox.queue;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import metropolia.edu.jukebox.MainActivity;
import metropolia.edu.jukebox.Playback;
import metropolia.edu.jukebox.R;

/**
 * Created by petri on 30.3.2016.
 */
public class QueueFragment extends Fragment{

    private static final String TAG = "QueueFragment";
    private static final String QUEUE_LIST_BUNDLE = "QueueList";
    private QueueListAdapter mAdapter;
    private View view;
    private QueueList queueList;
    private Button playPause, nextButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_queue, container, false);
        ((MainActivity)getActivity()).setTabFragment(getTag());
        RecyclerView resultsList = (RecyclerView) view.findViewById(R.id.queue_list);
        resultsList.setHasFixedSize(true);
        resultsList.setLayoutManager(new LinearLayoutManager(this.getContext()));
        resultsList.setAdapter(mAdapter);
        playPause = (Button)view.findViewById(R.id.playPause);
        playPause.setOnClickListener(mToggleMediaButton);
        nextButton = (Button)view.findViewById(R.id.next);
        nextButton.setOnClickListener(mToggleMediaButton);
        return view;
    }

    View.OnClickListener mToggleMediaButton = new View.OnClickListener(){

        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.playPause:
                    if(Playback.isPlay()) {
                        Log.d(TAG, "pause: " +Playback.isPlay());
                        ((MainActivity) getActivity()).mediaPause();
                        playPause.setBackgroundResource(android.R.drawable.ic_media_play);
                    }else{
                        Log.d(TAG, "play: " +Playback.isPlay());
                        ((MainActivity) getActivity()).mediaResme();
                        playPause.setBackgroundResource(android.R.drawable.ic_media_pause);
                    }
                    break;
                case R.id.next:
                    ((MainActivity) getActivity()).mediaNext();
                    break;
            }

        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(savedInstanceState != null){
            queueList = savedInstanceState.getParcelable(QUEUE_LIST_BUNDLE);
        }else{
            queueList = QueueList.getInstance();
        }
        mAdapter = new QueueListAdapter(this.getContext(), new QueueListAdapter.ItemSelectedListener() {
            @Override
            public void onItemSelected(View itemView, final Track item, int buttonId) {
                // Like Button
                boolean isUpdated = false;
                if(buttonId == R.id.vote_up){
                    isUpdated = queueList.updateVote(item.getId(), MainActivity.UserID, true);
                }
                if(isUpdated){
                    if(MainActivity.isHost){
                        ((MainActivity)getActivity()).connection.sendQueueListToClients();
                    }else{
                        ((MainActivity)getActivity()).connection.sendTrackToHost(item);
                    }
                }
            }
        });
    }

    public void updateListView(){
        mAdapter.notifyDataSetChanged();
    }

    public void updateMediaButton(){
        playPause.setBackgroundResource(android.R.drawable.ic_media_play);
    }

    @Override
    public void onResume() {
        super.onResume();
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onPause() {
        super.onPause();
        Bundle b = new Bundle();
        b.putParcelable(QUEUE_LIST_BUNDLE, queueList);
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }
}