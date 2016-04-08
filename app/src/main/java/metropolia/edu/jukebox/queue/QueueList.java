package metropolia.edu.jukebox.queue;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by petri on 5.4.2016.
 */
public class QueueList implements Parcelable{
    private static final String TAG = "QueueList";
    private static List<Track> queueList  = new ArrayList<>();
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


    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(this.queueList);
    }

    public QueueList(Parcel in){
        this.queueList = in.createTypedArrayList(Track.CREATOR);
    }

    /**
     * Returns QueueList object's queueList List<Track>
     * list of tracks
     *
     * @return List<Track>
     */
    public static List<Track> getQueueList(){
        if(!queueList.isEmpty()){
            Log.d("QueueList", ""+queueList.get(0).getName());
        }
        return queueList;
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
    public static void addToQueue(String id, String name, String artist, String image) {
        boolean trackIsListed = false;
        for( Track item : queueList) {
            if (item.getId() == id) {
                trackIsListed = true;
                break;
            } else {
                Log.d(TAG, "Track is already in list");
            }
        }
        if(!trackIsListed) queueList.add(new Track(id, name, artist, image));

    }

    /**
     * Adding user vote thumps up or down
     * Vote is boolean, false is thumps down, and true is up
     *
     * @param trackId
     * @param userId
     * @param vote
     */
    public static void updateVote(String trackId, String userId, Boolean vote) {
        for (Track track : queueList) {
            if( track.getId() == trackId){
                track.addVote(userId, vote);
                break;
            }
        }
    }
}
