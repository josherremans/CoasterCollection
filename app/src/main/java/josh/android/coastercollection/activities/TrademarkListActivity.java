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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import josh.android.coastercollection.R;
import josh.android.coastercollection.adapters.TrademarkAdapter;
import josh.android.coastercollection.application.CoasterApplication;
import josh.android.coastercollection.bo.Trademark;

public class TrademarkListActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private final static String LOG_TAG = "TRADMARK_LIST_ACTIVITY";

//    private CoasterCollectionDBHelper dbHelper = new CoasterCollectionDBHelper(this);

    private TrademarkAdapter trademarkAdapter;
    private ArrayList<Trademark> filteredList = new ArrayList<>();
    private LinkedHashMap<Character, Integer> mapIndex;

    private ListView lstvwTrademarks;
    private SearchView searchView;
    private Toolbar toolbar;

//    private ProgressBar progressBar;

    private String trademarkFilter = null;
    private String listViewType = "";

    public static boolean refreshTrademarkList = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trademark_list);

//        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        refreshTrademarkList = true;

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

        lstvwTrademarks = (ListView) findViewById(R.id.lstTrademarks);

        lstvwTrademarks.setOnItemLongClickListener(new TrademarkOnItemLongClickListener());

        lstvwTrademarks.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {
                    int i = lstvwTrademarks.getFirstVisiblePosition();

                    alterIndex((Trademark) lstvwTrademarks.getAdapter().getItem(i));
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
            }
        });

        searchView = (SearchView) findViewById(R.id.editTrademarkSearch);

        searchView.setVisibility(View.GONE);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                TrademarkListActivity.this.trademarkFilter = query;

                filteredList.clear();
                filteredList.addAll(getTrademarkList(query));

                trademarkAdapter.notifyDataSetChanged();

                getIndexList(filteredList);

                alterIndex(filteredList.get(0));

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if ((newText == null) || (newText.length() == 0)) {
                    TrademarkListActivity.this.trademarkFilter = null;

                    searchView.setVisibility(View.GONE);

                    int n = CoasterApplication.collectionData.lstTrademarks.size();

                    filteredList.clear();
                    filteredList.addAll(CoasterApplication.collectionData.lstTrademarks.subList(1,n));

                    trademarkAdapter.notifyDataSetChanged();

                    getIndexList(filteredList);

                    alterIndex(filteredList.get(0));
                }

                return false;
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        // *** Hide SoftInputPanel:
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        imm.hideSoftInputFromWindow(searchView.getWindowToken(), 0);

        if (refreshTrademarkList) {
            refreshTrademarkList = false;

            // *** Read shared preferences:

            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);

            listViewType = sharedPref.getString(SettingsFragment.PREF_KEY_LISTVIEW_TYPE, getResources().getStringArray(R.array.pref_listview_type_values)[0]); //"CardType");

            // *** Fetch data from DB:

            if (trademarkFilter != null) {
                filteredList.addAll(getTrademarkList(trademarkFilter));
            } else {
                int n = CoasterApplication.collectionData.lstTrademarks.size();

                filteredList.addAll(CoasterApplication.collectionData.lstTrademarks.subList(1,n));
            }

            trademarkAdapter = new TrademarkAdapter(this, listViewType, filteredList);

            if (listViewType.equals(getResources().getStringArray(R.array.pref_listview_type_values)[0])) { // "CardType"
                lstvwTrademarks.setDivider(null);
            } else {
                lstvwTrademarks.setDividerHeight(5);
            }

            lstvwTrademarks.setAdapter(trademarkAdapter);

            getIndexList(filteredList);

            alterIndex(filteredList.get(0));

            Log.i(LOG_TAG, "END onResume!");
        }
    }

    private ArrayList<Trademark> getTrademarkList(String strFilter) {
        ArrayList<Trademark> lstFiltered = new ArrayList<>();

        if ((strFilter != null) && (strFilter.length() > 0)) {
            int n = CoasterApplication.collectionData.lstTrademarks.size();

            for (Trademark t : CoasterApplication.collectionData.lstTrademarks.subList(1, n)) {
                if (t.getTrademark().regionMatches(true, 0, strFilter, 0, strFilter.length())) {
                    lstFiltered.add(t);
                }
            }
        }

        return lstFiltered;
    }

    private void getIndexList(ArrayList<Trademark> lstTrademark) {
        mapIndex = new LinkedHashMap<>();

        for (int i=0; i<lstTrademark.size(); i++) {
            Trademark tr = lstTrademark.get(i);
            Character index = tr.getTrademark().charAt(0);

            if (index < 'A') {
                index = '#';
            }

            if (index > 'Z') {
                index = '$';
            }

            if (mapIndex.get(index) == null)
                mapIndex.put(index, i);
        }
    }

    private void displayIndex(Character currentIndexPos) {
        LinearLayout indexLayout = (LinearLayout) findViewById(R.id.side_index);

        indexLayout.removeAllViews();

        TextView textView;

        List<Character> indexList = new ArrayList<>(mapIndex.keySet());

        for (Character index : indexList) {
            textView = (TextView) getLayoutInflater().inflate(R.layout.side_index_trademark_list_item, null);

            textView.setText(index.toString());

            if (index.equals(currentIndexPos)) {
                textView.setTextColor(getResources().getColor(R.color.colorAccent));
            }

            textView.setOnClickListener(new IndexOnClickListener());
            indexLayout.addView(textView);
        }
    }

    private void alterIndex(Trademark tr) {
        Character currentIndexPos = tr.getTrademark().charAt(0);

        if (currentIndexPos < 'A') {
            currentIndexPos = '#';
        }

        if (currentIndexPos > 'Z') {
            currentIndexPos = '$';
        }

        displayIndex(currentIndexPos);
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
//            startActivity(new Intent(CoasterListActivity.this, TrademarkListActivity.class));
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

    /*
    **
     */
    private class IndexOnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            TextView selectedIndex = (TextView) view;
            lstvwTrademarks.setSelection(mapIndex.get(selectedIndex.getText().charAt(0)));

            displayIndex(selectedIndex.getText().charAt(0));
        }
    }

    /*
    ** INNERCLASS: FabOnClickListener
     */
    private class FabOnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            startActivity(new Intent(TrademarkListActivity.this, AddTrademarkActivity.class));
        }
    }

    /*
    ** INNERCLASS: TrademarkOnItemLongClickListener
     */
    private class TrademarkOnItemLongClickListener implements AdapterView.OnItemLongClickListener {
        @Override
        public boolean onItemLongClick(AdapterView<?> parent, View view, int pos, long id) {
            Trademark clickedTrademark = (Trademark) trademarkAdapter.getItem(pos);

            Intent alterTrademarkIntent = new Intent(TrademarkListActivity.this, AddTrademarkActivity.class);

            alterTrademarkIntent.putExtra("extraTrademarkID", clickedTrademark.getTrademarkID());

            startActivity(alterTrademarkIntent);

            return true;
        }
    }
}
