package rest.o.gram.cache;

import rest.o.gram.entities.RestogramPhoto;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Roi
 * Date: 11/06/13
 */
public class RestogramPhotos {
    /**
     * Ctor
     */
    public RestogramPhotos() {
        photos = new ArrayList<>();
        token = null;

        firstPhotos = new ArrayList<>();
        firstToken = null;
    }

    /**
     * Returns photos
     */
    public List<RestogramPhoto> getPhotos() {
        return photos;
    }

    /**
     * Returns token
     */
    public String getToken() {
        return token;
    }

    /**
     * Returns first photos
     */
    public List<RestogramPhoto> getFirstPhotos() {
        return firstPhotos;
    }

    /**
     * Returns first token
     */
    public String getFirstToken() {
        return firstToken;
    }

    /**
     * Sets token
     */
    public void setToken(String token) {
        if(firstToken == null) {
            firstToken = token;
            firstPhotos.addAll(photos);
        }

        this.token = token;
    }

    private List<RestogramPhoto> photos; // Photos collection
    private String token; // Last token

    private List<RestogramPhoto> firstPhotos; // First photos collection
    private String firstToken; // First token
}
