package metropolia.edu.jukebox.queue;

/**
 * Created by petri on 5.4.2016.
 */
public class Vote {
    private String userID;
    private boolean vote = false;

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
