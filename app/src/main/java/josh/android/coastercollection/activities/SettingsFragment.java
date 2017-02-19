package josh.android.coastercollection.activities;

import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;

import josh.android.coastercollection.R;

/**
 * Created by Jos on 19/12/2016.
 */
public class SettingsFragment extends PreferenceFragment {
    public static String PREF_KEY_SORT_REVERSE = "pref_key_sort_reverse";
    public static String PREF_KEY_LISTVIEW_TYPE = "pref_key_listview_type";
    public static String PREF_KEY_SHOW_BACKIMAGE = "pref_key_show_backimage";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.preferences);

        Preference orderPref = getPreferenceManager().findPreference(PREF_KEY_SORT_REVERSE);
        Preference showBackImagePref = getPreferenceManager().findPreference(PREF_KEY_SHOW_BACKIMAGE);
        ListPreference listViewTypePref = (ListPreference) getPreferenceManager().findPreference(PREF_KEY_LISTVIEW_TYPE);

        listViewTypePref.setSummary(listViewTypePref.getEntry());

        orderPref.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                CoasterListActivity.refreshCoasterList = true;

                return true;
            }
        });

        showBackImagePref.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                CoasterListActivity.refreshCoasterList = true;

                return true;
            }
        });

        listViewTypePref.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                CoasterListActivity.refreshCoasterList = true;

                ListPreference listPref = (ListPreference) preference;

                int ind = listPref.findIndexOfValue((String) newValue);

                preference.setSummary(listPref.getEntries()[ind]);

                return true;
            }
        });
    }
}
