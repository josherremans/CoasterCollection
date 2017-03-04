package josh.android.coastercollection.adapters;

import android.os.AsyncTask;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import java.util.ArrayList;

import josh.android.coastercollection.application.CoasterApplication;
import josh.android.coastercollection.utils.Util;
import josh.android.coastercollection.bo.CoasterCollectionData;
import josh.android.coastercollection.databank.CoasterCollectionDBHelper;

/**
 * Created by Jos on 27/01/2017.
 */
public class LoadCoastersAsyncTask extends AsyncTask<Void, Integer, CoasterCollectionData> {
    private final static String LOG_TAG = "LOAD_COASTERS_ASYNCTASK";

    private CoasterCollectionDBHelper dbHelper;
    private boolean isReverseOrder;
//    private CoasterCollectionData data;
    private CoasterCollectionAdapter adapter;

    private ProgressBar progressBar;
    private Toolbar toolbar;

    public LoadCoastersAsyncTask(CoasterCollectionDBHelper dbHelper, CoasterCollectionAdapter adapter, boolean isReverseOrder, ProgressBar progressBar, Toolbar toolbar) {
        this.dbHelper = dbHelper;
//        this.data = data;
        this.isReverseOrder = isReverseOrder;
        this.adapter = adapter;

        this.progressBar = progressBar;
        this.toolbar = toolbar;
    }

    @Override
    protected CoasterCollectionData doInBackground(Void... x) {

        if (CoasterApplication.collectionData.lstCoasterTypes.size() == 0) {
            CoasterApplication.collectionData.lstCoasterTypes.clear();
            CoasterApplication.collectionData.lstCoasterTypes.addAll(dbHelper.getCoasterTypesFromDB());
            Log.i(LOG_TAG, "data.lstCoasterTypes: size=" + CoasterApplication.collectionData.lstCoasterTypes.size());
        }

        publishProgress(1);

        if (CoasterApplication.collectionData.mapTrademarks.size() == 0) {
            CoasterApplication.collectionData.mapTrademarks.clear();
            CoasterApplication.collectionData.mapTrademarks.putAll(dbHelper.getTrademarksFromDB());
            Log.i(LOG_TAG, "data.mapTrademarks: size=" + CoasterApplication.collectionData.mapTrademarks.size());
        }

        publishProgress(2);

        if (CoasterApplication.collectionData.mapCollectors.size() == 0) {
            CoasterApplication.collectionData.mapCollectors.clear();
            CoasterApplication.collectionData.mapCollectors.putAll(dbHelper.getCollectorsFromDB());
            Log.i(LOG_TAG, "data.mapCollectors: size=" + CoasterApplication.collectionData.mapCollectors.size());
        }

        publishProgress(3);

        if (CoasterApplication.collectionData.lstSeries.size() == 0) {
            CoasterApplication.collectionData.lstSeries.clear();
            CoasterApplication.collectionData.lstSeries.addAll(dbHelper.getSeriesFromDB(-1));
            Log.i(LOG_TAG, "data.lstSeries: size:" + CoasterApplication.collectionData.lstSeries.size());
        }

        publishProgress(4);

        if (CoasterApplication.collectionData.lstShapes.size() == 0) {
            CoasterApplication.collectionData.lstShapes.clear();
            CoasterApplication.collectionData.lstShapes.addAll(dbHelper.getShapesFromDB());
            Log.i(LOG_TAG, "data.lstShapes: size=" + CoasterApplication.collectionData.lstShapes.size());
        }

        publishProgress(5);

//        CoasterApplication.collectionData.mapCoasters.clear();
//        CoasterApplication.collectionData.mapCoasters.putAll(dbHelper.getCoasterCollectionFromDB(null, isReverseOrder));
        dbHelper.getCoasterCollectionFromDB(null, isReverseOrder);
        Log.i(LOG_TAG, "data.lstCoasters: size=" + CoasterApplication.collectionData.mapCoasters.size());

        publishProgress(6);

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

        progressBar.setVisibility(View.GONE);

        ArrayList<Long> lstCoasterIds = new ArrayList<>(coasterCollectionData.mapCoasters.keySet());     //new ArrayList<>();

        adapter.updateCoasterForList(lstCoasterIds);

        toolbar.setSubtitle("(" + adapter.getCount() + ")");

        adapter.notifyDataSetChanged();

        Util.createHistoryMatrix(); // TODO Replace this !!!
    }
}