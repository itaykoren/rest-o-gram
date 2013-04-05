package com.tau;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;

import android.widget.EditText;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.TextView;
import org.json.rpc.client.HttpJsonRpcClientTransport;
import org.json.rpc.client.JsonRpcInvoker;

import java.io.InputStream;
import java.net.URL;
import java.util.Random;


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
        EditText et1 = (EditText)findViewById(R.id.editTextLat);
        EditText et2 = (EditText)findViewById(R.id.editTextLon);

        double latitude = Double.parseDouble(et1.getText().toString());
        double longitude = Double.parseDouble(et2.getText().toString());
        double radius = 500; // TODO: fix

        try
        {
            JsonRpcInvoker invoker = new JsonRpcInvoker();
            RestogramService service = invoker.get(transport, "restogram", RestogramService.class);

            RestogramVenue[] venues = service.getNearby(latitude, longitude, radius);
            handle(service, venues);
        }
        catch(Exception e)
        {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void init()
    {
        try
        {
            transport = new HttpJsonRpcClientTransport(new URL(url));
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();

            StrictMode.setThreadPolicy(policy);
        }
        catch (Exception e)
        {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void handle(RestogramService service, RestogramVenue[] venues)
    {
        TextView text = (TextView)findViewById(R.id.textView);

        if(venues == null || venues.length == 0)
        {
            // TODO: report error
            text.setText("No Restaurant Found");
            ImageView image = (ImageView)findViewById(R.id.imageView1);
            image.setImageResource(android.R.color.transparent);
            return;
        }

        String venueID = venues[0].getId(); // TODO: fix
        RestogramPhoto[] photos = service.getPhotos(venueID);
        text.setText(venues[0].getName());
        handle(service, photos);
    }

    private void handle(RestogramService service, RestogramPhoto[] photos)
    {
        // TODO: implementation

        if(photos == null)
        {
            // TODO: report error
            return;
        }

        updatePhotos(photos);
    }

    private void updatePhotos(RestogramPhoto[] photos)
    {
        ImageView image = (ImageView)findViewById(R.id.imageView1);
        int rand = new Random().nextInt(photos.length - 1);
        String imageUrl = photos[rand].getStandardResolution().getImageUrl();
        DownloadImageTask task = new DownloadImageTask(image);
        task.execute(imageUrl);
    }

    private final String url = "http://rest-o-gram.appspot.com/jsonrpc";
    private HttpJsonRpcClientTransport transport;
}
