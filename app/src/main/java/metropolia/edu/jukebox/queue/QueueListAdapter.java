package metropolia.edu.jukebox.queue;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import metropolia.edu.jukebox.R;


/**
 * Created by petri on 5.4.2016.
 */
public class QueueListAdapter extends RecyclerView.Adapter<QueueListAdapter.ViewHolder>{
    private static final String TAG = "QueueListAdapter";
    //private final List<Track> mItems = new ArrayList<>();
    private final Context mContext;
    private final ItemSelectedListener mListener;
    QueueList queueList;

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public final TextView title;
        public final TextView subtitle;
        public final TextView votes;
        public final ImageView image;
        public final ImageButton upVote;

        public ViewHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.entity_title);
            subtitle = (TextView) itemView.findViewById(R.id.entity_subtitle);
            image = (ImageView) itemView.findViewById(R.id.entity_image);
            votes = (TextView) itemView.findViewById(R.id.entity_votes);
            upVote = (ImageButton) itemView.findViewById(R.id.vote_up);
            upVote.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {
            try {
                notifyItemChanged(getLayoutPosition());
                mListener.onItemSelected(v, queueList.getTrackList().get(getAdapterPosition()), v.getId());
            }catch(Exception e){
                Log.d(TAG, "Input id not ");
            }
        }
    }

    public interface ItemSelectedListener {
        void onItemSelected(View itemView, Track item, int buttonID);
    }

    public QueueListAdapter(Context context, ItemSelectedListener listener){
        mContext = context;
        mListener = listener;
        queueList = QueueList.getInstance();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.queue_list_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final Track item = queueList.getTrackList().get(position);
        holder.title.setText(item.getName());
        holder.subtitle.setText("by "+item.getArtist());
        holder.votes.setText(item.getVotes()+" likes");
        Picasso.with(mContext).load(item.getTrack_image()).into(holder.image);
    }

    @Override
    public int getItemCount() {
        return queueList.getTrackList().size();
    }
}

