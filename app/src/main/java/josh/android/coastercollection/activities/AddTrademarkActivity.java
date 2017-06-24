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
import josh.android.coastercollection.bo.Trademark;
import josh.android.coastercollection.databank.CoasterCollectionDBHelper;

public class AddTrademarkActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private final static String LOG_TAG = "ADD_TRADEMARK_ACTIVITY";

    private EditText editTrademark;
    private EditText editBrewery;

    private Trademark startTrademark;
    private Trademark endTrademark;

    private CoasterCollectionDBHelper dbHelper;

    private TextView txtTitleTrademark;

    private long nextTrademarkID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_trademark);
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

        long trademarkID = intentOrigine.getLongExtra("extraTrademarkID", -1);

        editTrademark = (EditText) findViewById(R.id.editTrademark);
        editBrewery = (EditText) findViewById(R.id.editBrewery);

        // *** DBHELPER:

        dbHelper = new CoasterCollectionDBHelper(this);

        // *** TrademarkID:

        txtTitleTrademark = (TextView) findViewById(R.id.txtTitleTrademark);

        if (trademarkID != -1) {
            startTrademark = dbHelper.getTrademarkByID(trademarkID);

            nextTrademarkID = trademarkID;
        } else {
            nextTrademarkID = dbHelper.getNextTrademarkIDFromDB();
        }

        txtTitleTrademark.setText(txtTitleTrademark.getText() + " " + nextTrademarkID);

    }

    @Override
    public void onResume() {
        super.onResume();

        Log.i(LOG_TAG, "IN onResume");

        if (startTrademark != null) {
            editTrademark.setText(startTrademark.getTrademark());
            editBrewery.setText(startTrademark.getBrewery());
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_add_coaster, menu);
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
            saveTrademark();

            return true;
        }

        if (id == R.id.action_copy) {
            Snackbar snackbar = Snackbar.make(coordinatorLayout, "You clicked Copy", Snackbar.LENGTH_LONG);

            snackbar.show();

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

    private boolean saveTrademark() {
        boolean res = validateTrademark();

        if (res) {
            // Save trademark to DB:

            dbHelper.putTrademarkInDB(endTrademark);

            CoasterApplication.collectionData.mapTrademarks.put(endTrademark.getTrademarkID(), endTrademark);

//            TrademarkListActivity.refreshTrademarkList = true;
            CoasterApplication.refreshTrademarks = true;

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

    private boolean validateTrademark() {
        boolean res = true;

        String inputTrademark = (editTrademark.getEditableText() == null ? "" : editTrademark.getEditableText().toString());
        String inputBrewery = (editBrewery.getEditableText() == null ? "" : editBrewery.getEditableText().toString());

        if (startTrademark == null) {
            for (Trademark tr : CoasterApplication.collectionData.mapTrademarks.values()) {
                if (tr.getTrademark().equals(inputTrademark)) {
                    res = false;
                }
            }
        }

        if (inputTrademark.length() == 0) {
            res = false;
        }

        if (res) {
            endTrademark = new Trademark(nextTrademarkID);

            if (startTrademark != null) {
                endTrademark.setFetchedFromDB(startTrademark.isFetchedFromDB());
            }

            endTrademark.fillTrademark(inputTrademark, inputBrewery);
        }

        return res;
    }
}
