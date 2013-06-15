package rest.o.gram.service;

import rest.o.gram.data.DataManager;
import rest.o.gram.iservice.RestogramAuthService;
import java.util.logging.Logger;

/**
 * Created with IntelliJ IDEA.
 * User: Or
 * Date: 5/19/13
 */
public class RestogramAuthServiceImpl implements RestogramAuthService {

    @Override
    public boolean addPhotoToFavorites(String photoId) {
        if (!DataManager.updatePhotoReference(photoId, true))
            return false;

        return DataManager.changePhotoYummiesCount(photoId, 1);
    }

    @Override
    public boolean removePhotoFromFavorites(String photoId) {
        if (!DataManager.updatePhotoReference(photoId, false))
            return false;

        return DataManager.changePhotoYummiesCount(photoId, -1);
    }

    private static final Logger log = Logger.getLogger(RestogramAuthServiceImpl.class.getName());
}
