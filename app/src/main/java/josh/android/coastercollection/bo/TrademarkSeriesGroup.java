package josh.android.coastercollection.bo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jos on 20/03/2017.
 */

public class TrademarkSeriesGroup {
    public String trademark;
    public final List<String> series = new ArrayList<String>();

    public TrademarkSeriesGroup(String trademark) {
        this.trademark = trademark;
    }
}
