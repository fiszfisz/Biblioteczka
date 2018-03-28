package com.wojtek.biblioteczka;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

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
            Book book = (Book)getItem(position);

            View view = inflater.inflate(R.layout.activity_main_item, parent, false);

            TextView titleTextView = view.findViewById(R.id.titleTextView);
            TextView authorTextView = view.findViewById(R.id.authorTextView);

            titleTextView.setText(book.title);
            authorTextView.setText(book.author);

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
        adapterData.add(book);

        book = new Book();
        book.author = "Philip K. Dick";
        book.title = "Raport Mniejszości";
        adapterData.add(book);

        adapter.notifyDataSetChanged();
    }
}
