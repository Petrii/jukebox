package metropolia.edu.jukebox;

import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import com.spotify.sdk.android.player.Spotify;

import metropolia.edu.jukebox.queue.QueueFragment;
import metropolia.edu.jukebox.search.SearchFragment;

public class MainActivity extends AppCompatActivity {

    public static String TOKEN;
    private static final String TAG = "MainActivity";
    public static boolean isHost = false;
    private String QueueFragmentTAG = "";
    private Playback playback;
    public Connection connection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        TOKEN = CredentialsHandler.getToken(this);

        connection = new Connection(this);

        ViewPager viewPager = (ViewPager) findViewById(R.id.view_pager);
        setupViewPager(viewPager);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.getTabAt(0).setIcon(R.drawable.ic_queue_music_white_48dp);
        tabLayout.getTabAt(1).setIcon(R.drawable.ic_search_white_48dp);
        tabLayout.getTabAt(2).setIcon(R.drawable.ic_settings_white_48dp);

        playBackThread();
    }

    public void advertise() {
        connection.advertise();
    }

    public void discover() {
        connection.discover();
    }

    /**
     * This wait QueueFragmentTAG and send it to Playback
     * Playback use this fragment to update QueueList ListView
     */
    private void playBackThread(){
        Runnable setup = new Runnable() {
            @Override
            public void run() {
                while(QueueFragmentTAG.equals("")){
                    // Wait loop. Is this correct way to ensure that QueueFragmentTAG is set
                    // and then start player.
                }
                startPlayback();
            }
        };
        new Thread(setup).start();
    }

    /**
     * Initialize playback thread
     */
    private void startPlayback(){
        QueueFragment queueFragment = (QueueFragment)this
                .getSupportFragmentManager()
                .findFragmentByTag(QueueFragmentTAG);
        playback = new Playback(this, queueFragment);
        new Thread(playback).start();
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFrag(new QueueFragment(), getString(R.string.queue));
        adapter.addFrag(new SearchFragment(), getString(R.string.search));
        adapter.addFrag(new SettingsFragment(), getString(R.string.settings));
        viewPager.setAdapter(adapter);
    }

    public void setTabFragment(String tag){
        QueueFragmentTAG = tag;
    }

    public String getTabFragment(){
        return QueueFragmentTAG;
    }

    @Override
    protected void onStart() {
        super.onStart();

        connection.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();

        //connection.disconnect();
    }

    @Override
    protected void onDestroy() {
        Spotify.destroyPlayer(this);
        super.onDestroy();
    }
}
