package com.tau;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import fi.foyt.foursquare.api.entities.CompactVenue;
import org.jinstagram.entity.users.feed.MediaFeedData;


public class MyActivity extends Activity {
    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);


        init();
    }

    public void onGoClicked(View view)
    {
        double latitude = 32.112; // TODO: fix
        double longitude = 34.839; // TODO: fix
        double radius = 500; // TODO: fix

        // Make the call to the service.
        m_service.getNearby(latitude, longitude, radius, m_nearbyCallback);
    }

    private void init()
    {
        // Initialize the service proxy.
        m_service = GWT.create(RestogramService.class);

        // Set up the callback object.
        m_nearbyCallback = new AsyncCallback<CompactVenue[]>() {
            public void onFailure(Throwable caught) {
                // TODO: Do something with errors.
            }

            public void onSuccess(CompactVenue[] result) {
                handle(result);
            }
        };

        // Set up the callback object.
        m_photosCallback = new AsyncCallback<MediaFeedData[]>() {
            public void onFailure(Throwable caught) {
                // TODO: Do something with errors.
            }

            public void onSuccess(MediaFeedData[] result) {
                handle(result);
            }
        };
    }

    private void handle(CompactVenue[] venues)
    {
        if(venues == null || venues.length == 0)
        {
            // TODO: report error
            return;
        }

        String venueID = venues[0].getId(); // TODO: fix
        m_service.getPhotos(venueID, m_photosCallback);
    }

    private void handle(MediaFeedData[] photos)
    {
        // TODO: implementation

        if(photos == null)
        {
            // TODO: report error
            return;
        }

        for(MediaFeedData data : photos)
            System.out.println(data);
    }

    private RestogramServiceAsync m_service;
    private AsyncCallback<CompactVenue[]> m_nearbyCallback;
    private AsyncCallback<MediaFeedData[]> m_photosCallback;
}
