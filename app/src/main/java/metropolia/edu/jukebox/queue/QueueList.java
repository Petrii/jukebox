package metropolia.edu.jukebox.queue;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by petri on 5.4.2016.
 *
 * QueueList handles to add, deleting / updating a tracks and a votes
 *
 */
public final class QueueList implements Parcelable{
    private static volatile QueueList instance;
    private static final String TAG = "QueueList";
    private static List<Track> trackList = new ArrayList<>();

    public static final Creator<QueueList> CREATOR = new Creator(){
        public QueueList createFromParcel(Parcel source){
            return new QueueList(source);
        }
        public QueueList[] newArray(int size){
            return new QueueList[size];
        }
    };

    public int describeContents() {
        return 0;
    }

    public static QueueList getInstance(){
        if(instance == null){
            synchronized (QueueList.class){
                instance = new QueueList();
            }
        }
        return instance;
    }

    /**
     * A private Constructor prevents any other class from
     * instantiating.
     */
    private QueueList(){ }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(this.trackList);
    }

    public QueueList(Parcel in){
        this.trackList = in.createTypedArrayList(Track.CREATOR);
    }

    /**
     * Returns QueueList object's trackList List<Track>
     * list of tracks
     *
     * @return List<Track>
     */
    public synchronized List<Track> getTrackList(){
        return trackList;
    }

    public void setList(List<Track> tracks){
        trackList = tracks;
    }

    /**
     * Adding new track to QueueList object
     * This also check if track is in list already
     *
     * @param id
     * @param name
     * @param artist
     * @param image
     */
    public synchronized void addToQueue(String id, String name, String artist, String image) {
        boolean trackIsListed = false;
        for( Track item : trackList) {
            if (item.getId() == id) {
                trackIsListed = true;
                break;
            }
        }
        if(!trackIsListed){
            trackList.add(new Track(id, name, artist, image));
            Collections.sort(trackList, new OrderListByVotes());
        }
    }

    public void addTrack(Track track) {
        trackList.add(track);
    }

    /**
     * Adding user vote thumps up or down
     * Vote is boolean, false is thumps down, and true is up
     *
     * @param position
     * @param userId
     * @param vote
     */
    public synchronized void updateVote(int position, String userId, Boolean vote) {
        trackList.get(position).addVote(userId, vote);
        Collections.sort(trackList, new OrderListByVotes());
    }

    /**
     *  Remove now playing track from queue list
     */
    public synchronized void deleteTrack(){
        trackList.remove(0);
    }

    public void clearData() {
        if(!trackList.isEmpty())
            trackList.clear();
    }
}
