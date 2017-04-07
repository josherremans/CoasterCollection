package josh.android.coastercollection.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.SparseArray;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ExpandableListView;

import java.util.ArrayList;
import java.util.LinkedHashMap;

import josh.android.coastercollection.R;
import josh.android.coastercollection.adapters.SeriesExpandableListAdapter;
import josh.android.coastercollection.application.CoasterApplication;
import josh.android.coastercollection.bo.Series;
import josh.android.coastercollection.bo.Trademark;
import josh.android.coastercollection.bo.TrademarkSeriesGroup;

public class SeriesListActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private final static String LOG_TAG = "SERIES_LIST_ACTIVITY";
    private final static int TAG_INDEX = 1;

    private SeriesExpandableListAdapter seriesAdapter;
    private ArrayList<Trademark> filteredList = new ArrayList<>();
    private LinkedHashMap<Character, Integer> mapIndex;

    private ExpandableListView lstvwSeries;
    private SearchView searchView;
    private Toolbar toolbar;

    private String trademarkFilter = null;
    private String listViewType = "";

    public static boolean refreshTrademarkList = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_series_list);

        refreshTrademarkList = true;

        // *** Read shared preferences:

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);

        listViewType = sharedPref.getString(SettingsFragment.PREF_KEY_LISTVIEW_TYPE, getResources().getStringArray(R.array.pref_listview_type_values)[0]); //"CardType");

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new FabOnClickListener());

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // *** VIEWS:

        lstvwSeries = (ExpandableListView) findViewById(R.id.lstSeries);

        SparseArray<TrademarkSeriesGroup> lstTrademarkSeriesGroup = new SparseArray<>();

        int i = 0;
        long previousTrId = 0;
        TrademarkSeriesGroup group = null;

        for (Series s : CoasterApplication.collectionData.lstSeries) {
            long trid = s.getTrademarkID();

            if (trid != previousTrId) {
                previousTrId = trid;

                Trademark tr = CoasterApplication.collectionData.mapTrademarks.get(trid);

                group = new TrademarkSeriesGroup(tr.getTrademark());

                lstTrademarkSeriesGroup.append(i++, group);
            }

            group.series.add(s.getSeries());
        }

        seriesAdapter = new SeriesExpandableListAdapter(this, lstTrademarkSeriesGroup);

        lstvwSeries.setAdapter(seriesAdapter);

//        lstvwSeries.setOnItemLongClickListener(new SeriesOnItemLongClickListener());

//        lstvwSeries.setOnScrollListener(new AbsListView.OnScrollListener() {
//            @Override
//            public void onScrollStateChanged(AbsListView view, int scrollState) {
//                if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {
//                    int i = lstvwSeries.getFirstVisiblePosition();
//
//                    setIndexPosition(((Trademark) lstvwSeries.getAdapter().getItem(i)).getTrademark().charAt(0));
//                }
//            }
//
//            @Override
//            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
//            }
//        });

        searchView = (SearchView) findViewById(R.id.editSeriesSearch);

        searchView.setVisibility(View.GONE);

//        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
//            @Override
//            public boolean onQueryTextSubmit(String query) {
//                SeriesListActivity.this.trademarkFilter = query;
//
//                filteredList.clear();
//                filteredList.addAll(getTrademarkList(query));
//
//                seriesAdapter.notifyDataSetChanged();
//
//                toolbar.setSubtitle("(" + seriesAdapter.getCount() + ")");
//
//                createIndexMap(filteredList);
//
//                displayIndex();
//
//                return false;
//            }
//
//            @Override
//            public boolean onQueryTextChange(String newText) {
//                if ((newText == null) || (newText.length() == 0)) {
//                    SeriesListActivity.this.trademarkFilter = null;
//
//                    searchView.setVisibility(View.GONE);
//
////                    int n = CoasterApplication.collectionData.lstTrademarks.size();
//
//                    filteredList.clear();
//                    filteredList.addAll(collectionData.mapTrademarks.values()); //subList(1,n));
//
//                    seriesAdapter.notifyDataSetChanged();
//
//                    toolbar.setSubtitle("(" + seriesAdapter.getCount() + ")");
//
//                    createIndexMap(filteredList);
//
//                    displayIndex();
//                }
//
//                return false;
//            }
//        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        // *** Hide SoftInputPanel:
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        imm.hideSoftInputFromWindow(searchView.getWindowToken(), 0);

//        if (refreshTrademarkList) {
            refreshTrademarkList = false;

            // *** Read shared preferences:

            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);

            listViewType = sharedPref.getString(SettingsFragment.PREF_KEY_LISTVIEW_TYPE, getResources().getStringArray(R.array.pref_listview_type_values)[0]); //"CardType");

            // *** Fetch data from DB:

//            filteredList.clear();
//
//            if (trademarkFilter != null) {
//                filteredList.addAll(getTrademarkList(trademarkFilter));
//            } else {
////                int n = CoasterApplication.collectionData.lstTrademarks.size();
//
//                filteredList.addAll(collectionData.mapTrademarks.values()); //.subList(1,n));
//            }

//            trademarkAdapter = new TrademarkAdapter(this, listViewType, filteredList);

            if (listViewType.equals(getResources().getStringArray(R.array.pref_listview_type_values)[0])) { // "CardType"
                lstvwSeries.setDivider(null);
            } else {
                lstvwSeries.setDividerHeight(5);
            }

