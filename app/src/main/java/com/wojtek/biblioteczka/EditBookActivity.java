package com.wojtek.biblioteczka;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
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
        EditText coverEditText = findViewById(R.id.coverEditText);

        authorEditText.setText(book.author);
        titleEditText.setText(book.title);
        cityEditText.setText(book.city);
        publisherEditText.setText(book.publisher);
        yearEditText.setText(book.year);
        coverEditText.setText(book.cover);
    }

    public void onSaveButtonClick(View view) {
        EditText authorEditText = findViewById(R.id.authorEditText);
        EditText titleEditText = findViewById(R.id.titleEditText);
        EditText cityEditText = findViewById(R.id.cityEditText);
        EditText publisherEditText = findViewById(R.id.publisherEditText);
        EditText yearEditText = findViewById(R.id.yearEditText);
        EditText coverEditText = findViewById(R.id.coverEditText);

        // TODO more sophisticated method to check if fields were modified
        book.author = authorEditText.getText().toString();
        book.title = titleEditText.getText().toString();
        book.city = cityEditText.getText().toString();
        book.publisher = publisherEditText.getText().toString();
        book.year = yearEditText.getText().toString();
        book.cover = coverEditText.getText().toString();

        Intent resultIntent = new Intent();
        resultIntent.putExtra("Book", book);
        setResult(Activity.RESULT_OK, resultIntent);
        finish();
    }

    public void onCancelButtonClick(View view) {
        setResult(Activity.RESULT_CANCELED);
        finish();
    }
}