package josh.android.coastercollection.activities;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.DataSetObserver;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
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
import android.widget.Toast;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import josh.android.coastercollection.R;
import josh.android.coastercollection.adapters.CoasterCollectionAdapter;
import josh.android.coastercollection.adapters.LoadCoastersAsyncTask;
import josh.android.coastercollection.application.CoasterApplication;
import josh.android.coastercollection.bl.ImageManager;
import josh.android.coastercollection.bo.Coaster;
import josh.android.coastercollection.bo.Trademark;
import josh.android.coastercollection.databank.CoasterCollectionDBHelper;

import static josh.android.coastercollection.application.CoasterApplication.collectionData;


public class CoasterListActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private final static String LOG_TAG = "COASTER_LIST_ACTIVITY";

    //keep track of camera capture intent
    private final static int CAMERA_CAPTURE = 11;

    //keep track of cropping intent
    private final static int PIC_CROP = 21;

    private CoasterCollectionDBHelper dbHelper = new CoasterCollectionDBHelper(this);

    private CoasterCollectionAdapter coasterCollectionAdapter;

    private ListView lstvwCoasterCollection;
    private SearchView searchView;
    private Toolbar toolbar;

    private ProgressBar progressBar;

    private String trademarkFilter = null;
    private boolean isReverseOrder = false;
    private String listViewType = "";

    private String cameraImageTimestamp = "";

    public static boolean refreshCoasterList = true;

    private AsyncTask loadNewDataTask;

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

        coasterCollectionAdapter = new CoasterCollectionAdapter(this, listViewType);

        coasterCollectionAdapter.registerDataSetObserver(new CoasterCollectionDataSetObserver());

        searchView = (SearchView) findViewById(R.id.editTrademarkSearch);
    }

    @Override
    public void onRestart() {
        super.onRestart();

        Log.i(LOG_TAG, "IN onRestart");
    }

    @Override
    public void onStart() {
        super.onStart();

        Log.i(LOG_TAG, "IN onStart");

        lstvwCoasterCollection.setOnItemLongClickListener(new CoasterOnItemLongClickListener());

        lstvwCoasterCollection.setAdapter(coasterCollectionAdapter);

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
    public void onPause() {
        super.onPause();
        Log.i(LOG_TAG, "IN onPause!");

        Long idObj = (Long) coasterCollectionAdapter.getItem(lstvwCoasterCollection.getFirstVisiblePosition());

        if (idObj != null) {
            CoasterApplication.currentCoasterID = (long) idObj;
        } else {
            CoasterApplication.currentCoasterID = -1;
        }

        Log.i(LOG_TAG, "CoasterApplication.currentCoasterID SET to: " + CoasterApplication.currentCoasterID);

        loadNewDataTask.cancel(true);
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.i(LOG_TAG, "IN onStop!");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(LOG_TAG, "IN onDestroy!");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(LOG_TAG, "IN onResume!");

        // *** Hide SoftInputPanel:
//        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
//
//        imm.hideSoftInputFromWindow(searchView.getWindowToken(), 0);

        Log.i(LOG_TAG, "onResume! notify=" + collectionData.notifyAdapter());

        if (CoasterApplication.collectionData.notifyAdapter()) {
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
        } else {
            if (CoasterApplication.currentCoasterID != -1) {
                Log.i(LOG_TAG, "Scroll to ID " + CoasterApplication.currentCoasterID);

                int pos = coasterCollectionAdapter.getPositionOfCoaster(CoasterApplication.currentCoasterID);

                lstvwCoasterCollection.setSelection(pos);
            }
        }

        Log.i(LOG_TAG, "END onResume!");
    }

    private void loadNewData(String trademarkFilter) {
        loadNewDataTask = new LoadCoastersAsyncTask(dbHelper, coasterCollectionAdapter, isReverseOrder, progressBar, toolbar).execute();
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
        ArrayList<Trademark> lstTrademarks = new ArrayList<>(collectionData.mapTrademarks.values());

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

        if (id == R.id.action_settings) {
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
            performTakePicture(CAMERA_CAPTURE);
        } else if (id == R.id.nav_gallery) {
            startActivity(new Intent(CoasterListActivity.this, GalleryActivity.class));
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

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        String imageName, path;
        File imageFile;
        Uri imageUri;
        Bitmap bm;

        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case CAMERA_CAPTURE:
                    imageName = "Coaster_" + cameraImageTimestamp + "_camera.jpg";

                    path = ImageManager.DIR_TEMP_IMAGES + File.separator + imageName;

                    imageFile = new File(path);
                    imageUri = Uri.fromFile(imageFile);

                    //carry out the crop operation
                    performCrop(imageUri, PIC_CROP);

                    break;

                case PIC_CROP:
                    break;

                default:
                    break;
            }
        }
    }

    private void performTakePicture(int requestCode) {
        try {
            cameraImageTimestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());

            String imageName = "Coaster_" + cameraImageTimestamp + "_camera.jpg";
            String path = ImageManager.DIR_TEMP_IMAGES + File.separator + imageName;

            File file = new File(path);
            Uri outputFileUri = Uri.fromFile(file);

            //use standard intent to capture an image
            Intent captureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

            captureIntent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);

            //we will handle the returned data in onActivityResult
            startActivityForResult(captureIntent, requestCode);
        } catch (ActivityNotFoundException ex) {
            //display an error message
            String errorMessage = "Whoops - your device doesn't support capturing images!";

            Toast.makeText(CoasterListActivity.this, errorMessage, Toast.LENGTH_SHORT).show();

            Log.e(LOG_TAG, errorMessage);
        }
    }

    private void performCrop(Uri picUri, int requestCode) {
        try {
            //call the standard crop action intent (the user device may not support it)
            Intent cropIntent = new Intent("com.android.camera.action.CROP");

            //indicate image type and Uri
            cropIntent.setDataAndType(picUri, "image/*");

            //set crop properties
            cropIntent.putExtra("crop", "true");

            // don't retrieve data on return
            cropIntent.putExtra("return-data", false);

            String imageName = "Coaster_" + cameraImageTimestamp + "_camera.jpg";
            String path = ImageManager.DIR_TEMP_IMAGES + File.separator + imageName;

            File file = new File(path);
            cropIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));

            //start the activity - we handle returning in onActivityResult
            startActivityForResult(cropIntent, requestCode);
        } catch(ActivityNotFoundException ex) {
            //display an error message
            String errorMessage = "Whoops - your device doesn't support the crop action!";

            Toast.makeText(CoasterListActivity.this, errorMessage, Toast.LENGTH_SHORT).show();

            throw ex;
        }
    }

    /*
    ** INNERCLASS: CoasterCollectionDataSetObserver
     */
    private class CoasterCollectionDataSetObserver extends DataSetObserver {
        @Override
        public void onChanged() {
            super.onChanged();

            Log.i(LOG_TAG, "DataSetObserver: Scroll to ID " + CoasterApplication.currentCoasterID);

            int pos = coasterCollectionAdapter.getPositionOfCoaster(CoasterApplication.currentCoasterID);

            lstvwCoasterCollection.setSelection(pos);
        }
    }

    /*
    ** INNERCLASS: FabOnClickListener
     */
    private class FabOnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            long currentCoasterID = (long) coasterCollectionAdapter.getItem(lstvwCoasterCollection.getFirstVisiblePosition());

            long nextFreeCoasterID =  collectionData.getNextFreeCoasterID(currentCoasterID);

            Intent newCoasterIntent = new Intent(CoasterListActivity.this, AddCoasterActivity.class);

            newCoasterIntent.putExtra("extraNewCoasterID", nextFreeCoasterID);

            startActivity(newCoasterIntent);
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
