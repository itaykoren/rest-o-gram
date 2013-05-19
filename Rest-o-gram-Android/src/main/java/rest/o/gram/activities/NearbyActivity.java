package rest.o.gram.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.leanengine.*;
import rest.o.gram.R;
import rest.o.gram.client.RestogramClient;
import rest.o.gram.common.Defs;
import rest.o.gram.view.VenueViewAdapter;
import rest.o.gram.entities.RestogramVenue;
import rest.o.gram.tasks.results.GetInfoResult;
import rest.o.gram.tasks.results.GetNearbyResult;
import rest.o.gram.tasks.results.GetPhotosResult;
import rest.o.gram.tasks.ITaskObserver;

import static rest.o.gram.location.Utils.distance;

/**
 * Created with IntelliJ IDEA.
 * User: Roi
 * Date: 16/04/13
 */
public class NearbyActivity extends Activity implements ITaskObserver {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.nearby);
        diagManager =  new DialogManager();

        // Init venue list view
        ListView lv = (ListView)findViewById(R.id.lvVenues);
        viewAdapter = new VenueViewAdapter(this);
        lv.setAdapter(viewAdapter);

        // Get location parameters
        try {
            Intent intent = getIntent();
            latitude = intent.getDoubleExtra("latitude", 0.0);
            longitude = intent.getDoubleExtra("longitude", 0.0);
        }
        catch(Exception e) {
            // TODO: implementation
            return;
        }

        final NearbyActivity act = this;
        final  Button fbLogin = (Button)findViewById(R.id.fbLogin);
        fbLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                diagManager.showLoginDialog(act, new LoginListener() {
                    @Override
                    public void onSuccess() {
                        resetButtons();
                        if (RestogramClient.getInstance().isDebuggable())
                            Log.d("REST-O-GRAM", "login successfull!");
                     }

                    @Override
                    public void onCancel() {
                        if (RestogramClient.getInstance().isDebuggable())
                            Log.d("REST-O-GRAM", "login cancelled");
                    }

                    @Override
                    public void onError(LeanError error) {
                        final String errorMsg = error.getErrorMessage();
                        if (RestogramClient.getInstance().isDebuggable())
                            Log.d("REST-O-GRAM", "login - Error: " +  error.getErrorType().toString() +  " Error desc: " + errorMsg);
                        if (errorMsg != null && !errorMsg.isEmpty())
                            Toast.makeText(NearbyActivity.this, error.getErrorMessage(), Toast.LENGTH_LONG).show();
                    }
                });
            }
        });
        final  Button fbLogout = (Button)findViewById(R.id.fbLogout);
        fbLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LeanAccount.logoutInBackground(new NetworkCallback<Boolean>() {
                    @Override
                    public void onResult(Boolean... result) {
                        resetButtons();
                        Toast.makeText(act, "Successfully logged out.", Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onFailure(LeanError error) {
                        final String errorMsg = error.getErrorMessage();
                        if (RestogramClient.getInstance().isDebuggable())
                            Log.d("REST-O-GRAM", "logout - Error: " + error.getErrorType() + "Error desc: " + errorMsg);
                        if (errorMsg != null && !errorMsg.isEmpty())
                            Toast.makeText(act, error.getErrorMessage(), Toast.LENGTH_LONG).show();
                    }
                });
            }
        });
        resetButtons();

        // Send get nearby request
        RestogramClient.getInstance().getNearby(latitude, longitude, Defs.Location.DEFAULT_NEARBY_RADIUS, this);
    }

    private void resetButtons()
    {
        final  Button fbLogin = (Button)findViewById(R.id.fbLogin);
        final  Button fbLogout = (Button)findViewById(R.id.fbLogout);
        final TextView fbNick = (TextView)findViewById(R.id.fbNick);

       boolean isLoggedIn = LeanAccount.isUserLoggedIn();
       fbLogin.setClickable(!isLoggedIn);
       fbLogout.setClickable(isLoggedIn);
        if (!LeanAccount.isUserLoggedIn())
            fbNick.setText("");
        else
        {
            try
            {
                fbNick.setText(LeanAccount.getAccountData().getNickName());
                if (RestogramClient.getInstance().isDebuggable())
                    Log.d("REST-O-GRAM", "FB nick = " + LeanAccount.getAccountData().getNickName());
            } catch (LeanException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
        }
    }

    @Override
    public void onFinished(GetNearbyResult result) {
        if(result.getVenues() == null)
            return;

        addVenues(result.getVenues());
    }

    @Override
    public void onFinished(GetInfoResult result) {
        // TODO: implementation
    }

    @Override
    public void onFinished(GetPhotosResult result) {
        // TODO: implementation
    }

    private void addVenues(RestogramVenue[] venues) {
        // Traverse given venues
        for(final RestogramVenue venue : venues) {

            // TODO: get photo
            // Send get info request
            //RestogramClient.getInstance().getInfo(venue.getId(), this);

            // Calculate distance
            double d = distance(latitude, longitude, venue.getLatitude(), venue.getLongitude());
            if(d != 0.0)
                venue.setDistance(d);

            // Add venue
            viewAdapter.addVenue(venue);
        }

        viewAdapter.refresh();
    }

    public void onVenueClicked(RestogramVenue venue) {
        // Switch to "VenueActivity" with parameter "venue"
        Intent intent = new Intent(this, VenueActivity.class);
        intent.putExtra("venue", venue);
        startActivityForResult(intent, Defs.RequestCodes.RC_VENUE);
    }

    private double latitude; // Latitude
    private double longitude; // Longitude
    private VenueViewAdapter viewAdapter; // View adapter
    private DialogManager diagManager;
}
