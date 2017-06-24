package josh.android.coastercollection.comparators;

import java.util.Comparator;

import josh.android.coastercollection.bo.Collector;

/**
 * Created by Jos on 28/02/2017.
 */

public class CollectorComparator implements Comparator<Collector> {
    @Override
    public int compare(Collector c1, Collector c2) {
        return c1.getFullName().compareToIgnoreCase(c2.getFullName());
    }

//    @Override
//    public boolean equals(Object object) {
//        return false;
//    }
}
