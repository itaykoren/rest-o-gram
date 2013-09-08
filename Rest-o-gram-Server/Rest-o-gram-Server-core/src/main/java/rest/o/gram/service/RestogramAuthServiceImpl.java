package rest.o.gram.service;

import com.leanengine.server.LeanException;
import com.leanengine.server.appengine.DatastoreUtils;
import rest.o.gram.Defs;
import rest.o.gram.InstagramAccessManager;
import rest.o.gram.DataStoreConverters;
import rest.o.gram.data.DataManager;
import rest.o.gram.entities.Kinds;
import rest.o.gram.entities.RestogramPhoto;
import rest.o.gram.iservice.RestogramAuthService;
import rest.o.gram.results.PhotosResult;
import rest.o.gram.utils.InstagramUtils;

import java.util.logging.Logger;

/**
 * Created with IntelliJ IDEA.
 * User: Or
 * Date: 5/19/13
 */
public class RestogramAuthServiceImpl implements RestogramAuthService {

    @Override
    public boolean addPhotoToFavorites(final String photoId, final String originVenueId) {
        if (!DataManager.updatePhotoReference(photoId, true))
        {
            log.severe("cannot update photo reference");
            return true;
        }

        if (DataManager.isPhotoInCache(photoId)) // in DS already
        {
            log.info("YUMMIES: already in DS");
            if (!DataManager.changePhotoYummiesCount(photoId, 1))
            {
                log.severe("cannot cahne yummies count, photo:" + photoId);
                return true;
            }
            return true;
        }
        else if (DataManager.isPhotoPending(photoId)) //  pending photo
        {
            log.info("YUMMIES: photo is pending");
            final RestogramPhoto pendingPhoto = DataManager.getPendingPhoto(photoId);
            pendingPhoto.setYummies(1);
            try
            {
                DatastoreUtils.putPublicEntity(Kinds.PHOTO, pendingPhoto.getInstagram_id(),
                                                DataStoreConverters.photoToProps(pendingPhoto));
            } catch (LeanException e)
            {
                log.severe("cannot write an updated photo(yummies counter) to cache");
                return true;
            }
        }
        else //  get from instagram
        {
            log.info("YUMMIES: getting from instagram");
            final InstagramAccessManager.PrepareRequest prepareRequest =
                    new InstagramAccessManager.PrepareRequest() {
                        @Override
                        public byte[] getPayload() {
                            return photoId.getBytes();
                        }
                    };
            final RestogramPhoto restogramPhoto =
                    InstagramAccessManager.parallelFrontendInstagramRequest(Defs.Instagram.RequestType.GetPhoto,
                                                                            prepareRequest,
                                                                            RestogramPhoto.class);
            if (InstagramUtils.isNullOrEmpty(restogramPhoto))
                return true;

            //decode string to get the correct encoding
            restogramPhoto.decodeStrings();

            restogramPhoto.setOriginVenueId(originVenueId);
            restogramPhoto.setYummies(1);
            DataManager.cachePhoto(restogramPhoto);
            return true;
        }
        return true;
    }

    @Override
    public boolean removePhotoFromFavorites(String photoId) {
        if (!DataManager.updatePhotoReference(photoId, false))
        {
            log.severe("cannot update photo reference");
            return true;
        }

        return DataManager.changePhotoYummiesCount(photoId, -1);
    }

    @Override
    public PhotosResult getFavoritePhotos(final String token) {
        return DataManager.queryFavoritePhotos(token);
    }

    private static final Logger log = Logger.getLogger(RestogramAuthServiceImpl.class.getName());
}
