package com.wojtek.biblioteczka;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import org.xmlpull.v1.XmlPullParserException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    static final int ADD_BOOK_REQUEST = 1;
    static final int EDIT_BOOK_REQUEST = 2;

    private File dataFile;

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
            Book book = (Book)getItem(position);
            // TODO use convert view with the view holder
            View view = inflater.inflate(R.layout.activity_main_item, parent, false);

            TextView titleTextView = view.findViewById(R.id.titleTextView);
            TextView authorTextView = view.findViewById(R.id.authorTextView);
            ImageView imageView = view.findViewById(R.id.imageView);

            titleTextView.setText(book.title);
            authorTextView.setText(book.author);

            if (!book.cover.isEmpty()) {
                BitmapDownloaderTask task = new BitmapDownloaderTask(imageView, book.cover);
                task.execute();
            }

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

    protected synchronized void saveData()
    {
        try {
            bookcase.saveAsXml(dataFile);
        } catch (IOException e) {
            Log.e("MainActivity.onCreate", "Error on saving data: " + e.getMessage());
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        String tag = "MainActivity.onCreate";

        dataFile = new File(getFilesDir(), "books.xml");

        bookcase = new Bookcase();

        try {
            bookcase.loadFromXml(dataFile);
        } catch (FileNotFoundException e) {
            Log.i(tag, "Data file not found: " + e.getMessage());
        } catch (XmlPullParserException e) {
            Log.e(tag, "Data file format error: " + e.getMessage());
        } catch (IOException e) {
            Log.e(tag, "Data file unknown error: " + e.getMessage());
        }

        adapterData = bookcase.getBooks();
        adapter = new BookArrayAdapter(this, adapterData);
        ListView listView = findViewById(R.id.listView);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new OnItemClickListener());

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addBook(view);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case ADD_BOOK_REQUEST:
                if (resultCode == Activity.RESULT_OK) {
                    Book book = data.getParcelableExtra("Book");
                    bookcase.add(book);

                    saveData();

                    adapterData = bookcase.getBooks();
                    adapter.notifyDataSetChanged();
                }
                break;
            case EDIT_BOOK_REQUEST:
                if (resultCode == Activity.RESULT_OK) {
                    Book book = data.getParcelableExtra("Book");
                    bookcase.set(book);

                    saveData();

                    adapterData = bookcase.getBooks();
                    adapter.notifyDataSetChanged();
                }
                break;
            default:
                break;
        }
    }

    public void addBook(View view) {
        Book book = new Book();
        Intent intent = new Intent(view.getContext(), EditBookActivity.class);
        intent.putExtra("Book", book);
        startActivityForResult(intent, ADD_BOOK_REQUEST);
    }
}
