package josh.android.coastercollection.activities;

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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;

import java.util.ArrayList;

import josh.android.coastercollection.R;
import josh.android.coastercollection.adapters.CoasterCollectionAdapter;
import josh.android.coastercollection.adapters.LoadCoastersAsyncTask;
import josh.android.coastercollection.application.CoasterApplication;
import josh.android.coastercollection.bo.Coaster;
import josh.android.coastercollection.bo.Trademark;
import josh.android.coastercollection.databank.CoasterCollectionDBHelper;

import static josh.android.coastercollection.application.CoasterApplication.collectionData;

public class CoasterListActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private final static String LOG_TAG = "COASTER_LIST_ACTIVITY";

    private CoasterCollectionDBHelper dbHelper = new CoasterCollectionDBHelper(this);

    private CoasterCollectionAdapter coasterCollectionAdapter;

    private ListView lstvwCoasterCollection;
    private SearchView searchView;
    private Toolbar toolbar;

    private ProgressBar progressBar;

    private String trademarkFilter = null;
    private boolean isReverseOrder = false;
    private String listViewType = "";

    public static boolean refreshCoasterList = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coaster_list);

        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        refreshCoasterList = true;

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

        // *** Read shared preferences:

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        isReverseOrder = sharedPref.getBoolean(SettingsFragment.PREF_KEY_SORT_REVERSE, false);

        listViewType = sharedPref.getString(SettingsFragment.PREF_KEY_LISTVIEW_TYPE, getResources().getStringArray(R.array.pref_listview_type_values)[0]); //"CardType");

        // *** VIEWS:

        lstvwCoasterCollection = (ListView) findViewById(R.id.lstCoasterCollection);

        if (listViewType.equals(getResources().getStringArray(R.array.pref_listview_type_values)[0])) { // "CardType"
            lstvwCoasterCollection.setDivider(null);
        } else {
            lstvwCoasterCollection.setDividerHeight(5);
        }

        lstvwCoasterCollection.setOnItemLongClickListener(new CoasterOnItemLongClickListener());

        coasterCollectionAdapter = new CoasterCollectionAdapter(this, listViewType);

        lstvwCoasterCollection.setAdapter(coasterCollectionAdapter);

        searchView = (SearchView) findViewById(R.id.editTrademarkSearch);

        searchView.setVisibility(View.GONE);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Log.i(LOG_TAG, "onQueryTextSubmit!");

                CoasterListActivity.this.trademarkFilter = query;

                coasterCollectionAdapter.updateCoasterForList(getCoasterIds(query));

                toolbar.setSubtitle("(" + coasterCollectionAdapter.getCount() + ")");

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                Log.i(LOG_TAG, "onQueryTextChange!");

                if ((newText == null) || (newText.length() == 0)) {
                    Log.i(LOG_TAG, "onQueryTextChange! EMPTY");

                    CoasterListActivity.this.trademarkFilter = null;

                    coasterCollectionAdapter.updateCoasterForList(getCoasterIds(null));

                    toolbar.setSubtitle("(" + coasterCollectionAdapter.getCount() + ")");
                }

                return false;
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(LOG_TAG, "onResume!");

        // *** Hide SoftInputPanel:
