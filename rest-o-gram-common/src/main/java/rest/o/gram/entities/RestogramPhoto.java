package rest.o.gram.entities;

import com.google.gson.annotations.SerializedName;
import rest.o.gram.Utils.EncodingUtils;

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
                          String thumbnail, String standardResolution, long likes, String link,
                          String type, String user, String originVenueId, long yummies) {
        this.setCaption(caption);
        this.setCreatedTime(createdTime);
        this.setInstagram_id(instagram_id);
        this.setImageFilter(imageFilter);
        this.setThumbnail(thumbnail);
        this.setStandardResolution(standardResolution);
        this.setLikes(likes);
        this.setLink(link);
        this.setType(type);
        this.setUser(user);
        this.setOriginVenueId(originVenueId);
        this.setYummies(yummies);
    }

    public RestogramPhoto encodeStrings() {
         encodedCaption = EncodingUtils.encodeString(caption);
        return this;
    }

    public RestogramPhoto decodeStrings() {
        caption = EncodingUtils.decodeString(encodedCaption);
        return this;
    }

    public String getCaption() {
        return caption;
    }

    public byte[] getEncodedCaption() {
        return encodedCaption;
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

    public long getLikes() {
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

    public String getOriginVenueId() {
        return originVenueId;
    }

    public long getYummies() {
        return yummies;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public void setCreatedTime(String createdTime) {
        this.createdTime = createdTime;
    }

    public void setInstagram_id(String instagram_id) {
        this.instagram_id = instagram_id;
    }

    public void setImageFilter(String imageFilter) {
        this.imageFilter = imageFilter;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public void setStandardResolution(String standardResolution) {
        this.standardResolution = standardResolution;
    }

    public void setLikes(long likes) {
        this.likes = likes;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public void setOriginVenueId(String originVenueId) {
        this.originVenueId = originVenueId;
    }

    public void setYummies(long yummies) {
        this.yummies = yummies;
    }

    /** account related props **/

//    public long getId() {
//        return id;
//    }
//
//    public void setId(long id) {
//        this.id = id;
//    }

    public boolean is_favorite() {
        return is_favorite;
    }

    public void set_favorite(boolean is_favorite) {
        this.is_favorite = is_favorite;
    }

    @SerializedName("caption")
    private String caption;

    @SerializedName("encoded_caption")
    private byte[] encodedCaption;

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
    private long likes;

    @SerializedName("link")
    private String link;

    @SerializedName("type")
    private String type;

    @SerializedName("user")
    private String user;

    @SerializedName("originVenueId")
    private String originVenueId;

    @SerializedName("yummies")
    private long yummies;

    /** account related fields **/

    @SerializedName("id")
    private long id = Long.MIN_VALUE;

    @SerializedName("is_favorite")
    private boolean is_favorite;
}
