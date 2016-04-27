package metropolia.edu.jukebox.queue;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.ksoichiro.android.observablescrollview.ObservableRecyclerView;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollViewCallbacks;
import com.github.ksoichiro.android.observablescrollview.ScrollState;
import com.squareup.picasso.Picasso;

import metropolia.edu.jukebox.MainActivity;
import metropolia.edu.jukebox.Playback;
import metropolia.edu.jukebox.R;

/**
 * Created by petri on 30.3.2016.
 */
public class QueueFragment extends Fragment implements ObservableScrollViewCallbacks {

    private static final String TAG = "QueueFragment";
    private static final String QUEUE_LIST_BUNDLE = "QueueList";
    private QueueListAdapter mAdapter;
    private View view;
    private TextView nowArtist, nowTrack;
    private QueueList queueList;
    private Button playPause, nextButton;
    private ImageView imageView;
    private FrameLayout frameLayout;
    private ObservableRecyclerView resultsList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_queue, container, false);
        ((MainActivity)getActivity()).setTabFragment(getTag());

        /*RecyclerView resultsList = (RecyclerView) view.findViewById(R.id.queue_list);
        resultsList.setHasFixedSize(true);
        resultsList.setLayoutManager(new LinearLayoutManager(this.getContext()));
        resultsList.setAdapter(mAdapter);*/

        resultsList = (ObservableRecyclerView) view.findViewById(R.id.queue_list);
        resultsList.setScrollViewCallbacks(this);
        resultsList.setLayoutManager(new LinearLayoutManager(this.getContext()));
        resultsList.setAdapter(mAdapter);

        nowArtist = (TextView)view.findViewById(R.id.now_artist);
        nowTrack = (TextView)view.findViewById(R.id.now_track);
        imageView = (ImageView)view.findViewById(R.id.artist_image);
        playPause = (Button)view.findViewById(R.id.playPause);
        playPause.setOnClickListener(mToggleMediaButton);
        nextButton = (Button)view.findViewById(R.id.next);
        nextButton.setOnClickListener(mToggleMediaButton);
        frameLayout = (FrameLayout)view.findViewById(R.id.frame_playing);

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
        if(!queueList.getTrackList().isEmpty()){
            Picasso.with(getContext()).load(queueList.getTrackList()
                    .get(0).getTrack_image()).into(imageView);
            nowArtist.setText(queueList.getTrackList().get(0).getArtist());
            nowTrack.setText(queueList.getTrackList().get(0).getName());
        }
        mAdapter.notifyDataSetChanged();
    }

    public void updateMediaButton(){
        if(!Playback.isPlay())
            playPause.setBackgroundResource(android.R.drawable.ic_media_play);
        else
            playPause.setBackgroundResource(android.R.drawable.ic_media_pause);
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

    @Override
    public void onScrollChanged(int scrollY, boolean firstScroll, boolean dragging) {
        //frameLayout.animate().translationY(-scrollY);
        //resultsList.animate().translationY(-scrollY);

    }

    @Override
    public void onDownMotionEvent() {

    }

    @Override
    public void onUpOrCancelMotionEvent(ScrollState scrollState) {
        if (scrollState == ScrollState.UP) {
            if (frameLayout.isShown()) {
                frameLayout.setVisibility(View.GONE);
            }
        } else if (scrollState == ScrollState.DOWN) {
            if (!frameLayout.isShown()) {
                frameLayout.setVisibility(View.VISIBLE);
            }
        }
    }
}