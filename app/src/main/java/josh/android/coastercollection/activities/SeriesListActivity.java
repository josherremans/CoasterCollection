package josh.android.coastercollection.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.SparseArray;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import josh.android.coastercollection.R;
import josh.android.coastercollection.adapters.SeriesExpandableListAdapter;
import josh.android.coastercollection.application.CoasterApplication;
import josh.android.coastercollection.bo.Series;
import josh.android.coastercollection.bo.Trademark;
import josh.android.coastercollection.bo.TrademarkSeriesGroup;
import josh.android.coastercollection.databank.CoasterCollectionDBHelper;

public class SeriesListActivity extends FabBaseActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private final static String LOG_TAG = "SERIES_LIST_ACTIVITY";

    private SeriesExpandableListAdapter seriesAdapter;
    private LinkedHashMap<Character, Integer> mapIndex;

    private ExpandableListView lstvwSeries;
    private SearchView searchView;
    private Toolbar toolbar;

    private String listViewType = "";

//    public static boolean refreshSeriesList = true;

    SparseArray<TrademarkSeriesGroup> lstTrademarkSeriesGroup = new SparseArray<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_series_list);

//        refreshSeriesList = true;
        CoasterApplication.refreshSeries = true;

        // *** Read shared preferences:

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);

        listViewType = CoasterApplication.listViewType; //"CardType");

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new FabOnClickListener());

        setFabVisible();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // *** VIEWS:

        lstvwSeries = (ExpandableListView) findViewById(R.id.lstSeries);

        lstTrademarkSeriesGroup = new SparseArray<>();

        populateList(false);

        seriesAdapter = new SeriesExpandableListAdapter(this, lstTrademarkSeriesGroup);

        lstvwSeries.setAdapter(seriesAdapter);

