package josh.android.coastercollection.activities;

import android.app.DatePickerDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import josh.android.coastercollection.R;
import josh.android.coastercollection.application.CoasterApplication;
import josh.android.coastercollection.bl.ImageManager;
import josh.android.coastercollection.bo.Coaster;
import josh.android.coastercollection.bo.CoasterType;
import josh.android.coastercollection.bo.Collector;
import josh.android.coastercollection.bo.ImagePossibility;
import josh.android.coastercollection.bo.Series;
import josh.android.coastercollection.bo.Shape;
import josh.android.coastercollection.bo.Trademark;
import josh.android.coastercollection.databank.CoasterCollectionDBHelper;
import josh.android.coastercollection.databank.CoasterDB;
import josh.android.coastercollection.utils.Util;

public class AddCoasterActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private final static String LOG_TAG = "ADD_COASTER_ACTIVITY";

    //keep track of camera capture intent
    private final static int CAMERA_CAPTURE_FRONT = 11;
    private final static int CAMERA_CAPTURE_BACK = 12;

    //keep track of cropping intent
    private final static int PIC_CROP_FRONT = 21;
    private final static int PIC_CROP_BACK = 22;

    private final static int BACKPRESSED_TWICE_TIMEOUT = 2000; // msec

    private TextView txtTitleCoasterID;
    private EditText editTitleCoasterID;

    private long nextCoasterID;

    private Spinner spinTrademark;
    private AutoCompleteTextView editTrademark;
    private AutoCompleteTextView editCollectorName;

    private Spinner spinCoasterType;

    private Spinner spinSeries;
    private LinearLayout layoutSeriesNbr;
    private EditText editSeriesNbr;
    private TextView txtSeriesMaxNbr;

    private EditText editCoasterDesc;
    private EditText editCoasterText;

    private EditText editQuality;

    private EditText editFoundWhere;
    private EditText editFoundWhen;

    private AutoCompleteTextView editShape;
    private LinearLayout layoutMeas1;
    private LinearLayout layoutMeas2;

    private Spinner spinImageFront;
    private Spinner spinImageBack;
    private EditText editCoasterIDFrontImg;
    private EditText editCoasterIDBackImg;

    private AppCompatImageView imgCoasterFront;
    private AppCompatImageView imgCoasterBack;

    private TextView txtShapeMeas1;
    private TextView txtShapeMeas2;
    private EditText editShapeMeas1;
    private EditText editShapeMeas2;

    private ArrayList<Series> lstSeriesTrademark = new ArrayList<>();
    private ArrayList<Trademark> lstTrademarks = new ArrayList<>();
    private  ArrayList<Collector> lstCollectors = new ArrayList<>();

    private ArrayAdapter<Trademark> adapterTrademarks;
    private ArrayAdapter<Series> adapterSeries;
    private ArrayAdapter<Shape> adapterShapes;

    private Coaster startCoaster;
    private Coaster endCoaster;

    private int nBackPressed = 0;

    private CoasterCollectionDBHelper dbHelper;

    private static String imageNameFrontPart1;
    private static String imageNameBackPart1;

    private ArrayList<String> valProbs = new ArrayList<>();

    private Trademark dummyTrademark = new Trademark(-1, "(Select Trademark)");
    private Series dummySeries = new Series(-1, -1, "(Select Series)", 0);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_coaster);

        Log.i(LOG_TAG, "IN OnCreate");

        Intent intentOrigine = this.getIntent();

        long coasterID = intentOrigine.getLongExtra("extraCoasterID", -1);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
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

        // *** DBHELPER:

        dbHelper = new CoasterCollectionDBHelper(this);

        // *** COASTERID:

        txtTitleCoasterID = (TextView) findViewById(R.id.txtTitleCoasterID);
        editTitleCoasterID = (EditText) findViewById(R.id.editTitleCoasterID);

        if (coasterID != -1) {
            startCoaster = dbHelper.getCoasterByID(coasterID);

            nextCoasterID = coasterID;
        } else {
            nextCoasterID = dbHelper.getNextCoasterIDFromDB();
        }

        txtTitleCoasterID.setText("" + nextCoasterID);
        editTitleCoasterID.setText("" + nextCoasterID);

        if ((startCoaster != null) && (startCoaster.isFetchedFromDB())) {
            txtTitleCoasterID.setVisibility(View.VISIBLE);
            editTitleCoasterID.setVisibility(View.GONE);
        } else {
            txtTitleCoasterID.setVisibility(View.GONE);
            editTitleCoasterID.setVisibility(View.VISIBLE);
        }

        // *** TRADEMARK:

        spinTrademark = (Spinner) findViewById(R.id.spinTrademark);

        spinTrademark.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Trademark tr = (Trademark) parent.getItemAtPosition(position);

                long trId = tr.getTrademarkID();

                if (trId == -1) {
                    editTrademark.setText("");
                } else {
                    editTrademark.setText(tr.getTrademark());
                    editTrademark.setSelection(editTrademark.length());
                }

                clearSeriesTrademark();

                for (Series s: CoasterApplication.collectionData.lstSeries) {
                    if ((s.getSeriesID() == -1)
                            || (s.getTrademarkID() == trId)) {
                        lstSeriesTrademark.add(s);
                    }
                }

                adapterSeries.notifyDataSetChanged();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                clearSeriesTrademark();
            }
        });

        editTrademark = (AutoCompleteTextView) findViewById(R.id.editTrademark);

        editTrademark.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                long trId = ((Trademark) parent.getItemAtPosition(position)).getTrademarkID();

                spinTrademark.setSelection(position);

                clearSeriesTrademark();

                for (Series s: CoasterApplication.collectionData.lstSeries) {
                    if ((s.getSeriesID() == -1)
                            || (s.getTrademarkID() == trId)) {
                        lstSeriesTrademark.add(s);
                    }
                }

                adapterSeries.notifyDataSetChanged();
            }
        });

        // *** COASTERTYPE:

        spinCoasterType = (Spinner) findViewById(R.id.spinCoasterType);

        // *** SERIES:

        spinSeries = (Spinner) findViewById(R.id.spinSeries);
        layoutSeriesNbr = (LinearLayout) findViewById(R.id.layoutSeriesNbr);
        editSeriesNbr = (EditText) findViewById(R.id.editSeriesNbr);
        txtSeriesMaxNbr = (TextView) findViewById(R.id.txtSeriesMaxNbr);

        spinSeries.setVisibility(View.VISIBLE);
        layoutSeriesNbr.setVisibility(View.VISIBLE);

        spinSeries.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.i(LOG_TAG, "spinSeries.onItemSelected(" + position + ")");

                long nSeries = parent.getCount();
                long maxNbr = ((Series) parent.getItemAtPosition(position)).getMaxNumber();

                Log.i(LOG_TAG, "nSeries: " + nSeries + ", maxNbr: " + maxNbr);

                spinSeries.setVisibility(View.VISIBLE);

                if (maxNbr > 0) {
                    layoutSeriesNbr.setVisibility(View.VISIBLE);
                    txtSeriesMaxNbr.setText("" + maxNbr);
                } else {
                    layoutSeriesNbr.setVisibility(View.GONE);
                    txtSeriesMaxNbr.setText("");
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Log.i(LOG_TAG, "spinSeries.onNothingSelected(..)");

                layoutSeriesNbr.setVisibility(View.GONE);
            }
        });

        // *** QUALITY:

        editQuality = (EditText) findViewById(R.id.editQuality);

        // *** DESCRIPTION AND TEXT:

        editCoasterDesc = (EditText) findViewById(R.id.editCoasterDesc);
        editCoasterText = (EditText) findViewById(R.id.editCoasterText);

        // *** COLLECTOR:

        editCollectorName = (AutoCompleteTextView) findViewById(R.id.editCollectorName);

        // *** SHAPE:

        editShape = (AutoCompleteTextView) findViewById(R.id.editShape);
        layoutMeas1 = (LinearLayout) findViewById(R.id.layoutMeas1);
        layoutMeas2 = (LinearLayout) findViewById(R.id.layoutMeas2);
        txtShapeMeas1 = (TextView) findViewById(R.id.txtMeas1);
        txtShapeMeas2 = (TextView) findViewById(R.id.txtMeas2);
        editShapeMeas1 = (EditText) findViewById(R.id.editMeas1);
        editShapeMeas2 = (EditText) findViewById(R.id.editMeas2);

        editShape.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String shapeName = ((TextView) view).getText().toString();

                showShapeMeasurementInputs(shapeName);
            }
        });

        // *** FOUND:

        editFoundWhere = (EditText) findViewById(R.id.editFoundWhere);

        editFoundWhen = (EditText) findViewById(R.id.editFoundWhen);

        editFoundWhen.setInputType(InputType.TYPE_NULL);

        editFoundWhen.setText(Util.getDisplayDateNow());

        editFoundWhen.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    editFoundWhen.callOnClick();
                }
            }
        });

        editFoundWhen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // *** Hide SoftInputPanel:

                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

                imm.hideSoftInputFromWindow(editFoundWhen.getWindowToken(), 0);

                // *** Date:

                String strPrevDate = ((Editable) editFoundWhen.getEditableText()).toString();

                Date prevDate = Util.getDateFromString(strPrevDate);
                Calendar prevCal = Calendar.getInstance();
                prevCal.setTime(prevDate);

                DatePickerDialog datePickerDialog = new DatePickerDialog(AddCoasterActivity.this, new DatePickerDialog.OnDateSetListener() {
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        Calendar newCal = Calendar.getInstance();
                        newCal.set(year, monthOfYear, dayOfMonth);
                        Date newDate = newCal.getTime();

                        editFoundWhen.setText(Util.getDisplayDate(newDate));
                    }

                }, prevCal.get(Calendar.YEAR), prevCal.get(Calendar.MONTH), prevCal.get(Calendar.DAY_OF_MONTH));

                datePickerDialog.getDatePicker().setMaxDate(new Date().getTime());
                datePickerDialog.getDatePicker().setCalendarViewShown(false);
                datePickerDialog.show();
            }
        });

        // *** IMAGES:

        spinImageFront = (Spinner) findViewById(R.id.spinImageFront);
        spinImageBack = (Spinner) findViewById(R.id.spinImageBack);

        imgCoasterFront = (AppCompatImageView) findViewById(R.id.imgCoasterFront);
        imgCoasterBack = (AppCompatImageView) findViewById(R.id.imgCoasterBack);

        editCoasterIDFrontImg = (EditText) findViewById(R.id.editCoasterIDFrontImg);
        editCoasterIDBackImg = (EditText) findViewById(R.id.editCoasterIDBackImg);

        editCoasterIDFrontImg.setVisibility(View.GONE);
        editCoasterIDBackImg.setVisibility(View.GONE);

        imgCoasterFront.setVisibility(View.INVISIBLE);
        imgCoasterBack.setVisibility(View.INVISIBLE);

        imgCoasterFront.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageNameFrontPart1 = "Coaster_" + new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());

                imgCoasterFront.setTag(R.id.imageView, imageNameFrontPart1);

                performTakePicture(imgCoasterFront, CAMERA_CAPTURE_FRONT);
            }
        });

        imgCoasterBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageNameBackPart1 = "Coaster_" + new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());

                imgCoasterBack.setTag(R.id.imageView, imageNameBackPart1);

                performTakePicture(imgCoasterBack, CAMERA_CAPTURE_BACK);
            }
        });

        spinImageFront.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ImagePossibility imgPos = (ImagePossibility) parent.getItemAtPosition(position);

                if (imgPos.getId() == -1) {
                    imgCoasterFront.setVisibility(View.INVISIBLE);
                } else {
                    imgCoasterFront.setVisibility(View.VISIBLE);
                }

                editCoasterIDFrontImg.setVisibility(View.GONE);

                if (imgPos.getId() == 1) {
                    editCoasterIDFrontImg.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Nothing
            }
        });

        spinImageBack.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ImagePossibility imgPos = (ImagePossibility) parent.getItemAtPosition(position);

                editCoasterIDBackImg.setVisibility(View.GONE);

                switch ((int) imgPos.getId()) {
                    case -1:
                    case 0:
                        imgCoasterBack.setVisibility(View.INVISIBLE);
                        break;

                    case 1:
                    case 3:
                        imgCoasterBack.setVisibility(View.VISIBLE);
                        break;

                    case 2:
                        imgCoasterBack.setVisibility(View.VISIBLE);
                        editCoasterIDBackImg.setVisibility(View.VISIBLE);
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Nothing
            }
        });

        // *** Adapters:

        clearTrademarks();

        lstTrademarks.addAll(CoasterApplication.collectionData.mapTrademarks.values());

        adapterTrademarks = new ArrayAdapter<> (this, android.R.layout.simple_spinner_dropdown_item, lstTrademarks);

        spinTrademark.setAdapter(adapterTrademarks);
        editTrademark.setAdapter(adapterTrademarks);

        ArrayAdapter<CoasterType> adapterCoasterTypes = new ArrayAdapter<> (this, android.R.layout.simple_spinner_dropdown_item, CoasterApplication.collectionData.lstCoasterTypes);
        spinCoasterType.setAdapter(adapterCoasterTypes);

        adapterSeries = new ArrayAdapter<> (this, android.R.layout.simple_spinner_dropdown_item, lstSeriesTrademark);
        spinSeries.setAdapter(adapterSeries);

        lstCollectors = new ArrayList<>(CoasterApplication.collectionData.mapCollectors.values());

        ArrayAdapter<Collector> adapterCollectors = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, lstCollectors);
        editCollectorName.setAdapter(adapterCollectors);

        adapterShapes = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, CoasterApplication.collectionData.lstShapes);
        editShape.setAdapter(adapterShapes);

        ArrayAdapter<ImagePossibility> adapterImageFront = new ArrayAdapter<> (this, android.R.layout.simple_spinner_dropdown_item, CoasterDB.getImagePossibilities(true));
        spinImageFront.setAdapter(adapterImageFront);

        ArrayAdapter<ImagePossibility> adapterImageBack = new ArrayAdapter<> (this, android.R.layout.simple_spinner_dropdown_item, CoasterDB.getImagePossibilities(false));
        spinImageBack.setAdapter(adapterImageBack);
    }

    @Override
    public void onResume() {
        super.onResume();

        Log.i(LOG_TAG, "IN onResume");

        String imageName;
        Bitmap bmp;

        if (startCoaster != null) {
            Log.i(LOG_TAG, "startCoaster.getCoasterSeriesID(): " + startCoaster.getCoasterSeriesID());

            for (int i=0; i<lstTrademarks.size(); i++) {
                if (lstTrademarks.get(i).getTrademarkID() == startCoaster.getCoasterTrademarkID()) {
                    spinTrademark.setSelection(i);
                    break;
                }
            }

            if (startCoaster.getCoasterCategoryIDs().size() > 0) {
                for (int i = 0; i < CoasterApplication.collectionData.lstCoasterTypes.size(); i++) {
                    if (CoasterApplication.collectionData.lstCoasterTypes.get(i).getCoasterTypeID() == startCoaster.getCoasterCategoryIDs().get(0)) {
                        spinCoasterType.setSelection(i);
                        break;
                    }
                }
            }

            clearSeriesTrademark();

            for (Series s: CoasterApplication.collectionData.lstSeries) {
                if ((s.getSeriesID() == -1)
                    || (s.getTrademarkID() == startCoaster.getCoasterTrademarkID())) {
                    lstSeriesTrademark.add(s);
                }
            }

            Log.i(LOG_TAG, "lstSeriesTrademark.size(): " + lstSeriesTrademark.size());

            adapterSeries.notifyDataSetChanged();

            if (lstSeriesTrademark != null) {
                for (int i=0; i<lstSeriesTrademark.size(); i++) {
                    if (lstSeriesTrademark.get(i).getSeriesID() == startCoaster.getCoasterSeriesID()) {
                        Log.i(LOG_TAG, "spinSeries.setSelection(i): i=" + i);
                        spinSeries.setSelection(i);
                        break;
                    }
                }
            }

            for (int i=0; i<lstCollectors.size(); i++) {
                if (lstCollectors.get(i).getCollectorID() == startCoaster.getCollectorID()) {
                    editCollectorName.setText(lstCollectors.get(i).getDisplayName());
                    break;
                }
            }

            for (int i=0; i<CoasterApplication.collectionData.lstShapes.size(); i++) {
                if (CoasterApplication.collectionData.lstShapes.get(i).getShapeID() == startCoaster.getCoasterMainShape()) {
                    String shapeName = CoasterApplication.collectionData.lstShapes.get(i).getName();

                    editShape.setText(shapeName);

                    showShapeMeasurementInputs(shapeName);

                    break;
                }
            }

            editCoasterIDFrontImg.setVisibility(View.GONE);

            if ((startCoaster.getCoasterImageFrontName() == null) || (startCoaster.getCoasterImageFrontName().length() == 0)) {
                spinImageFront.setSelection(0);
            } else {
                if ((startCoaster.getCoasterImageFrontName().equals("-"))
                    || (startCoaster.getCoasterImageFrontName().contains("_" + startCoaster.getCoasterID() + "_"))) {
                    spinImageFront.setSelection(1);
                } else {
                    spinImageFront.setSelection(2);
                    editCoasterIDFrontImg.setVisibility(View.VISIBLE);

                    String tmpName = startCoaster.getCoasterImageFrontName();

                    String[] splittedName = tmpName.split("_");

                    String id = "";

                    if ((splittedName.length < 3) || (!splittedName[1].matches("\\d+"))) {
                        id = "" + dbHelper.getCoasterIDByImgName(true, tmpName);
                    } else {
                        id = tmpName.split("_")[1];
                    }

                    editCoasterIDFrontImg.setText(id);
                }
            }

            editCoasterIDBackImg.setVisibility(View.GONE);

            if ((startCoaster.getCoasterImageBackName() == null) || (startCoaster.getCoasterImageBackName().length() == 0)) {
                if ((startCoaster.getCoasterImageFrontName() == null) || (startCoaster.getCoasterImageFrontName().length() == 0)) {
                    spinImageBack.setSelection(0);
                } else {
                    spinImageBack.setSelection(1);
                }
            } else {
                if (startCoaster.getCoasterImageBackName().equals(startCoaster.getCoasterImageFrontName())) {
                    spinImageBack.setSelection(4);
                } else {
                    if ((startCoaster.getCoasterImageBackName().equals("-"))
                        || (startCoaster.getCoasterImageBackName().contains("_" + startCoaster.getCoasterID() + "_"))) {
                        spinImageBack.setSelection(2);
                    } else {
                        spinImageBack.setSelection(3);
                        editCoasterIDBackImg.setVisibility(View.VISIBLE);

                        String tmpName = startCoaster.getCoasterImageBackName();

                        String[] splittedName = tmpName.split("_");

                        String id = "";

                        if ((splittedName.length < 3) || (!splittedName[1].matches("\\d+"))) {
                            id = "" + dbHelper.getCoasterIDByImgName(false, tmpName);
                        } else {
                            id = splittedName[1];
                        }

                        editCoasterIDBackImg.setText(id);
                    }
                }
            }

            editCoasterDesc.setText(startCoaster.getCoasterDescription());
            editCoasterText.setText(startCoaster.getCoasterText());

            editFoundWhere.setText(startCoaster.getCollectionPlace());
            editFoundWhen.setText(Util.getDisplayDate(startCoaster.getCollectionDate()));

            editShapeMeas1.setText("" + startCoaster.getMeasurement1());
            editShapeMeas2.setText("" + startCoaster.getMeasurement2());

            editQuality.setText("" + startCoaster.getCoasterQuality());

            editSeriesNbr.setText("" + startCoaster.getCoasterSeriesIndex());

            imageName = startCoaster.getCoasterImageFrontName();

            if (imageNameFrontPart1 == null) {
                if ((imageName != null) && (imageName.length() > 0)) {
                    bmp = ImageManager.getBitmap(imageName, 500);

                    imgCoasterFront.setImageBitmap(bmp);
                }
            }

            imageName = startCoaster.getCoasterImageBackName();

            if (imageNameBackPart1 == null) {
                if ((imageName != null) && (imageName.length() > 0)) {
                    bmp = ImageManager.getBitmap(imageName, 500);

                    imgCoasterBack.setImageBitmap(bmp);
                }
            }
        }

        Log.i(LOG_TAG, "END onResume");
    }

    @Override
    public void onPause() {
        super.onPause();

        Log.i(LOG_TAG, "IN onPause");
    }

    @Override
    public void onStop() {
        super.onStop();

        Log.i(LOG_TAG, "IN onStop");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        Log.i(LOG_TAG, "IN onDestroy");
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);

        Log.i(LOG_TAG, "IN onSaveInstanceState");

        // Save UI state changes to the savedInstanceState.
        // This bundle will be passed to onCreate if the process is
        // killed and restarted.

        savedInstanceState.putString("ViewId_" + R.id.txtTitleCoasterID, txtTitleCoasterID.getText().toString());
        savedInstanceState.putString("ViewId_" + R.id.editTitleCoasterID, editTitleCoasterID.getEditableText().toString());
        savedInstanceState.putString("ViewId_" + R.id.editTrademark, editTrademark.getEditableText().toString());
        savedInstanceState.putString("ViewId_" + R.id.editCollectorName, editCollectorName.getEditableText().toString());
        savedInstanceState.putString("ViewId_" + R.id.editFoundWhere, editFoundWhere.getEditableText().toString());
        savedInstanceState.putString("ViewId_" + R.id.editFoundWhen, editFoundWhen.getEditableText().toString());
        savedInstanceState.putString("ViewId_" + R.id.editSeriesNbr, editSeriesNbr.getEditableText().toString());
        savedInstanceState.putString("ViewId_" + R.id.txtSeriesMaxNbr, txtSeriesMaxNbr.getText().toString());
        savedInstanceState.putString("ViewId_" + R.id.editShape, editShape.getEditableText().toString());

        savedInstanceState.putInt("ViewId_" + R.id.layoutMeas1 + "_Visible", layoutMeas1.getVisibility());
        savedInstanceState.putInt("ViewId_" + R.id.layoutMeas2 + "_Visible", layoutMeas2.getVisibility());

        savedInstanceState.putString("ViewId_" + R.id.editMeas1, editShapeMeas1.getEditableText().toString());
        savedInstanceState.putString("ViewId_" + R.id.editMeas2, editShapeMeas2.getEditableText().toString());
        savedInstanceState.putString("ViewId_" + R.id.editQuality, editQuality.getEditableText().toString());
        savedInstanceState.putString("ViewId_" + R.id.editCoasterDesc, editCoasterDesc.getEditableText().toString());
        savedInstanceState.putString("ViewId_" + R.id.editCoasterText, editCoasterText.getEditableText().toString());
        savedInstanceState.putString("ViewId_" + R.id.editCoasterIDFrontImg, editCoasterIDFrontImg.getEditableText().toString());
        savedInstanceState.putString("ViewId_" + R.id.editCoasterIDBackImg, editCoasterIDBackImg.getEditableText().toString());

        savedInstanceState.putInt("ViewId_" + R.id.spinTrademark, spinTrademark.getSelectedItemPosition());
        savedInstanceState.putInt("ViewId_" + R.id.spinCoasterType, spinCoasterType.getSelectedItemPosition());
        savedInstanceState.putInt("ViewId_" + R.id.spinSeries, spinSeries.getSelectedItemPosition());
        savedInstanceState.putInt("ViewId_" + R.id.spinImageFront, spinImageFront.getSelectedItemPosition());
        savedInstanceState.putInt("ViewId_" + R.id.spinImageBack, spinImageBack.getSelectedItemPosition());

        Log.i(LOG_TAG, "END onSaveInstanceState");
    }

    private void clearTrademarks() {
        lstTrademarks.clear();
        lstTrademarks.add(0, dummyTrademark);
    }

    private void clearSeriesTrademark() {
        lstSeriesTrademark.clear();
        lstSeriesTrademark.add(0, dummySeries);
    }

    private void showShapeMeasurementInputs(String shapeName) {
        layoutMeas1.setVisibility(View.GONE);
        layoutMeas2.setVisibility(View.GONE);

        for (Shape s : CoasterApplication.collectionData.lstShapes) {
            if (shapeName.equals(s.getName())) {
                txtShapeMeas1.setText(s.getNameMeasurement1() + ":");
                layoutMeas1.setVisibility(View.VISIBLE);

                if ((s.getNameMeasurement2() != null) && (s.getNameMeasurement2().length() > 0)) {
                    txtShapeMeas2.setText(s.getNameMeasurement2() + ":");
                    layoutMeas2.setVisibility(View.VISIBLE);
                }
            }
        }
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        Log.i(LOG_TAG, "IN onRestoreInstanceState");

        super.onRestoreInstanceState(savedInstanceState);

        // Restore UI state from the savedInstanceState.
        // This bundle has also been passed to onCreate.

        editCoasterDesc.setText(savedInstanceState.getString("ViewId_" + R.id.editCoasterDesc));
        spinTrademark.setSelection(savedInstanceState.getInt("ViewId_" + R.id.spinTrademark));
        spinCoasterType.setSelection(savedInstanceState.getInt("ViewId_" + R.id.spinCoasterType));
        spinSeries.setSelection(savedInstanceState.getInt("ViewId_" + R.id.spinSeries));
        spinImageFront.setSelection(savedInstanceState.getInt("ViewId_" + R.id.spinImageFront));
        spinImageBack.setSelection(savedInstanceState.getInt("ViewId_" + R.id.spinImageBack));

        int layoutMeas1Visibility = savedInstanceState.getInt("ViewId_" + R.id.layoutMeas1 + "_Visible");
        int layoutMeas2Visibility = savedInstanceState.getInt("ViewId_" + R.id.layoutMeas2 + "_Visible");

        if (layoutMeas1Visibility == View.VISIBLE) {
            layoutMeas1.setVisibility(View.VISIBLE);
        } else {
            layoutMeas1.setVisibility(View.GONE);
        }

        if (layoutMeas2Visibility == View.VISIBLE) {
            layoutMeas2.setVisibility(View.VISIBLE);
        } else {
            layoutMeas2.setVisibility(View.GONE);
        }

    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        String imageName, path;
        File imageFile;
        Uri imageUri;
        Bitmap bm;

        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case CAMERA_CAPTURE_FRONT:
                    imageName = imageNameFrontPart1 + "_orig.jpg";
                    path = ImageManager.DIR_TEMP_IMAGES + File.separator + imageName;

                    imageFile = new File(path);
                    imageUri = Uri.fromFile(imageFile);

                    imgCoasterFront.setTag(R.id.imageView, imageNameFrontPart1);

                    //carry out the crop operation
                    performCrop(imgCoasterFront, imageUri, PIC_CROP_FRONT);

                    break;

                case CAMERA_CAPTURE_BACK:
                    imageName = imageNameBackPart1 + ImageManager.ENDPART_ORIG_IMAGEFILE;
                    path = ImageManager.DIR_TEMP_IMAGES + File.separator + imageName;

                    imageFile = new File(path);
                    imageUri = Uri.fromFile(imageFile);

                    imgCoasterBack.setTag(R.id.imageView, imageNameBackPart1);

                    //carry out the crop operation
                    performCrop(imgCoasterBack, imageUri, PIC_CROP_BACK);

                    break;

                case PIC_CROP_FRONT:
                    imageName = imageNameFrontPart1 + ImageManager.ENDPART_CROPPED_IMAGEFILE;

                    bm = ImageManager.getBitmap(imageName, 500);

                    //display the returned cropped image
                    imgCoasterFront.setImageBitmap(bm);

                    // Image will be saved when saving the coaster!
                    break;

                case PIC_CROP_BACK:
                    imageName = imageNameBackPart1 + ImageManager.ENDPART_CROPPED_IMAGEFILE;

                    bm = ImageManager.getBitmap(imageName, 500);

                    //display the returned cropped image
                    imgCoasterBack.setImageBitmap(bm);

                    // Image will be saved when saving the coaster!
                    break;

                default:
                    break;
            }
        }
    }

    private void performTakePicture(ImageView imageView, int requestCode) {
        try {
            String imageTagName = (String) imageView.getTag(R.id.imageView);

            String imageName = imageTagName + "_orig.jpg";
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

            Toast.makeText(AddCoasterActivity.this, errorMessage, Toast.LENGTH_SHORT).show();

            Log.e(LOG_TAG, errorMessage);
        }
    }

    private void performCrop(ImageView imageView, Uri picUri, int requestCode) {
        try {
            String imageTagName = (String) imageView.getTag(R.id.imageView);

            //call the standard crop action intent (the user device may not support it)
            Intent cropIntent = new Intent("com.android.camera.action.CROP");

            //indicate image type and Uri
            cropIntent.setDataAndType(picUri, "image/*");

            //set crop properties
            cropIntent.putExtra("crop", "true");

//            //indicate aspect of desired crop
//            cropIntent.putExtra("aspectX", 1);
//            cropIntent.putExtra("aspectY", 1);
//
//            //indicate output X and Y
//            cropIntent.putExtra("outputX", 256);
//            cropIntent.putExtra("outputY", 256);

            // don't retrieve data on return
            cropIntent.putExtra("return-data", false);

            String imageName = imageTagName + "_cropped.jpg";
            String path = ImageManager.DIR_TEMP_IMAGES + File.separator + imageName;

            File file = new File(path);
            cropIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));

            //start the activity - we handle returning in onActivityResult
            startActivityForResult(cropIntent, requestCode);
        } catch(ActivityNotFoundException ex) {
            //display an error message
            String errorMessage = "Whoops - your device doesn't support the crop action!";

            Toast.makeText(AddCoasterActivity.this, errorMessage, Toast.LENGTH_SHORT).show();

            throw ex;
        }
    }

    @Override
    public void onBackPressed() {
        nBackPressed++;

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if ((nBackPressed == 2) || (!changesMade())) {
                nBackPressed = 0;
                super.onBackPressed();
            } else {
                String errorMessage = "There are unsaved changes!\nPress Back again to discard changes!";

                Toast.makeText(AddCoasterActivity.this, errorMessage, Toast.LENGTH_LONG).show();

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        nBackPressed = 0;
                    }
                }, BACKPRESSED_TWICE_TIMEOUT);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_add_coaster, menu);
        return true;
    }

    private boolean changesMade() {
        boolean changed = true;
        boolean valid = validateCoaster();

        Log.i(LOG_TAG, "changesMade: valid: " + valid);

        if (valid) {
            // Compare endCoaster with startCoaster:

            changed = !endCoaster.equals(startCoaster);

            Log.i(LOG_TAG, "changesMade: endCoaster equals startCoaster: " + !changed);
        }

        return changed;
    }

    private boolean validateCoaster() {
        boolean res = true;

        long inputCoasterID = (editTitleCoasterID.getEditableText() == null || editTitleCoasterID.getEditableText().toString().length() == 0 ? -1 : Long.parseLong(editTitleCoasterID.getEditableText().toString()));
        long selectedTrademarkID = (spinTrademark.getSelectedItem() == null ? -1 : ((Trademark) spinTrademark.getSelectedItem()).getTrademarkID());
        long selectedCoasterTypeID = (spinCoasterType.getSelectedItem() == null ? -1 : ((CoasterType) spinCoasterType.getSelectedItem()).getCoasterTypeID());
        long selectedSeriesID = (spinSeries.getSelectedItem() == null ? -1 : ((Series) spinSeries.getSelectedItem()).getSeriesID());
        long selectedSeriesIndex = (editSeriesNbr.getEditableText() == null || editSeriesNbr.getEditableText().toString().length() == 0 ? -1 : Long.parseLong(editSeriesNbr.getEditableText().toString()));
        String inputCoasterDesc = (editCoasterDesc.getEditableText() == null ? "" : editCoasterDesc.getEditableText().toString());
        String inputCoasterText = (editCoasterText.getEditableText() == null ? "" : editCoasterText.getEditableText().toString());
        long inputQuality = (editQuality.getEditableText() == null || editQuality.getEditableText().toString().length() == 0 ? -1 : Long.parseLong(editQuality.getEditableText().toString()));
        String inputCollectorName = (editCollectorName.getEditableText() == null ? "" : editCollectorName.getEditableText().toString());
        String inputCollectorWhere = (editFoundWhere.getEditableText() == null ? null : editFoundWhere.getEditableText().toString());
        String inputCollectorWhen = (editFoundWhen.getEditableText() == null ? "" : editFoundWhen.getEditableText().toString());
        String inputShapeName = (editShape.getEditableText() == null ? "" : editShape.getEditableText().toString());
        Long inputShapeMeas1 = (editShapeMeas1.getEditableText() == null || editShapeMeas1.getEditableText().toString().length() == 0 ? -1 : Long.parseLong(editShapeMeas1.getEditableText().toString()));
        Long inputShapeMeas2 = (editShapeMeas2.getEditableText() == null || editShapeMeas2.getEditableText().toString().length() == 0 ? -1 : Long.parseLong(editShapeMeas2.getEditableText().toString()));
        ImagePossibility selectedImagePossibilityFront = (ImagePossibility) spinImageFront.getSelectedItem();
        ImagePossibility selectedImagePossibilityBack = (ImagePossibility) spinImageBack.getSelectedItem();
        String coasterIDFrontImage = (editCoasterIDFrontImg.getEditableText() == null ? "" : editCoasterIDFrontImg.getEditableText().toString());
        String coasterIDBackImage = (editCoasterIDBackImg.getEditableText() == null ? "" : editCoasterIDBackImg.getEditableText().toString());

        ArrayList<String> imageNames = new ArrayList<>(2);

        valProbs.clear();

        if (inputCoasterID == -1) {
            valProbs.add("CoasterID");
            res = false;
        }

        if (selectedTrademarkID == -1) {
            valProbs.add("TrademarkID");
            res = false;
        }

        if (selectedCoasterTypeID == -1) {
            valProbs.add("CoasterTypeID");
            res = false;
        }

        if (inputCoasterText.length() == 0) {
            valProbs.add("CoasterText");
            res = false;
        }

        if ((inputQuality < 0) || (inputQuality > 10)) {
            valProbs.add("Quality");
            res = false;
        }

        long collectorID = checkCollectorInput(inputCollectorName);

        if (collectorID == -1) {
            valProbs.add("CollectorID");
            res = false;
        }

        long shapeID = checkShapeInput(inputShapeName);

        if (shapeID == -1) {
            valProbs.add("ShapeID");
            res = false;
        }

        if (inputShapeMeas1 == -1) {
            valProbs.add("ShapeMeas1");
            res = false;
        }

        if ((selectedImagePossibilityFront == null) || (selectedImagePossibilityFront.getId() == -1)
            || (selectedImagePossibilityBack == null) || (selectedImagePossibilityBack.getId() == -1)) {
            valProbs.add("Images");
            res = false;
        } else {
            if ((selectedImagePossibilityFront.getId() == 1) && (coasterIDFrontImage.length() == 0)) {
                valProbs.add("Images");
                res = false;
            }

            if ((selectedImagePossibilityBack.getId() == 2) && (coasterIDBackImage.length() == 0)) {
                valProbs.add("Images");
                res = false;
            }

            if (res) {
                if (coasterIDFrontImage.length() == 0) {
                    coasterIDFrontImage = "" + nextCoasterID;
                }

                if (coasterIDBackImage.length() == 0) {
                    coasterIDBackImage = "" + nextCoasterID;
                }

                String trademark = ((Trademark) spinTrademark.getSelectedItem()).getTrademark();

                imageNames = getCoasterImageNames(selectedImagePossibilityFront, selectedImagePossibilityBack, trademark, Long.parseLong(coasterIDFrontImage), Long.parseLong(coasterIDBackImage));
            }
        }

        if (res) {
            endCoaster = new Coaster(inputCoasterID);

            if (startCoaster != null) {
                endCoaster.setFetchedFromDB(startCoaster.isFetchedFromDB());
            }

            endCoaster.setCoasterTrademarkID(selectedTrademarkID);
            endCoaster.addCoasterCategory(selectedCoasterTypeID);
            endCoaster.setCoasterSeriesID(selectedSeriesID);
            endCoaster.setCoasterSeriesIndex(selectedSeriesIndex);
            endCoaster.setCoasterDescription(inputCoasterDesc);
            endCoaster.setCoasterText(inputCoasterText);
            endCoaster.setCoasterQuality(inputQuality);
            endCoaster.setCollectorID(collectorID);
            endCoaster.setCollectionPlace(inputCollectorWhere);
            endCoaster.setCollectionDate(inputCollectorWhen);
            endCoaster.addCoasterShapeID(shapeID);
            endCoaster.setMeasurements(inputShapeMeas1, inputShapeMeas2);
            endCoaster.setCoasterImageFrontName(imageNames.get(0));
            endCoaster.setCoasterImageBackName(imageNames.get(1));
        }

        return res;
    }

    private ArrayList<String> getCoasterImageNames(ImagePossibility imagePossibilityFront, ImagePossibility imagePossibilityBack, String trademark, long coasterIDFront, long coasterIDBack) {
        ArrayList<String> imageNames = new ArrayList<>(2);
        String imageNameFront = "";
        String imageNameBack = "";
        String trademarkForImageName = trademark.toLowerCase().replaceAll("[ \"\']", "");

        switch ((int) imagePossibilityFront.getId()) {
            case -1:
                break;

            case 0:
                if (imagePossibilityBack.getId() == 3) {
                    imageNameFront = trademarkForImageName + "_" + coasterIDFront + "_fb.jpg";
                } else {
                    imageNameFront = trademarkForImageName + "_" + coasterIDFront + "_f.jpg";
                }
                break;

            case 1:
                imageNameFront = dbHelper.getCoasterByID(coasterIDFront).getCoasterImageFrontName();
                break;
        }

        switch ((int) imagePossibilityBack.getId()) {
            case -1:
            case 0:
                break;

            case 1:
                imageNameBack = trademarkForImageName + "_" + coasterIDBack + "_b.jpg";
                break;

            case 2:
                imageNameBack = dbHelper.getCoasterByID(coasterIDBack).getCoasterImageBackName();
                break;

            case 3:
                imageNameBack = imageNameFront;
                break;
        }

        imageNames.add(imageNameFront);
        imageNames.add(imageNameBack);

        return imageNames;
    }

    private long checkCollectorInput(String collectorName) {
        for (Collector c : CoasterApplication.collectionData.mapCollectors.values()) {
            if (c.getDisplayName().equals(collectorName)) {
                return c.getCollectorID();
            }
        }

        return -1;
    }

    private long checkShapeInput(String shapeName) {
        for (Shape sh : CoasterApplication.collectionData.lstShapes) {
            if (sh.getName().equals(shapeName)) {
                return sh.getShapeID();
            }
        }

        return -1;
    }

    private boolean saveCoaster() {
        boolean res = validateCoaster();

        String imageName;

        if (res) {
            // Save images (if any) to filesystem:

//            imageTagName = (String) imgCoasterFront.getTag(R.id.imageView);

            if (imageNameFrontPart1 != null) {
                imageName = imageNameFrontPart1 + ImageManager.ENDPART_CROPPED_IMAGEFILE;

                ImageManager.verifyStoragePermissions(this);

                ImageManager.saveDefImage(imageName, endCoaster.getCoasterImageFrontName());

                imageNameFrontPart1 = null;
            }

//            imageTagName = (String) imgCoasterBack.getTag(R.id.imageView);

            if (imageNameBackPart1 != null) {
                imageName = imageNameBackPart1 + ImageManager.ENDPART_CROPPED_IMAGEFILE;

                ImageManager.verifyStoragePermissions(this);

                ImageManager.saveDefImage(imageName, endCoaster.getCoasterImageBackName());

                imageNameBackPart1 = null;
            }

            // Save coaster to DB:

            dbHelper.putCoasterInDB(endCoaster);

            try {
                startCoaster = (Coaster) endCoaster.clone();
            } catch (CloneNotSupportedException e) {
                e.printStackTrace();
            }

//            CoasterListActivity.refreshCoasterList = true;

            CoasterApplication.collectionData.mapCoasters.put(endCoaster.getCoasterID(), endCoaster);

            CoasterApplication.collectionData.setNotifyAdapter(true);

            CoordinatorLayout coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinatorlayout);

            Snackbar snackbar = Snackbar.make(coordinatorLayout, "Saved!", Snackbar.LENGTH_LONG);

            snackbar.show();
        } else {
            String strValProbs = valProbs.toString();

            CoordinatorLayout coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinatorlayout);

            Snackbar snackbar = Snackbar.make(coordinatorLayout, "Validation problems!\n" + strValProbs, Snackbar.LENGTH_LONG);

            snackbar.show();
        }

        return res;
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
            saveCoaster();

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

    /*
    ** INNERCLASS: FabOnClickListener
     */
    private class FabOnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
//            Snackbar.make(view, "You clicked the Fab", Snackbar.LENGTH_LONG).setAction("Action", null).show();

            Intent gogogo = new Intent(AddCoasterActivity.this, AddCoasterActivity.class);

            gogogo.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                    | Intent.FLAG_ACTIVITY_SINGLE_TOP
                    | Intent.FLAG_ACTIVITY_CLEAR_TASK);

            startActivity(gogogo);
        }
    }

    /*
    ** INNERCLASS: CoasterOnItemClickListener
     */
    private class CoasterOnItemClickListener implements AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        }
    }
}
