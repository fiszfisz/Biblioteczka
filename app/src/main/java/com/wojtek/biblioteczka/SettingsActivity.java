package com.wojtek.biblioteczka;

import android.preference.PreferenceActivity;
import android.os.Bundle;

public class SettingsActivity extends PreferenceActivity {

    final static String SORT_METHOD = "sort_method_preference";
    final static String SORT_METHOD_AUTHOR = "sort_method_author_preference";
    final static String SORT_METHOD_TITLE = "sort_method_title_preference";
    final static String SORT_METHOD_YEAR = "sort_method_year_preference";

    final static String ALLOW_BREAK_TITLES = "allow_break_titles_preference";

    final static String SYNC_ENABLED = "sync_enabled_preference";
    final static String SYNC_LOCATION = "sync_location_preference";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
    }
}
