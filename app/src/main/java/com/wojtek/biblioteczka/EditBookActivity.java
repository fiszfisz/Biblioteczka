package com.wojtek.biblioteczka;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.lang.ref.WeakReference;
import java.net.URL;
import java.net.URLConnection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EditBookActivity extends AppCompatActivity {

    static final String tag = "EditBookActivity";

    private Book book;

    private class WebsiteDownloaderTask extends AsyncTask<String, Void, String> {
        private final String address;
        private final WeakReference<View> viewWeakReference;

        public WebsiteDownloaderTask(View view, String url) {
            address = url ;
            viewWeakReference = new WeakReference<>(view);
        }

        @Override
        protected void onPreExecute() {
            // Placeholder
        }

        @Override
        protected String doInBackground(String... params) {
            String data;

            try {
                URL url = new URL(address);
                URLConnection connection = url.openConnection();
                InputStream is = connection.getInputStream();
                BufferedReader br = new BufferedReader(new InputStreamReader(is));

                String line;
                StringBuffer buffer = new StringBuffer();

                while ((line = br.readLine()) != null) {
                    buffer.append(line);
                    buffer.append('\n');
                }

                br.close();
                is.close();

                data = buffer.toString();
            } catch(Exception e) {
                data = null;
            }

            return data;
        }

        @Override
        protected void onPostExecute(String data) {
            if (isCancelled()) {
                data = null;
            }

            if (viewWeakReference != null) {
                View view = viewWeakReference.get();
                if(view != null) {
                    if (data != null) {
                        String line;
                        BufferedReader br;

                        String authorText;
                        String titleText;
                        String coverText;

                        Pattern authorPattern;
                        Pattern titlePattern;
                        Pattern coverPattern;

                        Matcher authorMatcher;
                        Matcher titleMatcher;
                        Matcher coverMatcher;

                        EditText authorEditText = view.findViewById(R.id.authorEditText);
                        EditText titleEditText = view.findViewById(R.id.titleEditText);
                        EditText coverEditText = view.findViewById(R.id.coverEditText);

                        if (address.contains("empik.com")) {
                            authorPattern = Pattern.compile("<title>(.*) - (.*?) \\| .*?<\\/title>");
                            titlePattern = Pattern.compile("<meta property=\\\"og:title\\\" content=\\\"(.*?)\\\" \\/>");
                            coverPattern = Pattern.compile("<meta property=\\\"og:image\\\" content=\\\"(.*?)\\\" \\/>");

                            br = new BufferedReader(new StringReader(data));

                            try {
                                while ((line = br.readLine()) != null) {
                                    authorMatcher = authorPattern.matcher(line);
                                    if (authorMatcher.matches()) {
                                        authorText = authorMatcher.group(2);
                                        authorEditText.setText(authorText);
                                    }

                                    titleMatcher = titlePattern.matcher(line);
                                    if (titleMatcher.matches()) {
                                        titleText = titleMatcher.group(1);
                                        titleEditText.setText(titleText);
                                    }

                                    coverMatcher = coverPattern.matcher(line);
                                    if (coverMatcher.matches()) {
                                        coverText = coverMatcher.group(1);
                                        coverEditText.setText(coverText);
                                    }
                                }
                            } catch (Exception e) {
                                Log.e(tag, "Unknown error while reading parsed data: " + e.getMessage());
                            }
                        }

                        if (address.contains("lubimyczytac.pl")) {
                            authorPattern = Pattern.compile("<title>(.*?) - (.*?) \\(.*?\\) - .*<\\/title>");
                            titlePattern = Pattern.compile("<title>(.*?) - (.*?) \\(.*?\\) - .*<\\/title>");
                            coverPattern = Pattern.compile("<meta property=\\\"og:image\\\" content=\\\"(.*?)\\\" \\/>");

                            br = new BufferedReader(new StringReader(data));

                            try {
                                while ((line = br.readLine()) != null) {
                                    authorMatcher = authorPattern.matcher(line);
                                    if (authorMatcher.find()) {
                                        authorText = authorMatcher.group(1);
                                        authorEditText.setText(authorText);
                                    }

                                    titleMatcher = titlePattern.matcher(line);
                                    if (titleMatcher.find()) {
                                        titleText = titleMatcher.group(2);
                                        titleEditText.setText(titleText);
                                    }

                                    coverMatcher = coverPattern.matcher(line);
                                    if (coverMatcher.find()) {
                                        coverText = coverMatcher.group(1);
                                        coverEditText.setText(coverText);
                                    }
                                }
                            } catch (Exception e) {
                                Log.e(tag, "Unknown error while reading parsed data: " + e.getMessage());
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_book);

        Intent intent = getIntent();

        try {
            book = intent.getParcelableExtra("Book");
        } catch (Exception e) {
            Log.e(tag, "Cannot get book parcel");
        }

        if (book != null) {
            setContents(book);
        }

        if (book.isEmpty()) {
            ClipboardManager clipboardManager = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
            ClipData.Item item = clipboardManager.getPrimaryClip().getItemAt(0);
            String pasteData = item.getText().toString();
            if (pasteData != null) {
                if (pasteData.startsWith("http") || pasteData.contains("www.")) {
                    loadClipboardContents(pasteData);
                }
            }
        }
    }

    private void loadClipboardContents(String address) {
        WebsiteDownloaderTask task = new WebsiteDownloaderTask(findViewById(R.id.constraint_layout), address);
        Log.i(tag, "Parsing data: " + address);
        task.execute();
    }

    private void setContents(Book book) {
        EditText authorEditText = findViewById(R.id.authorEditText);
        EditText titleEditText = findViewById(R.id.titleEditText);
        EditText publisherEditText = findViewById(R.id.publisherEditText);
        EditText cityEditText = findViewById(R.id.cityEditText);
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
        EditText publisherEditText = findViewById(R.id.publisherEditText);
        EditText cityEditText = findViewById(R.id.cityEditText);
        EditText yearEditText = findViewById(R.id.yearEditText);
        EditText coverEditText = findViewById(R.id.coverEditText);

        // TODO more sophisticated method to check if fields were modified
        book.author = authorEditText.getText().toString().trim();
        book.title = titleEditText.getText().toString().trim();
        book.publisher = publisherEditText.getText().toString().trim();
        book.city = cityEditText.getText().toString().trim();
        book.year = yearEditText.getText().toString().trim();
        book.cover = coverEditText.getText().toString().trim();

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
