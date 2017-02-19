package josh.android.coastercollection.bo;

import java.util.ArrayList;
import java.util.LinkedHashMap;

/**
 * Created by Jos on 27/01/2017.
 */
public class CoasterCollectionData {
//    public ArrayList<Coaster> lstCoasters;
    public LinkedHashMap<Long, Coaster> mapCoasters;
    public ArrayList<CoasterType> lstCoasterTypes;
    public ArrayList<Trademark> lstTrademarks;
    public ArrayList<Collector> lstCollectors;
    public ArrayList<Series> lstSeries;
    public ArrayList<Shape> lstShapes;

    private ArrayList<Long> trademarkFilter;

    private boolean notifyAdapter = false;

    public CoasterCollectionData() {
//        this.lstCoasters = new ArrayList<>();
        this.mapCoasters = new LinkedHashMap<>();
        this.lstCoasterTypes = new ArrayList<>();
        this.lstTrademarks = new ArrayList<>();
        this.lstCollectors = new ArrayList<>();
        this.lstSeries = new ArrayList<>();
        this.lstShapes = new ArrayList<>();
    }

    public void setNotifyAdapter(boolean notify) {
        this.notifyAdapter = notify;
    }

    public boolean notifyAdapter() {
        boolean notify = notifyAdapter;

        notifyAdapter = false;

        return notify;
    }

    public void clearAll() {
//        this.lstCoasters.clear();
        this.mapCoasters.clear();
        this.lstCoasterTypes.clear();
        this.lstTrademarks.clear();
        this.lstCollectors.clear();
        this.lstSeries.clear();
        this.lstShapes.clear();
    }

//    public void clearCoasters() {
//        this.lstCoasters.clear();
//    }

//    public void setFilter(ArrayList<Long> trademarkFilter) {
//        this.trademarkFilter = trademarkFilter;
//    }

//    public ArrayList<Long> getFilter() {
//        return this.trademarkFilter;
//    }
}
