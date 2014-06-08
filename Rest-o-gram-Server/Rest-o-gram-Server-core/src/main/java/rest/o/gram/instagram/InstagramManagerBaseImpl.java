package rest.o.gram.instagram;

import com.google.gson.Gson;
import org.jinstagram.Instagram;
import org.jinstagram.entity.common.Pagination;
import org.jinstagram.exceptions.InstagramException;
import rest.o.gram.ApisConverters;
import rest.o.gram.credentials.ICredentialsFactory;
import rest.o.gram.credentials.RandomCredentialsFactory;
import rest.o.gram.service.InstagramServices.Entities.RestogramPhotos;
import rest.o.gram.utils.InstagramUtils;

import java.util.logging.Logger;

/**
 * Created with IntelliJ IDEA.
 * User: Or
 * Date: 6/8/14
 */
public abstract class InstagramManagerBaseImpl implements InstagramManager {

    public InstagramManagerBaseImpl() {
        try {
            // TODO: when instagram login is implemented - prefer user credentials whenever possible
            m_credentialsFactory = new RandomCredentialsFactory();
        } catch (Exception e) {
            log.severe(String.format("an error occurred while initializing instagram manager, error: %s",
                    e.getMessage()));
        }
    }

    @Override
    public RestogramPhotos getRecentMedia(final String token, final String venueId) {
        final Pagination pagination = new Gson().fromJson(token, Pagination.class);
        RestogramPhotos photos = null;

        try {
            final Instagram instagram = getInstagramCredentials();
            photos = ApisConverters.convertToRestogramPhotos(instagram.getRecentMediaNextPage(pagination), venueId);
        } catch (InstagramException e) {
            log.warning(String.format("first media search has failed, retry, error: %s", e.getMessage()));

            try {
                final Instagram instagram = getInstagramCredentials();
                photos = ApisConverters.convertToRestogramPhotos(instagram.getRecentMediaNextPage(pagination), venueId);
            } catch (InstagramException e2) {
                log.severe(String.format("second media search has failed, error: %s", e2.getMessage()));
                return null;
            }
        }
        return photos;
    }

    protected Instagram getInstagramCredentials() {
        return InstagramUtils.createInstagramAPI(m_credentialsFactory, log);
    }

    private static final Logger log = Logger.getLogger(InstagramManagerBaseImpl.class.getName());
    protected ICredentialsFactory m_credentialsFactory;
}