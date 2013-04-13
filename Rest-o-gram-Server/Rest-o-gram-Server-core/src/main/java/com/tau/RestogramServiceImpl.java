package com.tau;

import fi.foyt.foursquare.api.FoursquareApi;
import fi.foyt.foursquare.api.FoursquareApiException;
import fi.foyt.foursquare.api.Result;
import fi.foyt.foursquare.api.entities.CompactVenue;
import fi.foyt.foursquare.api.entities.VenuesSearchResult;
import org.jinstagram.Instagram;
import org.jinstagram.entity.common.Location;
import org.jinstagram.entity.locations.LocationSearchFeed;
import org.jinstagram.entity.users.feed.MediaFeed;
import org.jinstagram.entity.users.feed.MediaFeedData;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

/**
 * Created with IntelliJ IDEA.
 * User: Roi
 * Date: 31/03/13
 */
public class RestogramServiceImpl implements RestogramService {

    public RestogramServiceImpl()
    {
        try
        {
            m_foursquare = new FoursquareApi(FOURSQUARE_CLIENT_ID, FOURSQUARE_CLIENT_SECRET, "");
            m_instagram = new Instagram(INSTAGRAM_CLIENT_ID);
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    /**
     * @return array of venus near given location
     */
    public RestogramVenue[] getNearby(double latitude, double longitude)
    {
        String location = latitude + "," + longitude;

        // TODO: manage foursquare categories...
        String categories = "4d4b7105d754a06374d81259";

        Map<String, String> params = new HashMap<String, String>();
        params.put("ll", location);
        params.put("categoryId", categories);
        params.put("intent", "match");

        return doGetNearby(params);
    }

    /**
     * @return array of venus near given location within given radius
     */
    public RestogramVenue[] getNearby(double latitude, double longitude, double radius)
    {
        String location = latitude + "," + longitude;

        // TODO: manage foursquare categories...
        String categories = "4d4b7105d754a06374d81259";

        Map<String, String> params = new HashMap<String, String>();
        params.put("ll", location);
        params.put("radius", Double.toString(radius));
        params.put("categoryId", categories);

        return doGetNearby(params);
    }

    /**
     * @return array of media related to venue given its ID
     */
    public RestogramPhoto[] getPhotos(String venueID)
    {
        MediaFeed recentMediaByLocation;
        try
        {
            LocationSearchFeed locationSearchFeed = m_instagram.searchFoursquareVenue(venueID);
            List<Location> locationList = locationSearchFeed.getLocationList();
            long locationId = locationList.get(0).getId(); // TODO: fix
            recentMediaByLocation = m_instagram.getRecentMediaByLocation(locationId);
        }
        catch(Exception e)
        {
            // TODO: report error
            return null;
        }

        if(recentMediaByLocation == null)
        {
            // TODO: report error
            return null;
        }

        List<MediaFeedData> data = recentMediaByLocation.getData();
        if(data == null || data.size() == 0)
        {
            // TODO: report error
            return null;
        }

        RestogramPhoto[] photos = new RestogramPhoto[data.size()];

        int i = 0;
        for (MediaFeedData media : data)
            photos[i++] = new RestogramPhoto(media);

        return photos;
    }

    private RestogramVenue[] doGetNearby(Map<String, String> params)
    {
        Result<VenuesSearchResult> result;
        try {
            result = m_foursquare.venuesSearch(params);
        } catch (FoursquareApiException e) {
            e.printStackTrace();
            // TODO: report error
            return null;
        }

        if (result.getMeta().getCode() != 200)
        {
            // TODO: report error
            return null;
        }

        CompactVenue[] arr = result.getResult().getVenues();
        if(arr.length == 0)
            return null;

        RestogramVenue[] venues = new RestogramVenue[arr.length];
        for(int i = 0; i < arr.length; i++) {
            CompactVenue v = arr[i];
            venues[i] = new RestogramVenue(v.getId(), v.getName(),
                                            v.getLocation().getAddress(),
                                            v.getLocation().getCity(),
                                            v.getLocation().getState(),
                                            v.getLocation().getPostalCode(),
                                            v.getLocation().getCountry(),
                                            v.getLocation().getLat(),
                                            v.getLocation().getLng(),
                                            v.getLocation().getDistance(),
                                            v.getUrl());
        }

        return venues;
    }

    private FoursquareApi m_foursquare;
    private Instagram m_instagram;

    // Foursquare Client Id
    private static final String FOURSQUARE_CLIENT_ID = "OERIKGO1WPRTY2RWWF3IMX5FUGBLSCES1OJ1F3BBLFOIBF3T";

    // Foursquare Client Secret
    private static final String FOURSQUARE_CLIENT_SECRET = "3MQLEAAV5YH2O0ZIWDVJ515KYRDROA3DPQJG4ZDPZHXXCMTF";

    // Instagram Client Id
    private static final String INSTAGRAM_CLIENT_ID = "4d32ff70646e46a992a4ad5a0945ef3f";
}
