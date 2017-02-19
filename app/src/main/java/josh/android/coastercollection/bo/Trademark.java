package josh.android.coastercollection.bo;

public class Trademark {

	private long trademarkID = -1;

	private boolean fetchedFromDB = false;
	
	private String trademark = "";
	private String brewery = "";

	public void setFetchedFromDB() {
		this.fetchedFromDB = true;
	}

	public void setFetchedFromDB(boolean fetchedFromDB) {
		this.fetchedFromDB = fetchedFromDB;
	}

	public boolean isFetchedFromDB() {
		return this.fetchedFromDB;
	}
	
	public Trademark(long ID) {
		this(ID, "", "");
	}
	
	public Trademark(long ID, String trademark) {
		this(ID, trademark, "");
	}
	
	public Trademark(long ID, String trademark, String brewery) {
		this.trademarkID = ID;
		this.trademark = trademark;
		this.brewery = brewery;
	}
	
	public long getTrademarkID() {
		return trademarkID;
	}

	public String getTrademark() {
		return trademark;
	}

	public String getBrewery() {
		return brewery;
	}

	public void fillTrademark(String trademark, String brewery) {
		this.trademark = trademark;
		this.brewery = brewery;
	}

	@Override
	public String toString() {
		return getTrademark();
	}
}
