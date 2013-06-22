package rest.o.gram;

import com.google.appengine.api.datastore.Entity;
import com.leanengine.server.appengine.DatastoreUtils;
import rest.o.gram.entities.Kinds;
import rest.o.gram.entities.Props;
import rest.o.gram.entities.RestogramPhoto;
import rest.o.gram.entities.RestogramVenue;

import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: Or
 * Date: 5/24/13
 */
public final class DataStoreConverters {
    public static Entity venueToEntity(final RestogramVenue venue) {
        Entity entity = new Entity(Kinds.VENUE, venue.getFoursquare_id());
        entity.setUnindexedProperty(Props.Venue.NAME, venue.getName());
        entity.setUnindexedProperty(Props.Venue.ADDRESS, venue.getAddress());
        entity.setUnindexedProperty(Props.Venue.CITY, venue.getCity());
        entity.setUnindexedProperty(Props.Venue.STATE, venue.getState());
        entity.setUnindexedProperty(Props.Venue.POSTAL_CODE, venue.getPostalCode());
        entity.setUnindexedProperty(Props.Venue.COUNTRY, venue.getCountry());
        entity.setUnindexedProperty(Props.Venue.LAT, venue.getLatitude());
        entity.setUnindexedProperty(Props.Venue.LONG, venue.getLongitude());
        entity.setUnindexedProperty(Props.Venue.DISTANCE, venue.getDistance());
        entity.setUnindexedProperty(Props.Venue.URL, venue.getUrl());
        entity.setUnindexedProperty(Props.Venue.PHONE, venue.getPhone());
        //entity.setUnindexedProperty(Props.Venue.DESCRIPTION, venue.getDescription());
        //entity.setUnindexedProperty(Props.Venue.IMAGE_URL, venue.getImageUrl());
        return entity;
    }

    public static Map<String, DatastoreUtils.PropertyDescription> venueToProps(final RestogramVenue venue)
    {
        Map<String,DatastoreUtils.PropertyDescription> props = new HashMap<>(11);
        props.put(Props.Venue.NAME, new  DatastoreUtils.PropertyDescription(venue.getName(), false));
        props.put(Props.Venue.ADDRESS, new DatastoreUtils.PropertyDescription(venue.getAddress(), false));
        props.put(Props.Venue.CITY, new DatastoreUtils.PropertyDescription(venue.getCity(), false));
        props.put(Props.Venue.STATE, new DatastoreUtils.PropertyDescription(venue.getState(), false));
        props.put(Props.Venue.POSTAL_CODE, new DatastoreUtils.PropertyDescription(venue.getPostalCode(), false));
        props.put(Props.Venue.COUNTRY, new DatastoreUtils.PropertyDescription(venue.getCountry(), false));
        props.put(Props.Venue.LAT, new DatastoreUtils.PropertyDescription(venue.getLatitude(), false));
        props.put(Props.Venue.LONG, new DatastoreUtils.PropertyDescription(venue.getLongitude(), false));
        props.put(Props.Venue.DISTANCE, new DatastoreUtils.PropertyDescription(venue.getDistance(), false));
        props.put(Props.Venue.URL, new DatastoreUtils.PropertyDescription(venue.getUrl(), false));
        props.put(Props.Venue.PHONE, new DatastoreUtils.PropertyDescription(venue.getPhone(), false));
        return props;
    }

    public static RestogramVenue entityToVenue(final Entity entity) {
        RestogramVenue venue = new RestogramVenue();
        venue.setFoursquare_id(entity.getKey().getName());
        // TODO: handle the case of an entity that the curr account has ref to...
        //if (entity.hasId())
        //    venue.setId(entity.getId());
        venue.setName((String)entity.getProperty(Props.Venue.NAME));
        venue.setAddress((String)entity.getProperty(Props.Venue.ADDRESS));
        venue.setCity((String)entity.getProperty(Props.Venue.CITY));
        venue.setState((String)entity.getProperty(Props.Venue.STATE));
        venue.setPostalCode((String)entity.getProperty(Props.Venue.POSTAL_CODE));
        venue.setCountry((String)entity.getProperty(Props.Venue.COUNTRY));
        venue.setLatitude((double)entity.getProperty(Props.Venue.LAT));
        venue.setLongitude((double)entity.getProperty(Props.Venue.LONG));
        venue.setDistance((double)entity.getProperty(Props.Venue.DISTANCE));
        venue.setUrl((String)entity.getProperty(Props.Venue.URL));
        venue.setPhone((String)entity.getProperty(Props.Venue.PHONE));
        //venue.setDescription(entity.getText(Props.Venue.DESCRIPTION));
        //venue.setImageUrl(entity.getString(Props.Venue.IMAGE_URL));
        return venue;
    }

