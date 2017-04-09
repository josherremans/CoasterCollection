package josh.android.coastercollection.bo;

import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class Coaster implements Cloneable {
	private final static String LOG_TAG = "COASTER";

	public static final String COASTER_DATE_FORMAT = "dd/MM/yyyy";
	public static final String COASTER_DATE_FORMAT_DB = "yyyyMMdd";

	private boolean fetchedFromDB = false;
	private long coasterID = -1;

	private ArrayList<Long> coasterCategoryIDs = new ArrayList<Long>();

	private long coasterTrademarkID = -1;
	private long coasterSeriesID = -1;
	private long coasterSeriesIndex = -1;

	private ArrayList<Long> coasterShapeIDs = new ArrayList<Long>();

	private long measure1 = -1; // width, diameter
	private long measure2 = -1; // height

	private String coasterText = null;
	private String coasterDescription = null;

	private long collectorID = -1;
	private String collectionPlace = null;
	private Date collectionDate = null;

	private String coasterImageFrontName = null;
	private String coasterImageBackName = null;

	private long coasterQuality = -1;

	public void setFetchedFromDB() {
		this.fetchedFromDB = true;
	}

	public void setFetchedFromDB(boolean fetchedFromDB) {
		this.fetchedFromDB = fetchedFromDB;
	}

	public boolean isFetchedFromDB() {
		return this.fetchedFromDB;
	}

	public Coaster(long ID) {
		this.coasterID = ID;
	}

	public void alterCoasterID(long coasterID) {
		this.coasterID = coasterID;
	}

	public long getCoasterID() {
		return coasterID;
	}

	public long getCoasterTrademarkID() {
		return coasterTrademarkID;
	}

	public ArrayList<Long> getCoasterCategoryIDs() {
		return coasterCategoryIDs;
	}

	public void addCoasterCategory(long coasterCategoryID) {
		this.coasterCategoryIDs.add(coasterCategoryID);
	}

	public long getCoasterSeriesID() {
		return coasterSeriesID;
	}

	public void setCoasterSeriesID(long coasterSeriesID) {
		this.coasterSeriesID = coasterSeriesID;
	}

	public long getCoasterSeriesIndex() {
		return coasterSeriesIndex;
	}

	public void setCoasterSeriesIndex(long coasterSeriesIndex) {
		this.coasterSeriesIndex = coasterSeriesIndex;
	}

	public ArrayList<Long> getCoasterShapeIDs() {
		return coasterShapeIDs;
	}

	public long getCoasterMainShape() {
		if (coasterShapeIDs.size() > 0) {
			return coasterShapeIDs.get(0);
		}

		return -1;
	}

	public void addCoasterShapeID(long coasterShapeID) {
		this.coasterShapeIDs.add(coasterShapeID);
	}

	public long getMeasurement1() {
		return measure1;
	}

	public long getMeasurement2() {
		return measure2;
	}

	public void setMeasurements(long width, long height) {
		this.measure1 = width;
		this.measure2 = height;
	}

	public String getCoasterText() {
		return coasterText;
	}

	public void setCoasterText(String coasterText) {
		this.coasterText = coasterText;
	}

	public String getCoasterDescription() {
		return coasterDescription;
	}

	public void setCoasterDescription(String coasterDescription) {
		this.coasterDescription = coasterDescription;
	}

	public long getCollectorID() {
		return collectorID;
	}

	public void setCollectorID(long collectorID) {
		this.collectorID = collectorID;
	}

	public String getCollectionPlace() {
		return collectionPlace;
	}

	public void setCollectionPlace(String collectionPlace) {
		this.collectionPlace = collectionPlace;
	}

	public Date getCollectionDate() {
		return collectionDate;
	}

	public void setCollectionDate(Date collectionDate) {
		this.collectionDate = collectionDate;
	}

	public void setCollectionDate(String strCollectionDate) {
		SimpleDateFormat sdf = new SimpleDateFormat(COASTER_DATE_FORMAT);

		try {
			this.collectionDate = sdf.parse(strCollectionDate);
		} catch (ParseException e) {
		}
	}

	public String getCoasterImageFrontName() {
		return coasterImageFrontName;
	}

	public void setCoasterImageFrontName(String coasterImageFrontName) {
		this.coasterImageFrontName = coasterImageFrontName;
	}

	public String getCoasterImageBackName() {
		return coasterImageBackName;
	}

	public void setCoasterImageBackName(String coasterImageBackName) {
		this.coasterImageBackName = coasterImageBackName;
	}

	public void setCoasterTrademarkID(long coasterTrademarkID) {
		this.coasterTrademarkID = coasterTrademarkID;
	}

	public void setCoasterQuality(long quality) {
		this.coasterQuality = quality;
	}

	public long getCoasterQuality() {
		return this.coasterQuality;
	}

	@Override
	public Object clone() throws CloneNotSupportedException {
		Coaster newObj = (Coaster) super.clone();

		newObj.fetchedFromDB = this.fetchedFromDB;

		newObj.coasterID = this.coasterID;

		newObj.coasterCategoryIDs = new ArrayList<Long>();

		if (this.coasterCategoryIDs != null) {
			for (Long id : this.coasterCategoryIDs) {
				newObj.coasterCategoryIDs.add(new Long(id));
			}
		}

		newObj.coasterTrademarkID = this.coasterTrademarkID;
		newObj.coasterSeriesID = this.coasterSeriesID;
		newObj.coasterSeriesIndex = this.coasterSeriesIndex;

		newObj.coasterShapeIDs = new ArrayList<Long>();

		if (this.coasterShapeIDs != null) {
			for (Long id : this.coasterShapeIDs) {
				newObj.coasterShapeIDs.add(new Long(id));
			}
		}

		newObj.measure1 = this.measure1; // width, diameter
		newObj.measure2 = this.measure2; // height

		newObj.coasterText = this.coasterText;
		newObj.coasterDescription = this.coasterDescription;

		newObj.collectorID = this.collectorID;
		newObj.collectionPlace = this.collectionPlace;
		newObj.collectionDate = this.collectionDate;

		newObj.coasterImageFrontName = this.coasterImageFrontName;
		newObj.coasterImageBackName = this.coasterImageBackName;

		newObj.coasterQuality = this.coasterQuality;

		return newObj;
	}

	@Override
	public boolean equals(Object another) {
		if (another == null)
			return false;

		if (another instanceof Coaster) {
			Coaster other = (Coaster) another;

			if (this == another)
				return true;

			Log.i(LOG_TAG, "equals: sofar (1) res: " + true);

			if ((this.coasterID == other.coasterID)
				&& (this.getCoasterTrademarkID() == other.getCoasterTrademarkID())
				&& (this.getCoasterSeriesID() == other.getCoasterSeriesID())
				&& (this.getCoasterSeriesIndex() == other.getCoasterSeriesIndex())
				&& (this.getCoasterDescription().equals(other.getCoasterDescription()))
				&& (this.getCoasterText().equals(other.getCoasterText()))
				&& (this.getCoasterQuality() == other.getCoasterQuality())
				&& (this.getCollectorID() == other.getCollectorID())
				&& (this.getCollectionPlace().equals(other.getCollectionPlace()))
				&& (this.getCollectionDate().equals(other.getCollectionDate()))
				&& (this.getCoasterImageFrontName().equals(other.getCoasterImageFrontName()))
				&& (this.getCoasterImageBackName().equals(other.getCoasterImageBackName()))) {

				boolean res = true;

				Log.i(LOG_TAG, "equals: sofar (2) res: " + res);

				if (this.getCoasterShapeIDs().size() == other.getCoasterShapeIDs().size()) {
					int n = this.getCoasterShapeIDs().size();

					for (int i=0; i<n; i++) {
						if (!this.getCoasterShapeIDs().get(i).equals(other.getCoasterShapeIDs().get(i))) {
							res = false;
						}
					}
				} else {
					res = false;
				}

				Log.i(LOG_TAG, "equals: sofar (3) res: " + res);

				if (this.getCoasterCategoryIDs().size() == other.getCoasterCategoryIDs().size()) {
					int n = this.getCoasterCategoryIDs().size();

					for (int i=0; i<n; i++) {
						if (!this.getCoasterCategoryIDs().get(i).equals(other.getCoasterCategoryIDs().get(i))) {
							res = false;
						}
					}
				} else {
					res = false;
				}

				Log.i(LOG_TAG, "equals: END res: " + res);

				return res;
			} else {
				Log.i(LOG_TAG, "equals: res: " + false);

				Log.i(LOG_TAG, "equals: getCoasterSeriesIndex: " + this.getCoasterSeriesIndex() + " vs " + other.getCoasterSeriesIndex());

				return false;
			}
		}

		return false;
	}
}
