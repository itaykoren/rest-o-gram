package rest.o.gram;

import org.jinstagram.Instagram;
import org.jinstagram.entity.media.MediaInfoFeed;
import org.jinstagram.exceptions.InstagramException;
import rest.o.gram.credentials.Credentials;
import rest.o.gram.credentials.ICredentialsFactory;
import rest.o.gram.credentials.RandomCredentialsFactory;
import rest.o.gram.entities.RestogramPhoto;
import rest.o.gram.utils.InstagramUtils;

import java.util.logging.Logger;

/**
 * Created with IntelliJ IDEA.
 * User: Or
 * Date: 6/16/13
 */
public final class ApisAccessManager {

    public static RestogramPhoto getPhoto(final String id, final String originVenueId)  {
        long lid;
        try {
            lid = InstagramUtils.extractMediaId(id);
        }
        catch (Exception e) {
            log.severe("cannot extract media id from media feed string id");
            e.printStackTrace();
            return null;
        }

        MediaInfoFeed mediaInfo = null;
        try {
            Credentials credentials = credentialsFactory.createInstagramCredentials();
            log.info("instagram credentials type = " + credentials.getType());
            Instagram instagram = new Instagram(credentials.getClientId());
            mediaInfo = instagram.getMediaInfo(lid);
        }
        catch (InstagramException e) {
            log.warning("first get photo has failed, retry");
            e.printStackTrace();
            try {
                Credentials credentials = credentialsFactory.createInstagramCredentials();
                log.info("instagram credentials type = " + credentials.getType());
                Instagram instagram = new Instagram(credentials.getClientId());
                mediaInfo = instagram.getMediaInfo(lid);
            }
            catch (InstagramException e2) {
                log.severe("second get photo has failed");
                e2.printStackTrace();
                return null;
            }
        }

        return ApisConverters.convertToRestogramPhoto(mediaInfo.getData(), originVenueId);
    }

    private static final Logger log = Logger.getLogger(ApisAccessManager.class.getName());
    private static final ICredentialsFactory credentialsFactory = new RandomCredentialsFactory();
}
