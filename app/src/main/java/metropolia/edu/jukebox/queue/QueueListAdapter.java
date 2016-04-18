package metropolia.edu.jukebox.queue;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.common.base.Joiner;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import kaaes.spotify.webapi.android.models.ArtistSimple;
import kaaes.spotify.webapi.android.models.Image;
import metropolia.edu.jukebox.Playback;
import metropolia.edu.jukebox.R;
import metropolia.edu.jukebox.search.SearchResultsAdapter;


/**
 * Created by petri on 5.4.2016.
 */
public class QueueListAdapter extends RecyclerView.Adapter<QueueListAdapter.ViewHolder>{
    private static final String TAG = "QueueListAdapter";
    //private final List<Track> mItems = new ArrayList<>();
    private final Context mContext;
    private String UserID = "Jukebox User"; //Default user
    QueueList queueList;

    public class ViewHolder extends RecyclerView.ViewHolder {

        public final TextView title;
        public final TextView subtitle;
        public final TextView votes;
        public final ImageView image;
        public final ImageButton upVote, downVote;

        public ViewHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.entity_title);
            subtitle = (TextView) itemView.findViewById(R.id.entity_subtitle);
            image = (ImageView) itemView.findViewById(R.id.entity_image);
            votes = (TextView) itemView.findViewById(R.id.entity_votes);
            upVote = (ImageButton) itemView.findViewById(R.id.vote_up);
            downVote = (ImageButton) itemView.findViewById(R.id.vote_down);
            upVote.setOnClickListener(mOnVoteUp);
            downVote.setOnClickListener(mOnVoteDown);
        }

        /**
         * Thumps ups button
         */
        private View.OnClickListener mOnVoteUp = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final int position = getLayoutPosition();
                queueList.updateVote(position, UserID, true);
                notifyDataSetChanged();
            }
        };

        /**
         * Thumps down button
         */
        private View.OnClickListener mOnVoteDown = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final int position = getLayoutPosition();
                queueList.updateVote(position, UserID, false);
                notifyDataSetChanged();
            }
        };
    }

    public QueueListAdapter(Context context){
        mContext = context;
        queueList = QueueList.getInstance();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.queue_list_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Track item = queueList.getQueueList().get(position);
        holder.title.setText(item.getName());
        holder.subtitle.setText(item.getArtist());
        holder.votes.setText(""+item.getVotes());
        Picasso.with(mContext).load(item.getTrack_image()).into(holder.image);
    }

    @Override
    public int getItemCount() {
        return queueList.getQueueList().size();
    }
}

