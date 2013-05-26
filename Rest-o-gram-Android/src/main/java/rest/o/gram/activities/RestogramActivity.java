package rest.o.gram.activities;

import android.app.Activity;
import android.os.Bundle;
import rest.o.gram.activities.helpers.FavoriteHelper;
import rest.o.gram.activities.helpers.LoginHelper;

/**
 * Created with IntelliJ IDEA.
 * User: Hen
 * Date: 24/05/13
 */
public class RestogramActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize login helper
        loginHelper = new LoginHelper(this);

        // Initialize favorite helper
        favoriteHelper = new FavoriteHelper();
    }

    /**
     * Called after user has logged in
     */
    public void onUserLoggedIn() {
        favoriteHelper.refresh();
    }

    /**
     * Called after user has logged out
     */
    public void onUserLoggedOut() {
        // TODO
    }

    protected LoginHelper loginHelper; // Login helper
    protected FavoriteHelper favoriteHelper; // Favorite helper
}
