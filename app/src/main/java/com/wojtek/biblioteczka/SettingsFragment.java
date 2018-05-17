package com.wojtek.biblioteczka;

import android.os.Bundle;
import android.support.v7.preference.PreferenceFragmentCompat;

public class SettingsFragment extends PreferenceFragmentCompat {

    final static String SORT_METHOD = "sort_method_preference";
    final static String SORT_METHOD_AUTHOR = "sort_method_author_preference";
    final static String SORT_METHOD_TITLE = "sort_method_title_preference";
    final static String SORT_METHOD_YEAR = "sort_method_year_preference";

    final static String ALLOW_BREAK_TITLES = "allow_break_titles_preference";

    final static String SYNC_ENABLED = "sync_enabled_preference";
    final static String SYNC_LOCATION = "sync_location_preference";

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.preferences);
    }
}
