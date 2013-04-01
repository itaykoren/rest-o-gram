package tau;

import fi.foyt.foursquare.api.FoursquareApi;
import fi.foyt.foursquare.api.FoursquareApiException;
import fi.foyt.foursquare.api.Result;
import fi.foyt.foursquare.api.entities.CompactVenue;
import fi.foyt.foursquare.api.entities.VenuesSearchResult;
import org.jinstagram.Instagram;
import org.jinstagram.entity.common.ImageData;
import org.jinstagram.entity.common.Images;
import org.jinstagram.entity.common.Location;
import org.jinstagram.entity.locations.LocationSearchFeed;
import org.jinstagram.entity.users.feed.MediaFeed;
import org.jinstagram.entity.users.feed.MediaFeedData;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Roi
 * Date: 3/29/13
 */
public class MainServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        //response.setContentType("text/plain");
        //response.getWriter().println("Rest-o-Gram!!!");

        // Test

        System.out.println("*** Test Started ***");

        FoursquareApi foursquareApi = new FoursquareApi(FOURSQUARE_CLIENT_ID, FOURSQUARE_CLIENT_SECRET, "");
        String location = "32.112,34.839";

        // TODO: manage foursquare categories...
        String categories = "4d4b7105d754a06374d81259";

        Result<VenuesSearchResult> result = null;
        try {
            result = foursquareApi.venuesSearch(location, null, null, null, null, null, null, categories, null, null, null);
        } catch (FoursquareApiException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

        String foursquareId = "";

        if (result.getMeta().getCode() == 200) {
            // if query was ok we can finally we do something with the data
            for (CompactVenue venue : result.getResult().getVenues()) {
                // TODO: Do something we the data
                System.out.println(venue.getName());

                if(foursquareId.isEmpty())
                    foursquareId = venue.getId();
            }
        } else {
            // TODO: Proper error handling
            System.out.println("Error occured: ");
            System.out.println("  code: " + result.getMeta().getCode());
            System.out.println("  type: " + result.getMeta().getErrorType());
            System.out.println("  detail: " + result.getMeta().getErrorDetail());
        }

        if(foursquareId.isEmpty())
        {
            System.out.println("Error occured: no venues found");
            return;
        }

        Instagram instagram = new Instagram(INSTAGRAM_CLIENT_ID);
        LocationSearchFeed locationSearchFeed = instagram.searchFoursquareVenue(foursquareId);
        List<Location> locationList = locationSearchFeed.getLocationList();
        long locationId = locationList.get(0).getId();
        MediaFeed recentMediaByLocation = instagram.getRecentMediaByLocation(locationId);

        String imageUrl = "";

        for(MediaFeedData item : recentMediaByLocation.getData())
        {
               System.out.println(item);

            if(imageUrl.isEmpty()) {
                Images images = item.getImages();
                ImageData standardResolution = images.getStandardResolution();
                imageUrl = standardResolution.getImageUrl();
            }
        }

        if(imageUrl.isEmpty())
        {
            response.setContentType("text/plain");
            response.getWriter().println("Rest-o-Gram: no image found");
        }
        else
        {
            response.setContentType("image/jpeg");
            response.sendRedirect(imageUrl);
        }

        System.out.println("*** Test Finished ***");
        // Test
    }

    // Instagram Client Id
    private static final String INSTAGRAM_CLIENT_ID = "4d32ff70646e46a992a4ad5a0945ef3f";

    // Foursquare Client Id
    private static final String FOURSQUARE_CLIENT_ID = "OERIKGO1WPRTY2RWWF3IMX5FUGBLSCES1OJ1F3BBLFOIBF3T";

    // Foursquare Client Secret
    private static final String FOURSQUARE_CLIENT_SECRET = "3MQLEAAV5YH2O0ZIWDVJ515KYRDROA3DPQJG4ZDPZHXXCMTF";
}
