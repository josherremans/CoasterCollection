package josh.android.coastercollection.activities;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.support.annotation.Nullable;

import josh.android.coastercollection.R;
import josh.android.coastercollection.application.CoasterApplication;
import josh.android.coastercollection.bl.ImageManager;

/**
 * Created by Jos on 19/12/2016.
 */
public class SettingsFragment extends PreferenceFragment {

    CoasterApplication coasterApplication;
    String keyShowDetails;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.preferences);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (coasterApplication == null) {
            coasterApplication = (CoasterApplication) getActivity().getApplication();

            keyShowDetails = getString(R.string.pref_key_show_info_details);
        }

        getPreferenceManager().getSharedPreferences().registerOnSharedPreferenceChangeListener(new SharedPreferences.OnSharedPreferenceChangeListener() {
            @Override
            public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
                // Re-read the preferences!
                coasterApplication.readPreferences();

//                CoasterApplication.refreshCoasters = true;
                CoasterApplication.collectionData.setNotifyAdapter(true);

                if (key.equals(keyShowDetails)) {
                    ImageManager.clearCache();
                }
            }
        });

        ListPreference listViewTypePref = (ListPreference) getPreferenceManager().findPreference(getString(R.string.pref_key_listview_type));

        listViewTypePref.setSummary(listViewTypePref.getEntry());

        listViewTypePref.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                CoasterApplication.listViewType = (String) newValue;
                CoasterApplication.refreshCoasters = true;

                ListPreference listPref = (ListPreference) preference;

                int ind = listPref.findIndexOfValue((String) newValue);

                preference.setSummary(listPref.getEntries()[ind]);

                return true;
            }
        });
    }
}
