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

    public RestogramVenue(String id, String name, String address, String city,
            String state, String postalCode, String country,
            double latitude, double longitude, double distance, String url) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.city = city;
        this.state = state;
        this.postalCode = postalCode;
        this.country = country;
        this.latitude = latitude;
        this.longitude = longitude;
        this.distance = distance;
        this.url = url;
    }

    public RestogramVenue(String id, String name, String description, String imageUrl) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.imageUrl = imageUrl;
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
     * Returns address
     *
     * @return address
     */
    public String getAddress() {
        return address;
    }

    /**
     * Returns city
     *
     * @return city
     */
    public String getCity() {
        return city;
    }

    /**
     * Returns state
     *
     * @return state
     */
    public String getState() {
        return state;
    }

    /**
     * Returns postal Code
     *
     * @return postal Code
     */
    public String getPostalCode() {
        return postalCode;
    }

    /**
     * Returns country
     *
     * @return country
     */
    public String getCountry() {
        return country;
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
     * Returns distance
     *
     * @return distance
     */
    public double getDistance() {
        return distance;
    }

    /**
     * Returns url for this venue
     *
     * @return url for this venue
     */
    public String getUrl() {
        return url;
    }

    /**
     * Returns description of this venue
     *
     * @return description of this venue
     */
    public String getDescription() {
        return description;
    }

    /**
     * Returns image url for this venue
     *
     * @return image url for this venue
     */
    public String getImageUrl() {
        return imageUrl;
    }

    private String id;
    private String name;
    private String address;
    private String city;
    private String state;
    private String postalCode;
    private String country;
    private double latitude;
    private double longitude;
    private double distance;
    private String url;

    private String description;
    private String imageUrl;
}

