package rest.o.gram.view;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.TextView;
import rest.o.gram.R;
import rest.o.gram.common.Utils;
import rest.o.gram.entities.RestogramPhoto;

/**
 * Created with IntelliJ IDEA.
 * User: Hen
 * Date: 6/2/13
 */
public class PhotoInfoView extends AbstractPopupView {
    /**
     * Ctor
     */
    public PhotoInfoView(Activity context, RestogramPhoto photo) {
        initialize(context, photo);
    }

    /**
     * Initializes this view
     */
    private void initialize(Activity context, RestogramPhoto photo) {
        try {
            LayoutInflater inflater = (LayoutInflater)context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            layout = inflater.inflate(R.layout.photo_info, (ViewGroup)context.findViewById(R.id.popup_element));
            impl = new PopupWindow(layout, 400, 400, true);
            impl.setOutsideTouchable(true);
            impl.setBackgroundDrawable(new BitmapDrawable());
            impl.setOnDismissListener(new PopupWindow.OnDismissListener() {
                @Override
                public void onDismiss() {
                    isOpen = false;
                }
            });
            impl.setAnimationStyle(R.style.PopupWindowAnimation);

            // Update UI
            Utils.updateTextView((TextView)layout.findViewById(R.id.tvLikes), String.valueOf(photo.getLikes()) + " likes");
            Utils.updateTextView((TextView)layout.findViewById(R.id.tvCreationTime), Utils.convertDate(photo.getCreatedTime()));
            Utils.updateTextView((TextView)layout.findViewById(R.id.tvUsername), photo.getUser());
            Utils.updateTextView((TextView)layout.findViewById(R.id.tvTitle), photo.getCaption());
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
