import com.google.appengine.repackaged.com.google.common.base.Joiner;
import org.junit.Before;
import org.junit.Test;
import rest.o.gram.entities.RestogramVenue;
import rest.o.gram.foursquare.FoursquareManager;
import rest.o.gram.foursquare.FoursquareManagerImpl;

import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;

/**
 * Created with IntelliJ IDEA.
 * User: Roi
 * Date: 30/05/14
 */
public class TestService {

    private FoursquareManager foursquareManager;

    @Before
    public void setUp() {
        foursquareManager = new FoursquareManagerImpl();
    }

    @Test
    public void testGetNearby() {
        List<RestogramVenue> result = foursquareManager.getNearby(48.853015, 2.368884, 500);

        assertNotNull(result);
        assertTrue(result.size() > 0);

        System.out.println(Joiner.on(" ").join(result));
    }

    @Test
    public void testGetInfo() {
        RestogramVenue result = foursquareManager.getInfo("4b2fe2b0f964a520f7f124e3");

        assertNotNull(result);
        assertEquals("Café Français", result.getName());
    }
}
