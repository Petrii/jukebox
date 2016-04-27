package metropolia.edu.jukebox;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.TextView;

import com.spotify.sdk.android.player.Spotify;

import metropolia.edu.jukebox.queue.QueueFragment;
import metropolia.edu.jukebox.search.SearchFragment;

public class MainActivity extends AppCompatActivity {

    public static String TOKEN;
    public static String UserID = "JukeBox";
    public static boolean isHost = false;
    public static boolean updateUI = false;
    private static final String TAG = "MainActivity";

    public Connection connection;
    private Playback playback;
    private String QueueFragmentTAG = "";
    private QueueFragment queueFragment;
    private ViewPager viewPager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        TOKEN = CredentialsHandler.getToken(this);
        Intent intent = getIntent();

        this.connection = new Connection(this);
        this.isHost = intent.getBooleanExtra("isHost", false);

        viewPager = (ViewPager) findViewById(R.id.view_pager);
        setupViewPager(viewPager);

        setupTabIcons();
        intitializeFragmentTag();

        if(isHost){
            this.playback = new Playback(this, this);
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

    /**
     * This wait QueueFragmentTAG and send it to Playback
     * Playback use this fragment to update QueueList ListView
     */
    private void intitializeFragmentTag(){
        Runnable setup = new Runnable() {
            @Override
            public void run() {
                while(QueueFragmentTAG.equals("")){
                    // Wait loop. Is this correct way to ensure that QueueFragmentTAG is set
                    // and then start player.
                }
                uiUpdateThread();
                Log.d(TAG, "start playback");
                new Thread(playback).start();
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
                                if(updateUI) {
                                    queueFragment.updateListView();
                                    updateUI = false;
                                }
                                queueFragment.updateMediaButton();
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

    public void mediaResme(){
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
    protected void onStart() {
        super.onStart();
        connection.connect();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("isHost", isHost);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
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
}
