package rest.o.gram.activities.visitors;

import rest.o.gram.R;
import rest.o.gram.activities.*;

/**
 * Created with IntelliJ IDEA.
 * User: Roi
 * Date: 6/6/13
 */
public class ActionBarIconVisitor implements IActivityVisitor {
    @Override
    public void visit(RestogramActivity activity) {
    }

    @Override
    public void visit(RestogramActionBarActivity activity) {
        try {
            activity.getMenu().getItem(0).setIcon(R.drawable.ic_explore);
            activity.getMenu().getItem(1).setIcon(R.drawable.ic_map);
            activity.getMenu().getItem(2).setIcon(R.drawable.ic_me);
        }
        catch(Exception e) {
            // Empty
        }
    }

    @Override
    public void visit(HomeActivity activity) {
    }

    @Override
    public void visit(NearbyActivity activity) {
    }

    @Override
    public void visit(ExploreActivity activity) {
        try {
            activity.getMenu().getItem(0).setIcon(R.drawable.ic_explore_on);
            activity.getMenu().getItem(1).setIcon(R.drawable.ic_map);
            activity.getMenu().getItem(2).setIcon(R.drawable.ic_me);
        }
        catch(Exception e) {
            // Empty
        }
    }

    @Override
    public void visit(MapActivity activity) {
        try {
            activity.getMenu().getItem(0).setIcon(R.drawable.ic_explore);
            activity.getMenu().getItem(1).setIcon(R.drawable.ic_map_on);
            activity.getMenu().getItem(2).setIcon(R.drawable.ic_me);
        }
        catch(Exception e) {
            // Empty
        }
    }

    @Override
    public void visit(PersonalActivity activity) {
        try {
            activity.getMenu().getItem(0).setIcon(R.drawable.ic_explore);
            activity.getMenu().getItem(1).setIcon(R.drawable.ic_map);
            activity.getMenu().getItem(2).setIcon(R.drawable.ic_me_on);
        }
        catch(Exception e) {
            // Empty
        }
    }

    @Override
    public void visit(PhotoActivity activity) {
    }

    @Override
    public void visit(VenueActivity activity) {
    }
}
