package metropolia.edu.jukebox;

import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyCallback;
import kaaes.spotify.webapi.android.SpotifyError;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Track;
import kaaes.spotify.webapi.android.models.TracksPager;
import retrofit.client.Response;

/**
 * Created by petri on 29.3.2016.
 */
public class Search {

    private SpotifyService mSpotifyApi;
    private int mCurrentOffset;
    private int mPagesize;
    private String mCurrentQuery;
    private List<Track> mTrackItems = new ArrayList<>();

    public Search(String accessToken){
        SpotifyApi spotifyApi = new SpotifyApi();
        if (accessToken != null) {
           spotifyApi.setAccessToken(accessToken);
        } else {
            Log.d("accessToken","No valid access token");
        }
        mSpotifyApi = spotifyApi.getService();
    }

    public void search(String searchQuery){
        if(searchQuery != null && !searchQuery.isEmpty() && !searchQuery.equals(mCurrentQuery)){
            mCurrentQuery = searchQuery;
            getFirstResults();
        }
    }

    public List<Track> getResults(){
        return mTrackItems;
    }

    private void getFirstResults(){
        mCurrentOffset = 0;
        mPagesize = 20;
        mTrackItems.clear();
        getData();
    }

    private void getData() {
        Map<String, Object> options = new HashMap<>();
        options.put(SpotifyService.OFFSET, mCurrentOffset);
        options.put(SpotifyService.LIMIT, mPagesize);

        mSpotifyApi.searchTracks(mCurrentQuery, options, new SpotifyCallback<TracksPager>() {
            @Override
            public void failure(SpotifyError spotifyError) {
                Log.d("search Tracks", ""+spotifyError);
            }

            @Override
            public void success(TracksPager tracksPager, Response response) {
                mTrackItems.addAll(tracksPager.tracks.items);
            }
        });
    }
}
