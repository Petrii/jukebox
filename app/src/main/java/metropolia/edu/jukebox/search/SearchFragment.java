package metropolia.edu.jukebox.search;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import kaaes.spotify.webapi.android.models.ArtistSimple;
import kaaes.spotify.webapi.android.models.Track;
import metropolia.edu.jukebox.MainActivity;
import metropolia.edu.jukebox.R;
import metropolia.edu.jukebox.queue.QueueList;


public class SearchFragment extends Fragment implements Search.View {
    private static final String KEY_CURRENT_QUERY = "CURRENT_QUERY";

    private Search.ActionListener mActionListener;
    private ScrollListener mScrollListener = new ScrollListener(new LinearLayoutManager(this.getContext()));
    private SearchResultsAdapter mAdapter;
    private View view;
    private QueueList queueList;

    private class ScrollListener extends ResultListScrollListener {
        public ScrollListener(LinearLayoutManager layoutManager) {
            super(layoutManager);
        }
        @Override
        public void onLoadMore() {
            mActionListener.loadMoreResults();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_search, container, false);
        final TextView searchText = (TextView) LayoutInflater.from(this.getContext()).inflate(R.layout.custom_search,null);
        final SearchView searchView = (SearchView) view.findViewById(R.id.search_view);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                mActionListener.search(query);
                searchView.clearFocus();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        // Adding selected track to QueueList
        mAdapter = new SearchResultsAdapter(this.getContext(),new SearchResultsAdapter.ItemSelectedListener(){
            @Override
            public void onItemSelected(View itemView, Track item) {
                String artist = "";

                for (ArtistSimple i : item.artists) {
                    // Takes a last artist
                    artist = i.name;
                }

                if (MainActivity.isHost) {
                    queueList.addToQueue(item.id, item.name, artist, item.album.images.get(0).url);
                    ((MainActivity) getActivity()).connection.sendQueueListToClients();
                } else {
                    ((MainActivity) getActivity()).connection.sendTrackToHost(
                            new metropolia.edu.jukebox.queue.Track(
                                    item.id,
                                    item.name,
                                    artist,
                                    item.album.images.get(0).url));
                }

                // Just some user input indicator
                Toast.makeText(getContext(), item.name+" added to queue!", Toast.LENGTH_SHORT).show();
            }
        });

        RecyclerView resultsList = (RecyclerView) view.findViewById(R.id.search_results);
        resultsList.setHasFixedSize(true);
        resultsList.setLayoutManager(new LinearLayoutManager(this.getContext()));
        resultsList.setAdapter(mAdapter);
        resultsList.addOnScrollListener(mScrollListener);

        return view;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        queueList = QueueList.getInstance();
        mActionListener = new SearchPresenter(this.getContext(), this);
        mActionListener.init(MainActivity.TOKEN);

        // If Activity was recreated wit active search restore it
        if (savedInstanceState != null) {
            String currentQuery = savedInstanceState.getString(KEY_CURRENT_QUERY);
            mActionListener.search(currentQuery);
        }
    }

    @Override
    public void reset() {
        mScrollListener.reset();
        mAdapter.clearData();
    }

    @Override
    public void onPause() {
        super.onPause();
        mActionListener.pause();
    }

    @Override
    public void onResume() {
        super.onResume();
        mActionListener.resume();
    }

    @Override
    public void addData(List<Track> items) {
        mAdapter.addData(items);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mActionListener.getCurrentQuery() != null) {
            outState.putString(KEY_CURRENT_QUERY, mActionListener.getCurrentQuery());
        }
    }

    @Override
    public void onDestroy() {
        mActionListener.destroy();
        super.onDestroy();
    }
}
