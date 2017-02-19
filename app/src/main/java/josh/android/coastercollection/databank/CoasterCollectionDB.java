//package josh.android.coastercollection.databank;
//
//import android.content.Context;
//import android.database.sqlite.SQLiteDatabase;
//
//import java.sql.SQLException;
//
///**
// * Created by Jos on 11/12/2016.
// */
//public class CoasterCollectionDB {
//    // Database fields
//    private SQLiteDatabase database;
//    private CoasterCollectionDBHelper dbHelper;
//    private String[] allColumns = { CoasterCollectionDBHelper.COLUMN_ID,
//            CoasterCollectionDBHelper.COLUMN_COMMENT };
//
//    public CoasterCollectionDB(Context context) {
//        dbHelper = new CoasterCollectionDBHelper(context);
//    }
//
//    public void open() throws SQLException {
//        database = dbHelper.getWritableDatabase();
//    }
//
//    public void close() {
//        dbHelper.close();
//    }
//}
