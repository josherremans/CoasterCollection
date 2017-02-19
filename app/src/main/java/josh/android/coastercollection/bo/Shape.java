package josh.android.coastercollection.bo;

/**
 * Created by Jos on 30/11/2016.
 */
public class Shape {
    private long id;
    private String name;
    private long parentID;
    private String nameMeasurement1;
    private String nameMeasurement2;

    public Shape(long id, String name, long parentID, String nameMeasurement1, String nameMeasurement2) {
        this.id = id;
        this.name = name;
        this.parentID = parentID;
        this.nameMeasurement1 = nameMeasurement1;
        this.nameMeasurement2 = nameMeasurement2;
    }

    public long getShapeID() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public String getNameMeasurement1() {
        return nameMeasurement1;
    }

    public String getNameMeasurement2() {
        return nameMeasurement2;
    }

    @Override
    public String toString() {
        return getName();
    }
}
