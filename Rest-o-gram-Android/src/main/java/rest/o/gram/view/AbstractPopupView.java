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
        try {
            if(isOpen)
                return false;

            if(impl == null)
                return false;

            // Open
            impl.showAtLocation(layout, Gravity.CENTER, 0, 0);
            isOpen = true;
            return true;
        }
        catch(Exception e) {
            return false;
        }
    }

    @Override
    public boolean close() {
        try {
            if(!isOpen)
                return false;

            if(impl == null)
                return false;

            // Close
            impl.dismiss();
            isOpen = false;
            return true;
        }
        catch(Exception e) {
            return false;
        }
    }

    protected PopupWindow impl; // Popup window
    protected View layout; // Layout
    protected boolean isOpen = false; // Is open flag
}
