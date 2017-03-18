package josh.android.coastercollection.activities;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import josh.android.coastercollection.R;
import josh.android.coastercollection.bo.CollectionHistoryMatrix;
import josh.android.coastercollection.utils.Util;

public class StatisticsActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private final static String LOG_TAG = "STATISTICS_ACTIVITY";

//    private CoasterCollectionDBHelper dbHelper;

//    private TextView txtTitleTrademark;

    private CollectionHistoryMatrix stats;

    private TableLayout tableLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        stats = Util.createHistoryMatrix();

        // *** Titla:

//        txtTitleTrademark = (TextView) findViewById(txtTitleTrademark);

//        txtTitleTrademark.setText(txtTitleTrademark.getText() + " " + nextTrademarkID);

        tableLayout = (TableLayout) findViewById(R.id.layoutTableStatistics);
    }

    @Override
    public void onResume() {
        super.onResume();

        Log.i(LOG_TAG, "IN onResume");

        tableLayout.removeAllViews();

        addTitleRow();

        boolean startColor = true;

        for (int year=stats.getMaxYear(); year>=stats.getMinYear(); year--) {
            addYearRow(year, startColor);

            startColor = !startColor;
        }
    }

    private void addTitleRow() {
        TableRow titleRow = new TableRow(this);

        titleRow.setBackgroundResource(R.color.colorAccent);

        addNewColomnTextView(titleRow, " Year", true);
        addNewColomnTextView(titleRow, " Total", true);

        addNewColomnTextView(titleRow, " Jan", true);
        addNewColomnTextView(titleRow, " Feb", true);
        addNewColomnTextView(titleRow, " Mar", true);
        addNewColomnTextView(titleRow, " Apr", true);
        addNewColomnTextView(titleRow, " Mai", true);
        addNewColomnTextView(titleRow, " Jun", true);
        addNewColomnTextView(titleRow, " Jul", true);
        addNewColomnTextView(titleRow, " Aug", true);
        addNewColomnTextView(titleRow, " Sep", true);
        addNewColomnTextView(titleRow, " Oct", true);
        addNewColomnTextView(titleRow, " Nov", true);
        addNewColomnTextView(titleRow, " Dec", true);

        tableLayout.addView(titleRow);
    }

    private void addYearRow(int year, boolean startColor) {
        TableRow yearRow = new TableRow(this);

        if (startColor) {
            yearRow.setBackgroundResource(R.color.colorSpinnerGradientStart);
        } else {
            yearRow.setBackgroundResource(R.color.colorSpinnerGradientEnd);
        }

        addNewColomnTextView(yearRow, "" + year, true);
        TextView yearTotalTextView = addNewColomnTextView(yearRow, "", true);

        addNewColomnTextView(yearRow, "" + stats.getCount(year, 0));
        addNewColomnTextView(yearRow, "" + stats.getCount(year, 1));
        addNewColomnTextView(yearRow, "" + stats.getCount(year, 2));
        addNewColomnTextView(yearRow, "" + stats.getCount(year, 3));
        addNewColomnTextView(yearRow, "" + stats.getCount(year, 4));
        addNewColomnTextView(yearRow, "" + stats.getCount(year, 5));
        addNewColomnTextView(yearRow, "" + stats.getCount(year, 6));
        addNewColomnTextView(yearRow, "" + stats.getCount(year, 7));
        addNewColomnTextView(yearRow, "" + stats.getCount(year, 8));
        addNewColomnTextView(yearRow, "" + stats.getCount(year, 9));
        addNewColomnTextView(yearRow, "" + stats.getCount(year, 10));
        addNewColomnTextView(yearRow, "" + stats.getCount(year, 11));

        int yearTotal = 0;

        for (int month=0; month < 12; month++) {
            yearTotal += stats.getCount(year, month);
        }

        yearTotalTextView.setText("" + yearTotal);

        tableLayout.addView(yearRow);
    }

    private TextView addNewColomnTextView(TableRow row, String text) {
        return addNewColomnTextView(row, text, false);
    }

    private TextView addNewColomnTextView(TableRow row, String text, boolean bold) {
        TextView v = new TextView(this);

        v.setPadding(2, 2, 2, 2);

        v.setText(text);
        v.setTextSize(20);
        v.setMinWidth(100);
        v.setGravity(Gravity.END);

        if (bold) {
            v.setTypeface(null, Typeface.BOLD);
        }

        row.addView(v);

        return v;
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
            Snackbar snackbar = Snackbar.make(coordinatorLayout, "You clicked Save", Snackbar.LENGTH_LONG);

            snackbar.show();

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
}
