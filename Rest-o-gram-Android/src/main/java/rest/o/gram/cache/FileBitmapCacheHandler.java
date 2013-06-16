package rest.o.gram.cache;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import rest.o.gram.common.Defs;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

/**
 * Created with IntelliJ IDEA.
 * User: Roi
 * Date: 6/13/13
 */
public class FileBitmapCacheHandler extends AbstractBitmapCacheHandler {
    /**
     * Ctor
     */
    public FileBitmapCacheHandler(Context context) {
        this.context = context;
    }

    @Override
    protected boolean doSave(String id, Bitmap bitmap) {
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
    protected Bitmap doLoad(String id) {
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
    protected boolean doClear() {
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
            String path = context.getExternalCacheDir().getAbsolutePath() +
                    Defs.Data.BITMAP_CACHE_PREFIX;
            return saveBitmap(bitmap, new File(path), id + ".png");
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
        String path = context.getFilesDir().getAbsolutePath() +
                Defs.Data.BITMAP_CACHE_PREFIX;
        return saveBitmap(bitmap, new File(path), id + ".png");
    }

    /**
     * Attempts to load bitmap according to its id from external directory
     * Returns bitmap if successful, null otherwise
     */
    private Bitmap loadExternal(String id) {
        String state = Environment.getExternalStorageState();

        if(Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) { // Can read
            String path = context.getExternalCacheDir().getAbsolutePath() +
                    Defs.Data.BITMAP_CACHE_PREFIX + id  + ".png";
            return loadBitmap(new File(path));
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
        String path = context.getFilesDir().getAbsolutePath() +
                Defs.Data.BITMAP_CACHE_PREFIX + id  + ".png";
        return loadBitmap(new File(path));
    }

    /**
     * Clears all external data
     */
    private void clearExternal() {
        String state = Environment.getExternalStorageState();

        if(Environment.MEDIA_MOUNTED.equals(state)) {  // Can write
            String path = context.getExternalCacheDir().getAbsolutePath() + Defs.Data.BITMAP_CACHE_PREFIX;
            clearDirectory(new File(path));
        }
    }

    /**
     * Clears all internal data
     */
    private void clearInternal() {
        String path = context.getFilesDir().getAbsolutePath() + Defs.Data.BITMAP_CACHE_PREFIX;
        clearDirectory(new File(path));
    }

    /**
     * Attempts to save bitmap to file
     * Returns true if successful, false otherwise
     */
    private boolean saveBitmap(Bitmap bitmap, File directory, String filename) {
        try {
            if(!directory.exists())
                directory.mkdirs();

            File file = new File(directory, filename);
            FileOutputStream out = new FileOutputStream(file);
            return saveBitmap(bitmap, out);
        }
        catch(Exception | Error e) {
            return false;
        }
    }

    /**
     * Attempts to save bitmap to file output stream
     * Returns true if successful, false otherwise
     */
    private boolean saveBitmap(Bitmap bitmap, FileOutputStream out) {
        try {
            bitmap.compress(Bitmap.CompressFormat.PNG, 90, out);
            out.flush();
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
            return loadBitmap(in);
        }
        catch(Exception | Error e) {
            return null;
        }
    }

    /**
     * Attempts to load bitmap from file input stream
     * Returns bitmap if successful, null otherwise
     */
    private Bitmap loadBitmap(FileInputStream in) {
        try {
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
