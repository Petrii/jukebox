package metropolia.edu.jukebox;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;

import java.util.List;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Album;
import kaaes.spotify.webapi.android.models.Track;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;


public class SearchActivity extends AppCompatActivity {
    static final String EXTRA_TOKEN = "EXTRA_TOKEN";
    private Search mActionListener;
    private SearchResultAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        Intent intent = getIntent();
        String accessToken = intent.getStringExtra(EXTRA_TOKEN);

        mActionListener = new Search(accessToken);
        mAdapter = new SearchResultAdapter(this);

        final SearchView searchView = (SearchView)findViewById(R.id.search_view);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener(){
            @Override
            public boolean onQueryTextSubmit(String query) {
                mActionListener.search(query);
                mAdapter.addData(mActionListener.getResults());
                searchView.clearFocus();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        ListView resultList = (ListView)findViewById(R.id.search_results);
        resultList.setAdapter(mAdapter);
    }

    public static Intent createIntent(Context context){
        return new Intent(context, SearchActivity.class);
    }
}
