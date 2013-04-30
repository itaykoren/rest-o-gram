package rest.o.gram.results;

import com.google.gson.annotations.SerializedName;
import rest.o.gram.entities.RestogramPhoto;

/**
 * Created with IntelliJ IDEA.
 * User: Or
 * Date: 4/22/13
 */
public class PhotosResult {

    public PhotosResult() {}

    public PhotosResult(RestogramPhoto[] photos, String token){
        this.photos = photos;
        this.token = token;
    }

    public String getToken() {
        return token;
    }

    public RestogramPhoto[] getPhotos() {
        return photos;
    }

    @SerializedName("photos")
    private RestogramPhoto[] photos;

    @SerializedName("token")
    private String token;
}
