package rest.o.gram.service.InstagramServices.Entities;

import com.google.gson.annotations.SerializedName;
import org.jinstagram.entity.common.Pagination;
import rest.o.gram.entities.RestogramPhoto;

import java.io.Serializable;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Or
 * Date: 9/5/13
 */

// TODO: make immutable and avoid direct use of the Instagram Pagination type
public class RestogramPhotos implements Serializable {

    public RestogramPhotos() {}

    public RestogramPhotos(final List<RestogramPhoto> photos, Pagination pagination) {
        this.photos = photos;
        this.pagination = pagination;
    }

    public List<RestogramPhoto> getPhotos() {
        return photos;
    }

    public void setPhotos(final List<RestogramPhoto> photos) {
        this.photos = photos;
    }

    public Pagination getPagination() {
        return pagination;
    }

    public void setPagination(Pagination pagination) {
        this.pagination = pagination;
    }

    public RestogramPhotos encodeStrings() {
        if (photos != null)
        {
            for (final RestogramPhoto currPhoto : photos)
                if (currPhoto != null)
                    currPhoto.encodeStrings();
        }
        return this;
    }

    public RestogramPhotos decodeStrings() {
        if (photos != null)
        {
            for (final RestogramPhoto currPhoto : photos)
                if (currPhoto != null)
                    currPhoto.decodeStrings();
        }
        return this;
    }

    @SerializedName("photos")
    private List<RestogramPhoto> photos;

    @SerializedName("pagination")
    private Pagination pagination;
}
