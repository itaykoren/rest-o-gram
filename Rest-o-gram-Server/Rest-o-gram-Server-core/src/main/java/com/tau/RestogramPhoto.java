package com.tau;

import com.google.gson.annotations.SerializedName;
import org.jinstagram.entity.common.Caption;
import org.jinstagram.entity.common.ImageData;
import org.jinstagram.entity.common.Images;
import org.jinstagram.entity.common.Location;
import org.jinstagram.entity.users.feed.MediaFeedData;

/**
* Created with IntelliJ IDEA.
* User: Roi
* Date: 4/5/13
*/
public class RestogramPhoto {

    public RestogramPhoto() {
    }

    public RestogramPhoto(Caption caption, String createdTime, String id, String imageFilter, ImageData standardResolution, String link, Location location, String type) {
        this.caption = caption;
        this.createdTime = createdTime;
        this.id = id;
        this.imageFilter = imageFilter;
        this.standardResolution = standardResolution;
        this.link = link;
        this.location = location;
        this.type = type;
    }

    public RestogramPhoto(MediaFeedData other) {
        this(other.getCaption(), other.getCreatedTime(), other.getId(), other.getImageFilter(), other.getImages().getStandardResolution(), other.getLink(), other.getLocation(), other.getType());
    }

    @SerializedName("caption")
    private Caption caption;

    @SerializedName("created_time")
    private String createdTime;

    @SerializedName("id")
    private String id;

    @SerializedName("filter")
    private String imageFilter;

    //@SerializedName("images")
    //private Images images;

    @SerializedName("standard_resolution")
    private ImageData standardResolution;

    @SerializedName("link")
    private String link;

    @SerializedName("location")
    private Location location;

    @SerializedName("type")
    private String type;

    public ImageData getStandardResolution() {
        return standardResolution;
    }

    public void setStandardResolution(ImageData standardResolution) {
        this.standardResolution = standardResolution;
    }

    /**
     * @return the location
     */
    public Location getLocation() {
        return location;
    }

    /**
     * @param location the location to set
     */
    public void setLocation(Location location) {
        this.location = location;
    }

    /**
     * @return the caption
     */
    public Caption getCaption() {
        return caption;
    }

    /**
     * @param caption the caption to set
     */
    public void setCaption(Caption caption) {
        this.caption = caption;
    }

    /**
     * @return the link
     */
    public String getLink() {
        return link;
    }

    /**
     * @param link the link to set
     */
    public void setLink(String link) {
        this.link = link;
    }

    /**
     * @return the createdTime
     */
    public String getCreatedTime() {
        return createdTime;
    }

    /**
     * @param createdTime the createdTime to set
     */
    public void setCreatedTime(String createdTime) {
        this.createdTime = createdTime;
    }

//    /**
//     * @return the images
//     */
//    public Images getImages() {
//        return images;
//    }

//    /**
//     * @param images the images to set
//     */
//    public void setImages(Images images) {
//        this.images = images;
//    }

    /**
     * @return the imageFilter
     */
    public String getImageFilter() {
        return imageFilter;
    }

    /**
     * @param imageFilter the imageFilter to set
     */
    public void setImageFilter(String imageFilter) {
        this.imageFilter = imageFilter;
    }

    /**
     * @return the id
     */
    public String getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return String.format("MediaFeedData [caption=%s, createdTime=%s, id=%s, imageFilter=%s, standardResolution=%s, link=%s, location=%s, type=%s]",
                caption, createdTime, id, imageFilter, standardResolution, link, location, type);
    }
}
