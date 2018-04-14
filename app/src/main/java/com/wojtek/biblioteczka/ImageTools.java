package com.wojtek.biblioteczka;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.net.URL;
import java.net.URLConnection;

public class ImageTools {

    static Bitmap downloadBitmap(String address) {
        Bitmap bmp;

        try {
            URL url = new URL(address);
            URLConnection connection = url.openConnection();
            bmp = BitmapFactory.decodeStream(connection.getInputStream());
        } catch(Exception e) {
            bmp = null;
        }

        return bmp;
    }
}
