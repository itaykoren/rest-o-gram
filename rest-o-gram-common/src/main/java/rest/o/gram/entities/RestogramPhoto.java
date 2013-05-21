package rest.o.gram.entities;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
* Created with IntelliJ IDEA.
* User: Roi
* Date: 4/5/13
*/
public class RestogramPhoto implements Serializable {

    public RestogramPhoto() {
    }

    public RestogramPhoto(String caption, String createdTime, String instagram_id, String imageFilter,
                          String thumbnail, String standardResolution, int likes, String link,
                          String type, String user) {
        this.caption = caption;
        this.createdTime = createdTime;
        this.instagram_id = instagram_id;
        this.imageFilter = imageFilter;
        this.thumbnail = thumbnail;
        this.standardResolution = standardResolution;
        this.likes = likes;
        this.link = link;
        this.type = type;
        this.user = user;
    }

    public String getCaption() {
        return caption;
    }

    public String getCreatedTime() {
        return createdTime;
    }

    public String getInstagram_id() {
        return instagram_id;
    }

    public String getImageFilter() {
        return imageFilter;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public String getStandardResolution() {
        return standardResolution;
    }

    public int getLikes() {
        return likes;
    }

    public String getLink() {
        return link;
    }

    public String getType() {
        return type;
    }

    public String getUser() {
        return user;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @SerializedName("caption")
    private String caption;

    @SerializedName("created_time")
    private String createdTime;

    @SerializedName("instagram_id")
    private String instagram_id;

    @SerializedName("filter")
    private String imageFilter;

    @SerializedName("thumbnail")
    private String thumbnail;

    @SerializedName("standard_resolution")
    private String standardResolution;

    @SerializedName("likes")
    private int likes;

    @SerializedName("link")
    private String link;

    @SerializedName("type")
    private String type;

    @SerializedName("user")
    private String user;

    @SerializedName("id")
    private long id = Long.MIN_VALUE;;
}
