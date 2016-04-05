package metropolia.edu.jukebox.queue;

import android.widget.ArrayAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by petri on 5.4.2016.
 */
public class Queue {
    private List<Track> queueList = new ArrayList<Track>();

    public Queue(){
        queueList.add(new Track("1","Biisi1","Artisti1"));
    }

    public void addNewTrack(String id, String name, String artist){
        queueList.add(new Track(id,name,artist));
    }

    public void updateVote(String trackId, String userId, Boolean vote){
        for (Track track : queueList) {
           if( track.getId() == trackId){
               track.addVote(userId, vote);
               break;
           }
        }
    }

    public List<Track> getQueueList(){
        return queueList;
    }
}
