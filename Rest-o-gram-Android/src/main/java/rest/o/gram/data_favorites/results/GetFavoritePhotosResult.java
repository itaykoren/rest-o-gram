package rest.o.gram.data_favorites.results;

import rest.o.gram.entities.RestogramPhoto;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Itay
 * Date: 07/09/13
 */
public class GetFavoritePhotosResult {

    public GetFavoritePhotosResult(List<RestogramPhoto> photos, String token) {
        decodeCaptions(photos);
        this.photos = photos;
        this.token = token;
    }

    public List<RestogramPhoto> getPhotos() {
        return photos;
    }

    public String getToken() {
        return token;
    }

    private void decodeCaptions(List<RestogramPhoto> photos) {

        if (photos != null) {
            for (RestogramPhoto photo : photos)
                photo.decodeStrings();
        }
    }

    private List<RestogramPhoto> photos;
    private String token;

}
