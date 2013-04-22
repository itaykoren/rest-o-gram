package rest.o.gram.common;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import rest.o.gram.R;
import rest.o.gram.activities.NearbyActivity;
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
    public VenueViewAdapter(NearbyActivity activity) {
        this.activity = activity;
        venueList = new LinkedList<RestogramVenue>();
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
        final RestogramVenue venue = venueList.get(i);

        // Set UI with venue information
        Utils.updateTextView((TextView)v.findViewById(R.id.tvName), venue.getName());
        Utils.updateTextView((TextView)v.findViewById(R.id.tvAddress), venue.getAddress());
        Utils.updateTextView((TextView)v.findViewById(R.id.tvPhone), venue.getPhone());

        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                activity.onVenueClicked(venue);
            }
        });

        return v;
    }

    /**
     * Adds venue
     * */
    public void addVenue(RestogramVenue venue) {
        venueList.add(venue);
    }

    /**
     * Refreshes this adapter
     */
    public void refresh() {
        notifyDataSetChanged();
    }

    private NearbyActivity activity;
    private List<RestogramVenue> venueList; // Venue list
    private LayoutInflater inflater;
}
