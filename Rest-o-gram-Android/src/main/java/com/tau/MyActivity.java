package com.tau;

import android.app.Activity;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import org.json.rpc.client.HttpJsonRpcClientTransport;
import org.json.rpc.client.JsonRpcInvoker;

import java.net.URL;


public class MyActivity extends Activity implements ITaskObserver {
    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        clear();
        init();
    }

    public void onGoClicked(View view)
    {
        EditText et1 = (EditText)findViewById(R.id.editTextLat);
        EditText et2 = (EditText)findViewById(R.id.editTextLon);

        double latitude = Double.parseDouble(et1.getText().toString());
        double longitude = Double.parseDouble(et2.getText().toString());
        double radius = 500; // TODO: fix

        clear();

        GetNearbyTask task = new GetNearbyTask(transport, this);
        task.execute(latitude, longitude, radius);
    }

    public void onNextClicked(View view)
    {
        if(currPhotos == null)
            return;

        currPhotoIndex = (currPhotoIndex + 1) % currPhotos.length;

        ImageView image = (ImageView)findViewById(R.id.imageView1);
        String imageUrl = currPhotos[currPhotoIndex].getStandardResolution().getImageUrl();
        DownloadImageTask task = new DownloadImageTask(image);
        task.execute(imageUrl);
    }

    public void onFinished(RestogramVenue[] venues)
    {
        TextView text = (TextView)findViewById(R.id.textView);

        if(venues == null || venues.length == 0)
        {
            // TODO: report error
            text.setText("No Restaurant Found");
            return;
        }

        RestogramVenue venue = venues[0]; // TODO: fix
        String venueID = venue.getId();

        text.setText(venue.getName());

        GetPhotosTask task = new GetPhotosTask(transport, this);
        task.execute(venueID);
    }

    public void onFinished(RestogramPhoto[] photos)
    {
        // TODO: implementation
        if(photos == null)
        {
            // TODO: report error
            return;
        }

        Button button = (Button)findViewById(R.id.buttonNext);
        button.setClickable(true);

        updatePhotos(photos);
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

    private void updatePhotos(RestogramPhoto[] photos)
    {
        currPhotos = photos;
        currPhotoIndex = 0;

        ImageView image = (ImageView)findViewById(R.id.imageView1);
        String imageUrl = photos[currPhotoIndex].getStandardResolution().getImageUrl();
        DownloadImageTask task = new DownloadImageTask(image);
        task.execute(imageUrl);
    }

    private void clear()
    {
        currPhotos = null;
        currPhotoIndex = -1;

        TextView text = (TextView)findViewById(R.id.textView);
        text.setText("");
        text.invalidate();

        ImageView image = (ImageView)findViewById(R.id.imageView1);
        image.setImageResource(android.R.color.transparent);

        Button button = (Button)findViewById(R.id.buttonNext);
        button.setClickable(false);
    }

    private final String url = "http://rest-o-gram.appspot.com/jsonrpc";
    private HttpJsonRpcClientTransport transport;

    private RestogramPhoto[] currPhotos;
    private int currPhotoIndex;
}
