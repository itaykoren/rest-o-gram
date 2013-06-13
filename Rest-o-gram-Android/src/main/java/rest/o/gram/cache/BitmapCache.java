package rest.o.gram.cache;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

/**
 * Created with IntelliJ IDEA.
 * User: Roi
 * Date: 6/13/13
 */
public class BitmapCache implements IBitmapCache {
    /**
     * Ctor
     */
    public BitmapCache(Context context) {
        this.context = context;
    }

    @Override
    public boolean save(String id, Bitmap bitmap) {
        try {
            // Try to save to external directory
            if(saveExternal(id, bitmap))
                return true;

            // Try to save to internal directory
            if(saveInternal(id, bitmap))
                return true;

            return false;
        }
        catch(Exception | Error e) {
            return false;
        }
    }

    @Override
    public Bitmap load(String id) {
        try {
            Bitmap bitmap;

            // Try to load from external directory
            bitmap = loadExternal(id);
            if(bitmap != null)
                return bitmap;

            // Try to load from internal directory
            bitmap = loadInternal(id);
            if(bitmap != null)
                return bitmap;

            return null;
        }
        catch(Exception | Error e) {
            return null;
        }
    }

    @Override
    public boolean clear() {
        try {
            // Clear all data
            clearExternal();
            clearInternal();
            return true;
        }
        catch(Exception | Error e) {
            return false;
        }
    }

    /**
     * Attempts to save given bitmap with given id to external directory
     * Returns true if successful, false otherwise
     */
    boolean saveExternal(String id, Bitmap bitmap) {
        String state = Environment.getExternalStorageState();

        if(Environment.MEDIA_MOUNTED.equals(state)) {  // Can write
            File file = new File(context.getExternalCacheDir(), id);
            return saveBitmap(bitmap, file);
        }
        else { // Cannot write
            return false;
        }
    }

    /**
     * Attempts to save given bitmap with given id to internal directory
     * Returns true if successful, false otherwise
     */
    boolean saveInternal(String id, Bitmap bitmap) {
        // TODO
        return false;
    }

    /**
     * Attempts to load bitmap according to its id from external directory
     * Returns bitmap if successful, null otherwise
     */
    private Bitmap loadExternal(String id) {
        String state = Environment.getExternalStorageState();

        if(Environment.MEDIA_MOUNTED.equals(state) ||
           Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) { // Can read
            File file = new File(context.getExternalCacheDir(), id);
            return loadBitmap(file);
        }
        else { // Cannot read
            return null;
        }
    }

    /**
     * Attempts to load bitmap according to its id from internal directory
     * Returns bitmap if successful, null otherwise
     */
    private Bitmap loadInternal(String id) {
        // TODO
        return null;
    }

    /**
     * Clears all external data
     */
    private void clearExternal() {
        String state = Environment.getExternalStorageState();

        if(Environment.MEDIA_MOUNTED.equals(state)) {  // Can write
            clearDirectory(context.getExternalCacheDir());
        }
    }

    /**
     * Clears all internal data
     */
    private void clearInternal() {
        // TODO
    }

    /**
     * Attempts to save bitmap to file
     * Returns true if successful, false otherwise
     */
    private boolean saveBitmap(Bitmap bitmap, File file) {
        try {
            FileOutputStream out = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 90, out);
            out.close();
            return true;
        }
        catch(Exception | Error e) {
            return false;
        }
    }

    /**
     * Attempts to load bitmap from file
     * Returns bitmap if successful, null otherwise
     */
    private Bitmap loadBitmap(File file) {
        try {
            FileInputStream in = new FileInputStream(file);
            Bitmap bitmap = BitmapFactory.decodeStream(in);
            in.close();
            return bitmap;
        }
        catch(Exception | Error e) {
            return null;
        }
    }

    /**
     * Clears all files in given directory
     */
    private void clearDirectory(File directory) {
        if(!directory.exists() && !directory.isDirectory())
            return;

        File[] files = directory.listFiles();
        for(File f : files) {
            f.delete();
        }
    }

    private Context context; // Context object
}
