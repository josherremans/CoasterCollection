package josh.android.coastercollection.bo;

/**
 * Created by Jos on 23/04/2017.
 */

public class GalleryItem {

    private long coasterID;
    private String imageName;
    private long seriesNbr = -1;
    private int dummyPictureResID = -1;

    public String getImageName() {
        return imageName;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }

    public Long getCoasterID() {
        return coasterID;
    }

    public void setCoasterID(long coasterID) {
        this.coasterID = coasterID;
    }

    public long getSeriesNbr() {
        return this.seriesNbr;
    }

    public void setSeriesNbr(long seriesNbr) {
        this.seriesNbr = seriesNbr;
    }

    public int getDummyPictureResID() {
        return this.dummyPictureResID;
    }

    public void setDummyPictureResID(int resID) {
        this.dummyPictureResID = resID;
    }
}