//        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
//
//        imm.hideSoftInputFromWindow(searchView.getWindowToken(), 0);

        if (collectionData.notifyAdapter()) {
            coasterCollectionAdapter.notifyDataSetChanged();
        }

        if (refreshCoasterList) {
            Log.i(LOG_TAG, "onResume! Refreshing!");

            refreshCoasterList = false;

            // *** Read shared preferences:

            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
            isReverseOrder = sharedPref.getBoolean(SettingsFragment.PREF_KEY_SORT_REVERSE, false);

            listViewType = sharedPref.getString(SettingsFragment.PREF_KEY_LISTVIEW_TYPE, getResources().getStringArray(R.array.pref_listview_type_values)[0]); //"CardType");

            // *** Fetch data from DB:

            loadNewData(this.trademarkFilter);
        }

        Log.i(LOG_TAG, "END onResume!");
    }

    private void loadNewData(String trademarkFilter) {
        new LoadCoastersAsyncTask(dbHelper, coasterCollectionAdapter, isReverseOrder, progressBar, toolbar).execute();
    }

    private ArrayList<Long> getCoasterIds(String filterByTrademark) {
        ArrayList<Long> lstCoasterIds = new ArrayList<>();

        ArrayList<Long> lstTrademarkIds = new ArrayList<>();

        if (filterByTrademark != null) {
            lstTrademarkIds = getTrademarkIdList(filterByTrademark);
        }

        if (filterByTrademark == null) {
            lstCoasterIds.addAll(collectionData.mapCoasters.keySet());
        } else {
            for (Coaster c : collectionData.mapCoasters.values()) {
                for (Long trId : lstTrademarkIds) {
                    if (c.getCoasterTrademarkID() == trId.longValue()) {
                        lstCoasterIds.add(c.getCoasterID());
                        break;
                    }
                }
            }
        }

        return lstCoasterIds;
    }

    private ArrayList<Long> getTrademarkIdList(String filterByTrademark) {
//        LinkedHashMap<Long, Trademark> mapTrademarks = dbHelper.getTrademarksFromDB();

        ArrayList<Trademark> lstTrademarks = new ArrayList<>(CoasterApplication.collectionData.mapTrademarks.values());

        ArrayList<Long> lstTrademarkIds = new ArrayList<>();

        if ((filterByTrademark != null) && (filterByTrademark.length() > 0)) {
            for (Trademark t : lstTrademarks) {
                if (t.getTrademark().regionMatches(true, 0, filterByTrademark, 0, filterByTrademark.length())) {
                    lstTrademarkIds.add(t.getTrademarkID());
                }
            }
        }

        return lstTrademarkIds;
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
        getMenuInflater().inflate(R.menu.menu_coaster_list, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        Log.i(LOG_TAG, "item id clicked: " + id);

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
            if (searchView.getVisibility() == View.VISIBLE) {
                searchView.setQuery("", false);
                searchView.setVisibility(View.GONE);

                Log.i(LOG_TAG, "searchView GONE gezet!");
            } else {
                searchView.setVisibility(View.VISIBLE);

                Log.i(LOG_TAG, "searchView VISIBLE gezet!");
            }

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
            startActivity(new Intent(CoasterListActivity.this, StatisticsActivity.class));
        } else if (id == R.id.nav_trademarks) {
            startActivity(new Intent(CoasterListActivity.this, TrademarkListActivity.class));
        } else if (id == R.id.nav_series) {
            startActivity(new Intent(CoasterListActivity.this, SeriesListActivity.class));
        } else if (id == R.id.nav_donors) {
            startActivity(new Intent(CoasterListActivity.this, CollectorListActivity.class));
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);

        return true;
    }

    /*
    ** INNERCLASS: FabOnClickListener
     */
    private class FabOnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            startActivity(new Intent(CoasterListActivity.this, AddCoasterActivity.class));
        }
    }

    /*
    ** INNERCLASS: CoasterOnItemLongClickListener
     */
    private class CoasterOnItemLongClickListener implements AdapterView.OnItemLongClickListener {
        @Override
        public boolean onItemLongClick(AdapterView<?> parent, View view, int pos, long id) {
            Coaster clickedCoaster = coasterCollectionAdapter.getRealItem(pos); //CoasterApplication.collectionData.lstCoasters.get(pos);

            Intent alterCoasterIntent = new Intent(CoasterListActivity.this, AddCoasterActivity.class);

            alterCoasterIntent.putExtra("extraCoasterID", clickedCoaster.getCoasterID());

            startActivity(alterCoasterIntent);

            return true;
        }
    }
}
