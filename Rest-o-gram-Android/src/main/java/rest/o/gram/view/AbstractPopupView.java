package rest.o.gram.view;

import android.view.Gravity;
import android.view.View;
import android.widget.PopupWindow;

/**
 * Created with IntelliJ IDEA.
 * User: Hen
 * Date: 6/8/13
 */
public class AbstractPopupView implements IPopupView {
    @Override
    public boolean isOpen() {
        return isOpen;
    }

    @Override
    public boolean open() {
        if(isOpen)
            return false;

        if(impl == null)
            return false;

        // Open
        impl.showAtLocation(layout, Gravity.CENTER, 0, 0);
        isOpen = true;
        return true;
    }

    @Override
    public boolean close() {
        if(!isOpen)
            return false;

        if(impl == null)
            return false;

        // Close
        impl.dismiss();
        isOpen = false;
        return true;
    }

    protected PopupWindow impl; // Popup window
    protected View layout; // Layout
    protected boolean isOpen = false; // Is open flag
}
