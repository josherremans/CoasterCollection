package josh.android.coastercollection.adapters;

import android.os.AsyncTask;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import java.util.ArrayList;

import josh.android.coastercollection.application.CoasterApplication;
import josh.android.coastercollection.bo.CoasterCollectionData;
import josh.android.coastercollection.databank.CoasterCollectionDBHelper;

import static josh.android.coastercollection.application.CoasterApplication.searchCoasterText;

/**
 * Created by Jos on 27/01/2017.
 */
public class LoadCoastersAsyncTask extends AsyncTask<Void, Integer, CoasterCollectionData> {
    private final static String LOG_TAG = "LOAD_COASTERS_ASYNCTASK";

    private CoasterCollectionDBHelper dbHelper;
    private boolean isReverseOrder;
    private CoasterCollectionAdapter adapter;

    private ProgressBar progressBar;
    private Toolbar toolbar;

    public LoadCoastersAsyncTask(CoasterCollectionDBHelper dbHelper, CoasterCollectionAdapter adapter,
                                 boolean isReverseOrder,
                                 ProgressBar progressBar, Toolbar toolbar) {
        this.dbHelper = dbHelper;
        this.adapter = adapter;

        this.isReverseOrder = isReverseOrder;

        this.progressBar = progressBar;
        this.toolbar = toolbar;
    }

    @Override
    protected CoasterCollectionData doInBackground(Void... x) {

//        if (CoasterApplication.collectionData.lstCoasterTypes.size() == 0) {
        if (CoasterApplication.refreshCoasterTypes) {
            CoasterApplication.collectionData.lstCoasterTypes.clear();
            CoasterApplication.collectionData.lstCoasterTypes.addAll(dbHelper.getCoasterTypesFromDB());
            Log.i(LOG_TAG, "data.lstCoasterTypes: size=" + CoasterApplication.collectionData.lstCoasterTypes.size());
        }

//        publishProgress(1);

//        if (CoasterApplication.collectionData.mapTrademarks.size() == 0) {
        if (CoasterApplication.refreshTrademarks) {
            CoasterApplication.collectionData.mapTrademarks.clear();
            CoasterApplication.collectionData.mapTrademarks.putAll(dbHelper.getTrademarksFromDB());
            Log.i(LOG_TAG, "data.mapTrademarks: size=" + CoasterApplication.collectionData.mapTrademarks.size());
        }

//        publishProgress(2);

//        if (CoasterApplication.collectionData.mapCollectors.size() == 0) {
        if (CoasterApplication.refreshCollectors) {
            CoasterApplication.collectionData.mapCollectors.clear();
            CoasterApplication.collectionData.mapCollectors.putAll(dbHelper.getCollectorsFromDB());
            Log.i(LOG_TAG, "data.mapCollectors: size=" + CoasterApplication.collectionData.mapCollectors.size());
        }

//        publishProgress(3);

//        if (CoasterApplication.collectionData.lstSeries.size() == 0) {
        if (CoasterApplication.refreshSeries) {
            CoasterApplication.collectionData.lstSeries.clear();
            CoasterApplication.collectionData.lstSeries.addAll(dbHelper.getSeriesFromDB(-1));
            Log.i(LOG_TAG, "data.lstSeries: size:" + CoasterApplication.collectionData.lstSeries.size());
        }

//        publishProgress(4);

//        if (CoasterApplication.collectionData.lstShapes.size() == 0) {
        if (CoasterApplication.refreshShapes) {
            CoasterApplication.collectionData.lstShapes.clear();
            CoasterApplication.collectionData.lstShapes.addAll(dbHelper.getShapesFromDB());
            Log.i(LOG_TAG, "data.lstShapes: size=" + CoasterApplication.collectionData.lstShapes.size());
        }

//        publishProgress(5);

        if (isCancelled()) {
            return CoasterApplication.collectionData;
        }

        if (CoasterApplication.refreshCoasters) {
            dbHelper.getCoasterCollectionFromDB(null, isReverseOrder, searchCoasterText);
            Log.i(LOG_TAG, "data.lstCoasters: size=" + CoasterApplication.collectionData.mapCoasters.size());
        }

//        publishProgress(6);

        return CoasterApplication.collectionData;
    }

    @Override
    protected void onPreExecute() {
        progressBar.setMax(6); // 100%
        progressBar.setVisibility(View.VISIBLE);

        adapter.updateCoasterForList(new ArrayList<Long>());
    }

    @Override
    protected void onProgressUpdate(Integer... progress) {
        progressBar.setProgress(progress[0]);
    }

    @Override
    protected void onPostExecute(CoasterCollectionData coasterCollectionData) {
        Log.i(LOG_TAG, "onPostExecute!!!");
        Log.i(LOG_TAG, "Adapter is notified!!!");

        CoasterApplication.refreshCoasterTypes = false;
        CoasterApplication.refreshShapes = false;
        CoasterApplication.refreshCollectors = false;
        CoasterApplication.refreshTrademarks = false;
        CoasterApplication.refreshSeries = false;
        CoasterApplication.refreshCoasters = false;

        progressBar.setVisibility(View.GONE);

        ArrayList<Long> lstCoasterIds = new ArrayList<>(coasterCollectionData.mapCoasters.keySet());     //new ArrayList<>();

        adapter.updateCoasterForList(lstCoasterIds);

        toolbar.setSubtitle("(#" + adapter.getCount() + ")");

        adapter.notifyDataSetChanged();
    }
}