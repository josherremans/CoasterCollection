package josh.android.coastercollection.application;

import android.app.Application;
import android.content.Intent;
import android.util.Log;

import java.io.PrintWriter;
import java.io.StringWriter;

import josh.android.coastercollection.activities.OnCrashActivity;
import josh.android.coastercollection.bo.CoasterCollectionData;
import josh.android.coastercollection.enums.IIntentExtras;

/**
 * Created by Jos on 29/01/2017.
 */
public class CoasterApplication extends Application {

    private static String LOG_TAG = "COASTER_APPLICATION";

    public static CoasterCollectionData collectionData = new CoasterCollectionData();

    public static long currentCoasterID = -1;

    private static CoasterApplication applicationInstance;

    /**
     * Called when the application is created.
     */
    @Override
    public void onCreate() {
        super.onCreate();

        applicationInstance = this;

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
}
