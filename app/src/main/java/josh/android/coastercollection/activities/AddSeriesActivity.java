package josh.android.coastercollection.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;

import josh.android.coastercollection.R;
import josh.android.coastercollection.application.CoasterApplication;
import josh.android.coastercollection.bo.Series;
import josh.android.coastercollection.bo.Trademark;
import josh.android.coastercollection.databank.CoasterCollectionDBHelper;
import josh.android.coastercollection.enums.IIntentExtras;

import static josh.android.coastercollection.application.CoasterApplication.collectionData;

public class AddSeriesActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private final static String LOG_TAG = "ADD_SERIES_ACTIVITY";

    private AutoCompleteTextView editTrademark;
    private EditText editSeriesMaxNbr;
    private EditText editSeries;

    private Spinner spinTrademark;

    private CheckBox chkboxSeriesOrdered;

    private Series startSeries;
    private Series endSeries;

    private CoasterCollectionDBHelper dbHelper;

    private ArrayList<Trademark> lstTrademarks = new ArrayList<>();
    private ArrayAdapter<Trademark> adapterTrademarks;

    private TextView txtTitleAddSeries;

    private long nextSeriesID;

    private Trademark dummyTrademark = new Trademark(-1, "(Select Trademark)");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_series);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });

        Intent intentOrigine = this.getIntent();

        long seriesID = intentOrigine.getLongExtra(IIntentExtras.EXTRA_SERIESID, -1);

        editTrademark = (AutoCompleteTextView) findViewById(R.id.editTrademark);
        editSeries = (EditText) findViewById(R.id.editSeries);
        editSeriesMaxNbr = (EditText) findViewById(R.id.editSeriesMaxNbr);

        spinTrademark = (Spinner) findViewById(R.id.spinTrademark);

        chkboxSeriesOrdered = (CheckBox) findViewById(R.id.chkboxSeriesOrdered);

        // *** Spinners and others:

        editTrademark.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.i(LOG_TAG, "IN editTrademark.setOnItemClickListener");

                long trId = ((Trademark) parent.getItemAtPosition(position)).getTrademarkID();

                spinTrademark.setSelection(position);

//                updateSeriesOutput();
            }
        });

        spinTrademark.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Trademark tr = (Trademark) spinTrademark.getItemAtPosition(position);

                long trId = tr.getTrademarkID();

                if (trId == -1) {
                    editTrademark.setText("");
                } else {
                    editTrademark.setText(tr.getTrademark());
                    editTrademark.setSelection(editTrademark.length());
                }

//                updateSeriesOutput();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
//                updateSeriesOutput();
            }
        });

        // *** Adapters and their data:

        lstTrademarks.clear();
        lstTrademarks.add(0, dummyTrademark);
        lstTrademarks.addAll(collectionData.mapTrademarks.values());

        adapterTrademarks = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, lstTrademarks);

        spinTrademark.setAdapter(adapterTrademarks);
        editTrademark.setAdapter(adapterTrademarks);

        // *** DBHELPER:

        dbHelper = new CoasterCollectionDBHelper(this);

        // *** SeriesID:

        txtTitleAddSeries = (TextView) findViewById(R.id.txtTitleAddSeries);

        if (seriesID != -1) {
            startSeries = dbHelper.getSeriesByID(seriesID);

            nextSeriesID = seriesID;
        } else {
            nextSeriesID = dbHelper.getNextSeriesIDFromDB();
        }

        txtTitleAddSeries.setText(txtTitleAddSeries.getText() + " " + nextSeriesID);

    }

    @Override
    public void onResume() {
        super.onResume();

        Log.i(LOG_TAG, "IN onResume");

        String currentTrademark = "";

        if (startSeries != null) {
            for (int i = 0; i < lstTrademarks.size(); i++) {
                if (lstTrademarks.get(i).getTrademarkID() == startSeries.getTrademarkID()) {
                    spinTrademark.setSelection(i);

                    currentTrademark = lstTrademarks.get(i).getTrademark();

                    break;
                }
            }

            editTrademark.setText(currentTrademark);
            editSeries.setText(startSeries.getSeries());

            if (startSeries.getMaxNumber() != -1) {
                editSeriesMaxNbr.setText("" + startSeries.getMaxNumber());
            }

            chkboxSeriesOrdered.setChecked(startSeries.isOrdered());
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
            saveSeries();

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

    private boolean saveSeries() {
        boolean res = validateSeries();

        if (res) {
            // Save series to DB:

            dbHelper.putSeriesInDB(endSeries);

            if ((startSeries == null) || (!startSeries.isFetchedFromDB())) {
                CoasterApplication.collectionData.lstSeries.add(endSeries);
            }

//            SeriesListActivity.refreshSeriesList = true;
            CoasterApplication.refreshSeries = true;

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

    private boolean validateSeries() {
        boolean res = true;

        long inputTrademarkID = ((Trademark) spinTrademark.getSelectedItem()).getTrademarkID();
        String inputSeries = (editSeries.getEditableText() == null ? "" : editSeries.getEditableText().toString());
        long inputSeriesMaxNbr = (editSeriesMaxNbr.getEditableText() == null ? -1 : editSeriesMaxNbr.getEditableText().toString().length() == 0 ? -1 : Long.parseLong(editSeriesMaxNbr.getEditableText().toString()));
        boolean inputSeriesOrdered = chkboxSeriesOrdered.isChecked();

        if (startSeries == null) {
            for (Series ser : CoasterApplication.collectionData.lstSeries) {
                if ((ser.getTrademarkID() == inputTrademarkID) && (ser.getSeries().equals(inputSeries))) {
                    Log.e(LOG_TAG, "SERIES ALREADY EXISTS!");

                    res = false;
                }
            }
        }

        if (inputSeries.length() == 0) {
            Log.w(LOG_TAG, "SERIES IS EMPTY!");

            res = false;
        }

        if (res) {
            endSeries = new Series(nextSeriesID, inputTrademarkID, inputSeries, inputSeriesMaxNbr, inputSeriesOrdered);

            if (startSeries != null) {
                endSeries.setFetchedFromDB(startSeries.isFetchedFromDB());
            }
        }

        return res;
    }
}
