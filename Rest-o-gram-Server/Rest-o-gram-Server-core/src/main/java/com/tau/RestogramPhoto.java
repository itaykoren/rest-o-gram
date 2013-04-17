package com.tau;

import com.google.gson.annotations.SerializedName;
import org.jinstagram.entity.common.*;
import org.jinstagram.entity.users.feed.MediaFeedData;

import java.io.Serializable;

/**
* Created with IntelliJ IDEA.
* User: Roi
* Date: 4/5/13
*/
public class RestogramPhoto implements Serializable {

    public RestogramPhoto() {
    }

    public RestogramPhoto(Caption caption, String createdTime, String id, String imageFilter,
                          String thumbnail, String standardResolution, int likes, String link,
                          Location location, String type, User user) {
        this.caption = caption;
        this.createdTime = createdTime;
        this.id = id;
        this.imageFilter = imageFilter;
        this.thumbnail = thumbnail;
        this.standardResolution = standardResolution;
        this.likes = likes;
        this.link = link;
        this.location = location;
        this.type = type;
        this.user = user;
    }

    public RestogramPhoto(MediaFeedData other) {
        this(other.getCaption(), other.getCreatedTime(), other.getId(), other.getImageFilter(),
                other.getImages().getThumbnail().getImageUrl(), other.getImages().getStandardResolution().getImageUrl(),
                other.getLikes().getCount(), other.getLink(), other.getLocation(), other.getType(), other.getUser());
    }

    @Override
    public String toString() {
        return String.format("RestogramPhoto [caption=%s, createdTime=%s, id=%s, imageFilter=%s, thumbnail=%s, standardResolution=%s, likes=%s, link=%s, location=%s, type=%s, user=%s]",
                caption, createdTime, id, imageFilter, thumbnail, standardResolution, likes, link, location, type, user);
    }

    public Caption getCaption() {
        return caption;
    }

    public String getCreatedTime() {
        return createdTime;
    }

    public String getId() {
        return id;
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

    public Location getLocation() {
        return location;
    }

    public String getType() {
        return type;
    }

    public User getUser() {
        return user;
    }

    @SerializedName("caption")
    private Caption caption;

    @SerializedName("created_time")
    private String createdTime;

    @SerializedName("id")
    private String id;

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

    @SerializedName("location")
    private Location location;

    @SerializedName("type")
    private String type;

    @SerializedName("user")
    private User user;
}
