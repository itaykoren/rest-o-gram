package com.tau;

import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * User: Roi
 * Date: 4/5/13
 */
public class RestogramVenue implements Serializable {

    public RestogramVenue() {
    }

    public RestogramVenue(String id, String name, double latitude, double longitude, String url) {
        this.id = id;
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
        this.url = url;
    }

    /**
     * Returns id of the venue
     *
     * @return id of the venue
     */
    public String getId() {
        return id;
    }

    /**
     * Returns name of the venue
     *
     * @return name of the venue
     */
    public String getName() {
        return name;
    }

    /**
     * Returns venue's latitude
     *
     * @return venue's latitude
     */
    public double getLatitude() {
        return latitude;
    }

    /**
     * Returns venue's longitude
     *
     * @return venue's longitude
     */
    public double getLongitude() {
        return longitude;
    }

    /**
     * Returns url for this venue
     *
     * @return url for this venue
     */
    public String getUrl() {
        return url;
    }

    private String id;
    private String name;
    private double latitude;
    private double longitude;
    private String url;
}

