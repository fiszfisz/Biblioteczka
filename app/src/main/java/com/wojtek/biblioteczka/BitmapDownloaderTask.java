package com.wojtek.biblioteczka;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
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
    protected void onPreExecute() {
        ImageView imageView = imageViewWeakReference.get();
        Drawable load = imageView.getContext().getResources().getDrawable(R.drawable.ic_load);
        imageView.setImageDrawable(load);
    }

    @Override
    protected Bitmap doInBackground(String... params) {
        return ImageTools.downloadBitmap(url);
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        if (isCancelled()) {
            bitmap = null;
        }

        ImageView imageView = imageViewWeakReference.get();

        if (imageView != null) {
            if (bitmap != null) {
                imageView.setImageBitmap(bitmap);
            } else {
                Drawable placeholder = imageView.getContext().getResources().getDrawable(R.drawable.ic_book);
                imageView.setImageDrawable(placeholder);
            }
        }
    }
}
