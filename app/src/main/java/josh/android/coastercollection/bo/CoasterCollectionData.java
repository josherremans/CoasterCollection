package josh.android.coastercollection.bo;

import java.util.ArrayList;
import java.util.LinkedHashMap;

/**
 * Created by Jos on 27/01/2017.
 */
public class CoasterCollectionData {
    public LinkedHashMap<Long, Coaster> mapCoasters;
    public ArrayList<CoasterType> lstCoasterTypes;
    public LinkedHashMap<Long, Trademark> mapTrademarks;
    public LinkedHashMap<Long, Collector> mapCollectors;
    public ArrayList<Series> lstSeries;
    public ArrayList<Shape> lstShapes;

    private ArrayList<Long> trademarkFilter;

    private boolean notifyAdapter = false;

    public CoasterCollectionData() {
        this.mapCoasters = new LinkedHashMap<>();
        this.lstCoasterTypes = new ArrayList<>();
        this.mapTrademarks = new LinkedHashMap<>();
        this.mapCollectors = new LinkedHashMap<>();
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
        this.mapCoasters.clear();
        this.lstCoasterTypes.clear();
        this.mapTrademarks.clear();
        this.mapCollectors.clear();
        this.lstSeries.clear();
        this.lstShapes.clear();
    }
}
