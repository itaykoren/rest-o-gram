package rest.o.gram.entities;

import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * User: Roi
 * Date: 4/5/13
 */
public class RestogramVenue implements Serializable {

    public RestogramVenue() {
    }

    public RestogramVenue(String foursquare_id, String name, String address, String city,
            String state, String postalCode, String country,
            double latitude, double longitude, double distance, String url, String phone) {
        this.foursquare_id = foursquare_id;
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
        this.phone = phone;
    }

    public RestogramVenue(String foursquare_id, String name, String description, String imageUrl) {
        this.foursquare_id = foursquare_id;
        this.name = name;
        this.description = description;
        this.imageUrl = imageUrl;
    }

    public String getFoursquare_id() {
        return foursquare_id;
    }

    public void setFoursquare_id(String foursquare_id) {
        this.foursquare_id = foursquare_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    private String foursquare_id;
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
    private String phone;

    private String description;
    private String imageUrl;
    private long id = Long.MIN_VALUE;

}

