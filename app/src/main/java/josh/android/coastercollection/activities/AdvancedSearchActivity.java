package josh.android.coastercollection.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import josh.android.coastercollection.R;
import josh.android.coastercollection.application.CoasterApplication;
import josh.android.coastercollection.databank.CoasterCollectionDBHelper;

public class AdvancedSearchActivity extends AppCompatActivity {

    private final static String LOG_TAG = "ADVANCED_SEARCH";

    private CoasterCollectionDBHelper dbHelper = new CoasterCollectionDBHelper(this);

    private EditText editTextSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_advanced_search);

        Log.i(LOG_TAG, "IN OnCreate");

        editTextSearch = (EditText) findViewById(R.id.editSearchByCoasterText);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    @Override
    protected void onResume() {
        super.onResume();

        editTextSearch.setText(CoasterApplication.searchCoasterText);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_advanced_search, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        CoordinatorLayout coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinatorlayout);

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_filter) {
            CoasterApplication.searchCoasterText = editTextSearch.getEditableText().toString();
            CoasterApplication.refreshCoasters = true;

            Intent intent = new Intent(AdvancedSearchActivity.this, CoasterListActivity.class);

            startActivity(intent);

            return true;
        }

        if (id == R.id.action_clear_filter) {
            editTextSearch.getEditableText().clear();

            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
