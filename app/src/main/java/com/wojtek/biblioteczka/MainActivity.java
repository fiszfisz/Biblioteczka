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
import java.util.Collections;
import java.util.Comparator;

public class MainActivity extends AppCompatActivity {

    static final int ADD_BOOK_REQUEST = 1;
    static final int EDIT_BOOK_REQUEST = 2;
    static final int SHOW_BOOK_REQUEST = 3;

    static final String tag = "MainActivity.onCreate";

    private File dataFile;
    private Bookcase bookcase;
    private Comparator<Book> comparator;

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

            TextView authorTextView = view.findViewById(R.id.authorTextView);
            TextView titleTextView = view.findViewById(R.id.titleTextView);
            ImageView imageView = view.findViewById(R.id.coverImageView);

            authorTextView.setText(book.author);
            titleTextView.setText(book.title);

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

            Intent intent = new Intent(view.getContext(), ShowBookActivity.class);
            intent.putExtra("Book", book);
            startActivityForResult(intent, SHOW_BOOK_REQUEST);
        }
    }

    private class OnItemLongClickListener implements AdapterView.OnItemLongClickListener {
        @Override
        public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
            Book book = (Book)parent.getItemAtPosition(position);

            Intent intent = new Intent(view.getContext(), EditBookActivity.class);
            intent.putExtra("Book", book);
            startActivityForResult(intent, EDIT_BOOK_REQUEST);

            return true;
        }
    }

    protected void reloadData()
    {
        adapterData = bookcase.getBooks();
        Collections.sort(adapterData, comparator);
        adapter.notifyDataSetChanged();
    }

    protected synchronized void saveData()
    {
        try {
            bookcase.saveAsXml(dataFile);
        } catch (IOException e) {
            Log.e(tag, "Error on saving data: " + e.getMessage());
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dataFile = new File(getFilesDir(), "books.xml");
        bookcase = new Bookcase();
        comparator = Book.TitleComparator;

        adapterData = bookcase.getBooks();
        Collections.sort(adapterData, Book.TitleComparator);
        adapter = new BookArrayAdapter(this, adapterData);
        ListView listView = findViewById(R.id.listView);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new OnItemClickListener());
        listView.setOnItemLongClickListener(new OnItemLongClickListener());

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addBook(view);
            }
        });

        try {
            bookcase.loadFromXml(dataFile);
        } catch (FileNotFoundException e) {
            Log.i(tag, "Data file not found: " + e.getMessage());
        } catch (XmlPullParserException e) {
            Log.e(tag, "Data file format error: " + e.getMessage());
        } catch (IOException e) {
            Log.e(tag, "Data file unknown error: " + e.getMessage());
        }

        reloadData();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case ADD_BOOK_REQUEST:
                if (resultCode == Activity.RESULT_OK) {
                    Book book = data.getParcelableExtra("Book");
                    bookcase.add(book);
                    saveData();
                    reloadData();
                }
                break;
            case EDIT_BOOK_REQUEST:
                if (resultCode == Activity.RESULT_OK) {
                    Book book = data.getParcelableExtra("Book");
                    bookcase.set(book);
                    saveData();
                    reloadData();
                }
                break;
            case SHOW_BOOK_REQUEST:
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
