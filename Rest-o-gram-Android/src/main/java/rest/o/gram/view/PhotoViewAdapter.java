package rest.o.gram.view;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.util.Pair;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import rest.o.gram.activities.PhotoActivity;
import rest.o.gram.cache.IBitmapCache;
import rest.o.gram.client.RestogramClient;
import rest.o.gram.common.Defs;
import rest.o.gram.common.Utils;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

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

        // Create photo set
        photoSet = new TreeSet<>();
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
            imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
        }
        else { // View is recycled
            imageView = (ImageView)view;
        }

        final Pair<String, String> item = photoList.get(i);
        final IBitmapCache cache = RestogramClient.getInstance().getBitmapCache();
        final Bitmap bitmap = cache.load(item.second);

        imageView.setImageBitmap(bitmap);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onPhotoClicked(item.first, item.second);
            }
        });

        return imageView;
    }

    @Override
    public void addPhoto(String photoId, String bitmapId) {
        if(photoSet.contains(photoId))
            return;

        photoList.add(new Pair<>(photoId, bitmapId));
        photoSet.add(photoId);
    }

    @Override
    public void refresh() {
        notifyDataSetChanged();
    }

    @Override
    public void clear() {
        photoList.clear();
        photoSet.clear();
    }

    private void onPhotoClicked(String photoId, String bitmapId) {
        // Switch to "PhotoActivity" with parameters "photo" & "thumbnail_bitmap"
        Intent intent = new Intent(context, PhotoActivity.class);
        intent.putExtra("photo", photoId);
        intent.putExtra("thumbnail_bitmap", bitmapId);
        Utils.changeActivity(context, intent, Defs.RequestCodes.RC_PHOTO, false);
    }

    private Activity context; // Context
    private List<Pair<String,String>> photoList; // Photos list
    private Set<String> photoSet; // Photos set
}
