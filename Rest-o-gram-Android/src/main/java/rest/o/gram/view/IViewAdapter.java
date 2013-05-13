package rest.o.gram.view;

import android.view.View;

/**
 * Created with IntelliJ IDEA.
 * User: Roi
 * Date: 13/05/13
 */
public interface IViewAdapter {
    /**
     * Adds view
     * */
    void addView(View view);

    /**
     * Refreshes this adapter
     */
    void refresh();

    /**
     * Clears all views
     */
    void clear();
}
