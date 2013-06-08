package rest.o.gram.view;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.util.Pair;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import rest.o.gram.activities.PhotoActivity;
import rest.o.gram.common.Defs;
import rest.o.gram.common.Utils;
import rest.o.gram.entities.RestogramPhoto;

import java.util.LinkedList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Roi
 * Date: 20/04/13
 */
public class PhotoViewAdapter extends BaseAdapter implements IPhotoViewAdapter {
    /**
     * Ctor
     */
    public PhotoViewAdapter(Activity context) {
        // Set context
        this.context = context;

        // Create photo list
        photoList = new LinkedList<>();
    }

    @Override
    public int getCount() {
        return photoList.size();
    }

    @Override
    public Object getItem(int i) {
        if(i < 0 || i >= photoList.size())
            return null;

        return photoList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if(i < 0 || i >= photoList.size())
            return null;

        final ImageView imageView;

        if (view == null) { // View is not recycled
            imageView = new SquareImageView(context);
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        }
        else { // View is recycled
            imageView = (ImageView)view;
        }

        final Pair<String, Bitmap> item = photoList.get(i);
        imageView.setImageBitmap(item.second);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onPhotoClicked(item.first, item.second);
            }
        });

        return imageView;
    }

    @Override
    public void addPhoto(String photoId, Bitmap bitmap) {
        Pair<String, Bitmap> pair = new Pair<>(photoId, bitmap);
        photoList.add(pair);
    }

    @Override
    public void refresh() {
        notifyDataSetChanged();
    }

    @Override
    public void clear() {
        photoList.clear();
    }

    private void onPhotoClicked(String photoId, Bitmap bitmap) {
        // Switch to "PhotoActivity" with parameters "photo" & "thumbnail_bitmap"
        Intent intent = new Intent(context, PhotoActivity.class);
        intent.putExtra("photo", photoId);
        intent.putExtra("thumbnail_bitmap", bitmap);
        Utils.changeActivity(context, intent, Defs.RequestCodes.RC_PHOTO, false);
    }

    private Activity context; // Context
    private List<Pair<String,Bitmap>> photoList; // Photos list
}
