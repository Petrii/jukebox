package metropolia.edu.jukebox;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.spotify.sdk.android.player.Player;

public class MainActivity extends AppCompatActivity {
    private Player mPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.menu_search){
            Intent intent = SearchActivity.createIntent(this);
            intent.putExtra(SearchActivity.EXTRA_TOKEN, CredentialsHandler.getToken(this));
            startActivity(intent);
        }
        return true;
    }
}
