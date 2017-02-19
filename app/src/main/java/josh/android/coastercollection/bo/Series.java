package josh.android.coastercollection.bo;

/**
 * Created by Jos on 4/12/2016.
 */
public class Series {
    private long seriesID = -1;
    private long trademarkID = -1;
    private String series = "";
    private long maxNumber = 0;

    public Series(long seriesID, long trademarkID, String series, long maxNumber) {
        this.seriesID = seriesID;
        this.trademarkID = trademarkID;
        this.series = series;
        this.maxNumber = maxNumber;
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

    @Override
    public String toString() {
        return getSeries();
    }
}
