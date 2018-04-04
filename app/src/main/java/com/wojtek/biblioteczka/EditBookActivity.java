package com.wojtek.biblioteczka;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;

public class EditBookActivity extends AppCompatActivity {

    private Book book;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_book);

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
        EditText authorEditText = findViewById(R.id.authorEditText);
        EditText titleEditText = findViewById(R.id.titleEditText);
        EditText cityEditText = findViewById(R.id.cityEditText);
        EditText publisherEditText = findViewById(R.id.publisherEditText);
        EditText yearEditText = findViewById(R.id.yearEditText);

        authorEditText.setText(book.author);
        titleEditText.setText(book.title);
        cityEditText.setText(book.city);
        publisherEditText.setText(book.publisher);
        yearEditText.setText(book.year);
    }
}
