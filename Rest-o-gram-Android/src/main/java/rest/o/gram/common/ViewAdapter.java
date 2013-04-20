package rest.o.gram.common;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.LinkedList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Roi
 * Date: 20/04/13
 */
public class ViewAdapter extends BaseAdapter {
    /**
     * Ctor
     * */
    public ViewAdapter() {
        // Create view list
        viewList = new LinkedList<View>();
    }

    @Override
    public int getCount() {
        return viewList.size();
    }

    @Override
    public Object getItem(int i) {
        if(i < 0 || i >= viewList.size())
            return null;

        return viewList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if(i < 0 || i >= viewList.size())
            return null;

        return viewList.get(i);
    }

    /**
     * Adds view
     * */
    public void addView(View view) {
        viewList.add(view);
    }

    /**
     * Refreshes this adapter
     */
    public void refresh() {
        notifyDataSetChanged();
    }

    private List<View> viewList; // View list
}
