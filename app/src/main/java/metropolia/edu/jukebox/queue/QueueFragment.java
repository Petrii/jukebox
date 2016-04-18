package metropolia.edu.jukebox.queue;


import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import metropolia.edu.jukebox.MainActivity;
import metropolia.edu.jukebox.Playback;
import metropolia.edu.jukebox.R;
import metropolia.edu.jukebox.search.ResultListScrollListener;

/**
 * Created by petri on 30.3.2016.
 */
public class QueueFragment extends Fragment {

    private static final String TAG = "QueueFragment";
    private static final String QUEUE_LIST_BUNDLE = "QueueList";
    private QueueListAdapter mAdapter;
    private View view;
    private QueueList queueList;
    private ProgressBar playingBar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_queue, container, false);
        ((MainActivity)getActivity()).setTabFragment(getTag());
        RecyclerView resultsList = (RecyclerView) view.findViewById(R.id.queue_list);
        resultsList.setHasFixedSize(true);
        resultsList.setLayoutManager(new LinearLayoutManager(this.getContext()));
        resultsList.setAdapter(mAdapter);
        playingBar = (ProgressBar)view.findViewById(R.id.progressBar);
        return view;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(savedInstanceState != null){
            //QueueList q = QueueList.getInstance();
            queueList = savedInstanceState.getParcelable(QUEUE_LIST_BUNDLE);
            //QueueList.setList(savedInstanceState.getParcelable(QUEUE_LIST_BUNDLE));
        }else{
            queueList = QueueList.getInstance();
        }
        mAdapter = new QueueListAdapter(this.getContext());
    }

    /**
     * This help us to add new tracks to queue list.
     * Also this add separately to QueueListAdapter and QueueList.
     *
     * @param id
     * @param name
     * @param artist
     * @param image
     */
    public void addToQueueList(String id, String name, String artist, String image){
        queueList.addToQueue(id, name, artist, image);
        mAdapter.notifyDataSetChanged();
    }

    public void updateListView(){
        mAdapter.notifyDataSetChanged();
    }

    public synchronized void initProgressBar(int trackLength){
        playingBar.setVisibility(View.VISIBLE);
        playingBar.setProgress(0);
        playingBar.setMax(trackLength);
    }

    public synchronized void updateBar(int position){
        playingBar.setProgress(position);
    }

    @Override
    public void onResume() {
        super.onResume();
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d("onStart", "ok");
    }

    @Override
    public void onPause() {
        super.onPause();
        Bundle b = new Bundle();
        b.putParcelable(QUEUE_LIST_BUNDLE, queueList);
        Log.d(TAG, "onPause");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, "onStop");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.d(TAG, "onDestroyView");
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }
}