package metropolia.edu.jukebox.queue;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import metropolia.edu.jukebox.R;
import metropolia.edu.jukebox.search.ResultListScrollListener;

/**
 * Created by petri on 30.3.2016.
 */
public class QueueFragment extends Fragment {

    private ScrollListener mScrollListener = new ScrollListener(new LinearLayoutManager(this.getContext()));
    private QueueListAdapter mAdapter;
    private View view;
    private Queue mQueue;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_queue, container, false);

        RecyclerView resultsList = (RecyclerView) view.findViewById(R.id.queue_list);
        resultsList.setHasFixedSize(true);
        resultsList.setLayoutManager(new LinearLayoutManager(this.getContext()));
        resultsList.setAdapter(mAdapter);
        resultsList.addOnScrollListener(mScrollListener);

        return view;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAdapter = new QueueListAdapter(this.getContext(), new QueueListAdapter.ItemSelectedListener() {
            @Override
            public void onItemSelected(View itemView, Track item) {
                // mActionListener.selectTrack(item);
            }
        });
        mAdapter.addData( new Queue().getQueueList() );
    }

    private class ScrollListener extends ResultListScrollListener {

        public ScrollListener(LinearLayoutManager layoutManager) {
            super(layoutManager);
        }

        @Override
        public void onLoadMore() {
            //mActionListener.loadMoreResults();
        }
    }
}
