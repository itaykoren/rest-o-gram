package rest.o.gram.data;

import com.leanengine.LeanEntity;
import rest.o.gram.entities.Kinds;
import rest.o.gram.entities.Props;
import rest.o.gram.entities.RestogramPhoto;
import rest.o.gram.entities.RestogramVenue;

/**
 * Created with IntelliJ IDEA.
 * User: Or
 * Date: 5/23/13
 */
public final class Converters {
//    public static LeanEntity venueToLeanEntity(final RestogramVenue venue) {
//        LeanEntity entity = LeanEntity.initPublicEntity(Kinds.VENUE, venue.getFoursquare_id());
//        entity.put(Props.Venue.NAME, venue.getName());
//        entity.put(Props.Venue.ADDRESS, venue.getAddress());
//        entity.put(Props.Venue.CITY, venue.getCity());
//        entity.put(Props.Venue.STATE, venue.getState());
//        entity.put(Props.Venue.POSTAL_CODE, venue.getPostalCode());
//        entity.put(Props.Venue.COUNTRY, venue.getCountry());
//        entity.put(Props.Venue.LAT, venue.getLatitude());
//        entity.put(Props.Venue.LONG, venue.getLongitude());
//        entity.put(Props.Venue.DISTANCE, venue.getDistance());
//        entity.put(Props.Venue.URL, venue.getUrl());
//        entity.put(Props.Venue.PHONE, venue.getPhone());
//        //entity.putText(Props.Venue.DESCRIPTION, venue.getDescription());
//        //entity.put(Props.Venue.IMAGE_URL, venue.getImageUrl());
//        return entity;
//    }

    public static LeanEntity venueRefToLeanEntity(final RestogramVenue venue) {
        LeanEntity entity;
        if (venue.getId() == Long.MIN_VALUE)
            entity = LeanEntity.initPrivateEntity(Kinds.VENUE_REFERENCE);
        else
            entity = LeanEntity.initPrivateEntity(Kinds.VENUE_REFERENCE, venue.getId());
        entity.put(Props.VenueRef.FOURSQUARE_ID, venue.getFoursquare_id());
        entity.put(Props.VenueRef.IS_FAVORITE, venue.isfavorite());
        return entity;
    }

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
        return venue;
    }

//    public static LeanEntity photoToLeanEntity(final RestogramPhoto photo) {
//        LeanEntity entity = LeanEntity.initPublicEntity(Kinds.PHOTO, photo.getInstagram_id());
//        entity.put(Props.Photo.CAPTION, photo.getCaption());
//        entity.put(Props.Photo.CREATED_TIME, photo.getCreatedTime());
//        entity.put(Props.Photo.IMAGE_FILTER, photo.getImageFilter());
//        entity.put(Props.Photo.THUMBNAIL, photo.getThumbnail());
//        entity.put(Props.Photo.STANDARD_RESOLUTION, photo.getStandardResolution());
//        entity.put(Props.Photo.IMAGE_FILTER, photo.getImageFilter());
//        entity.put(Props.Photo.LIKES, photo.getLikes());
//        entity.put(Props.Photo.LINK, photo.getLink());
//        entity.put(Props.Photo.TYPE, photo.getType());
//        entity.put(Props.Photo.USER, photo.getUser());
//        return entity;
//    }

    public static LeanEntity photoRefToLeanEntity(final RestogramPhoto photo) {
        LeanEntity entity;
        if (photo.getId() == Long.MIN_VALUE)
            entity = LeanEntity.initPrivateEntity(Kinds.PHOTO_REFERENCE);
        else
            entity = LeanEntity.initPrivateEntity(Kinds.PHOTO_REFERENCE, photo.getId());
        entity.put(Props.PhotoRef.INSTAGRAM_ID, photo.getInstagram_id());
        entity.put(Props.PhotoRef.IS_FAVORITE, photo.is_favorite());
        return entity;
    }

    public static RestogramPhoto leanEntityToPhoto(final LeanEntity entity) {
        RestogramPhoto photo = new RestogramPhoto();
        photo.setInstagram_id(entity.getUniqueName());
        if (entity.hasId())
            photo.setId(entity.getId());
        photo.setCaption(entity.getString(Props.Photo.CAPTION));
        photo.setCreatedTime(entity.getString(Props.Photo.CREATED_TIME));
        photo.setImageFilter(entity.getString(Props.Photo.IMAGE_FILTER));
        photo.setThumbnail(entity.getString(Props.Photo.THUMBNAIL));
        photo.setStandardResolution(entity.getString(Props.Photo.STANDARD_RESOLUTION));
        photo.setLikes(entity.getLong(Props.Photo.LIKES));
        photo.setLink(entity.getString(Props.Photo.LINK));
        photo.setType(entity.getString(Props.Photo.TYPE));
        photo.setUser(entity.getString(Props.Photo.USER));
        return photo;
    }

    public static String[] photosRefsToNames(LeanEntity[] photosRefs) {
        String[] ids = new String[photosRefs.length];
        for (int i = 0; i < photosRefs.length; i++)
        {
            ids[i] = photosRefs[i].getString(Props.PhotoRef.INSTAGRAM_ID);
        }
        return ids;
    }

    public static String[] venuesRefsToNames(LeanEntity[] venuesRefs) {
        String[] ids = new String[venuesRefs.length];
        for (int i = 0; i < venuesRefs.length; i++)
        {
            ids[i] = venuesRefs[i].getString(Props.VenueRef.FOURSQUARE_ID);
        }
        return ids;
    }
}
