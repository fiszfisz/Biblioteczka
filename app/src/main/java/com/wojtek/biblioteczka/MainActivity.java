package com.wojtek.biblioteczka;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import org.xmlpull.v1.XmlPullParserException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import jcifs.smb.SmbFile;

public class MainActivity extends AppCompatActivity
        implements BookListFragment.Callbacks, ShowBookFragment.Callbacks, EditBookFragment.Callbacks {

    public static final String tag = "MainActivity";

    private File dataFile;
    private Bookcase bookcase;
    private Comparator<Book> comparator;

    private Context context;
    private SharedPreferences sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        context = getApplicationContext();
        sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        dataFile = new File(getFilesDir(), "books.xml");
        bookcase = new Bookcase();

        setCompareMethod(null);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_menu);
        } else {
            Log.w(tag, "Action bar not present");
        }

        setNavigationViewListener();

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
        startUpdate();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);

        switch (item.getItemId()) {
            case android.R.id.home:
                drawerLayout.openDrawer(GravityCompat.START);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    protected void setNavigationViewListener() {
        final DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
        final NavigationView navigationView = findViewById(R.id.nav_view);

        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                        // TODO add checked only for selected sort method
                        item.setChecked(true);

                        switch (item.getItemId()) {
                            case R.id.title_sort_drawer:
                                setCompareMethod(SettingsFragment.SORT_METHOD_TITLE);
                                reloadData();
                                break;
                            case R.id.author_sort_drawer:
                                setCompareMethod(SettingsFragment.SORT_METHOD_AUTHOR);
                                reloadData();
                                break;
                            case R.id.year_sort_drawer:
                                setCompareMethod(SettingsFragment.SORT_METHOD_YEAR);
                                reloadData();
                                break;
                            case R.id.settings_drawer:
                                openSettings();
                                break;
                        }

                        item.setChecked(false);
                        drawerLayout.closeDrawers();

                        return true;
                    }
                }
        );
    }

    private void setCompareMethod(String method) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sharedPref.edit();

        if (method == null) {
            method = sharedPref.getString(SettingsFragment.SORT_METHOD, "");
        }

        switch (method) {
            case SettingsFragment.SORT_METHOD_AUTHOR:
                editor.putString(SettingsFragment.SORT_METHOD, SettingsFragment.SORT_METHOD_AUTHOR);
                comparator = Book.AuthorComparator;
                break;
            case SettingsFragment.SORT_METHOD_TITLE:
                editor.putString(SettingsFragment.SORT_METHOD, SettingsFragment.SORT_METHOD_TITLE);
                comparator = Book.TitleComparator;
                break;
            case SettingsFragment.SORT_METHOD_YEAR:
                editor.putString(SettingsFragment.SORT_METHOD, SettingsFragment.SORT_METHOD_YEAR);
                comparator = Book.YearComparator;
                break;
            default:
                editor.putString(SettingsFragment.SORT_METHOD, SettingsFragment.SORT_METHOD_TITLE);
                comparator = Book.TitleComparator;
                break;
        }

        editor.commit();
    }

    protected synchronized void reloadData() {
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.main_fragment);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        if (fragment == null) {
            fragment = new BookListFragment();
            transaction.add(R.id.main_fragment, fragment);
        } else {
            transaction.detach(fragment);
            transaction.attach(fragment);
        }

        transaction.commit();
    }

    protected synchronized void saveData() {
        try {
            bookcase.saveAsXml(dataFile);
        } catch (IOException e) {
            Log.e(tag, "Error on saving data: " + e.getMessage());
        }
    }

    protected synchronized void synchronizeData() {
        boolean sync_enabled = sharedPref.getBoolean(SettingsFragment.SYNC_ENABLED, false);
        final String sync_location = sharedPref.getString(SettingsFragment.SYNC_LOCATION, "");

        Thread toastStart = new Thread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(context, R.string.sync_started, Toast.LENGTH_SHORT).show();
            }
        });

        Thread toastEnd = new Thread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(context, R.string.sync_finished, Toast.LENGTH_SHORT).show();
            }
        });

        Thread toastError = new Thread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(context, R.string.sync_error, Toast.LENGTH_LONG).show();
            }
        });

        if (sync_location.isEmpty()) {
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putBoolean(SettingsFragment.SYNC_ENABLED, false);
            editor.commit();
            sync_enabled = false;
        }

        if (sync_enabled) {
            try {
                runOnUiThread(toastStart);

                String name = "smb://" + sync_location + "/books.xml";
                SmbFile syncFile = new SmbFile(name);
                bookcase.synchronizeWithSmb(syncFile);

                runOnUiThread(toastEnd);
            } catch (Exception e) {
                Log.e(tag, "Synchronization not finished: " + e.getMessage());
                runOnUiThread(toastError);
            }
        }
    }

    protected void startUpdate() {
        Thread sync = new Thread(new Runnable() {
            @Override
            public void run() {
                synchronizeData();
                saveData();

                runOnUiThread(new Thread(new Runnable() {
                    @Override
                    public void run() {
                        reloadData();
                    }
                }));
            }
        });

        sync.start();
    }

    @Override
    public Book getBook(int id) {
        return bookcase.get(id);
    }

    @Override
    public void setBook(Book book) {
        bookcase.set(book);
        startUpdate();
    }

    @Override
    public void removeBook(int id) {
        bookcase.remove(id);
        startUpdate();
    }

    @Override
    public ArrayList<Book> getBooks() {
        ArrayList<Book> books = new ArrayList<>(bookcase.getBooks());
        Collections.sort(books, comparator);
        return books;
    }

    @Override
    public void showBook(int id) {
        Bundle bundle = new Bundle();
        bundle.putInt("Book", id);

        ShowBookFragment fragment = new ShowBookFragment();
        fragment.setArguments(bundle);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.main_fragment, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    @Override
    public void editBook(int id) {
        Bundle bundle = new Bundle();
        bundle.putInt("Book", id);

        EditBookFragment fragment = new EditBookFragment();
        fragment.setArguments(bundle);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.main_fragment, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    public void openSettings() {
        SettingsFragment fragment = new SettingsFragment();

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.main_fragment, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}
