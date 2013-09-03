package rest.o.gram.data_favorites;

import android.util.Log;
import com.leanengine.LeanEntity;
import rest.o.gram.client.RestogramClient;
import rest.o.gram.entities.Kinds;
import rest.o.gram.entities.Props;
import rest.o.gram.entities.RestogramPhoto;
import rest.o.gram.entities.RestogramVenue;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Or
 * Date: 5/23/13
 */
final class Converters {

    public static RestogramVenue leanEntityToVenue(final LeanEntity entity) {
        RestogramVenue venue = new RestogramVenue();
        venue.setFoursquare_id(entity.getUniqueName());
        if (entity.hasId())
            venue.setId(entity.getId());
        venue.setName(entity.getString(Props.Venue.NAME));
        venue.setAddress(entity.getString(Props.Venue.ADDRESS));
        venue.setCity(entity.getString(Props.Venue.CITY));
        venue.setState(entity.getString(Props.Venue.STATE));
        venue.setPostalCode(entity.getString(Props.Venue.POSTAL_CODE));
        venue.setCountry(entity.getString(Props.Venue.COUNTRY));
        venue.setLatitude(entity.getDouble(Props.Venue.LAT));
        venue.setLongitude(entity.getDouble(Props.Venue.LONG));
        venue.setDistance(entity.getDouble(Props.Venue.DISTANCE));
        venue.setUrl(entity.getString(Props.Venue.URL));
        venue.setPhone(entity.getString(Props.Venue.PHONE));
        //venue.setDescription(entity.getText(Props.Venue.DESCRIPTION));
        //venue.setImageUrl(entity.getString(Props.Venue.IMAGE_URL));

        if (RestogramClient.getInstance().getAuthenticationProvider().isUserLoggedIn())
            venue.setfavorite(entity.getBoolean(Props.VenueRef.IS_FAVORITE));

        return venue;
    }

    public static List<RestogramVenue> leanEntitiesToVenues(final LeanEntity... leanEntities){
        final List<RestogramVenue> venues = new ArrayList<>(leanEntities.length);
        for (int i = 0; i < leanEntities.length; ++i)
            venues.add(leanEntityToVenue(leanEntities[i]));
        return  venues;
    }

    public static RestogramPhoto leanEntityToPhoto(final LeanEntity entity) {
        RestogramPhoto photo = new RestogramPhoto();
        photo.setInstagram_id(entity.getUniqueName());
//        if (entity.hasId())
//            photo.setId(entity.getId());
        photo.setCaption(entity.getString(Props.Photo.CAPTION));
        photo.setCreatedTime(entity.getString(Props.Photo.CREATED_TIME));
        photo.setImageFilter(entity.getString(Props.Photo.IMAGE_FILTER));
        photo.setThumbnail(entity.getString(Props.Photo.THUMBNAIL));
        photo.setStandardResolution(entity.getString(Props.Photo.STANDARD_RESOLUTION));
        photo.setLikes(entity.getLong(Props.Photo.LIKES));
        photo.setLink(entity.getString(Props.Photo.LINK));
        photo.setType(entity.getString(Props.Photo.TYPE));
        photo.setUser(entity.getString(Props.Photo.USER));
        photo.setOriginVenueId(entity.getString(Props.Photo.ORIGIN_VENUE_ID));
        if (entity.hasProperty(Props.Photo.YUMMIES))
            photo.setYummies(entity.getLong(Props.Photo.YUMMIES));
        else
            photo.setYummies(0);

        if (RestogramClient.getInstance().getAuthenticationProvider().isUserLoggedIn())
            photo.set_favorite(entity.getBoolean(Props.PhotoRef.IS_FAVORITE));

        return photo;
    }

    public static List<RestogramPhoto> leanEntitiesToPhotos(final LeanEntity... leanEntities){
        final List<RestogramPhoto> photos = new ArrayList<>(leanEntities.length);
        for (int i = 0; i < leanEntities.length; ++i)
            photos.add(leanEntityToPhoto(leanEntities[i]));
        return  photos;
    }
}