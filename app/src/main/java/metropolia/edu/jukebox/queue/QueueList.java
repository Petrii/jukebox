package metropolia.edu.jukebox.queue;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import metropolia.edu.jukebox.Playback;

/**
 * Created by petri on 5.4.2016.
 *
 * QueueList handles to add, deleting / updating a tracks and a votes
 *
 */
public final class QueueList implements Parcelable{
    private static volatile QueueList instance;
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

    public static QueueList getInstance(){
        if(instance == null){
            synchronized (QueueList.class){
                if(instance == null){
                    instance = new QueueList();
                }
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
    public synchronized List<Track> getQueueList(){
        if(!queueList.isEmpty()){
            Log.d("QueueList", "getQueueList() "+queueList.get(0).getName());
        }
        return queueList;
    }

    public void setList(List<Track> tracks){
        queueList = tracks;
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
        for( Track item : queueList) {
            if (item.getId() == id) {
                trackIsListed = true;
                break;
            }
        }
        if(!trackIsListed){
            queueList.add(new Track(id, name, artist, image));
            Collections.sort(queueList, new OrderListByVotes());
        }
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
        queueList.get(position).addVote(userId, vote);
        Collections.sort(queueList, new OrderListByVotes());
    }

    /**
     *  Remove now playing track from queue list
     */
    public synchronized void deleteTrack(){
        queueList.remove(0);
    }

    public void clearData() {
        if(!queueList.isEmpty())
            queueList.clear();
    }
}
