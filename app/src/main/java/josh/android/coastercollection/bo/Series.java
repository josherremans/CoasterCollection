package josh.android.coastercollection.bo;

/**
 * Created by Jos on 4/12/2016.
 */
public class Series {
    private long seriesID = -1;

    private boolean fetchedFromDB = false;

    private long trademarkID = -1;
    private String series = "";
    private long maxNumber = 0;
    private boolean ordered = false;

    public Series(long seriesID, long trademarkID, String series, long maxNumber, boolean ordered) {
        this.seriesID = seriesID;
        this.trademarkID = trademarkID;
        this.series = series;
        this.maxNumber = maxNumber;
        this.ordered = ordered;
    }

    public void setFetchedFromDB() {
        this.fetchedFromDB = true;
    }

    public void setFetchedFromDB(boolean fetchedFromDB) {
        this.fetchedFromDB = fetchedFromDB;
    }

    public boolean isFetchedFromDB() {
        return this.fetchedFromDB;
    }

    public long getSeriesID() {
        return this.seriesID;
    }

    public long getTrademarkID() {
        return this.trademarkID;
    }

    public String getSeries() {
        return this.series;
    }

    public long getMaxNumber() { return this.maxNumber; }

    public boolean isOrdered() { return this.ordered; }

    @Override
    public String toString() {
        return getSeries();
    }
}
