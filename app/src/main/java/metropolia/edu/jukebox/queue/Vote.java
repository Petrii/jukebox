package metropolia.edu.jukebox.queue;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by petri on 5.4.2016.
 */
public class Vote implements Parcelable {
    private String userID;
    private boolean vote = false;
    public static final Creator<Vote> CREATOR = new Creator(){

        public Vote createFromParcel(Parcel source) {
            return new Vote(source);
        }

        public Vote[] newArray(int size) {
            return new Vote[0];
        }
    };

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel dest, int flags){
        dest.writeString(this.userID);
        dest.writeByte((byte)(vote ? 1:0));
    }

    protected Vote(Parcel in){
        this.userID = in.readString();
        this.vote = in.readByte() != 0;
    }


    public Vote(String userId, boolean vote){
        this.userID = userId;
        this.vote = vote;
    }

    public String getUserID(){
        return userID;
    }

    public boolean getVote(){
        return vote;
    }
}
