package metropolia.edu.jukebox.queue;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import kaaes.spotify.webapi.android.models.Image;
import metropolia.edu.jukebox.MainActivity;
import metropolia.edu.jukebox.R;
import metropolia.edu.jukebox.search.ResultListScrollListener;

/**
 * Created by petri on 30.3.2016.
 */
public class QueueFragment extends Fragment {

    private static final String LOG_TAG = "QueueFragment";
    private ScrollListener mScrollListener = new ScrollListener(new LinearLayoutManager(this.getContext()));
    private QueueListAdapter mAdapter;
    private View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_queue, container, false);

        ((MainActivity)getActivity()).setTabFragment(getTag());

        RecyclerView resultsList = (RecyclerView) view.findViewById(R.id.queue_list);
        resultsList.setHasFixedSize(true);
        resultsList.setLayoutManager(new LinearLayoutManager(this.getContext()));
        resultsList.setAdapter(mAdapter);
        resultsList.addOnScrollListener(mScrollListener);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        mAdapter.clearData();
        mAdapter.addData(QueueList.getQueueList());
        mAdapter.notifyDataSetChanged();
        Log.d("onStart", "ok");
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAdapter = new QueueListAdapter(this.getContext(), new QueueListAdapter.ItemSelectedListener() {
            @Override
            public void onItemSelected(View itemView, Track item) {;
            }
        });
    }

    private class ScrollListener extends ResultListScrollListener {

        public ScrollListener(LinearLayoutManager layoutManager) {
            super(layoutManager);
        }

        @Override
        public void onLoadMore() {
        }
    }

    /**
     * This help us to add new tracks to queue list.
     * Also this add separately to QueueListAdapter and QueueList.
     *
     * @param id
     * @param name
     * @param artist
     */
    public void addToQueueList(String id, String name, String artist){
        QueueList.addToQueue(id, name, artist);
        mAdapter.addNewTrack(id, name, artist);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(LOG_TAG, "onPause");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(LOG_TAG, "onStop");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.d(LOG_TAG, "onDestroyView");
    }
}
