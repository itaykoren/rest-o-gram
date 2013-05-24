package rest.o.gram.activities;

import android.os.Bundle;
import rest.o.gram.R;


/**
 * Created with IntelliJ IDEA.
 * User: Hen
 * Date: 24/05/13
 */
public class PersonalActivity extends RestogramActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.personal);

        // TODO:
        // IDataHistoryManager dataHistoryManager = RestogramClient.getInstance().getDataHistoryManager();
        // HistoryManager.loadVenues(Defs.Data.SortOrder.SortOrderLIFO)
    }

    @Override
    protected void onPause() {
        super.onPause();

        // TODO
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // TODO
    }

}
