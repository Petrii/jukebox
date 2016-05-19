package metropolia.edu.jukebox;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.TextView;
import android.widget.Toast;

import com.spotify.sdk.android.player.Spotify;

import metropolia.edu.jukebox.queue.QueueFragment;
import metropolia.edu.jukebox.resources.Beacon;
import metropolia.edu.jukebox.resources.Connection;
import metropolia.edu.jukebox.resources.CredentialsHandler;
import metropolia.edu.jukebox.resources.Playback;
import metropolia.edu.jukebox.resources.ViewPagerAdapter;
import metropolia.edu.jukebox.search.SearchFragment;
import metropolia.edu.jukebox.share.RootFragment;

public class MainActivity extends AppCompatActivity{
    public static String TOKEN;
    public static final String CLIENT_ID = "5edab87c1536471aab90d32d5c528875";
    public static String UserID = "JukeBox"; // Default user, used in Votes
    public static String jukeboxLoginAuth = null; // Auth code from Beacon
    public static boolean isHost = false; // Indicator if user is Host
    public static boolean updateUI = false; // Indicator if UI needs to update
    public static boolean isActive = false;
    private static final String TAG = "MainActivity";

    public Connection connection; // Communication between phones
    public Beacon beacon; // Searching beacons
    private Playback playback = null; // Spotify player
    private String QueueFragmentTAG = ""; // QueueFragment tag for helping to call updateUI methods
    private QueueFragment queueFragment; // =//=
    private ViewPager viewPager;

    private static final int REQUEST_PERMISSION = 42;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.connection = new Connection(this, this);
        TOKEN = CredentialsHandler.getToken(this);
        viewPager = (ViewPager) findViewById(R.id.view_pager);
        setupViewPager(viewPager);
        setupTabIcons();
        intitializeFragmentTag();
    }

    @Override
    protected void onStart() {
        super.onStart();
        connection.connectGoogleApi();
        if(isActive && !isHost && jukeboxLoginAuth!=null){
            connection.discover(jukeboxLoginAuth);
        }
    }

    private void setupTabIcons() {
        final TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        tabLayout.setupWithViewPager(viewPager);

        final TextView tabQueue = (TextView) LayoutInflater.from(this).inflate(R.layout.custom_tab,null);
        tabQueue.setText("Playlist");
        tabQueue.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_queue_music_white_24dp, 0, 0);
        tabLayout.getTabAt(0).setCustomView(tabQueue);

        final TextView tabAdd = (TextView) LayoutInflater.from(this).inflate(R.layout.custom_tab,null);
        tabAdd.setText("Add Music");
        tabAdd.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.add_music, 0, 0);
        tabLayout.getTabAt(1).setCustomView(tabAdd);

        final TextView tabShare = (TextView) LayoutInflater.from(this).inflate(R.layout.custom_tab,null);
        tabShare.setText("Share");
        tabShare.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_share_white_24dp, 0, 0);
        tabLayout.getTabAt(2).setCustomView(tabShare);
    }

    private void setupViewPager(ViewPager viewPager) {
        final ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFrag(new QueueFragment(), getString(R.string.queue));
        adapter.addFrag(new SearchFragment(), getString(R.string.search));
        adapter.addFrag(new RootFragment(), getString(R.string.settings));
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(2);
    }

    /**
     * This wait QueueFragmentTAG and send it to Playback
     * Playback use this fragment to update QueueList ListView
     */
    private void intitializeFragmentTag(){
        new Thread(
            new Runnable() {
            @Override
            public void run() {
                try {
                    while(QueueFragmentTAG.equals("")){
                        Thread.sleep(1000);
                        // Wait loop. Is this correct way to ensure that QueueFragmentTAG is set
                        // and then start player.
                    }
                    uiUpdateThread();
                    Log.d(TAG, "start playback");
                    new Thread(playback).start();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        ).start();
    }

    // User Interface updating thread
    private void uiUpdateThread() {
        queueFragment = (QueueFragment)this
                .getSupportFragmentManager()
                .findFragmentByTag(QueueFragmentTAG);

        new Thread(){
            public void run(){
                while(true){
                    try {
                        Thread.sleep(1000);
                        runOnUiThread(new Runnable(){
                            @Override
                            public void run() {
                                if(updateUI) {
                                    queueFragment.updateListView();
                                    updateUI = false;
                                }
                                queueFragment.updateNowPlaying();
                            }
                        });
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.start();
    }

    // Media controls
    public void mediaResume(){
        playback.resume();
    }
    public void mediaPause(){
        playback.pause();
    }
    public void mediaNext(){
        playback.next();
    }
    public void setTabFragment(String tag){
        QueueFragmentTAG = tag;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("isHost", isHost);
        outState.putBoolean("isActive", isActive);
        outState.putString("authClientCode", jukeboxLoginAuth);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        jukeboxLoginAuth = savedInstanceState.getString("authClientCode");
        isActive = savedInstanceState.getBoolean("isActive");
        if(!TOKEN.isEmpty())
            isHost = savedInstanceState.getBoolean("isHost");
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        connection.disconnect();
        Spotify.destroyPlayer(this);
        super.onDestroy();
    }

    private void showToast(String text) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
    }

}
