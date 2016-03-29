package metropolia.edu.jukebox;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Album;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class SearchActivity extends AppCompatActivity {
    static final String EXTRA_TOKEN = "EXTRA_TOKEN";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        Intent intent = getIntent();
        String token = intent.getStringExtra(EXTRA_TOKEN);

        ListView searchView = (ListView)findViewById(R.id.search_results);
    }

    public static Intent createIntent(Context context){
        return new Intent(context, SearchActivity.class);
    }
}
