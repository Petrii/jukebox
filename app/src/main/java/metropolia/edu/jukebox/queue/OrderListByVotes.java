package metropolia.edu.jukebox.queue;

import java.util.Comparator;

/**
 * Created by petri on 16.4.2016.
 */
public class OrderListByVotes implements Comparator<Track> {
    @Override
    public int compare(Track lhs, Track rhs) {
        return Integer.compare(rhs.getVotes(), lhs.getVotes());
    }
}
