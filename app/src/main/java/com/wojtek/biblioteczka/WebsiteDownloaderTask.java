package com.wojtek.biblioteczka;

import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.lang.ref.WeakReference;
import java.net.URL;
import java.net.URLConnection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WebsiteDownloaderTask extends AsyncTask<String, Void, String> {
    public static String tag = "WebsiteDownloaderTask";

    private final String address;
    private final WeakReference<View> viewWeakReference;

    public WebsiteDownloaderTask(View view, String url) {
        address = url ;
        viewWeakReference = new WeakReference<>(view);
    }

    @Override
    protected void onPreExecute() {
        View view = viewWeakReference.get();
        ImageView imageView = view.findViewById(R.id.stateImageView);
        Drawable load = imageView.getContext().getResources().getDrawable(R.drawable.ic_load);
        imageView.setImageDrawable(load);

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

                    ImageView imageView = view.findViewById(R.id.stateImageView);
                    Drawable ok = imageView.getContext().getResources().getDrawable(R.drawable.ic_ok);

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
                                    authorText = authorMatcher.group(2);
                                    authorEditText.setText(authorText);
                                }

                                titleMatcher = titlePattern.matcher(line);
                                if (titleMatcher.find()) {
                                    titleText = titleMatcher.group(1);
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

                    imageView.setImageDrawable(ok);
                }
            }
        }
    }
}