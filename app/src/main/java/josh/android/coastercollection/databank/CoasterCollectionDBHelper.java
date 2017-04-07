package josh.android.coastercollection.databank;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.TextUtils;
import android.util.Log;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Locale;

import josh.android.coastercollection.application.CoasterApplication;
import josh.android.coastercollection.bo.Coaster;
import josh.android.coastercollection.bo.CoasterType;
import josh.android.coastercollection.bo.Collector;
import josh.android.coastercollection.bo.Series;
import josh.android.coastercollection.bo.Shape;
import josh.android.coastercollection.bo.Trademark;

public class CoasterCollectionDBHelper extends SQLiteOpenHelper {
    private static final String LOG_TAG = "DB_HELPER";

    private static final String DATABASE_NAME = "coastercollection.db";
    //private static final String DATABASE_PATH = "/storage/extSdCard/Coasters/db";
//    private static final String DATABASE_PATH = Environment.getExternalStorageDirectory() + "/Coasters/db";
    private static final String DATABASE_PATH = "/mnt/extSdCard/Android/data/josh.android.coastercollection/Coasters/db";

    private static final int DATABASE_VERSION = 1;

    private static Context cx;

    //private ContentResolver myContentResolver;

    public CoasterCollectionDBHelper(Context context) {
        super(context, DATABASE_PATH + File.separator + DATABASE_NAME, null, DATABASE_VERSION);

        cx = context;

        //myContentResolver = context.getContentResolver();
    }

    // Method is called during creation of the database
    @Override
    public void onCreate(SQLiteDatabase database) {
        //DBCollector.onCreate(database);
        //DBTrademark.onCreate(database);
    }

