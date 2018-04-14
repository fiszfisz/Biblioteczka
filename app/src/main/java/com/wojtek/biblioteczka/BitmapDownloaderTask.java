package com.wojtek.biblioteczka;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.widget.ImageView;

import java.lang.ref.WeakReference;

public class BitmapDownloaderTask extends AsyncTask<String, Void, Bitmap> {
    private final String url;
    private final WeakReference<ImageView> imageViewWeakReference;

    public BitmapDownloaderTask(ImageView imageView, String address) {
        url = address;
        imageViewWeakReference = new WeakReference<>(imageView);
    }

    @Override
    protected Bitmap doInBackground(String... params) {
        return ImageTools.downloadBitmap(url);
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        if (isCancelled()) {
            return;
        }

        if (imageViewWeakReference != null) {
            ImageView imageView = imageViewWeakReference.get();
            if (imageView != null) {
                imageView.setImageBitmap(bitmap);
            }
        }
    }
}
