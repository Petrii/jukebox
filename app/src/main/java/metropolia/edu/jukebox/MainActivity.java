package metropolia.edu.jukebox;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import com.spotify.sdk.android.player.Player;
import com.spotify.sdk.android.player.Spotify;
import metropolia.edu.jukebox.queue.QueueFragment;
import metropolia.edu.jukebox.queue.QueueList;
import metropolia.edu.jukebox.search.SearchFragment;

public class MainActivity extends AppCompatActivity {

    public static String TOKEN;
    private String QueueFragmentTAG = "QueueFragment TAG";
    private Playback playback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TOKEN = CredentialsHandler.getToken(this);

        //playback = new Playback(this);
        //playback.playFirstInQueue(QueueList.getQueueList());

        ViewPager viewPager = (ViewPager) findViewById(R.id.view_pager);
        setupViewPager(viewPager);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.getTabAt(0).setIcon(R.drawable.ic_queue_music_white_48dp);
        tabLayout.getTabAt(1).setIcon(R.drawable.ic_search_white_48dp);
        tabLayout.getTabAt(2).setIcon(R.drawable.ic_settings_white_48dp);
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

    /*
    @Override
    protected void onDestroy() {
        Spotify.destroyPlayer(this);
        super.onDestroy();
    }*/
}
