package rest.o.gram.view;

import android.app.Activity;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
public class PhotoInfoView {
    /**
     * Ctor
     */
    public PhotoInfoView(Activity context, RestogramPhoto photo) {
        initialize(context, photo);
    }

    public boolean isOpen() {
        return isOpen;
    }

    /**
     * Opens this view
     */
    public boolean open() {
        if(isOpen)
            return false;

        if(popupWindow == null)
            return false;

        // Open
        popupWindow.showAtLocation(layout, Gravity.CENTER, 0, 0);
        isOpen = true;
        return true;
    }

    /**
     * Closes this view
     */
    public boolean close() {
        if(!isOpen)
            return false;

        if(popupWindow == null)
            return false;

        // Close
        popupWindow.dismiss();
        isOpen = false;
        return true;
    }

    /**
     * Initializes this view
     */
    private void initialize(Activity context, RestogramPhoto photo) {
        try {
            LayoutInflater inflater = (LayoutInflater)context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            layout = inflater.inflate(R.layout.photo_info, (ViewGroup)context.findViewById(R.id.popup_element));
            popupWindow = new PopupWindow(layout, 350, 350, true);

            // Update UI
            Utils.updateTextView((TextView)layout.findViewById(R.id.tvLikes), String.valueOf(photo.getLikes()) + " likes");
            Utils.updateTextView((TextView)layout.findViewById(R.id.tvCreationTime), Utils.convertDate(photo.getCreatedTime()));
            Utils.updateTextView((TextView)layout.findViewById(R.id.tvUsername), photo.getUser());
            Utils.updateTextView((TextView)layout.findViewById(R.id.tvTitle), photo.getCaption());

            Button btnClosePopup = (Button)layout.findViewById(R.id.btn_close_popup);
            btnClosePopup.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    close();
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean isOpen = false;
    private PopupWindow popupWindow;
    private View layout;
}
