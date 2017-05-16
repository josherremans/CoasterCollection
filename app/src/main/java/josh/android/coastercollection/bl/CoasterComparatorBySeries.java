package josh.android.coastercollection.bl;

import java.util.Comparator;

import josh.android.coastercollection.bo.Coaster;

/**
 * Created by Jos on 7/05/2017.
 */

public class CoasterComparatorBySeries implements Comparator<Coaster> {
    public int compare(Coaster left, Coaster right) {
        if (left.getCoasterSeriesID() < right.getCoasterSeriesID()) {
            return -1;
        } else if (left.getCoasterSeriesID() > right.getCoasterSeriesID()) {
            return 1;
        } else {
            if (left.getCoasterSeriesIndex() < right.getCoasterSeriesIndex()) {
                return -1;
            } else if (left.getCoasterSeriesIndex() > right.getCoasterSeriesIndex()) {
                return 1;
            } else {
                return 0;
            }
        }
    }

    @Override
    public boolean equals(Object object) {
        return false;
    }
}
