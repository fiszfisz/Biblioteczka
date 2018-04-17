package com.wojtek.biblioteczka;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.WeakHashMap;

public class ImageTools {

    static WeakHashMap<String, Bitmap> bitmapCache = new WeakHashMap<>();

    static Bitmap downloadBitmap(String address) {
        Bitmap bmp;

        synchronized (bitmapCache) {
            if (bitmapCache.containsKey(address)) {
                bmp = bitmapCache.get(address);
                return bmp;
            }
        }

        try {
            URL url = new URL(address);
            URLConnection connection = url.openConnection();
            InputStream inputStream = connection.getInputStream();
            bmp = BitmapFactory.decodeStream(inputStream);
            bitmapCache.put(address, bmp);
        } catch(Exception e) {
            bmp = null;
        }

        return bmp;
    }
}
