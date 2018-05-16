package com.wojtek.biblioteczka;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class BookListFragment extends Fragment implements View.OnClickListener {

    public interface Callbacks {
        ArrayList<Book> getBooks();
        void showBook(int id);
        void editBook(int id);
        void removeBook(int id);
    }

    private Callbacks callbacks;

    private BookArrayAdapter adapter;
    private ArrayList<Book> adapterData;

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
        MainActivity activity = (MainActivity)getActivity();

        View view = inflater.inflate(R.layout.fragment_book_list, container, false);

        adapterData = new ArrayList<>();
        adapter = new BookArrayAdapter(activity, adapterData);
        ListView listView = view.findViewById(R.id.listView);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new OnItemClickListener());
        registerForContextMenu(listView);

        FloatingActionButton fab = view.findViewById(R.id.fab);
        fab.setOnClickListener(this);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        adapterData.clear();
        adapterData.addAll(callbacks.getBooks());

        adapter.getFilter().filter("");
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

        MenuInflater inflater = getActivity().getMenuInflater();
        inflater.inflate(R.menu.context_menu, menu);
    }


    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();

        Book book;

        switch (item.getItemId()) {
            case R.id.context_menu_edit:
                book = adapterData.get(info.position);
                callbacks.editBook(book.id);
                return true;
            case R.id.context_menu_remove:
                book = adapterData.get(info.position);
                callbacks.removeBook(book.id);
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fab:
                callbacks.editBook(-1);
                break;
        }
    }

    private class BookArrayAdapter extends ArrayAdapter {
        private ArrayList<Book> booksOriginal;
        private ArrayList<Book> booksFiltered;
        private BookFilter filter;

        public BookArrayAdapter(Context context, ArrayList<Book> array) {
            super(context, 0, array);
            booksOriginal = array;
            booksFiltered = new ArrayList<>();
            booksFiltered.addAll(booksOriginal);
        }

        @Override
        public int getCount() {
            return booksFiltered.size();
        }

        @Override
        public Book getItem(int position) {
            return booksFiltered.get(position);
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            Book book = getItem(position);
            // TODO use convert view with the view holder
            View view = inflater.inflate(R.layout.fragment_book_item, parent, false);
            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getContext());

            TextView authorTextView = view.findViewById(R.id.authorTextView);
            TextView titleTextView = view.findViewById(R.id.titleTextView);
            TextView publisherTextView = view.findViewById(R.id.publisherTextView);
            TextView yearTextView = view.findViewById(R.id.yearTextView);
            ImageView imageView = view.findViewById(R.id.coverImageView);

            String titleText = book.title;
            if (sharedPref.getBoolean(SettingsFragment.ALLOW_BREAK_TITLES, true)) {
                titleText = titleText.replace(". ", ".\n");
            }

            authorTextView.setText(book.author);
            titleTextView.setText(titleText);
            publisherTextView.setText(book.publisher);
            yearTextView.setText(book.year);

            if (!book.cover.isEmpty()) {
                BitmapDownloaderTask task = new BitmapDownloaderTask(imageView, book.cover);
                task.execute();
            }

            return view;
        }

        @NonNull
        @Override
        public Filter getFilter() {
            if (filter == null) {
                filter = new BookFilter();
            }
            return filter;
        }

        private class BookFilter extends Filter {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                String text = constraint.toString().toLowerCase();
                FilterResults result = new FilterResults();

                ArrayList<Book> filtered = new ArrayList<>();

                for (Book book : booksOriginal) {
                    if (book.version < 0) {
                        continue;
                    }

                    if (text.isEmpty()) {
                        filtered.add(book);
                    } else {
                        if (book.author.toLowerCase().contains(text)) {
                            filtered.add(book);
                        }
                        if (book.title.toLowerCase().contains(text)) {
                            filtered.add(book);
                        }
                    }
                }

                result.values = filtered;
                result.count = filtered.size();

                return result;
            }

            @SuppressWarnings("unchecked")
            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                booksFiltered = (ArrayList<Book>)results.values;
                notifyDataSetChanged();
                clear();

                for (int i = 0, l = booksFiltered.size(); i < l; i++) {
                    add(booksFiltered.get(i));
                    notifyDataSetInvalidated();
                }
            }
        }
    }

    private class OnItemClickListener implements AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Book book = (Book)parent.getItemAtPosition(position);
            callbacks.showBook(book.id);
        }
    }
}
