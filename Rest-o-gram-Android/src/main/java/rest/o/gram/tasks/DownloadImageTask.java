package rest.o.gram.tasks;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

import java.io.InputStream;

/**
 * Created with IntelliJ IDEA.
 * User: Roi
 * Date: 4/5/13
 */
public class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
    public DownloadImageTask(ImageView imageView) {
        this.imageView = imageView;
        this.width = -1;
        this.height = -1;
    }

    public DownloadImageTask(ImageView imageView, int width, int height) {
        this.imageView = imageView;
        this.width = Math.max(width, 1);
        this.height = Math.max(height, 1);
    }

    protected Bitmap doInBackground(String... urls) {
        String url = urls[0];
        Bitmap bitmap = null;
        try {
            bitmap = downloadBitmap(url);
        }
        catch (Exception e) {
            Log.e("REST-O-GRAM", "DOWNLOAD IMAGE - FIRST ATTEMPT FAILED");
            Log.e("REST-O-GRAM", "image url: " + url);
            e.printStackTrace();
            try {
                bitmap = downloadBitmap(url);
            }
            catch (Exception e2) {
                Log.e("REST-O-GRAM", "DOWNLOAD IMAGE - SECOND ATTEMPT FAILED");
                Log.e("REST-O-GRAM", "image url: " + url);
                e2.printStackTrace();
            }
        }
        return bitmap;
    }

    protected void onPostExecute(Bitmap result) {
        imageView.setImageBitmap(result);
    }

    private Bitmap downloadBitmap(String url) {
        Bitmap bitmap;

        try {
            InputStream in = new java.net.URL(url).openStream();

            // Width & height undefined - download normally
            if(width == -1 && height == -1) {
                in = new java.net.URL(url).openStream();
                bitmap = BitmapFactory.decodeStream(in);
                in.close();
                return bitmap;
            }

            // Get image bounds
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(in, null, options);
            in.close();

            // Calculate scale
            Boolean scaleByHeight = Math.abs(options.outHeight - height) >= Math.abs(options.outWidth - width);

            if(options.outHeight * options.outWidth * 2 >= 200*200*2) {
                // Load, scaling to smallest power of 2 that'll get it <= desired dimensions
                double sampleSize = scaleByHeight
                        ? options.outHeight / height
                        : options.outWidth / width;
                options.inSampleSize =
                        (int)Math.pow(2d, Math.floor(
                                Math.log(sampleSize)/Math.log(2d)));
            }

            // Download image
            options.inJustDecodeBounds = false;
            in = new java.net.URL(url).openStream();
            bitmap = BitmapFactory.decodeStream(in, null, options);
            in.close();
        }
        catch(Exception e) {
            return null;
        }

        return bitmap;
    }

    private ImageView imageView; // Target image view
    private int width; // Target image width
    private int height; // Target image height
}
