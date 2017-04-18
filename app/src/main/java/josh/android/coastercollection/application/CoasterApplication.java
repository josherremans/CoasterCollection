package josh.android.coastercollection.application;

import android.app.Application;

import josh.android.coastercollection.bo.CoasterCollectionData;

/**
 * Created by Jos on 29/01/2017.
 */
public class CoasterApplication extends Application {

    public static CoasterCollectionData collectionData = new CoasterCollectionData();

    public static long currentCoasterID = -1;
}
