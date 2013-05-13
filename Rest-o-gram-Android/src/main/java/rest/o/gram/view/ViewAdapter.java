package rest.o.gram.view;

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
public class ViewAdapter extends BaseAdapter implements IViewAdapter {
    /**
     * Ctor
     */
    public ViewAdapter() {
        // Create view list
        viewList = new LinkedList<View>();
    }

    /**
     * Ctor
     */
    public ViewAdapter(int width, int height) {
        this();
        this.width = width;
        this.height = height;
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

    @Override
    public void addView(View view) {
        viewList.add(view);
    }

    @Override
    public void refresh() {
        notifyDataSetChanged();
    }

    @Override
    public void clear() {
        viewList.clear();
    }

    @Override
    public int width() {
        return width;
    }

    @Override
    public int height() {
        return height;
    }

    private List<View> viewList; // View list
    private int width = -1; // View width
    private int height = -1; // View height
}
