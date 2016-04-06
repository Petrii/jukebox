package metropolia.edu.jukebox;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import metropolia.edu.jukebox.queue.QueueFragment;
import metropolia.edu.jukebox.search.SearchFragment;

public class MainActivity extends AppCompatActivity {

    public static String TOKEN;
    private String QueueFragmentTAG = "QueueFragment TAG";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TOKEN = CredentialsHandler.getToken(this);

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
        adapter.addFrag(new QueueFragment(), "QueueList");
        adapter.addFrag(new SearchFragment(), "Search");
        adapter.addFrag(new SettingsFragment(), "Settings");
        viewPager.setAdapter(adapter);
    }

    public void setTabFragment(String tag){
        QueueFragmentTAG = tag;
    }

    public String getTabFragment(){
        return QueueFragmentTAG;
    }
}