    public static Entity photoToEntity(final RestogramPhoto photo) {
        Entity entity = new Entity(Kinds.PHOTO, photo.getInstagram_id());
        entity.setUnindexedProperty(Props.Photo.CAPTION, photo.getCaption());
        entity.setUnindexedProperty(Props.Photo.CREATED_TIME, photo.getCreatedTime());
        entity.setUnindexedProperty(Props.Photo.IMAGE_FILTER, photo.getImageFilter());
        entity.setUnindexedProperty(Props.Photo.THUMBNAIL, photo.getThumbnail());
        entity.setUnindexedProperty(Props.Photo.STANDARD_RESOLUTION, photo.getStandardResolution());
        entity.setUnindexedProperty(Props.Photo.IMAGE_FILTER, photo.getImageFilter());
        entity.setUnindexedProperty(Props.Photo.LIKES, photo.getLikes());
        entity.setUnindexedProperty(Props.Photo.LINK, photo.getLink());
        entity.setUnindexedProperty(Props.Photo.TYPE, photo.getType());
        entity.setUnindexedProperty(Props.Photo.USER, photo.getUser());
        entity.setUnindexedProperty(Props.Photo.ORIGIN_VENUE_ID, photo.getOriginVenueId());
        entity.setUnindexedProperty(Props.Photo.YUMMIES, photo.getYummies());
        return entity;
    }

    public static Map<String, DatastoreUtils.PropertyDescription> photoToProps(final RestogramPhoto photo){
        Map<String,DatastoreUtils.PropertyDescription> props = new HashMap<>(12);
        props.put(Props.Photo.CAPTION, new DatastoreUtils.PropertyDescription(photo.getCaption(), false));
        props.put(Props.Photo.CREATED_TIME, new DatastoreUtils.PropertyDescription(photo.getCreatedTime(), false));
        props.put(Props.Photo.IMAGE_FILTER,new DatastoreUtils.PropertyDescription(photo.getImageFilter(),  false));
        props.put(Props.Photo.THUMBNAIL, new DatastoreUtils.PropertyDescription(photo.getThumbnail(), false));
        props.put(Props.Photo.STANDARD_RESOLUTION, new DatastoreUtils.PropertyDescription(photo.getStandardResolution(), false));
        props.put(Props.Photo.IMAGE_FILTER, new DatastoreUtils.PropertyDescription(photo.getImageFilter(), false));
        props.put(Props.Photo.LIKES, new DatastoreUtils.PropertyDescription(photo.getLikes(), false));
        props.put(Props.Photo.LINK, new DatastoreUtils.PropertyDescription(photo.getLink(), false));
        props.put(Props.Photo.TYPE, new DatastoreUtils.PropertyDescription(photo.getType(), false));
        props.put(Props.Photo.USER, new DatastoreUtils.PropertyDescription(photo.getUser(), false));
        props.put(Props.Photo.ORIGIN_VENUE_ID, new DatastoreUtils.PropertyDescription(photo.getOriginVenueId(), true));
        props.put(Props.Photo.YUMMIES, new DatastoreUtils.PropertyDescription(photo.getYummies(), true));
        return props;
    }

    public static RestogramPhoto entityToPhoto(final Entity entity) {
        RestogramPhoto photo = new RestogramPhoto();
        photo.setInstagram_id(entity.getKey().getName());
        // TODO: handle the case of an entity that the curr account has ref to...
        //if (entity.hasId())
        //    photo.setId(entity.getId());
        photo.setCaption((String)entity.getProperty(Props.Photo.CAPTION));
        photo.setCreatedTime((String)entity.getProperty(Props.Photo.CREATED_TIME));
        photo.setImageFilter((String)entity.getProperty(Props.Photo.IMAGE_FILTER));
        photo.setThumbnail((String)entity.getProperty(Props.Photo.THUMBNAIL));
        photo.setStandardResolution((String)entity.getProperty(Props.Photo.STANDARD_RESOLUTION));
        photo.setLikes((long)entity.getProperty(Props.Photo.LIKES));
        photo.setLink((String)entity.getProperty(Props.Photo.LINK));
        photo.setType((String)entity.getProperty(Props.Photo.TYPE));
        photo.setUser((String)entity.getProperty(Props.Photo.USER));
        photo.setOriginVenueId((String)entity.getProperty(Props.Photo.ORIGIN_VENUE_ID));
        photo.setYummies((long)entity.getProperty(Props.Photo.YUMMIES));
        return photo;
    }


}