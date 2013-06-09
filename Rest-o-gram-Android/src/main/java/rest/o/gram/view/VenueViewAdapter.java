package rest.o.gram.view;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import rest.o.gram.R;
import rest.o.gram.cache.IRestogramCache;
import rest.o.gram.client.RestogramClient;
import rest.o.gram.common.IRestogramListener;
import rest.o.gram.common.Utils;
import rest.o.gram.entities.RestogramVenue;

import java.util.LinkedList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Roi
 * Date: 22/04/13
 */
public class VenueViewAdapter extends BaseAdapter {
    /**
     * Ctor
     * */
    public VenueViewAdapter(Activity activity, IRestogramListener listener) {
        this.listener = listener;
        venueList = new LinkedList<>();
        inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return venueList.size();
    }

    @Override
    public Object getItem(int i) {
        if(i < 0 || i >= venueList.size())
            return null;

        return venueList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if(i < 0 || i >= venueList.size())
            return null;

        View v = inflater.inflate(R.layout.nearby_list_item, null);
        final String venueId = venueList.get(i);

        // Get venue from cache
        IRestogramCache cache = RestogramClient.getInstance().getCache();
        RestogramVenue venue = cache.findVenue(venueId);

        // Set UI with venue information
        Utils.updateTextView((TextView)v.findViewById(R.id.tvName), venue.getName());
        Utils.updateTextView((TextView)v.findViewById(R.id.tvAddress), venue.getAddress());
        Utils.updateTextView((TextView)v.findViewById(R.id.tvPhone), venue.getPhone());

        if(showDistance) {
            try {
                String distanceStr = String.format("%1$,.0f", venue.getDistance());
                Utils.updateTextView((TextView)v.findViewById(R.id.tvDistance), distanceStr + "m away");
            }
            catch(Exception e) {
                // Empty
            }
        }

        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                IRestogramCache cache = RestogramClient.getInstance().getCache();
                RestogramVenue venue = cache.findVenue(venueId);
                listener.onVenueSelected(venue);
            }
        });

        return v;
    }

    /**
     * Adds venue
     * */
    public void addVenue(String venueId) {
        venueList.add(venueId);
    }

    /**
     * Refreshes this adapter
     */
    public void refresh() {
        notifyDataSetChanged();
    }

    /**
     * Clears all venues
     */
    public void clear() {
        venueList.clear();
    }

    /**
     * Determines whether to show distance label
     */
    public void showDistance(boolean show) {
        showDistance = show;
    }

    private IRestogramListener listener;
    private List<String> venueList; // Venue list
    private LayoutInflater inflater;
    private boolean showDistance = true; // Show distance flag
}
