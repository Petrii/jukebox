package metropolia.edu.jukebox.queue;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.common.base.Joiner;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import kaaes.spotify.webapi.android.models.ArtistSimple;
import kaaes.spotify.webapi.android.models.Image;
import metropolia.edu.jukebox.R;
import metropolia.edu.jukebox.search.SearchResultsAdapter;


/**
 * Created by petri on 5.4.2016.
 */
public class QueueListAdapter extends RecyclerView.Adapter<QueueListAdapter.ViewHolder>{
    private static final String TAG = "QueueListAdapter";
    private final List<Track> mItems = new ArrayList<>();
    private final Context mContext;
    private final ItemSelectedListener mListener;

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public final TextView title;
        public final TextView subtitle;
        public final TextView votes;
        public final ImageView image;

        public ViewHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.entity_title);
            subtitle = (TextView) itemView.findViewById(R.id.entity_subtitle);
            image = (ImageView) itemView.findViewById(R.id.entity_image);
            votes = (TextView) itemView.findViewById(R.id.entity_votes);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            notifyItemChanged(getLayoutPosition());
            mListener.onItemSelected(v, mItems.get(getAdapterPosition()));
        }
    }

    public interface ItemSelectedListener {
        void onItemSelected(View itemView, Track item);
    }

    public QueueListAdapter(Context context, ItemSelectedListener listener){
        mContext = context;
        mListener = listener;
    }

    public void clearData() {
        if(!mItems.isEmpty())
            mItems.clear();
    }

    public void addData(List<Track> items) {
        mItems.addAll(items);
        notifyDataSetChanged();
    }

    public void addNewTrack(String id, String name, String artist){
        boolean trackIsListed = false;
        for( Track item : mItems) {
            if (item.getId() == id) {
                trackIsListed = true;
                break;
            } else {
                Log.d(TAG, "Track is already in list");
            }
        }
        if(!trackIsListed)mItems.add(new Track(id, name, artist));
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.queue_list_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Track item = mItems.get(position);

        holder.title.setText(item.getName());

        List<String> names = new ArrayList<>();
        for (Track i : mItems) {
            names.add(i.getArtist());
        }
        Joiner joiner = Joiner.on(", ");
        holder.subtitle.setText(joiner.join(names));

        /*Image image = item.album.images.get(0);
        if (image != null) {
            Picasso.with(mContext).load(image.url).into(holder.image);
        }*/
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }
}

