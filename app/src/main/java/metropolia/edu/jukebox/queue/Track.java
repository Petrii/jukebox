package metropolia.edu.jukebox.queue;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by petri on 5.4.2016.
 */
public class Track implements Parcelable{
    private static final String TAG = "Queue Track";
    private String track_id;
    private String track_name;
    private String track_artist;
    private String track_image;
    private boolean userIsVoted = false;
    private List<Vote> vote = new ArrayList<>();

    public static final Creator<Track> CREATOR = new Creator() {

        public Track createFromParcel(Parcel source) {
            return new Track(source);
        }

        public Track[] newArray(int size) {
            return new Track[0];
        }
    };

    public int describeContents() { return 0; }

    public void writeToParcel(Parcel dest, int flags){
        dest.writeString(this.track_id);
        dest.writeString(this.track_name);
        dest.writeString(this.track_artist);
        dest.writeString(this.track_image);
        dest.writeTypedList(this.vote);
    }

    public Track(Parcel in){
        this.track_id = in.readString();
        this.track_name = in.readString();
        this.track_artist = in.readString();
        this.track_image = in.readString();
        this.vote = in.createTypedArrayList(Vote.CREATOR);
    }

    /**
     * Add new Track
     *
     * @param id
     * @param name
     * @param artist
     * @param image
     */
    public Track(String id, String name, String artist, String image){
        this.track_id = id;
        this.track_name = name;
        this.track_artist = artist;
        this.track_image = image;
    }

    /**
     * Add Vote to track
     *
     * @param userId
     * @param newVote
     */
    public void addVote(String userId, boolean newVote){
        for (Vote voteItem : vote) {
            if(voteItem.getUserID().equals(userId)){
                userIsVoted = true;
            }
        }
        if(!userIsVoted) vote.add(new Vote(userId, newVote));
        userIsVoted = false;
    }

    /**
     * Get track id
     *
     * @return String
     */
    public String getId(){
        return track_id;
    }

    /**
     * Get track name
     *
     * @return String
     */
    public String getName(){
        return track_name;
    }

    /**
     * Get track artist
     *
     * @return String
     */
    public String getArtist(){
        return track_artist;
    }

    public List<Vote> getVoteList(){
        return vote;
    }

    /**
     * Get track votes
     *
     * @return int
     */
    public int getVotes(){
        int votes = 0;
        for(Vote item : vote){
            if(item.getVote()){
                votes++;
            }else{
                votes--;
            }
        }
        return votes;
    }

    /**
     * Get track image
     *
     * @return ImageView
     */
    public String getTrack_image() {
        return track_image;
    }
}
