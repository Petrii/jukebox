package metropolia.edu.jukebox.queue;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

import kaaes.spotify.webapi.android.models.Image;

/**
 * Created by petri on 5.4.2016.
 */
public class Track implements Parcelable{
    private static final String TAG = "Queue Track";
    private String track_id;
    private String track_name;
    private String track_artist;
    private String track_image;
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
        dest.writeTypedList(this.vote);
    }

    protected Track(Parcel in){
        this.track_id = in.readString();
        this.track_name = in.readString();
        this.track_artist = in.readString();
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
        boolean userIsVoted = false;
        for (Vote voteItem : vote) {
            if(voteItem.getUserID() == userId){
                userIsVoted = true;
            }
        }
        if(!userIsVoted) vote.add(new Vote(userId, newVote));
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

    /**
     * Get track votes
     *
     * @return List<Votes>
     */
    public List<Vote> getVotes(){
        return vote;
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
