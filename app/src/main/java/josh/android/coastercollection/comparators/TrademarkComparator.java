package josh.android.coastercollection.comparators;

import java.util.Comparator;

import josh.android.coastercollection.bo.Trademark;

/**
 * Created by Jos on 28/02/2017.
 */

public class TrademarkComparator implements Comparator<Trademark> {
    @Override
    public int compare(Trademark left, Trademark right) {
        return left.getTrademark().compareToIgnoreCase(right.getTrademark());
    }

//    @Override
//    public boolean equals(Object object) {
//        return false;
//    }
}
