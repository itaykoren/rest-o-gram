package rest.o.gram.entities;

import rest.o.gram.Utils.EncodingUtils;

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

    public RestogramVenue encodeStrings() {
        encodedName = EncodingUtils.encodeString(name);
        encodedAddress = EncodingUtils.encodeString(address);
        encodedCity = EncodingUtils.encodeString(city);
        encodedState = EncodingUtils.encodeString(state);
        encodedCountry = EncodingUtils.encodeString(country);
        return this;
    }

    public RestogramVenue decodeStrings() {
        name = EncodingUtils.decodeString(encodedName);
        address = EncodingUtils.decodeString(encodedAddress);
        city = EncodingUtils.decodeString(encodedCity);
        state = EncodingUtils.decodeString(encodedState);
        country = EncodingUtils.decodeString(encodedCountry);
        return  this;
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

    public byte[] getEncodedName() {
        return encodedName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public byte[] getEncodedAddress() {
        return encodedAddress;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public byte[] getEncodedCity() {
        return encodedCity;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public byte[] getEncodedState() {
        return encodedState;
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

    public byte[] getEncodedCountry() {
        return encodedCountry;
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

    /** account related props **/

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public boolean isfavorite() {
        return is_favorite;
    }

    public void setfavorite(boolean is_favorite) {
        this.is_favorite = is_favorite;
    }

    private String foursquare_id;
    private String name;
    private byte[] encodedName;
    private String address;
    private byte[] encodedAddress;
    private String city;
    private byte[] encodedCity;
    private String state;
    private byte[] encodedState;
    private String postalCode;
    private String country;
    private byte[] encodedCountry;
    private double latitude;
    private double longitude;
    private double distance;
    private String url;
    private String phone;

    private String description;
    private String imageUrl;

    /** account related fields **/

    private long id = Long.MIN_VALUE;
    private boolean is_favorite;
}

