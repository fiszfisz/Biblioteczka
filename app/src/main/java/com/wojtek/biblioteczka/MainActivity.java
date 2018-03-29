package com.wojtek.biblioteczka;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    protected BookArrayAdapter adapter;
    protected ArrayList<Book> adapterData;

    private class BookArrayAdapter extends ArrayAdapter {
        public BookArrayAdapter(Context context, ArrayList<Book> array) {
            super(context, 0, array);
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            final Book book = (Book)getItem(position);

            final View view = inflater.inflate(R.layout.activity_main_item, parent, false);

            TextView titleTextView = view.findViewById(R.id.titleTextView);
            TextView authorTextView = view.findViewById(R.id.authorTextView);

            titleTextView.setText(book.title);
            authorTextView.setText(book.author);

            Thread loader = new Thread() {
                @Override
                public void run() {
                    try {
                        ImageView imageView = view.findViewById(R.id.imageView);
                        URL url = new URL(book.cover);
                        URLConnection connection = url.openConnection();
                        Bitmap bmp = BitmapFactory.decodeStream(connection.getInputStream());
                        imageView.setImageBitmap(bmp);
                    } catch (Exception e) {
                        // Image stays the same
                    }
                }
            };

            try {
                loader.start();
                loader.join();
            } catch (Exception e) {

            }

            return view;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        adapterData = new ArrayList<>();
        adapter = new BookArrayAdapter(this, adapterData);
        ListView listView = findViewById(R.id.listView);
        listView.setAdapter(adapter);

        Book book;

        book = new Book();
        book.author = "Jarosław Grzędowicz";
        book.title = "Hel 3";
        book.cover = "https://s.znak.com.pl/files/covers/card/b3/T255415.jpg";
        adapterData.add(book);

        book = new Book();
        book.author = "Philip K. Dick";
        book.title = "Raport Mniejszości";
        book.cover = "http://ecsmedia.pl/c/raport-mniejszosci-w-iext45139908.jpg";
        adapterData.add(book);

        book = new Book();
        book.author = "Andrzej Sapkowski";
        book.title = "Miecz Przeznaczenia";
        book.cover = "https://www.granice.pl/sys6/pliki/okladka_k/c4ad2da802a2eb57b140b2dfa9d20cbc.jpeg";
        adapterData.add(book);

        book = new Book();
        book.author = "Dmitry Glukhovsky";
        book.title = "Metro 2033";
        book.cover = "http://menmagazine.pl/wp-content/uploads/2015/09/metro2033_cover_front_480x720.jpg";
        adapterData.add(book);

        book = new Book();
        book.author = "Dmitry Glukhovsky";
        book.title = "Metro 2034";
        book.cover = "https://7.allegroimg.com/s512/03e38d/b5ea9ffb4a75b84a14e684eeea67";
        adapterData.add(book);

        book = new Book();
        book.author = "Jarosław Grzędowicz";
        book.title = "Pan Lodowego Ogrodu TOM 4";
        book.cover = "http://ecsmedia.pl/c/pan-lodowego-ogrodu-tom-4-b-iext43252937.jpg";
        adapterData.add(book);

        book = new Book();
        book.author = "Chips Hardy";
        book.title = "Poluj, bo upolują ciebie";
        book.cover = "http://www.wydawnictwoamber.pl/files/1652309683/poluj-bo-upoluja-ciebie_38.jpg";
        adapterData.add(book);

        adapter.notifyDataSetChanged();
    }
}
