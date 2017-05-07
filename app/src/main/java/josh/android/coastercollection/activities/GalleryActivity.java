package josh.android.coastercollection.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import java.util.ArrayList;

import josh.android.coastercollection.R;
import josh.android.coastercollection.adapters.GalleryAdapter;
import josh.android.coastercollection.application.CoasterApplication;
import josh.android.coastercollection.bo.Coaster;
import josh.android.coastercollection.bo.GalleryItem;
import josh.android.coastercollection.enums.IIntentExtras;

public class GalleryActivity extends AppCompatActivity {

    private final static String LOG_TAG = "GALLERY_ACTIVITY";

    private Toolbar toolbar;

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

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.imagegallery);
        recyclerView.setHasFixedSize(true);

        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getApplicationContext(),2);

        recyclerView.setLayoutManager(layoutManager);

        ArrayList<GalleryItem> galleryItems = prepareData(seriesID, trademarkID, collectorID);

        GalleryAdapter adapter = new GalleryAdapter(getApplicationContext(), galleryItems);

        recyclerView.setAdapter(adapter);

        if (toolbar != null) {
            //TODO Add name to subtitle in Gallery toolbar!

            // Remark: toolbar.setTitle("...") does not work! Have to use getSupportActionBar().setTitle("...")!!!

            if (seriesID != -1) {
                getSupportActionBar().setTitle("Series");
                toolbar.setSubtitle("(#" + adapter.getItemCount() + ") " + name);
            } else if (trademarkID != -1) {
                getSupportActionBar().setTitle("Trademark");
                toolbar.setSubtitle("(#" + adapter.getItemCount() + ") " + name);
            } else if (collectorID != -1) {
                getSupportActionBar().setTitle("Collector");
                toolbar.setSubtitle("(#" + adapter.getItemCount() + ") " + name);
            } else {
                getSupportActionBar().setTitle("Collection");
                toolbar.setSubtitle("(#" + adapter.getItemCount() + ")");
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        Log.i(LOG_TAG, "onResume");
    }

    private ArrayList<GalleryItem> prepareData(long seriesID, long trademarkID, long collectorID){
        ArrayList<GalleryItem> lstImages = new ArrayList<>();

        int l = CoasterApplication.collectionData.mapCoasters.values().size();

        for(Coaster c:  CoasterApplication.collectionData.mapCoasters.values()){
            if (((seriesID != -1) && (c.getCoasterSeriesID() == seriesID))
                || ((trademarkID != -1) && (c.getCoasterTrademarkID() == trademarkID))
                    || ((collectorID != -1) && (c.getCollectorID() == collectorID))
                || ((seriesID == -1) && (trademarkID == -1) && (collectorID == -1))) {
                if ((c.getCoasterImageFrontName() != null)
                    && (c.getCoasterImageFrontName().length() > 0)) {
                    GalleryItem galleryItem = new GalleryItem();

                    galleryItem.setImage_title(c.getCoasterImageFrontName());
                    galleryItem.setImage_ID(c.getCoasterID());

                    lstImages.add(galleryItem);
                }

                if ((c.getCoasterImageBackName() != null)
                        && (c.getCoasterImageBackName().length() > 0)) {
                    GalleryItem galleryItem = new GalleryItem();

                    galleryItem.setImage_title(c.getCoasterImageBackName());
                    galleryItem.setImage_ID(c.getCoasterID());

                    lstImages.add(galleryItem);
                }
            }
        }

        return lstImages;
    }
}
