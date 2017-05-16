package josh.android.coastercollection.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import java.util.ArrayList;
import java.util.Collections;

import josh.android.coastercollection.R;
import josh.android.coastercollection.adapters.GalleryAdapter;
import josh.android.coastercollection.application.CoasterApplication;
import josh.android.coastercollection.bl.CoasterComparatorBySeries;
import josh.android.coastercollection.bo.Coaster;
import josh.android.coastercollection.bo.GalleryItem;
import josh.android.coastercollection.bo.Series;
import josh.android.coastercollection.enums.IIntentExtras;

public class GalleryActivity extends AppCompatActivity {

    private final static String LOG_TAG = "GALLERY_ACTIVITY";

    private Toolbar toolbar;

    private GalleryAdapter adapter = null;

    private int n_coasters_shown;
    private static int n_cols = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);

        Log.i(LOG_TAG, "onCreate");

        Intent intentOrigine = this.getIntent();

        Long seriesID = intentOrigine.getLongExtra(IIntentExtras.EXTRA_SERIESID, -1);
        Long trademarkID = intentOrigine.getLongExtra(IIntentExtras.EXTRA_TRADEMARKID, -1);
        Long collectorID = intentOrigine.getLongExtra(IIntentExtras.EXTRA_COLLECTORID, -1);
        String name = intentOrigine.getStringExtra(IIntentExtras.EXTRA_GALLERY_NAME);

        if (name == null) {
            name = "";
        }

        toolbar = (Toolbar) findViewById(R.id.toolbar);

        if (toolbar != null) {
            setSupportActionBar(toolbar);
        }

        RecyclerView recyclerView = createRecyclerView(n_cols);

        ArrayList<GalleryItem> galleryItems = prepareData(seriesID, trademarkID, collectorID);

        adapter = new GalleryAdapter(getApplicationContext(), galleryItems);

        recyclerView.setAdapter(adapter);

        if (toolbar != null) {
            // Remark: toolbar.setTitle("...") does not work! Have to use getSupportActionBar().setTitle("...")!!!

            if (seriesID != -1) {
                getSupportActionBar().setTitle("Series");
                toolbar.setSubtitle("(#" + n_coasters_shown + ") " + name);
            } else if (trademarkID != -1) {
                getSupportActionBar().setTitle("Trademark");
                toolbar.setSubtitle("(#" + n_coasters_shown + ") " + name);
            } else if (collectorID != -1) {
                getSupportActionBar().setTitle("Collector");
                toolbar.setSubtitle("(#" + n_coasters_shown + ") " + name);
            } else {
                getSupportActionBar().setTitle("Collection");
                toolbar.setSubtitle("(#" + n_coasters_shown + ")");
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        Log.i(LOG_TAG, "onResume");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_gallery, menu);

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

        if (id == R.id.action_zoom_in) {
            if (n_cols > 1) {
                n_cols--;
            }

            RecyclerView recyclerView = createRecyclerView(n_cols);

            recyclerView.setAdapter(adapter);

//            adapter.notifyDataSetChanged();

            return true;
        }

        if (id == R.id.action_zoom_out) {
            if (n_cols < 4) {
                n_cols++;
            }

            RecyclerView recyclerView = createRecyclerView(n_cols);

            recyclerView.setAdapter(adapter);

//            adapter.notifyDataSetChanged();

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private RecyclerView createRecyclerView(int n_cols) {
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.imagegallery);
        recyclerView.setHasFixedSize(true);

        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getApplicationContext(), n_cols);

        recyclerView.setLayoutManager(layoutManager);

        return recyclerView;
    }

    private ArrayList<GalleryItem> prepareData(long seriesID, long trademarkID, long collectorID){
        ArrayList<Coaster> lstCoasters = new ArrayList<>();
        ArrayList<GalleryItem> lstImages = new ArrayList<>();

//        int l = CoasterApplication.collectionData.mapCoasters.values().size();

        for(Coaster c:  CoasterApplication.collectionData.mapCoasters.values()){
            if (((seriesID != -1) && (c.getCoasterSeriesID() == seriesID))
                || ((trademarkID != -1) && (c.getCoasterTrademarkID() == trademarkID))
                    || ((collectorID != -1) && (c.getCollectorID() == collectorID))
                || ((seriesID == -1) && (trademarkID == -1) && (collectorID == -1))) {

                lstCoasters.add(c);
            }
        }

        n_coasters_shown = lstCoasters.size();

        if (seriesID != -1) {
            Series seriesGallery = null;

            for (Series s: CoasterApplication.collectionData.lstSeries) {
                if (s.getSeriesID() == seriesID) {
                    seriesGallery = s;
                    break;
                }
            }

            if (seriesGallery.isOrdered()) {
                Collections.sort(lstCoasters, new CoasterComparatorBySeries());

                ArrayList<Coaster> lstAdd = new ArrayList<>();

                for (int i=1; i<=seriesGallery.getMaxNumber(); i++) {
                    boolean found = false;

                    for (Coaster c: lstCoasters) {
                        if (c.getCoasterSeriesIndex() == i) {
                            found = true;
                            break;
                        }
                    }

                    if (!found) {
                        Coaster c = new Coaster(-1);

                        c.setCoasterSeriesID(seriesID);
                        c.setCoasterSeriesIndex(i);
                        c.setCoasterImageFrontName("-");

                        lstAdd.add(c);
                    }
                }

                lstCoasters.addAll(lstAdd);

                Collections.sort(lstCoasters, new CoasterComparatorBySeries());
            }
        }

        for (Coaster c: lstCoasters) {
            if ((c.getCoasterImageFrontName() != null)
                    && (c.getCoasterImageFrontName().length() > 0)) {
                GalleryItem galleryItem = new GalleryItem();

                galleryItem.setImageName(c.getCoasterImageFrontName());
                galleryItem.setCoasterID(c.getCoasterID());

                if (seriesID != -1) {
                    galleryItem.setSeriesNbr(c.getCoasterSeriesIndex());
                }

                lstImages.add(galleryItem);
            }

            if ((c.getCoasterImageBackName() != null)
                    && (c.getCoasterImageBackName().length() > 0)) {
                GalleryItem galleryItem = new GalleryItem();

                galleryItem.setImageName(c.getCoasterImageBackName());
                galleryItem.setCoasterID(c.getCoasterID());

                if (seriesID != -1) {
                    galleryItem.setSeriesNbr(c.getCoasterSeriesIndex());
                }

                lstImages.add(galleryItem);
            }
        }

        return lstImages;
    }
}
