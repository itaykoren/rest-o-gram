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
public class BitmapCache implements IBitmapCache {
    /**
     * Ctor
     */
    public BitmapCache(Context context) {
        handler = new LruBitmapCacheHandler();
        handler.setNext(new FileBitmapCacheHandler(context));
    }

    @Override
    public boolean save(String id, Bitmap bitmap) {
        try {
            return handler.save(id, bitmap);
        }
        catch(Exception | Error e) {
            return false;
        }
    }

    @Override
    public Bitmap load(String id) {
        try {
            return handler.load(id);
        }
        catch(Exception | Error e) {
            return null;
        }
    }

    @Override
    public boolean clear() {
        try {
            return handler.clear();
        }
        catch(Exception | Error e) {
            return false;
        }
    }

    private IBitmapCacheHandler handler; // Handler object
}
