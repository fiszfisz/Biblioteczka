package com.wojtek.biblioteczka;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    static final int ADD_BOOK_REQUEST = 1;
    static final int EDIT_BOOK_REQUEST = 2;

    private Bookcase bookcase;

    private BookArrayAdapter adapter;
    private ArrayList<Book> adapterData;

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
            ImageView imageView = view.findViewById(R.id.imageView);

            titleTextView.setText(book.title);
            authorTextView.setText(book.author);

            BitmapDownloaderTask task = new BitmapDownloaderTask(imageView, book.cover);
            task.execute();

            return view;
        }
    }

    private class OnItemClickListener implements AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Book book = (Book)parent.getItemAtPosition(position);

            Intent intent = new Intent(view.getContext(), EditBookActivity.class);
            intent.putExtra("Book", book);
            startActivityForResult(intent, EDIT_BOOK_REQUEST);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bookcase = new Bookcase(getFilesDir());

        adapterData = bookcase.getBooks();
        adapter = new BookArrayAdapter(this, adapterData);
        ListView listView = findViewById(R.id.listView);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new OnItemClickListener());

        Book book;

        book = new Book();
        book.author = "Jarosław Grzędowicz";
        book.title = "Hel 3";
        book.cover = "http://ecsmedia.pl/c/hel-3-w-iext47374727.jpg";
        bookcase.add(book);

        book = new Book();
        book.author = "Philip K. Dick";
        book.title = "Raport Mniejszości";
        book.cover = "http://ecsmedia.pl/c/raport-mniejszosci-w-iext45139908.jpg";
        bookcase.add(book);

        book = new Book();
        book.author = "Andrzej Sapkowski";
        book.title = "Miecz Przeznaczenia";
        book.cover = "https://www.granice.pl/sys6/pliki/okladka_k/c4ad2da802a2eb57b140b2dfa9d20cbc.jpeg";
        bookcase.add(book);

        book = new Book();
        book.author = "Dmitry Glukhovsky";
        book.title = "Metro 2033";
        book.cover = "http://menmagazine.pl/wp-content/uploads/2015/09/metro2033_cover_front_480x720.jpg";
        bookcase.add(book);

        book = new Book();
        book.author = "Dmitry Glukhovsky";
        book.title = "Metro 2034";
        book.cover = "https://7.allegroimg.com/s512/03e38d/b5ea9ffb4a75b84a14e684eeea67";
        bookcase.add(book);

        book = new Book();
        book.author = "Jarosław Grzędowicz";
        book.title = "Pan Lodowego Ogrodu TOM 4";
        book.cover = "http://ecsmedia.pl/c/pan-lodowego-ogrodu-tom-4-w-iext43252937.jpg";
        bookcase.add(book);

        book = new Book();
        book.author = "Chips Hardy";
        book.title = "Poluj, bo upolują ciebie";
        book.cover = "http://ecsmedia.pl/c/poluj-bo-upoluja-ciebie-w-iext38782418.jpg";
        bookcase.add(book);

        adapterData = bookcase.getBooks();
        adapter.notifyDataSetChanged();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case ADD_BOOK_REQUEST:
                break;
            case EDIT_BOOK_REQUEST:
                if (resultCode == Activity.RESULT_OK) {
                    Book book = data.getParcelableExtra("Book");
                    bookcase.set(book);

                    adapterData = bookcase.getBooks();
                    adapter.notifyDataSetChanged();
                }
                break;
            default:
                break;
        }
    }
}
