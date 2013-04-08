package com.tau;

import android.app.Activity;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.widget.*;
import org.json.rpc.client.HttpJsonRpcClientTransport;

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
        clear();

        EditText et1 = (EditText)findViewById(R.id.editTextLat);
        EditText et2 = (EditText)findViewById(R.id.editTextLon);
        SeekBar sb1 = (SeekBar) findViewById(R.id.seekBarRadius);

        double latitude, longitude, radius;

        try
        {
            latitude = Double.parseDouble(et1.getText().toString());
            longitude = Double.parseDouble(et2.getText().toString());
            radius = sb1.getProgress();
        }
        catch(NumberFormatException e)
        {
            System.out.println("Error: " + e.getMessage());
            return;
        }

        updateText("Searching...");

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
        if(venues == null || venues.length == 0)
        {
            // TODO: report error
            updateText("No Restaurant Found");
            return;
        }

        RestogramVenue venue = venues[0]; // TODO: fix
        String venueID = venue.getId();

        updateText(venue.getName());

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

            final TextView tvRadius = (TextView)findViewById(R.id.textViewRadius);
            SeekBar sbRadius = (SeekBar)findViewById(R.id.seekBarRadius);
            final int stepSize = 10;

            sbRadius.setOnSeekBarChangeListener( new SeekBar.OnSeekBarChangeListener()
            {
                public void onProgressChanged(SeekBar seekBar, int progress,
                                              boolean fromUser)
                {
                    // TODO Auto-generated method stub

                    progress = ((int)Math.round(progress/stepSize))*stepSize;
                    seekBar.setProgress(progress);

                    tvRadius.setText("Radius: " + progress);
                }

                public void onStartTrackingTouch(SeekBar seekBar)
                {
                    // TODO Auto-generated method stub
                }

                public void onStopTrackingTouch(SeekBar seekBar)
                {
                    // TODO Auto-generated method stub
                }
            });

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

        updateText("");

        ImageView image = (ImageView)findViewById(R.id.imageView1);
        image.setImageResource(android.R.color.transparent);

        Button button = (Button)findViewById(R.id.buttonNext);
        button.setClickable(false);
    }

    private void updateText(String text)
    {
        TextView tv = (TextView)findViewById(R.id.textView);
        tv.setText(text);
        tv.invalidate();
    }

    private final String url = "http://rest-o-gram.appspot.com/jsonrpc";
    //private final String url = "http://localhost:8080/jsonrpc";
    private HttpJsonRpcClientTransport transport;

    private RestogramPhoto[] currPhotos;
    private int currPhotoIndex;
}
