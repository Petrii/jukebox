package metropolia.edu.jukebox;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v13.app.ActivityCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.TextView;
import android.widget.Toast;

import com.spotify.sdk.android.player.Spotify;

import metropolia.edu.jukebox.queue.QueueFragment;
import metropolia.edu.jukebox.search.SearchFragment;
import metropolia.edu.jukebox.share.Beacon;
import metropolia.edu.jukebox.share.ShareFragment;

public class MainActivity extends AppCompatActivity {

    public static String TOKEN;
    public static String UserID = "JukeBox";
    public static String jukeboxLoginAuth = null;
    public static boolean isHost = false;
    public static boolean updateUI = false;
    private static final String TAG = "MainActivity";

    public Connection connection;
    public Beacon beacon;
    private Playback playback;
    private String QueueFragmentTAG = "";
    private QueueFragment queueFragment;
    private ViewPager viewPager;

    private static final int REQUEST_RESOLVE_ERROR = 100;
    private static final int REQUEST_PERMISSION = 42;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        TOKEN = CredentialsHandler.getToken(this);
        Intent intent = getIntent();
        this.isHost = intent.getBooleanExtra("isHost", false);
        this.beacon = new Beacon(this, this);
        this.connection = new Connection(this, this, isHost);

        if (BeaconService.ACTION_DISMISS.equals(getIntent().getAction())) {
            Intent mIntent = new Intent(this, BeaconService.class);
            intent.setAction(BeaconService.ACTION_DISMISS);
            startService(mIntent);
        }

        viewPager = (ViewPager) findViewById(R.id.view_pager);
        setupViewPager(viewPager);

        setupTabIcons();
        intitializeFragmentTag();

        if(isHost){
            this.playback = new Playback(this, this);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_RESOLVE_ERROR) {
            if (resultCode == RESULT_OK) {
                // Permission granted or error resolved successfully then we proceed
                // with publish and subscribe..
                beacon.publishAndSubscribe();
            } else {
                // This may mean that user had rejected to grant nearby permission.
                showToast("Failed to resolve error with code " + resultCode);
            }
        }

        if (requestCode == REQUEST_PERMISSION) {
            if (resultCode != RESULT_OK) {
                showToast("We need location permission to get scan results!");
                finish();
            }
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

    private void setupViewPager(ViewPager viewPager) {
        final ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFrag(new QueueFragment(), getString(R.string.queue));
        adapter.addFrag(new SearchFragment(), getString(R.string.search));
        adapter.addFrag(new ShareFragment(), getString(R.string.settings));

        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(2);
    }

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
    protected void onStart() {
        super.onStart();
        final int result = ActivityCompat
                .checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION);
        if (result != PackageManager.PERMISSION_GRANTED) {
            //Ask for the location permission
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                    REQUEST_PERMISSION);
        }
        beacon.onStart();
        connection.connect();
        if(!isHost && jukeboxLoginAuth!=null){
            connection.discover(jukeboxLoginAuth);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("isHost", isHost);
        outState.putString("authClientCode", jukeboxLoginAuth);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        jukeboxLoginAuth = savedInstanceState.getString("authClientCode");
        if(!TOKEN.isEmpty())
            isHost = savedInstanceState.getBoolean("isHost");
    }

    @Override
    protected void onStop() {
        beacon.onStop();
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
