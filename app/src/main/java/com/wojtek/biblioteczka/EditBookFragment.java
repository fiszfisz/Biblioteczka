package com.wojtek.biblioteczka;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

public class EditBookFragment extends Fragment implements View.OnClickListener {

    public interface Callbacks {
        Book getBook(int id);
        void setBook(Book book);
    }

    private static final String tag = "EditBookActivity";

    private Book book;
    private boolean newBook;

    private Callbacks callbacks;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            callbacks = (Callbacks)activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement Callbacks");
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        int id = getArguments().getInt("Book");

        if (id == -1) {
            book = new Book();
            newBook = true;
        } else {
            book = callbacks.getBook(id);
            newBook = false;
        }

        View view = inflater.inflate(R.layout.fragment_edit_book, container, false);

        Button saveButton = view.findViewById(R.id.saveButton);
        Button cancelButton = view.findViewById(R.id.cancelButton);

        saveButton.setOnClickListener(this);
        cancelButton.setOnClickListener(this);

        setContents(view, book);

        if (newBook) {
            ClipboardManager clipboardManager = (ClipboardManager)getActivity().getSystemService(Activity.CLIPBOARD_SERVICE);
            ClipData.Item item = clipboardManager.getPrimaryClip().getItemAt(0);
            String pasteData = item.getText().toString();

            if (pasteData != null) {
                if (pasteData.startsWith("http") || pasteData.contains("www.")) {
                    loadClipboardContents(view, pasteData);
                }
            }
        }

        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.saveButton:
                View view = v.getRootView();

                EditText authorEditText = view.findViewById(R.id.authorEditText);
                EditText titleEditText = view.findViewById(R.id.titleEditText);
                EditText publisherEditText = view.findViewById(R.id.publisherEditText);
                EditText cityEditText = view.findViewById(R.id.cityEditText);
                EditText yearEditText = view.findViewById(R.id.yearEditText);
                EditText coverEditText = view.findViewById(R.id.coverEditText);

                // TODO more sophisticated method to check if fields were modified
                book.author = authorEditText.getText().toString().trim();
                book.title = titleEditText.getText().toString().trim();
                book.publisher = publisherEditText.getText().toString().trim();
                book.city = cityEditText.getText().toString().trim();
                book.year = yearEditText.getText().toString().trim();
                book.cover = coverEditText.getText().toString().trim();

                if (!newBook) {
                    book.version++;
                }

                callbacks.setBook(book);

                getActivity().onBackPressed();
                break;
            case R.id.cancelButton:
                getActivity().onBackPressed();
                break;
        }
    }

    private void loadClipboardContents(View view, String address) {
        ConstraintLayout layout = view.findViewById(R.id.constraint_layout);
        WebsiteDownloaderTask task = new WebsiteDownloaderTask(layout, address);
        Log.i(tag, "Parsing data: " + address);
        task.execute();
    }

    private void setContents(View view, Book book) {
        EditText authorEditText = view.findViewById(R.id.authorEditText);
        EditText titleEditText = view.findViewById(R.id.titleEditText);
        EditText publisherEditText = view.findViewById(R.id.publisherEditText);
        EditText cityEditText = view.findViewById(R.id.cityEditText);
        EditText yearEditText = view.findViewById(R.id.yearEditText);
        EditText coverEditText = view.findViewById(R.id.coverEditText);

        authorEditText.setText(book.author);
        titleEditText.setText(book.title);
        cityEditText.setText(book.city);
        publisherEditText.setText(book.publisher);
        yearEditText.setText(book.year);
        coverEditText.setText(book.cover);
    }
}
