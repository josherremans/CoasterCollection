package josh.android.coastercollection.application;

import android.app.Application;
import android.content.Intent;
import android.util.Log;

import java.io.PrintWriter;
import java.io.StringWriter;

import josh.android.coastercollection.activities.OnCrashActivity;
import josh.android.coastercollection.bo.CoasterCollectionData;
import josh.android.coastercollection.enums.IIntentExtras;
import josh.android.coastercollection.utils.CoasterPreferences;

/**
 * Created by Jos on 29/01/2017.
 */
public class CoasterApplication extends Application {

    private static String LOG_TAG = "COASTER_APPLICATION";

    public static CoasterCollectionData collectionData = new CoasterCollectionData();

    public static long currentCoasterID = -1;

    private static CoasterApplication applicationInstance;

    public static boolean refreshTrademarks = true;
    public static boolean refreshCollectors = true;
    public static boolean refreshCoasterTypes = true;
    public static boolean refreshSeries = true;
    public static boolean refreshShapes = true;
    public static boolean refreshCoasters = true;

//    public static boolean notifyCoastersAdapter = false;

    public static boolean showCoasterDetails = false;
    public static boolean showCoasterDescription = false;
    public static boolean showCoasterText = false;
    public static boolean showCoasterQuality = false;
    public static boolean showCollector = false;
    public static boolean showLocation = false;
    public static boolean showCollectDate = false;
    public static boolean showShape = false;
    public static boolean showSeries = false;
    public static boolean showImageBackside = false;
    public static boolean showInReverseOrder = false;
    public static String listViewType = "";

    public static String searchCoasterText = null;

    /**
     * Called when the application is created.
     */
    @Override
    public void onCreate() {
        Log.i(LOG_TAG, "onCreate");

        super.onCreate();

        applicationInstance = this;

        readPreferences();

        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread thread, Throwable e) {
                handleUncaughtException(thread, e);
            }
        });
    }
    public void handleUncaughtException (Thread thread, Throwable e)
    {
        Log.e(LOG_TAG, "Uncaught exception:", e);

        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);

        e.printStackTrace(pw);

        String stackTraceString = sw.toString();

        Intent intent = new Intent (applicationInstance, OnCrashActivity.class);

        intent.setFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS | Intent.FLAG_ACTIVITY_NO_HISTORY | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);

        intent.putExtra(IIntentExtras.EXTRA_STACKTRACE, stackTraceString);

        startActivity (intent);

        System.exit(1);
    }

    public static CoasterApplication getInstance() {
        return applicationInstance;
    }

    public void readPreferences() {
        Log.i(LOG_TAG, "readPreferences: BEGIN");

        showCoasterDetails = CoasterPreferences.showCoasterDetails(this);
        showCoasterDescription = CoasterPreferences.showCoasterDescription(this);
        showCoasterText = CoasterPreferences.showCoasterText(this);
        showCoasterQuality = CoasterPreferences.showCoasterQuality(this);
        showCollector = CoasterPreferences.showCollector(this);
        showLocation = CoasterPreferences.showLocation(this);
        showImageBackside = CoasterPreferences.showImageBackside(this);
        showCollectDate = CoasterPreferences.showCollectDate(this);
        showShape = CoasterPreferences.showShape(this);
        showSeries = CoasterPreferences.showSeries(this);
        showInReverseOrder = CoasterPreferences.showInReverseOrder(this);
        listViewType = CoasterPreferences.getListViewType(this);

        Log.i(LOG_TAG, "showCoasterDetails: " + showCoasterDetails);
        Log.i(LOG_TAG, "showCoasterDescription: " + showCoasterDescription);
        Log.i(LOG_TAG, "showCoasterText: " + showCoasterText);
        Log.i(LOG_TAG, "showCoasterQuality: " + showCoasterQuality);
        Log.i(LOG_TAG, "showCollector: " + showCollector);
        Log.i(LOG_TAG, "showCollectDate: " + showCollectDate);
        Log.i(LOG_TAG, "showLocation: " + showLocation);
        Log.i(LOG_TAG, "showShape: " + showShape);
        Log.i(LOG_TAG, "showSeries: " + showSeries);
        Log.i(LOG_TAG, "showImageBackside: " + showImageBackside);
        Log.i(LOG_TAG, "showInReverseOrder: " + showInReverseOrder);
        Log.i(LOG_TAG, "listViewType: " + listViewType);

        Log.i(LOG_TAG, "readPreferences: END");
    }
}
