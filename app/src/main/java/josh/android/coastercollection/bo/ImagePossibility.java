package josh.android.coastercollection.bo;

/**
 * Created by Jos on 28/12/2016.
 */
public class ImagePossibility {
    private long id;
    private boolean front;
    private String displayName;

    public ImagePossibility(long id, boolean front, String displayName) {
        this.id = id;
        this.front = front;
        this.displayName = displayName;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public boolean isFront() {
        return front;
    }

    public void setFront(boolean front) {
        this.front = front;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    @Override
    public String toString() {
        return getDisplayName();
    }
}
