import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import rest.o.gram.iservice.RestogramService;
import rest.o.gram.results.VenueResult;
import rest.o.gram.results.VenuesResult;
import rest.o.gram.service.RestogramServiceImpl;

import java.util.Arrays;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;

/**
 * Created with IntelliJ IDEA.
 * User: Roi
 * Date: 30/05/14
 */
public class TestService {

    private RestogramService service;

    @Before
    public void setUp() {
        service = new RestogramServiceImpl();
    }

    @Test
    @Ignore("Involves DS manipulation")
    public void testGetNearby() {
        VenuesResult result = service.getNearby(48.853015, 2.368884, 500);

        assertNotNull(result);
        assertNotNull(result.getResult());
        assertTrue(result.getResult().length > 0);

        System.out.println(Arrays.toString(result.getResult()));
    }

    @Test
    @Ignore("Involves DS manipulation")
    public void testGetInfo() {
        VenueResult result = service.getInfo("4b2fe2b0f964a520f7f124e3");

        assertNotNull(result);
        assertNotNull(result.getResult());
        assertEquals("Café Français", result.getResult().getName());
    }
}
