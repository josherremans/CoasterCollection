package josh.android.coastercollection.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import josh.android.coastercollection.R;
import josh.android.coastercollection.application.CoasterApplication;
import josh.android.coastercollection.bo.Collector;
import josh.android.coastercollection.databank.CoasterCollectionDBHelper;

public class AddCollectorActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private final static String LOG_TAG = "ADD_COLLECTOR_ACTIVITY";

    private EditText editCollectorLastName;
    private EditText editCollectorFirstName;
    private EditText editCollectorInitials;
    private EditText editCollectorAlias;

    private Collector startCollector;
    private Collector endCollector;

    private CoasterCollectionDBHelper dbHelper;

    private TextView txtTitleCollector;

    private long nextCollectorID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_collector);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        Intent intentOrigine = this.getIntent();

        long collectorID = intentOrigine.getLongExtra("extraCollectorID", -1);

        editCollectorLastName = (EditText) findViewById(R.id.editCollectorLastName);
        editCollectorFirstName = (EditText) findViewById(R.id.editCollectorFirstName);
        editCollectorInitials = (EditText) findViewById(R.id.editCollectorInitials);
        editCollectorAlias = (EditText) findViewById(R.id.editCollectorAlias);

        // *** DBHELPER:

        dbHelper = new CoasterCollectionDBHelper(this);

        // *** TrademarkID:

        txtTitleCollector = (TextView) findViewById(R.id.txtTitleCollector);

        if (collectorID != -1) {
            startCollector = dbHelper.getCollectorByID(collectorID);

            nextCollectorID = collectorID;
        } else {
            nextCollectorID = dbHelper.getNextCollectorIDFromDB();
        }

        txtTitleCollector.setText(txtTitleCollector.getText() + " " + nextCollectorID);
    }

    @Override
    public void onResume() {
        super.onResume();

        Log.i(LOG_TAG, "IN onResume");

        if (startCollector != null) {
            editCollectorLastName.setText(startCollector.getLastName());
            editCollectorFirstName.setText(startCollector.getFirstName());
            editCollectorInitials.setText(startCollector.getInitials());
            editCollectorAlias.setText(startCollector.getAlias());
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_add_collector, menu);
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
        if (id == R.id.action_settings) {
            Snackbar snackbar = Snackbar.make(coordinatorLayout, "You clicked Settings", Snackbar.LENGTH_LONG);

            snackbar.show();

            return true;
        }

        if (id == R.id.action_save) {
            saveCollector();

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        CoordinatorLayout coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinatorlayout);

        Snackbar snackbar;

        if (id == R.id.nav_camera) {
            // Handle the camera action
            snackbar = Snackbar.make(coordinatorLayout, "You clicked Camera", Snackbar.LENGTH_LONG);

            snackbar.show();
        } else if (id == R.id.nav_gallery) {
            snackbar = Snackbar.make(coordinatorLayout, "You clicked Gallery", Snackbar.LENGTH_LONG);

            snackbar.show();
        } else if (id == R.id.nav_slideshow) {
            snackbar = Snackbar.make(coordinatorLayout, "You clicked Slideshow", Snackbar.LENGTH_LONG);

            snackbar.show();
        } else if (id == R.id.nav_statistics) {
            snackbar = Snackbar.make(coordinatorLayout, "You clicked Statistics", Snackbar.LENGTH_LONG);

            snackbar.show();
        } else if (id == R.id.nav_trademarks) {
            snackbar = Snackbar.make(coordinatorLayout, "You clicked Trademarks", Snackbar.LENGTH_LONG);

            snackbar.show();
        } else if (id == R.id.nav_series) {
            snackbar = Snackbar.make(coordinatorLayout, "You clicked Series", Snackbar.LENGTH_LONG);

            snackbar.show();
        } else if (id == R.id.nav_donors) {
            snackbar = Snackbar.make(coordinatorLayout, "You clicked Donors", Snackbar.LENGTH_LONG);

            snackbar.show();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);

        return true;
    }

    private boolean saveCollector() {
        boolean res = validateCollector();

        if (res) {
            // Save collector to DB:

            dbHelper.putCollectorInDB(endCollector);

            CoasterApplication.collectionData.mapCollectors.put(endCollector.getCollectorID(), endCollector);

//            CollectorListActivity.refreshCollectorList = true;
            CoasterApplication.refreshCollectors = true;

            CoordinatorLayout coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinatorlayout);

            Snackbar snackbar = Snackbar.make(coordinatorLayout, "Saved!", Snackbar.LENGTH_LONG);

            snackbar.show();
        } else {
            CoordinatorLayout coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinatorlayout);

            Snackbar snackbar = Snackbar.make(coordinatorLayout, "Validation problems!", Snackbar.LENGTH_LONG);

            snackbar.show();
        }

        return true;
    }

    private boolean validateCollector() {
        boolean res = true;

        String inputCollectorLastName = (editCollectorLastName.getEditableText() == null ? "" : editCollectorLastName.getEditableText().toString());
        String inputCollectorFirstName = (editCollectorFirstName.getEditableText() == null ? "" : editCollectorFirstName.getEditableText().toString());
        String inputCollectorInitials = (editCollectorInitials.getEditableText() == null ? "" : editCollectorInitials.getEditableText().toString());
        String inputCollectorAlias = (editCollectorAlias.getEditableText() == null ? "" : editCollectorAlias.getEditableText().toString());

        if (startCollector == null) {
            for (Collector c : CoasterApplication.collectionData.mapCollectors.values()) {
                if ((c.getLastName().equals(inputCollectorLastName))
                        && (c.getFirstName().equals(inputCollectorFirstName))) {
                    res = false;
                }
            }
        }

        if ((inputCollectorFirstName.length() == 0)
            && (inputCollectorLastName.length() == 0)
            && (inputCollectorAlias.length() == 0)) {
            res = false;
        }

        if (res) {
            endCollector = new Collector(nextCollectorID, inputCollectorFirstName, inputCollectorLastName);

            if (startCollector != null) {
                endCollector.setFetchedFromDB(startCollector.isFetchedFromDB());
            }

            endCollector.setInitials(inputCollectorInitials);
            endCollector.setAlias(inputCollectorAlias);
        }

        return res;
    }
}
