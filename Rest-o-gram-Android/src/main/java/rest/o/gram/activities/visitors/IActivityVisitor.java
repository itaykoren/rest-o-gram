package rest.o.gram.activities.visitors;

import rest.o.gram.activities.*;

/**
 * Created with IntelliJ IDEA.
 * User: Roi
 * Date: 6/6/13
 */
public interface IActivityVisitor {
    /**
     * Visits restogram activity
     */
    void visit(RestogramActivity activity);

    /**
     * Visits restogram action bar activity
     */
    void visit(RestogramActionBarActivity activity);

    /**
     * Visits home activity
     */
    void visit(HomeActivity activity);

    /**
     * Visits nearby activity
     */
    void visit(NearbyActivity activity);

    /**
     * Visits explore activity
     */
    void visit(ExploreActivity activity);

    /**
     * Visits map activity
     */
    void visit(MapActivity activity);

    /**
     * Visits personal activity
     */
    void visit(PersonalActivity activity);

    /**
     * Visits photo activity
     */
    void visit(PhotoActivity activity);

    /**
     * Visits venue activity
     */
    void visit(VenueActivity activity);
}
