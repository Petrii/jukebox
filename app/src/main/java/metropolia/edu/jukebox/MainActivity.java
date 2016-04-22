package metropolia.edu.jukebox;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.spotify.sdk.android.player.Spotify;

import metropolia.edu.jukebox.queue.QueueFragment;
import metropolia.edu.jukebox.search.SearchFragment;

public class MainActivity extends AppCompatActivity {

    public static String TOKEN;
    public static boolean isHost = false;
    public static boolean updateUI = false;
    private static final String TAG = "MainActivity";

    public Connection connection;
    private String QueueFragmentTAG = "";
    private Playback playback;
    private QueueFragment queueFragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        TOKEN = CredentialsHandler.getToken(this);
        Intent intent = getIntent();

        this.connection = new Connection(this);
        this.isHost = intent.getBooleanExtra("isHost", false);

        final ViewPager viewPager = (ViewPager) findViewById(R.id.view_pager);
        setupViewPager(viewPager);

        final TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.getTabAt(0).setIcon(R.drawable.ic_queue_music_white_48dp);
        tabLayout.getTabAt(1).setIcon(R.drawable.ic_search_white_48dp);
        tabLayout.getTabAt(2).setIcon(R.drawable.ic_share_white_48dp);

        intitializeFragentTag();
    }

    public void initializePlayBack(){
        this.playback = new Playback(this);
        new Thread(playback).start();
    }

    /**
     * This wait QueueFragmentTAG and send it to Playback
     * Playback use this fragment to update QueueList ListView
     */
    private void intitializeFragentTag(){
        Runnable setup = new Runnable() {
            @Override
            public void run() {
                while(QueueFragmentTAG.equals("")){
                    // Wait loop. Is this correct way to ensure that QueueFragmentTAG is set
                    // and then start player.
                }
                uiUpdateThread();
            }
        };
        new Thread(setup).start();
    }

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
                            Log.d(TAG, "uiUpdateThread handler");
                            if(updateUI) {
                                Log.d(TAG, "Updating");
                                queueFragment.updateListView();
                                updateUI = false;
                            }
                            }
                        });
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.start();
    }

    public void advertise() {
        connection.advertise();
    }

    public void discover() {
        connection.discover();
    }

    private void setupViewPager(ViewPager viewPager) {
        final ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFrag(new QueueFragment(), getString(R.string.queue));
        adapter.addFrag(new SearchFragment(), getString(R.string.search));
        adapter.addFrag(new SettingsFragment(), getString(R.string.settings));

        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(2);
    }

    public void setTabFragment(String tag){
        QueueFragmentTAG = tag;
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
