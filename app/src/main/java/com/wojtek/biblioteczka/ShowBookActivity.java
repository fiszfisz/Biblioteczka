package com.wojtek.biblioteczka;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class ShowBookActivity extends AppCompatActivity {

    private Book book;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_book);

        Intent intent = getIntent();

        try {
            book = intent.getParcelableExtra("Book");
        } catch (Exception e) {
            // TODO what to do if there is no parcel
        }

        if (book != null) {
            setContents(book);
        }
    }

    private void setContents(Book book) {
        TextView authorTextView = findViewById(R.id.authorTextView);
        TextView titleTextView = findViewById(R.id.titleTextView);
        TextView publisherTextView = findViewById(R.id.publisherTextView);
        TextView cityTextView = findViewById(R.id.cityTextView);
        TextView yearTextView = findViewById(R.id.yearTextView);
        ImageView coverImageView = findViewById(R.id.coverImageView);

        authorTextView.setText(book.author);
        titleTextView.setText(book.title);
        publisherTextView.setText(book.publisher);
        cityTextView.setText(book.city);
        yearTextView.setText(book.year);

        if (!book.cover.isEmpty()) {
            BitmapDownloaderTask task = new BitmapDownloaderTask(coverImageView, book.cover);
            task.execute();
        } else {
            coverImageView.setVisibility(View.GONE);
        }
    }
}
