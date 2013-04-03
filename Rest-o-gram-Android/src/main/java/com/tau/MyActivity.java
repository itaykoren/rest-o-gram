package com.tau;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import fi.foyt.foursquare.api.entities.CompactVenue;
import org.jinstagram.entity.users.feed.MediaFeedData;
import org.json.rpc.client.HttpJsonRpcClientTransport;
import org.json.rpc.client.JsonRpcInvoker;

import java.net.URL;


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

        JsonRpcInvoker invoker = new JsonRpcInvoker();
        RestogramService service = invoker.get(transport, "restogram", RestogramService.class);

        CompactVenue[] venues = service.getNearby(latitude, longitude, radius);
        handle(service, venues);
    }

    private void init()
    {
        try
        {
            transport = new HttpJsonRpcClientTransport(new URL(url));
        }
        catch (Exception e)
        {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void handle(RestogramService service, CompactVenue[] venues)
    {
        if(venues == null || venues.length == 0)
        {
            // TODO: report error
            return;
        }

        String venueID = venues[0].getId(); // TODO: fix
        MediaFeedData[] photos = service.getPhotos(venueID);
        handle(service, photos);
    }

    private void handle(RestogramService service, MediaFeedData[] photos)
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

    private final String url = "http://rest-o-gram.appspot.com/jsonrpc";
    private HttpJsonRpcClientTransport transport;
}
