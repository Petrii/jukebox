package metropolia.edu.jukebox.queue;

import java.util.ArrayList;
import java.util.List;

import kaaes.spotify.webapi.android.models.Image;

/**
 * Created by petri on 5.4.2016.
 */
public class Track {
    private String track_id;
    private String track_name;
    private String track_artist;
    private Image image;
    private List<Vote> vote = new ArrayList<Vote>();

    public Track(String id, String name, String artist){
        this.track_id = id;
        this.track_name = name;
        this.track_artist = artist;
    }

    public void addVote(String userId, boolean newVote){
        boolean userIsVoted = false;
        for (Vote voteItem : vote) {
            if(voteItem.getUserID() == userId){
                userIsVoted = true;
            }
        }
        if(!userIsVoted) vote.add(new Vote(userId, newVote));
    }

    public String getId(){
        return track_id;
    }

    public String getName(){
        return track_name;
    }

    public String getArtist(){
        return track_artist;
    }

    public Image getImage(){
        return image;
    }

    public List<Vote> getVotes(){
        return vote;
    }


}
