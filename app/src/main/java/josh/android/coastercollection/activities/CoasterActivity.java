package josh.android.coastercollection.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.File;

import josh.android.coastercollection.R;
import josh.android.coastercollection.application.CoasterApplication;
import josh.android.coastercollection.bl.ImageManager;
import josh.android.coastercollection.bo.Coaster;
import josh.android.coastercollection.bo.Series;
import josh.android.coastercollection.bo.Shape;
import josh.android.coastercollection.enums.IIntentExtras;
import josh.android.coastercollection.utils.Util;

import static josh.android.coastercollection.bl.ImageManager.DIR_DEF_IMAGES;

public class CoasterActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private final static String LOG_TAG = "COASTER_ACTIVITY";

    private TextView txtTitleCoasterID;
    private TextView txtTitleTrademark;
    private TextView txtSeriesNbr;
    private TextView txtSeriesName;
    private TextView txtCoasterDescription;
    private TextView txtCoasterText;
    private TextView txtQuality;
    private TextView txtQualityText;
    private TextView txtShape;
    private TextView txtMeasurements;
    private TextView txtDonationDate;
    private TextView txtDonor;
    private TextView txtCollectPlace;

    private AppCompatImageView imgCoasterFront;
    private AppCompatImageView imgCoasterBack;

    private LinearLayout layoutSeries;

    private Coaster coaster;
    private String trademark;
    private String collectorName;
    private Series foundSeries;
    private Shape foundShape;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_coaster);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // *************
        // *** Views ***
        // *************

        txtTitleCoasterID = (TextView) findViewById(R.id.txtTitleCoasterID);
        txtTitleTrademark = (TextView) findViewById(R.id.txtTitleTrademark);
        txtSeriesNbr = (TextView) findViewById(R.id.txtSeriesNbr);
        txtSeriesName = (TextView) findViewById(R.id.txtSeriesName);
        txtCoasterDescription = (TextView) findViewById(R.id.txtCoasterDescription);
        txtCoasterText = (TextView) findViewById(R.id.txtCoasterText);
        txtQuality = (TextView) findViewById(R.id.txtQuality);
        txtQualityText = (TextView) findViewById(R.id.txtQualityText);
        txtShape = (TextView) findViewById(R.id.txtShape);
        txtMeasurements = (TextView) findViewById(R.id.txtMeasurements);
        txtDonationDate = (TextView) findViewById(R.id.txtDonationDate);
        txtDonor = (TextView) findViewById(R.id.txtDonor);
        txtCollectPlace = (TextView) findViewById(R.id.txtCollectPlace);

        imgCoasterFront = (AppCompatImageView) findViewById(R.id.imgCoasterFront);
        imgCoasterBack = (AppCompatImageView) findViewById(R.id.imgCoasterBack);

        layoutSeries = (LinearLayout) findViewById(R.id.layoutSeries);

        // ************
        // *** Data ***
        // ************

        Intent intentOrigine = this.getIntent();

        final long coasterID = intentOrigine.getLongExtra("extraCoasterID", -1);

        coaster = CoasterApplication.collectionData.mapCoasters.get(coasterID);

        // Translate trademark id in trademark name:

        trademark = CoasterApplication.collectionData.mapTrademarks.get(coaster.getCoasterTrademarkID()).getTrademark();

        // Translate collector id in collector name:

        collectorName = CoasterApplication.collectionData.mapCollectors.get(coaster.getCollectorID()).getDisplayName();

        // Translate shape id in shape name:

        foundShape = null;

        for (Shape sh : CoasterApplication.collectionData.lstShapes) {
            if (sh.getShapeID() == coaster.getCoasterMainShape()) {
                foundShape = sh;
                break;
            }
        }

        // Translate series id in series name:
        foundSeries = null;

        if (coaster.getCoasterSeriesID() != -1) {
            for (Series ser : CoasterApplication.collectionData.lstSeries) {
                if (ser.getSeriesID() == coaster.getCoasterSeriesID()) {
                    foundSeries = ser;
                    break;
                }
            }
        }

        // ************************
        // *** OnClickListeners ***
        // ************************

        txtTitleTrademark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryIntent = new Intent(CoasterActivity.this, GalleryActivity.class);

                galleryIntent.putExtra(IIntentExtras.EXTRA_TRADEMARKID, coaster.getCoasterTrademarkID());
                galleryIntent.putExtra(IIntentExtras.EXTRA_GALLERY_SUBTITLE, trademark);

                startActivity(galleryIntent);
            }
        });

        layoutSeries.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (foundSeries != null) {
                    String maxOrdered = "";

                    if (foundSeries.getMaxNumber() > 0) {
                        maxOrdered += foundSeries.getMaxNumber();

                        if (foundSeries.isOrdered()) {
                            maxOrdered += "s";
                        }
                    }

                    Intent galleryIntent = new Intent(CoasterActivity.this, GalleryActivity.class);

                    galleryIntent.putExtra(IIntentExtras.EXTRA_SERIESID, coaster.getCoasterSeriesID());
                    galleryIntent.putExtra(IIntentExtras.EXTRA_SERIESMAX_ORDERED, maxOrdered);
                    galleryIntent.putExtra(IIntentExtras.EXTRA_GALLERY_SUBTITLE, foundSeries.getSeries());

                    startActivity(galleryIntent);
                }
            }
        });

        txtDonor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryIntent = new Intent(CoasterActivity.this, GalleryActivity.class);

                galleryIntent.putExtra(IIntentExtras.EXTRA_COLLECTORID, coaster.getCollectorID());
                galleryIntent.putExtra(IIntentExtras.EXTRA_GALLERY_SUBTITLE, collectorName);

                startActivity(galleryIntent);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();

        Log.i(LOG_TAG, "IN onResume");

        // FILL UP THE SCREEN:

        txtTitleCoasterID.setText("" + coaster.getCoasterID());
        txtTitleTrademark.setText(trademark);

        layoutSeries.setVisibility(View.GONE);

        if (coaster.getCoasterSeriesID() != -1) {
            layoutSeries.setVisibility(View.VISIBLE);

            if ((coaster.getCoasterSeriesIndex() > 0) && (foundSeries.getMaxNumber() > 0)) {
                txtSeriesNbr.setVisibility(View.VISIBLE);

                txtSeriesNbr.setText("" + coaster.getCoasterSeriesIndex() + "/" + foundSeries.getMaxNumber() + ":");
            } else {
                txtSeriesNbr.setVisibility(View.GONE);
            }

            txtSeriesName.setText(foundSeries.getSeries());
        }

        String strImgFront = coaster.getCoasterImageFrontName();
        String strImgBack = coaster.getCoasterImageBackName();

        imgCoasterFront.setVisibility(View.GONE);
        imgCoasterBack.setVisibility(View.GONE);

        imgCoasterFront.setTag(R.id.TAG_SIZE, Integer.valueOf(1550));
        imgCoasterBack.setTag(R.id.TAG_SIZE, Integer.valueOf(1550));

        if ((strImgFront != null) && (strImgFront.length() > 0)) {
            imgCoasterFront.setVisibility(View.VISIBLE);

            if (strImgFront.equals("-")) {
                int resID = ImageManager.getDummyPictureResourceID(coaster.getCoasterMainShape(), coaster.getMeasurement1(), coaster.getMeasurement2());

                imgCoasterFront.setImageResource(resID);
//                holder.txtImgCoasterFront.setVisibility(View.VISIBLE);
//                holder.txtImgCoasterFront.setText(R.string.lblNameUndefined);
            } else {
                File imgFile = ImageManager.getImgFile(strImgFront);

                if (imgFile.exists()) {
                    new ImageManager().load(strImgFront, DIR_DEF_IMAGES, imgCoasterFront);
                } else {
                    int resID = ImageManager.getDummyPictureResourceID(coaster.getCoasterMainShape(), coaster.getMeasurement1(), coaster.getMeasurement2());

                    imgCoasterFront.setImageResource(resID);
//                    holder.txtImgCoasterFront.setVisibility(View.VISIBLE);
//                    holder.txtImgCoasterFront.setText(R.string.lblOops);
                }
            }
        } else {
            int resID = ImageManager.getDummyPictureResourceID(coaster.getCoasterMainShape(), coaster.getMeasurement1(), coaster.getMeasurement2());

            imgCoasterFront.setImageResource(resID);
//            holder.txtImgCoasterFront.setVisibility(View.VISIBLE);
//            holder.txtImgCoasterFront.setText(R.string.lblUnknown);
        }

        if ((strImgBack != null) && (strImgBack.length() > 0)) {
            imgCoasterBack.setVisibility(View.VISIBLE);

            if (strImgBack.equals("-")) {
                int resID = ImageManager.getDummyPictureResourceID(coaster.getCoasterMainShape(), coaster.getMeasurement1(), coaster.getMeasurement2());

                imgCoasterBack.setImageResource(resID);
//                holder.txtImgCoasterFront.setVisibility(View.VISIBLE);
//                holder.txtImgCoasterFront.setText(R.string.lblNameUndefined);
            } else {
                File imgFile = ImageManager.getImgFile(strImgBack);

                if (imgFile.exists()) {
                    new ImageManager().load(strImgBack, DIR_DEF_IMAGES, imgCoasterBack);
                } else {
                    int resID = ImageManager.getDummyPictureResourceID(coaster.getCoasterMainShape(), coaster.getMeasurement1(), coaster.getMeasurement2());

                    imgCoasterBack.setImageResource(resID);
//                    holder.txtImgCoasterFront.setVisibility(View.VISIBLE);
//                    holder.txtImgCoasterFront.setText(R.string.lblOops);
                }
            }
        }
