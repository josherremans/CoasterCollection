package josh.android.coastercollection.databank;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by Jos on 17/02/2016.
 */
public class CoasterCollectionDBContract {

    // used for the UriMatcher:
    public static final int URI_CODE_COLLECTORS = 1;
    public static final int URI_CODE_COLLECTOR_ID = 2;
    public static final int URI_CODE_TRADEMARKS = 3;
    public static final int URI_CODE_TRADEMARK_ID = 4;

    public static final String CONTENT_AUTHORITY = "josh.android.coastercollection.contentprovider";

    public static final String PATH_COLLECTORS = "collectors";
    public static final String PATH_TRADEMARKS = "trademarks";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final class CollectionEntry implements BaseColumns {
        public static final String TABLE_NAME = "T_CoasterCollection";

        public static final String COLUMN_ID = "_ID";

        public static final String COLUMN_TRADEMARK_ID = "C_Trademark_ID";
        public static final String COLUMN_SERIES_ID = "C_Series_ID";
        public static final String COLUMN_SERIES_INDEX = "C_Series_Index";
        public static final String COLUMN_TEXT = "C_Text";
        public static final String COLUMN_DESCRIPTION = "C_Description";
        public static final String COLUMN_SHAPE_ID_MULTI = "C_Shape_ID_Multi";
        public static final String COLUMN_CATEGORY_ID = "C_Category_ID";
        public static final String COLUMN_MEASURE_1 = "C_Measure1_mm";
        public static final String COLUMN_MEASURE_2 = "C_Measure2_mm";
        public static final String COLUMN_COLLECTOR_ID = "C_Collector_ID";
        public static final String COLUMN_COLLECT_DATE = "C_CollectDate";
        public static final String COLUMN_COLLECT_PLACE = "C_CollectPlace";
        public static final String COLUMN_IMAGE_FRONT = "C_ImageFront";
        public static final String COLUMN_IMAGE_BACK = "C_ImageBack";
        public static final String COLUMN_QUALITY_ID = "C_Quality_ID";
    }

    public static final class CollectorEntry implements BaseColumns {
//        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_COLLECTORS).build();
//
//        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/collectors";
//        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/collector";

        public static final String TABLE_NAME = "T_Collector";

        public static final String COLUMN_ID = "_ID";
        public static final String COLUMN_FIRSTNAME = "C_FirstName";
        public static final String COLUMN_LASTNAME = "C_LastName";
        public static final String COLUMN_INITIALS = "C_Initials";
        public static final String COLUMN_ALIAS = "C_Alias";

//        public static Uri buildURI(long id) {
//            return ContentUris.withAppendedId(CONTENT_URI, id);
//        }
    }

    public static final class TrademarkEntry implements BaseColumns {
//        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_TRADEMARKS).build();
//
//        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/trademarks";
//        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/trademark";

        public static final String TABLE_NAME = "T_Trademark";

        public static final String COLUMN_ID = "_ID";
        public static final String COLUMN_TRADEMARK = "C_Trademark";
        public static final String COLUMN_BREWERY = "C_Brewery";
    }

    public static final class ShapeEntry implements BaseColumns {
        public static final String TABLE_NAME = "T_Shape";

        public static final String COLUMN_ID = "_ID";
        public static final String COLUMN_SHAPE = "C_Shape";
        public static final String COLUMN_PARENT_ID = "C_Parent_ID";
        public static final String COLUMN_MEASUREMENT_NAME_1 = "C_MeasurementName1";
        public static final String COLUMN_MEASUREMENT_NAME_2 = "C_MeasurementName2";
    }

    public static final class CoasterTypeEntry implements BaseColumns {
        public static final String TABLE_NAME = "T_Category";

        public static final String COLUMN_ID = "_ID";
        public static final String COLUMN_CATEGORY = "C_Category";
    }

    public static final class SeriesEntry implements BaseColumns {
//        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_TRADEMARKS).build();
//
//        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/trademarks";
//        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/trademark";

        public static final String TABLE_NAME = "T_Series";

        //public static final String COLUMN_ID = "_ID";
        public static final String COLUMN_SERIES = "C_Series";
        public static final String COLUMN_TRADEMARK_ID = "C_Trademark_ID";
        public static final String COLUMN_NUMBER = "C_Number";
    }
}
