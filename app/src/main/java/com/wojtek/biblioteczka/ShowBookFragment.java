package com.wojtek.biblioteczka;

import android.app.Activity;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class ShowBookFragment extends Fragment {

    public interface Callbacks {
        Book getBook(int id);
    }

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
        Book book = callbacks.getBook(id);

        View view = inflater.inflate(R.layout.fragment_show_book, container, false);

        setContents(view, book);

        return view;
    }

    private void setContents(View view, Book book) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity());

        TextView authorTextView = view.findViewById(R.id.authorTextView);
        TextView titleTextView = view.findViewById(R.id.titleTextView);
        TextView publisherTextView = view.findViewById(R.id.publisherTextView);
        TextView cityTextView = view.findViewById(R.id.cityTextView);
        TextView yearTextView = view.findViewById(R.id.yearTextView);
        ImageView coverImageView = view.findViewById(R.id.coverImageView);

        String titleText = book.title;
        if (sharedPref.getBoolean(SettingsFragment.ALLOW_BREAK_TITLES, true)) {
            titleText = titleText.replace(". ", ".\n");
        }

        authorTextView.setText(book.author);
        titleTextView.setText(titleText);
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