//        else {
//            imgCoasterFront.setImageResource(R.drawable.beer_bg_115_rectangle);
//            holder.txtImgCoasterFront.setVisibility(View.VISIBLE);
//            holder.txtImgCoasterFront.setText(R.string.lblUnknown);
//        }

        String desc = coaster.getCoasterDescription();

        txtCoasterDescription.setVisibility(View.GONE);

        if ((desc != null) && (desc.length() > 0)) {
            txtCoasterDescription.setVisibility(View.VISIBLE);
            txtCoasterDescription.setText(desc);
        }

        String text = coaster.getCoasterText();

        txtCoasterText.setVisibility(View.GONE);

        if ((text != null) && (text.length() > 0)) {
            txtCoasterText.setVisibility(View.VISIBLE);
            txtCoasterText.setText(text);
        }

        txtQuality.setText("" + coaster.getCoasterQuality() + " / 10");

        txtQualityText.setText(Util.getDisplayQuality((int) coaster.getCoasterQuality(), getResources()));

        txtShape.setVisibility(View.GONE);
        txtMeasurements.setVisibility(View.GONE);

        if (coaster.getCoasterMainShape() != -1) {
            txtShape.setVisibility(View.VISIBLE);
            txtMeasurements.setVisibility(View.VISIBLE);

            txtShape.setText(foundShape.getName());
            txtMeasurements.setText("(" + Util.getDisplayMeasurements(foundShape, coaster) + ")");
        }

        txtDonationDate.setText(Util.getDisplayDate(coaster.getCollectionDate()));
        txtDonor.setText(collectorName);
        txtCollectPlace.setText(coaster.getCollectionPlace());
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_coaster, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        Log.i(LOG_TAG, "item id clicked: " + id);

        if (id == R.id.action_edit) {
            Intent intent = new Intent(this, AddCoasterActivity.class);

            intent.putExtra("extraCoasterID", coaster.getCoasterID());

            this.startActivity(intent);

            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
