package josh.android.coastercollection.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import josh.android.coastercollection.application.CoasterApplication;
import josh.android.coastercollection.bo.Coaster;
import josh.android.coastercollection.bo.CollectionHistoryMatrix;
import josh.android.coastercollection.bo.Shape;

/**
 * Created by Jos on 4/01/2017.
 */
public class Util {
    private static final String LOG_TAG = "UTIL";

    private final static SimpleDateFormat dateFormatter = new SimpleDateFormat("dd/MM/yyyy", Locale.US);

    public static String getDisplayMeasurements(Shape sh, Coaster c) {
        StringBuilder str = new StringBuilder();

        if (c.getMeasurement1() > 0) {
            str.append(sh.getNameMeasurement1()).append(": ").append(c.getMeasurement1()).append("mm");
        }

        if (c.getMeasurement2() > 0) {
            str.append(" ").append(sh.getNameMeasurement2()).append(": ").append(c.getMeasurement2()).append("mm");
        }

        return str.toString();
    }

    public static String getDisplayDateNow() {
        Date currentDate = new Date();

        return getDisplayDate(currentDate);
    }

    public static String getDisplayDate(Date date) {
        return dateFormatter.format(date);
    }

    public static Date getDateFromString(String strDate) {
        try {
            return dateFormatter.parse(strDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static CollectionHistoryMatrix createHistoryMatrix() {
        CollectionHistoryMatrix history = new CollectionHistoryMatrix();

        Calendar cal = Calendar.getInstance();

        for(Coaster c: CoasterApplication.collectionData.mapCoasters.values()) {
            Date collectionDate = c.getCollectionDate();

            cal.setTime(collectionDate);

            int month = cal.get(Calendar.MONTH);
            int year = cal.get(Calendar.YEAR);

            history.increaseAmount(year, month);
        }

//        Log.i(LOG_TAG, "Matrix: " + history.toString());

        return history;
    }
}
