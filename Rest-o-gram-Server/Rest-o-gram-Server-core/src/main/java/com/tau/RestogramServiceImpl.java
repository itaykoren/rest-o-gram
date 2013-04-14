package com.tau;

import fi.foyt.foursquare.api.FoursquareApi;
import fi.foyt.foursquare.api.FoursquareApiException;
import fi.foyt.foursquare.api.Result;
import fi.foyt.foursquare.api.entities.*;
import org.jinstagram.Instagram;
import org.jinstagram.entity.common.Location;
import org.jinstagram.entity.locations.LocationSearchFeed;
import org.jinstagram.entity.users.feed.MediaFeed;
import org.jinstagram.entity.users.feed.MediaFeedData;
import org.jinstagram.exceptions.InstagramException;

import java.util.logging.Logger;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

/**
 * Created with IntelliJ IDEA.
 * User: Roi
 * Date: 31/03/13
 */
public class RestogramServiceImpl implements RestogramService {

    private static final Logger log = Logger.getLogger(RestogramServiceImpl.class.getName());

    public RestogramServiceImpl()
    {
        try
        {
            m_foursquare = new FoursquareApi(FOURSQUARE_CLIENT_ID, FOURSQUARE_CLIENT_SECRET, "");
            m_instagram = new Instagram(INSTAGRAM_CLIENT_ID);
        }
        catch(Exception e)
        {
            log.severe("an error occurred while initializing the service");
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
     * @return venue information according to its ID
     */
    public RestogramVenue getInfo(String venueID) {
        Result<CompleteVenue> result;
        try {
            result = m_foursquare.venue(venueID);
        } catch (FoursquareApiException e) {
            log.warning("first venue  " + venueID + " retrieval has failed, retry");
            e.printStackTrace();
            try {
                result = m_foursquare.venue(venueID);
            }
            catch (FoursquareApiException e2)
            {
                log.severe("second venue " + venueID + " retrieval has failed");
                e2.printStackTrace();
                return null;
                // TODO: report error
            }
        }

        if (result.getMeta().getCode() != 200) {
            log.severe("venue " + venueID + "retrieval returned an error code: " + result.getMeta().getCode());
            // TODO: report error
            return null;
        }

        CompleteVenue v = result.getResult();
        if(v == null)
        {
            log.severe("extracting info from venue has failed");
            // TODO: report error
            return null;
        }

        return convert(v);
    }

    /**
     * @return array of media related to venue given its ID
     */
    public RestogramPhoto[] getPhotos(String venueID)
    {
        List<MediaFeedData> data = doGetPhotos(venueID, RestogramPhotoFilter.None);
        if(data == null)
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

    /**
     * @return array of media related to venue given its ID, after applying given filter
     */
    public RestogramPhoto[] getPhotos(String venueID, RestogramPhotoFilter filter)
    {
        List<MediaFeedData> data = doGetPhotos(venueID, filter);
        if(data == null)
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
        try
        {
            result = m_foursquare.venuesSearch(params);
        }
        catch (FoursquareApiException e)
        {
            // TODO: test the "second-chance" policy
            try
            {
                log.warning("first venue search has failed, retry");
                e.printStackTrace();
                result = m_foursquare.venuesSearch(params);
            }
            catch (FoursquareApiException e2)
            {
                log.severe("second venue search has failed");
                e2.printStackTrace();
                // TODO: report error
                return null;
            }
        }

        if (result.getMeta().getCode() != 200)
        {
            // TODO: report error
            log.severe("venue search returned an error code: " + result.getMeta().getCode());
            return null;
        }

        CompactVenue[] arr = result.getResult().getVenues();

        RestogramVenue[] venues = new RestogramVenue[arr.length];
        for(int i = 0; i < arr.length; i++) {
            venues[i] = convert(arr[i]);
        }

        return venues;
    }

    private List<MediaFeedData> doGetPhotos(String venueID, RestogramPhotoFilter filter)
    {
        LocationSearchFeed locationSearchFeed = null;

        try
        {
            locationSearchFeed = m_instagram.searchFoursquareVenue(venueID);
        }
        catch (InstagramException e)
        {
            log.warning("first search for venue: " + venueID + " has failed, retry");
            e.printStackTrace();;
            try
            {
                locationSearchFeed = m_instagram.searchFoursquareVenue(venueID);
            }
            catch (InstagramException e2)
            {
                log.severe("second search for venue: " + venueID + "has failed");
                e.printStackTrace();;
            }
        }

        List<Location> locationList = locationSearchFeed.getLocationList();
        if (locationList.isEmpty()) // TODO: handle in a different way?
        {
            log.severe("venue:  " + venueID + " not found");
            return null;
        }
        long locationId = locationList.get(0).getId(); // TODO: what if we get multiple locations?

        MediaFeed recentMediaByLocation = null;
        try
        {
            recentMediaByLocation = m_instagram.getRecentMediaByLocation(locationId);
        }
        catch(InstagramException e)
        {
            log.warning("first recent media search for venue: " + venueID + "has failed, retry");
            e.printStackTrace();

            try
            {
                recentMediaByLocation = m_instagram.getRecentMediaByLocation(locationId);
            }
            catch (InstagramException e2)
            {
                log.severe("second search for recent media for venue: " + venueID + "has failed");
                e2.printStackTrace();
                return null;
            }
        }

        if(recentMediaByLocation == null)
        {
            // TODO: report error
            return null;
        }

        List<MediaFeedData> data = recentMediaByLocation.getData();
        if(data == null)
        {
            // TODO: report error
            return null;
        }

        // TODO: apply filter

        return data;
    }

    private RestogramVenue convert(CompactVenue venue) {
        fi.foyt.foursquare.api.entities.Location location = venue.getLocation();
        return new RestogramVenue(venue.getId(),
                                venue.getName(),
                                location.getAddress(),
                                location.getCity(),
                                location.getState(),
                                location.getPostalCode(),
                                location.getCountry(),
                                location.getLat(),
                                location.getLng(),
                                location.getDistance(),
                                venue.getUrl());
    }

    private RestogramVenue convert(CompleteVenue venue) {
        RestogramVenue result;
        String photoUrl;

        try {
            Photos photos = venue.getPhotos();
            if(photos == null)
                return null;

            PhotoGroup[] groups = photos.getGroups();
            if(groups == null || groups.length < 2)
                return null;

            PhotoGroup group = groups[1];
            Photo[] items = group.getItems();
            if(items == null || items.length == 0)
                return null;

            photoUrl = items[0].getUrl();
            result = new RestogramVenue(venue.getId(), venue.getName(), venue.getDescription(), photoUrl);
        }
        catch(Exception e) {
            // TODO: report error
            return null;
        }

        return result;
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
