package rest.o.gram.view;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import rest.o.gram.R;

/**
 * Created with IntelliJ IDEA.
 * User: Hen
 * Date: 6/8/13
 */
public class GenericPopupView extends AbstractPopupView {
    /**
     * Ctor
     */
    public GenericPopupView(Activity context, int layout, int id, int width, int height) {
        initialize(context, layout, id, width, height);
    }

    /**
     * Initializes this view
     */
    private void initialize(Activity context, int layout, int id, int width, int height) {
        try {
            LayoutInflater inflater = (LayoutInflater)context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            this.layout = inflater.inflate(layout, (ViewGroup)context.findViewById(id));
            impl = new PopupWindow(this.layout, width, height, true);
            impl.setOutsideTouchable(true);
            impl.setBackgroundDrawable(new BitmapDrawable());
            impl.setOnDismissListener(new PopupWindow.OnDismissListener() {
                @Override
                public void onDismiss() {
                    isOpen = false;
                }
            });
            impl.setAnimationStyle(R.style.PopupWindowAnimation);
        }
        catch(Exception e) {
            // Empty
        }
    }
}