//        lstvwSeries.setOnItemLongClickListener(new SeriesListActivity.SeriesOnItemLongClickListener());

        lstvwSeries.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {
                    setFabVisible();

                    int i = lstvwSeries.getFirstVisiblePosition();
                    long p = lstvwSeries.getExpandableListPosition(i);
                    int g = lstvwSeries.getPackedPositionGroup(p);

//                    Toast.makeText(SeriesListActivity.this, "Pos: i=" + i + ", p=" + p + ", g=" + g, Toast.LENGTH_LONG).show();

                    setIndexPosition(lstTrademarkSeriesGroup.get(g).trademark.toUpperCase().charAt(0));
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
            }
        });

        searchView = (SearchView) findViewById(R.id.editSeriesSearch);

        searchView.setVisibility(View.GONE);
    }

    @Override
    protected void onResume() {
        super.onResume();

        // *** Hide SoftInputPanel:
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        imm.hideSoftInputFromWindow(searchView.getWindowToken(), 0);

        if (CoasterApplication.refreshSeries) {
            CoasterApplication.refreshSeries = false;

            // *** Read shared preferences:

            listViewType = CoasterApplication.listViewType;

            if (listViewType.equals(getResources().getStringArray(R.array.pref_listview_type_values)[0])) { // "CardType"
                lstvwSeries.setDivider(null);
            } else {
                lstvwSeries.setDividerHeight(5);
            }

            populateList(true);

            seriesAdapter.notifyDataSetChanged();

            toolbar.setSubtitle("(" + CoasterApplication.collectionData.lstSeries.size() + " / " + seriesAdapter.getGroupCount() + ")");

            createIndexMap(lstTrademarkSeriesGroup);

            displayIndex();

            Log.i(LOG_TAG, "END onResume!");
        }
    }

    private void populateList(boolean refreshFromDB) {
        int i = 0;
        long previousTrId = 0;
        TrademarkSeriesGroup group = null;

        CoasterCollectionDBHelper dbHelper = new CoasterCollectionDBHelper(this);

        if (refreshFromDB) {
            CoasterApplication.collectionData.lstSeries.clear();
            CoasterApplication.collectionData.lstSeries.addAll(dbHelper.getSeriesFromDB(-1));
            Log.i(LOG_TAG, "data.lstSeries: size:" + CoasterApplication.collectionData.lstSeries.size());
        }

        for (Series s : CoasterApplication.collectionData.lstSeries) {
            long trid = s.getTrademarkID();

            if (trid != previousTrId) {
                previousTrId = trid;

                Trademark tr = CoasterApplication.collectionData.mapTrademarks.get(trid);

                group = new TrademarkSeriesGroup(tr.getTrademark());

                lstTrademarkSeriesGroup.append(i++, group);
            }

            group.series.add(s);
        }
    }

    private void createIndexMap(SparseArray<TrademarkSeriesGroup> lstTrademarkSeriesGroup) {
        mapIndex = new LinkedHashMap<>();

        for (int i=0; i<lstTrademarkSeriesGroup.size(); i++) {
            TrademarkSeriesGroup t = lstTrademarkSeriesGroup.get(i);
            Character indexChar = t.trademark.toUpperCase().charAt(0);

            if (indexChar < 'A') {
                indexChar = '#';
            }

            if (indexChar > 'Z') {
                indexChar = '$';
            }

            if (mapIndex.get(indexChar) == null)
                mapIndex.put(indexChar, i);
        }
    }

    private void displayIndex() {
        LinearLayout indexLayout = (LinearLayout) findViewById(R.id.side_index);

        indexLayout.removeAllViews();

        TextView textView;

        List<Character> indexList = new ArrayList<>(mapIndex.keySet());

        for (int i=0; i<indexList.size(); i++) {
            Character indexChar = indexList.get(i);

            textView = (TextView) getLayoutInflater().inflate(R.layout.side_index_list_item, null);

            textView.setText(indexChar.toString());
            textView.setTag(R.id.TAG_INDEX, indexChar);

            if (i == 0) {
                setIndexViewCurrent(textView);
            }

            textView.setOnClickListener(new SeriesListActivity.IndexOnClickListener());
            indexLayout.addView(textView);
        }
    }

    private void setIndexPosition(Character c) {
        Character indexChar = c;

        if (!(c.equals('#') || c.equals('$'))) {
            if (c < 'A') {
                indexChar = '#';
            }

            if (c > 'Z') {
                indexChar = '$';
            }
        }

        Log.i(LOG_TAG, "setIndexPosition on char: " + c + ", indexChar: " + indexChar);

        LinearLayout indexLayout = (LinearLayout) findViewById(R.id.side_index);

        final int childCount = indexLayout.getChildCount();

        for (int i = 0; i < childCount; i++) {
            TextView v = (TextView) indexLayout.getChildAt(i);

            if (v.getTag(R.id.TAG_INDEX).equals(indexChar)) {
                Log.i(LOG_TAG, "TAG gevonden!");
                setIndexViewCurrent(v);
            } else {
                Log.i(LOG_TAG, "TAG " + v.getTag(R.id.TAG_INDEX));
                setIndexViewNormal(v);
            }
        }
    }

    private void setIndexViewNormal(TextView v) {
        v.setTypeface(null, Typeface.NORMAL);
        v.setBackgroundColor(getResources().getColor(R.color.side_index_background_color_normal));
    }

    private void setIndexViewCurrent(TextView v) {
        v.setTypeface(null, Typeface.BOLD);
        v.setBackgroundColor(getResources().getColor(R.color.side_index_background_color_current));
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_trademark_list, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        CoordinatorLayout coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinatorlayout);

        Snackbar snackbar;

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            snackbar = Snackbar.make(coordinatorLayout, "You clicked Settings", Snackbar.LENGTH_LONG);

            snackbar.show();

            this.startActivity(new Intent(this, SettingsActivity.class));

            return true;
        }

        if (id == R.id.action_adv_filter) {
            snackbar = Snackbar.make(coordinatorLayout, "You clicked Advanced Filter", Snackbar.LENGTH_LONG);

            snackbar.show();

            return true;
        }

        if (id == R.id.action_filter) {
            searchView.setVisibility(View.VISIBLE);

            return true;
        }

        if (id == R.id.action_export) {
            snackbar = Snackbar.make(coordinatorLayout, "You clicked Export", Snackbar.LENGTH_LONG);

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
            startActivity(new Intent(SeriesListActivity.this, CollectorListActivity.class));
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);

        return true;
    }

    /*
    **
     */
    private class IndexOnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            TextView selectedIndex = (TextView) view;
            String strDisplayName = selectedIndex.getText().toString();
            lstvwSeries.setSelectedGroup(mapIndex.get(strDisplayName.toUpperCase().charAt(0)));

            setIndexPosition(strDisplayName.toUpperCase().charAt(0));
        }
    }

    /*
    ** INNERCLASS: FabOnClickListener
     */
    private class FabOnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            startActivity(new Intent(SeriesListActivity.this, AddSeriesActivity.class));
        }
    }
}
