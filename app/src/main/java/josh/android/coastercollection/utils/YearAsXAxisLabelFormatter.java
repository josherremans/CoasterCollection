package josh.android.coastercollection.utils;

import android.content.Context;
import android.text.format.DateFormat;

import com.jjoe64.graphview.helper.DateAsXAxisLabelFormatter;

/**
 * Created by Jos on 9/07/2017.
 */

public class YearAsXAxisLabelFormatter extends DateAsXAxisLabelFormatter {
    public YearAsXAxisLabelFormatter(Context context) {
        super(context);
    }

    @Override
    public String formatLabel(double value, boolean isValueX) {
        if (isValueX) {
            return DateFormat.format("yyyy", (long) value).toString();
        } else {
            return super.formatLabel(value, isValueX);
        }
    }
}
