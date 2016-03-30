package metropolia.edu.jukebox;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import com.google.common.base.Joiner;
import com.squareup.picasso.Picasso;

import kaaes.spotify.webapi.android.models.ArtistSimple;
import kaaes.spotify.webapi.android.models.Image;
import kaaes.spotify.webapi.android.models.Track;

/**
 * Created by petri on 29.3.2016.
 */
public class SearchResultAdapter extends BaseAdapter {
    private List<Track> mItems = new ArrayList<>();
    private LayoutInflater mLayoutInflater;
    private Context mContext;

    public SearchResultAdapter(Context context) {
        mContext = context;
    }

    public class ViewHolder{
        public TextView title;
        public TextView subtitle;
        public ImageView image;

        public ViewHolder(View itemView){
            title = (TextView) itemView.findViewById(R.id.entity_title);
            subtitle = (TextView) itemView.findViewById(R.id.entity_subtitle);
            image = (ImageView) itemView.findViewById(R.id.entity_image);
            //itemView.setOnClickListener(this);
        }
    }

    public interface ItemSelectedListener {
        void onItemSelected(View itemView, Track item);
    }

    /*
    * Adding all founded tracks to Adapter list
    *
    * @params List<Track> items
    */
    public void addData(List<Track> items){
        mItems.clear();
        mItems.addAll(items);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mItems.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        Track item = mItems.get(position);

        // When creating a row for the first time
        if(convertView == null){
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.list_item, parent, false);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else { // Recycling
            viewHolder = (ViewHolder)convertView.getTag();
        }
        viewHolder.title.setText(item.name);

        List<String> names = new ArrayList<>();
        for(ArtistSimple i : item.artists){
            names.add(i.name);
        }

        Joiner joiner = Joiner.on(", ");
        viewHolder.subtitle.setText(joiner.join(names));

        Image image = item.album.images.get(0);
        if(image != null){
            Picasso.with(mContext).load(image.url).into(viewHolder.image);
        }
        return convertView;
    }
}