    // Method is called during an upgrade of the database,
    // e.g. if you increase the database version
    @Override
    public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
        //DBCollector.onUpgrade(database, oldVersion, newVersion);
        //DBTrademark.onUpgrade(database, oldVersion, newVersion);
    }

    public long getNextCoasterIDFromDB() {
        SQLiteDatabase database = this.getReadableDatabase();

        String[] selectColumns = {"MAX(_ID)"};

        long nextID = 0;

        Cursor cursor = database.query(CoasterCollectionDBContract.CollectionEntry.TABLE_NAME,
                selectColumns, null, null, null, null, null);

        cursor.moveToFirst();

        if (!cursor.isAfterLast()) {
            nextID = cursor.getLong(0);
        }

        database.close();

        return ++nextID;
    }

    public long getNextTrademarkIDFromDB() {
        SQLiteDatabase database = this.getReadableDatabase();

        String[] selectColumns = {"MAX(" + CoasterCollectionDBContract.TrademarkEntry.COLUMN_ID + ")"};

        long nextID = 0;

        Cursor cursor = database.query(CoasterCollectionDBContract.TrademarkEntry.TABLE_NAME,
                selectColumns, null, null, null, null, null);

        cursor.moveToFirst();

        if (!cursor.isAfterLast()) {
            nextID = cursor.getLong(0);
        }

        database.close();

        return ++nextID;
    }

    public long getNextCollectorIDFromDB() {
        SQLiteDatabase database = this.getReadableDatabase();

        String[] selectColumns = {"MAX(" + CoasterCollectionDBContract.CollectorEntry.COLUMN_ID + ")"};

        long nextID = 0;

        Cursor cursor = database.query(CoasterCollectionDBContract.CollectorEntry.TABLE_NAME,
                selectColumns, null, null, null, null, null);

        cursor.moveToFirst();

        if (!cursor.isAfterLast()) {
            nextID = cursor.getLong(0);
        }

        database.close();

        return ++nextID;
    }

    public Coaster getCoasterByID(long id) {
        SQLiteDatabase database = this.getReadableDatabase();

        String[] selectColumns = {CoasterCollectionDBContract.CollectionEntry.COLUMN_ID,
                CoasterCollectionDBContract.CollectionEntry.COLUMN_TRADEMARK_ID,
                CoasterCollectionDBContract.CollectionEntry.COLUMN_CATEGORY_ID,
                CoasterCollectionDBContract.CollectionEntry.COLUMN_SERIES_ID,
                CoasterCollectionDBContract.CollectionEntry.COLUMN_SERIES_INDEX,
                CoasterCollectionDBContract.CollectionEntry.COLUMN_DESCRIPTION,
                CoasterCollectionDBContract.CollectionEntry.COLUMN_TEXT,
                CoasterCollectionDBContract.CollectionEntry.COLUMN_COLLECTOR_ID,
                CoasterCollectionDBContract.CollectionEntry.COLUMN_COLLECT_PLACE,
                CoasterCollectionDBContract.CollectionEntry.COLUMN_COLLECT_DATE,
                CoasterCollectionDBContract.CollectionEntry.COLUMN_SHAPE_ID_MULTI,
                CoasterCollectionDBContract.CollectionEntry.COLUMN_MEASURE_1,
                CoasterCollectionDBContract.CollectionEntry.COLUMN_MEASURE_2,
                CoasterCollectionDBContract.CollectionEntry.COLUMN_QUALITY_ID,
                CoasterCollectionDBContract.CollectionEntry.COLUMN_IMAGE_FRONT,
                CoasterCollectionDBContract.CollectionEntry.COLUMN_IMAGE_BACK};

        String whereClause = CoasterCollectionDBContract.CollectionEntry.COLUMN_ID + " = ?";
        String[] whereClauseArgs = {"" + id};
        String orderByClause = null;

        Cursor cursor = database.query(CoasterCollectionDBContract.CollectionEntry.TABLE_NAME,
                selectColumns, whereClause, whereClauseArgs, null, null, orderByClause);

        cursor.moveToFirst();

        Coaster c = null;

        if (!cursor.isAfterLast()) {
            c = cursorToCoaster(cursor);
        }

        // make sure to close the cursor
        cursor.close();

        database.close();

        return c;
    }

    public long getCoasterIDByImgName(boolean isFront, String imgName) {
        SQLiteDatabase database = this.getReadableDatabase();

        String[] selectColumns = {CoasterCollectionDBContract.CollectionEntry._ID};

        String whereClause = "";

        if (isFront) {
            whereClause = CoasterCollectionDBContract.CollectionEntry.COLUMN_IMAGE_FRONT + " = ?";
        } else {
            whereClause = CoasterCollectionDBContract.CollectionEntry.COLUMN_IMAGE_BACK + " = ?";
        }

        String[] whereClauseArgs = {imgName};
        String orderByClause = null;

        Cursor cursor = database.query(CoasterCollectionDBContract.CollectionEntry.TABLE_NAME,
                selectColumns, whereClause, whereClauseArgs, null, null, orderByClause);

        cursor.moveToFirst();

        long id = -1;

        if (!cursor.isAfterLast()) {
            id = cursor.getLong(0);
        }

        // make sure to close the cursor
        cursor.close();

        database.close();

        return id;
    }

    public Trademark getTrademarkByID(long id) {
        SQLiteDatabase database = this.getReadableDatabase();

        String[] selectColumns = {CoasterCollectionDBContract.TrademarkEntry._ID,
                CoasterCollectionDBContract.TrademarkEntry.COLUMN_TRADEMARK,
                CoasterCollectionDBContract.TrademarkEntry.COLUMN_BREWERY};

        String whereClause = CoasterCollectionDBContract.TrademarkEntry.COLUMN_ID + " = ?";
        String[] whereClauseArgs = {"" + id};
        String orderByClause = null;

        Cursor cursor = database.query(CoasterCollectionDBContract.TrademarkEntry.TABLE_NAME,
                selectColumns, whereClause, whereClauseArgs, null, null, orderByClause);

        cursor.moveToFirst();

        Trademark tr = null;

        if (!cursor.isAfterLast()) {
            tr = cursorToTrademark(cursor);
        }

        // make sure to close the cursor
        cursor.close();

        database.close();

        return tr;
    }

    public Collector getCollectorByID(long id) {
        SQLiteDatabase database = this.getReadableDatabase();

        String[] selectColumns = {CoasterCollectionDBContract.CollectorEntry._ID,
                CoasterCollectionDBContract.CollectorEntry.COLUMN_FIRSTNAME,
                CoasterCollectionDBContract.CollectorEntry.COLUMN_LASTNAME,
                CoasterCollectionDBContract.CollectorEntry.COLUMN_INITIALS,
                CoasterCollectionDBContract.CollectorEntry.COLUMN_ALIAS};

        String whereClause = CoasterCollectionDBContract.CollectorEntry.COLUMN_ID + " = ?";
        String[] whereClauseArgs = {"" + id};
        String orderByClause = null;

        Cursor cursor = database.query(CoasterCollectionDBContract.CollectorEntry.TABLE_NAME,
                selectColumns, whereClause, whereClauseArgs, null, null, orderByClause);

        cursor.moveToFirst();

        Collector c = null;

        if (!cursor.isAfterLast()) {
            c = cursorToCollecor(cursor);
        }

        // make sure to close the cursor
        cursor.close();

        database.close();

        return c;
    }

    public void removeCoasterFromDB(long coasterID) {
        SQLiteDatabase database = this.getWritableDatabase();

        String whereClause = CoasterCollectionDBContract.CollectionEntry.COLUMN_ID + " = ?";
        String[] whereArgs = {"" + coasterID};

        database.delete(CoasterCollectionDBContract.CollectionEntry.TABLE_NAME, whereClause, whereArgs);

        database.close();
    }

    public void putCoasterInDB(long coasterID, Coaster coaster) {
        SimpleDateFormat dateFormatter = new SimpleDateFormat(Coaster.COASTER_DATE_FORMAT_DB, Locale.US);

        String collectionDate = dateFormatter.format(coaster.getCollectionDate());

        SQLiteDatabase database = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(CoasterCollectionDBContract.CollectionEntry.COLUMN_ID, coaster.getCoasterID());
        values.put(CoasterCollectionDBContract.CollectionEntry.COLUMN_TRADEMARK_ID, coaster.getCoasterTrademarkID());
        values.put(CoasterCollectionDBContract.CollectionEntry.COLUMN_DESCRIPTION, coaster.getCoasterDescription());
        values.put(CoasterCollectionDBContract.CollectionEntry.COLUMN_TEXT, coaster.getCoasterText());
        values.put(CoasterCollectionDBContract.CollectionEntry.COLUMN_SERIES_ID, (coaster.getCoasterSeriesID() == -1 ? null : coaster.getCoasterSeriesID()));
        values.put(CoasterCollectionDBContract.CollectionEntry.COLUMN_SERIES_INDEX, (coaster.getCoasterSeriesIndex() == -1 ? null : coaster.getCoasterSeriesIndex()));
        values.put(CoasterCollectionDBContract.CollectionEntry.COLUMN_COLLECTOR_ID, coaster.getCollectorID());
        values.put(CoasterCollectionDBContract.CollectionEntry.COLUMN_COLLECT_PLACE, coaster.getCollectionPlace());
        values.put(CoasterCollectionDBContract.CollectionEntry.COLUMN_COLLECT_DATE, collectionDate);
        values.put(CoasterCollectionDBContract.CollectionEntry.COLUMN_SHAPE_ID_MULTI, coaster.getCoasterShapeIDs().get(0));
        values.put(CoasterCollectionDBContract.CollectionEntry.COLUMN_MEASURE_1, (coaster.getMeasurement1() == -1 ? null : coaster.getMeasurement1()));
        values.put(CoasterCollectionDBContract.CollectionEntry.COLUMN_MEASURE_2, (coaster.getMeasurement2() == -1 ? null : coaster.getMeasurement2()));
        values.put(CoasterCollectionDBContract.CollectionEntry.COLUMN_CATEGORY_ID, coaster.getCoasterCategoryIDs().get(0));
        values.put(CoasterCollectionDBContract.CollectionEntry.COLUMN_QUALITY_ID, coaster.getCoasterQuality());
        values.put(CoasterCollectionDBContract.CollectionEntry.COLUMN_IMAGE_FRONT, coaster.getCoasterImageFrontName());
        values.put(CoasterCollectionDBContract.CollectionEntry.COLUMN_IMAGE_BACK, coaster.getCoasterImageBackName());

        if (coaster.isFetchedFromDB()) {
            String whereClause = CoasterCollectionDBContract.CollectionEntry.COLUMN_ID + " = ?";
            String[] whereArgs = {"" + coasterID};

            database.update(CoasterCollectionDBContract.CollectionEntry.TABLE_NAME, values, whereClause, whereArgs);
        } else {
            database.insert(CoasterCollectionDBContract.CollectionEntry.TABLE_NAME, null, values);
        }

        database.close();
    }

    public void putTrademarkInDB(Trademark trademark) {
        SQLiteDatabase database = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(CoasterCollectionDBContract.TrademarkEntry.COLUMN_ID, trademark.getTrademarkID());
        values.put(CoasterCollectionDBContract.TrademarkEntry.COLUMN_TRADEMARK, trademark.getTrademark());
        values.put(CoasterCollectionDBContract.TrademarkEntry.COLUMN_BREWERY, trademark.getBrewery());

        if (trademark.isFetchedFromDB()) {
            String whereClause = CoasterCollectionDBContract.TrademarkEntry.COLUMN_ID + " = ?";
            String[] whereArgs = {"" + trademark.getTrademarkID()};

            database.update(CoasterCollectionDBContract.TrademarkEntry.TABLE_NAME, values, whereClause, whereArgs);
        } else {
            database.insert(CoasterCollectionDBContract.TrademarkEntry.TABLE_NAME, null, values);
        }

        database.close();
    }

    public void putCollectorInDB(Collector collector) {
        SQLiteDatabase database = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(CoasterCollectionDBContract.CollectorEntry.COLUMN_ID, collector.getCollectorID());
        values.put(CoasterCollectionDBContract.CollectorEntry.COLUMN_LASTNAME, collector.getLastName());
        values.put(CoasterCollectionDBContract.CollectorEntry.COLUMN_FIRSTNAME, collector.getFirstName());
        values.put(CoasterCollectionDBContract.CollectorEntry.COLUMN_INITIALS, collector.getInitials());
        values.put(CoasterCollectionDBContract.CollectorEntry.COLUMN_ALIAS, collector.getAlias());

        if (collector.isFetchedFromDB()) {
            String whereClause = CoasterCollectionDBContract.CollectorEntry.COLUMN_ID + " = ?";
            String[] whereArgs = {"" + collector.getCollectorID()};

            database.update(CoasterCollectionDBContract.CollectorEntry.TABLE_NAME, values, whereClause, whereArgs);
        } else {
            database.insert(CoasterCollectionDBContract.CollectorEntry.TABLE_NAME, null, values);
        }

        database.close();
    }

    public void getCoasterCollectionFromDB(ArrayList<Long> lstTrademarkIDs, boolean isReverseOrder) {
        Log.i(LOG_TAG, "getCoasterCollectionFromDB(..)");

        SQLiteDatabase database = this.getReadableDatabase();

        String[] selectColumns = {CoasterCollectionDBContract.CollectionEntry.COLUMN_ID,
                CoasterCollectionDBContract.CollectionEntry.COLUMN_TRADEMARK_ID,
                CoasterCollectionDBContract.CollectionEntry.COLUMN_CATEGORY_ID,
                CoasterCollectionDBContract.CollectionEntry.COLUMN_SERIES_ID,
                CoasterCollectionDBContract.CollectionEntry.COLUMN_SERIES_INDEX,
                CoasterCollectionDBContract.CollectionEntry.COLUMN_DESCRIPTION,
                CoasterCollectionDBContract.CollectionEntry.COLUMN_TEXT,
                CoasterCollectionDBContract.CollectionEntry.COLUMN_COLLECTOR_ID,
                CoasterCollectionDBContract.CollectionEntry.COLUMN_COLLECT_PLACE,
                CoasterCollectionDBContract.CollectionEntry.COLUMN_COLLECT_DATE,
                CoasterCollectionDBContract.CollectionEntry.COLUMN_SHAPE_ID_MULTI,
                CoasterCollectionDBContract.CollectionEntry.COLUMN_MEASURE_1,
                CoasterCollectionDBContract.CollectionEntry.COLUMN_MEASURE_2,
                CoasterCollectionDBContract.CollectionEntry.COLUMN_QUALITY_ID,
                CoasterCollectionDBContract.CollectionEntry.COLUMN_IMAGE_FRONT,
                CoasterCollectionDBContract.CollectionEntry.COLUMN_IMAGE_BACK};

        String whereClause = null;
        String[] whereClauseArgs = null;

        CoasterApplication.collectionData.mapCoasters.clear();

        if (lstTrademarkIDs != null) {
            if (lstTrademarkIDs.size() > 0) {
                String strIDPlaceHolders = constructInClause(lstTrademarkIDs);

                whereClause = CoasterCollectionDBContract.CollectionEntry.COLUMN_TRADEMARK_ID + " IN (" + strIDPlaceHolders + ")";

                whereClauseArgs = constructInClauseArgs(lstTrademarkIDs);
            } else {
                return;
            }
        }

        String orderByClause = null;

        if (isReverseOrder) {
            orderByClause = CoasterCollectionDBContract.CollectionEntry.COLUMN_ID + " desc";
        }

        Cursor cursor = database.query(CoasterCollectionDBContract.CollectionEntry.TABLE_NAME,
                selectColumns, whereClause, whereClauseArgs, null, null, orderByClause);

        cursor.moveToFirst();

        while (!cursor.isAfterLast()) {
            Coaster c = cursorToCoaster(cursor);

            CoasterApplication.collectionData.mapCoasters.put(c.getCoasterID(), c);

            cursor.moveToNext();
        }

        // make sure to close the cursor
        cursor.close();

        database.close();

        return;
    }

    private String constructInClause(ArrayList<Long> ids) {
        return TextUtils.join(",", Collections.nCopies(ids.size(), "?"));
    }

    private String[] constructInClauseArgs(ArrayList<Long> ids) {
        String[] idsArr = new String[ids.size()];

        int i=0;

        for(Long id : ids) {
            idsArr[i++] = Long.toString(id);
        }

        return idsArr;
    }

    public ArrayList<CoasterType> getCoasterTypesFromDB() {
        SQLiteDatabase database = this.getReadableDatabase();

        ArrayList<CoasterType> lstCoasterTypes = new ArrayList<>();

        CoasterType dummyCoasterType = new CoasterType(-1, -1, "(Select Coaster Type)");
        lstCoasterTypes.add(dummyCoasterType);

        String[] selectColumns = {CoasterCollectionDBContract.CoasterTypeEntry._ID,
                CoasterCollectionDBContract.CoasterTypeEntry.COLUMN_CATEGORY};

        Cursor cursor = database.query(CoasterCollectionDBContract.CoasterTypeEntry.TABLE_NAME, selectColumns, null, null, null, null, null);

        cursor.moveToFirst();

        while (!cursor.isAfterLast()) {
            CoasterType coasterType = cursorToCoasterType(cursor);

            lstCoasterTypes.add(coasterType);

            cursor.moveToNext();
        }

        // always close the cursor
        cursor.close();

        database.close();

        return lstCoasterTypes;
    }

    public LinkedHashMap<Long, Collector> getCollectorsFromDB() {
        SQLiteDatabase database = this.getReadableDatabase();

        LinkedHashMap<Long, Collector> mapCollectors = new LinkedHashMap<>();

        String[] selectColumns = {CoasterCollectionDBContract.CollectorEntry._ID,
                CoasterCollectionDBContract.CollectorEntry.COLUMN_FIRSTNAME,
                CoasterCollectionDBContract.CollectorEntry.COLUMN_LASTNAME,
                CoasterCollectionDBContract.CollectorEntry.COLUMN_INITIALS,
                CoasterCollectionDBContract.CollectorEntry.COLUMN_ALIAS};

        String orderBy = CoasterCollectionDBContract.CollectorEntry.COLUMN_LASTNAME + " ASC"
                + ", " + CoasterCollectionDBContract.CollectorEntry.COLUMN_FIRSTNAME + " ASC";

        Cursor cursor = database.query(CoasterCollectionDBContract.CollectorEntry.TABLE_NAME,
                selectColumns, null, null, null, null, orderBy);

        cursor.moveToFirst();

        while (!cursor.isAfterLast()) {
            Collector col = cursorToCollecor(cursor);

            mapCollectors.put(col.getCollectorID(), col);

            cursor.moveToNext();
        }

        // make sure to close the cursor
        cursor.close();

        database.close();

        return mapCollectors;
    }

    public LinkedHashMap<Long, Trademark> getTrademarksFromDB() {
        SQLiteDatabase database = this.getReadableDatabase();

        LinkedHashMap<Long, Trademark> mapTrademarks = new LinkedHashMap<>();

        String[] selectColumns = {CoasterCollectionDBContract.TrademarkEntry._ID,
                CoasterCollectionDBContract.TrademarkEntry.COLUMN_TRADEMARK,
                CoasterCollectionDBContract.TrademarkEntry.COLUMN_BREWERY};

        String orderBy = CoasterCollectionDBContract.TrademarkEntry.COLUMN_TRADEMARK + " ASC";

        Cursor cursor = database.query(CoasterCollectionDBContract.TrademarkEntry.TABLE_NAME, selectColumns, null, null, null, null, orderBy);

        cursor.moveToFirst();

        while (!cursor.isAfterLast()) {
            Trademark trademark = cursorToTrademark(cursor);

//            lstTrademarks.add(trademark);
            mapTrademarks.put(trademark.getTrademarkID(), trademark);

            cursor.moveToNext();
        }

        // always close the cursor
        cursor.close();

        database.close();

//        return lstTrademarks;
        return mapTrademarks;
    }

    public ArrayList<Series> getSeriesFromDB(long trademarkID) {
        SQLiteDatabase database = this.getReadableDatabase();

        ArrayList<Series> lstSeries = new ArrayList<Series>();

        String[] selectColumns = {CoasterCollectionDBContract.SeriesEntry._ID,
                CoasterCollectionDBContract.SeriesEntry.COLUMN_SERIES,
                CoasterCollectionDBContract.SeriesEntry.COLUMN_TRADEMARK_ID,
                CoasterCollectionDBContract.SeriesEntry.COLUMN_NUMBER};

        String whereClause = null;
        String whereArgs[] = null;

        if (trademarkID != -1) {
            whereClause = CoasterCollectionDBContract.SeriesEntry.COLUMN_TRADEMARK_ID + "=?";
            whereArgs = new String[1];
            whereArgs[0] = String.valueOf(trademarkID);
        }

        Cursor cursor = database.query(CoasterCollectionDBContract.SeriesEntry.TABLE_NAME, selectColumns, whereClause, whereArgs, null, null, null);

        cursor.moveToFirst();

        while (!cursor.isAfterLast()) {
            Series series = cursorToSeries(cursor);

            lstSeries.add(series);

            cursor.moveToNext();
        }

        // always close the cursor
        cursor.close();

        database.close();

        return lstSeries;
    }

    public ArrayList<Shape> getShapesFromDB() {
        SQLiteDatabase database = this.getReadableDatabase();

        ArrayList<Shape> lstShapes = new ArrayList<Shape>();

        Shape dummyShape = new Shape(-1, "(Select Shape)", -1, null, null);
        lstShapes.add(dummyShape);

        String[] selectColumns = {CoasterCollectionDBContract.ShapeEntry._ID,
                CoasterCollectionDBContract.ShapeEntry.COLUMN_SHAPE,
                CoasterCollectionDBContract.ShapeEntry.COLUMN_PARENT_ID,
                CoasterCollectionDBContract.ShapeEntry.COLUMN_MEASUREMENT_NAME_1,
                CoasterCollectionDBContract.ShapeEntry.COLUMN_MEASUREMENT_NAME_2};

        Cursor cursor = database.query(CoasterCollectionDBContract.ShapeEntry.TABLE_NAME, selectColumns, null, null, null, null, null);

        cursor.moveToFirst();

        while (!cursor.isAfterLast()) {
            Shape shape = cursorToShape(cursor);

            lstShapes.add(shape);

            cursor.moveToNext();
        }

        // always close the cursor
        cursor.close();

        database.close();

        return lstShapes;
    }

    private Collector cursorToCollecor(Cursor cursor) {
        long id = cursor.getLong(0);
        String firstName = cursor.getString(1);
        String lastName = cursor.getString(2);
        String initials = cursor.getString(3);
        String alias = cursor.getString(4);

        Collector collector = new Collector(id, firstName, lastName, initials, alias);

        collector.setFetchedFromDB();

        return collector;
    }

    private Trademark cursorToTrademark(Cursor cursor) {
        long id = cursor.getLong(0);
        String trademark = cursor.getString(1);
        String brewery = cursor.getString(2);

        Trademark t = new Trademark(id, trademark, brewery);

        t.setFetchedFromDB();

        return t;
    }

    private CoasterType cursorToCoasterType(Cursor cursor) {
        long id = cursor.getLong(0);
        String coasterTypeName = cursor.getString(1);

        CoasterType coasterType = new CoasterType(id, -1, coasterTypeName);

        return coasterType;
    }

    private Shape cursorToShape(Cursor cursor) {
        long id = cursor.getLong(0);
        String shape = cursor.getString(1);
        long parentID = cursor.getLong(2);
        String measurementName1 = cursor.getString(3);
        String measurementName2 = cursor.getString(4);

        Shape sh = new Shape(id, shape, parentID, measurementName1, measurementName2);

        return sh;
    }

    private Series cursorToSeries(Cursor cursor) {
        long id = cursor.getLong(0);
        String series = cursor.getString(1);
        long trademarkID = cursor.getLong(2);
        long maxNumber = cursor.getLong(3);

        Series s = new Series(id, trademarkID, series, maxNumber);

        return s;
    }

    private Coaster cursorToCoaster(Cursor cursor) {
        long id = cursor.getLong(0);
        long trid = cursor.getLong(1);
        long catid = (cursor.isNull(2) ? -1 : cursor.getLong(2));

        long seriesid = (cursor.isNull(3) ? -1 : cursor.getLong(3));
        long seriesind = (cursor.isNull(4) ? -1 : cursor.getLong(4));
        String desc = cursor.getString(5);
        String text = cursor.getString(6);
        long collectorid = cursor.getLong(7);
        String where = cursor.getString(8);
        String strDate = cursor.getString(9);
        long shapeid = (cursor.isNull(10) ? -1 : cursor.getLong(10));
        long meas1 = cursor.getLong(11);
        long meas2 = cursor.getLong(12);
        long qual = cursor.getLong(13);
        String imgfront = cursor.getString(14);
        String imgback = cursor.getString(15);

        Coaster c = new Coaster(id);

        c.setFetchedFromDB();

        c.setCoasterTrademarkID(trid);
        c.addCoasterCategory(catid);
        c.setCoasterDescription(desc);
        c.setCoasterText(text);

        if (seriesid != -1) {
            c.setCoasterSeriesID(seriesid);
        }

        if (seriesind != -1) {
            c.setCoasterSeriesIndex(seriesind);
        }

        c.setCollectionPlace(where);

        SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyyMMdd", Locale.US);

        Date collectionDate = null;
        try {
            collectionDate = dateFormatter.parse(strDate);
        } catch (ParseException e) {
        }

        c.setCollectionDate(collectionDate);
        c.setCollectorID(collectorid);

        c.addCoasterShapeID(shapeid);
        c.setMeasurements(meas1, meas2);

        c.setCoasterQuality(qual);

        c.setCoasterImageFrontName(imgfront);
        c.setCoasterImageBackName(imgback);

        return c;
    }
}
