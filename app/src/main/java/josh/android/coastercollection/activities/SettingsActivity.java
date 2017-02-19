package josh.android.coastercollection.activities;

import android.app.Activity;
import android.os.Bundle;
import android.preference.PreferenceManager;

import josh.android.coastercollection.R;

/**
 * Created by Jos on 19/12/2016.
 */
public class SettingsActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);

        // Display the fragment as the main content.
        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new SettingsFragment())
                .commit();
    }
}