//            lstvwTrademarks.setAdapter(trademarkAdapter);

            seriesAdapter.notifyDataSetChanged();

            toolbar.setSubtitle("(" + seriesAdapter.getGroupCount() + ")");

//            createIndexMap(filteredList);

//            displayIndex();

            Log.i(LOG_TAG, "END onResume!");
//        }
    }

//    private ArrayList<Trademark> getTrademarkList(String strFilter) {
//        ArrayList<Trademark> lstFiltered = new ArrayList<>();
//
//        if ((strFilter != null) && (strFilter.length() > 0)) {
////            int n = CoasterApplication.collectionData.lstTrademarks.size();
//
//            for (Trademark t : collectionData.mapTrademarks.values()) {
//                if (t.getTrademark().regionMatches(true, 0, strFilter, 0, strFilter.length())) {
//                    lstFiltered.add(t);
//                }
//            }
//        }
//
//        return lstFiltered;
//    }

//    private void createIndexMap(ArrayList<Trademark> lstTrademark) {
//        mapIndex = new LinkedHashMap<>();
//
//        for (int i=0; i<lstTrademark.size(); i++) {
//            Trademark tr = lstTrademark.get(i);
//            Character indexChar = tr.getTrademark().charAt(0);
//
//            if (indexChar < 'A') {
//                indexChar = '#';
//            }
//
//            if (indexChar > 'Z') {
//                indexChar = '$';
//            }
//
//            if (mapIndex.get(indexChar) == null)
//                mapIndex.put(indexChar, i);
//        }
//    }
//
//    private void displayIndex() {
//        LinearLayout indexLayout = (LinearLayout) findViewById(R.id.side_index);
//
//        indexLayout.removeAllViews();
//
//        TextView textView;
//
//        List<Character> indexList = new ArrayList<>(mapIndex.keySet());
//
//        for (int i=0; i<indexList.size(); i++) {
//            Character indexChar = indexList.get(i);
//
//            textView = (TextView) getLayoutInflater().inflate(R.layout.side_index_list_item, null);
//
//            textView.setText(indexChar.toString());
//            textView.setTag(R.id.TAG_INDEX, indexChar);
//
//            if (i == 0) {
//                setIndexViewCurrent(textView);
//            }
//
//            textView.setOnClickListener(new IndexOnClickListener());
//            indexLayout.addView(textView);
//        }
//    }

//    private void setIndexPosition(Character c) {
//        Character indexChar = c;
//
//        if (!(c.equals('#') || c.equals('$'))) {
//            if (c < 'A') {
//                indexChar = '#';
//            }
//
//            if (c > 'Z') {
//                indexChar = '$';
//            }
//        }
//
//        Log.i(LOG_TAG, "setIndexPosition on char: " + c + ", indexChar: " + indexChar);
//
//        LinearLayout indexLayout = (LinearLayout) findViewById(R.id.side_index);
//
//        final int childCount = indexLayout.getChildCount();
//
//        for (int i = 0; i < childCount; i++) {
//            TextView v = (TextView) indexLayout.getChildAt(i);
//
//            if (v.getTag(R.id.TAG_INDEX).equals(indexChar)) {
//                Log.i(LOG_TAG, "TAG gevonden!");
//                setIndexViewCurrent(v);
//            } else {
//                Log.i(LOG_TAG, "TAG " + v.getTag(R.id.TAG_INDEX));
//                setIndexViewNormal(v);
//            }
//        }
//    }

//    private void setIndexViewNormal(TextView v) {
//        v.setTextColor(getResources().getColor(R.color.black_overlay));
//        v.setTypeface(null, Typeface.NORMAL);
//        v.setBackgroundColor(getResources().getColor(R.color.side_index_background_color_normal));
//    }
//
//    private void setIndexViewCurrent(TextView v) {
//        v.setTextColor(getResources().getColor(R.color.colorAccent));
//        v.setTypeface(null, Typeface.BOLD);
//        v.setBackgroundColor(getResources().getColor(R.color.side_index_background_color_light));
//    }

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
//    private class IndexOnClickListener implements View.OnClickListener {
//        @Override
//        public void onClick(View view) {
//            TextView selectedIndex = (TextView) view;
//            lstvwSeries.setSelection(mapIndex.get(selectedIndex.getText().charAt(0)));
//
//            setIndexPosition(selectedIndex.getText().charAt(0));
//        }
//    }

    /*
    ** INNERCLASS: FabOnClickListener
     */
    private class FabOnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            startActivity(new Intent(SeriesListActivity.this, AddTrademarkActivity.class));
        }
    }

    /*
    ** INNERCLASS: SeriesOnItemLongClickListener
     */
    private class SeriesOnItemLongClickListener implements AdapterView.OnItemLongClickListener {
        @Override
        public boolean onItemLongClick(AdapterView<?> parent, View view, int pos, long id) {
//            Trademark clickedTrademark = (Trademark) seriesAdapter.getItem(pos);
//
//            Intent alterTrademarkIntent = new Intent(SeriesListActivity.this, AddTrademarkActivity.class);
//
//            alterTrademarkIntent.putExtra("extraTrademarkID", clickedTrademark.getTrademarkID());
//
//            startActivity(alterTrademarkIntent);

            return true;
        }
    }
}
