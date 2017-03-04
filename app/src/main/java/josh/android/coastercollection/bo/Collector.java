package josh.android.coastercollection.bo;

public class Collector {

	private long collectorID = -1;

	private boolean fetchedFromDB = false;
	
	private String firstName = "";
	private String lastName = "";
	private String initials = "";
	private String alias = "";
	
	public Collector(long ID) {
		this.collectorID = ID;
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
	
	public Collector(long ID, String firstName, String lastName) {
		this.collectorID = ID;
		this.firstName = firstName;
		this.lastName = lastName;
	}
	
	public Collector(long ID, String firstName, String lastName, String initials, String alias) {
		this.collectorID = ID;
		this.firstName = firstName;
		this.lastName = lastName;
		this.initials = initials;
		this.alias = alias;
	}
	
	public long getCollectorID() {
		return collectorID;
	}
	
	public void setInitials(String initials) {
		this.initials = initials;
	}
	
	public String getInitials() {
		return initials;
	}
	
	public void setAlias(String alias) {
		this.alias = alias;
	}
	
	public String getAlias() {
		return alias;
	}
	
	public String getFirstName() {
		String firstNm = "";
		
		if (firstName != null) {
			firstNm = firstName;
		}
		
		return firstNm;
	}
	
	public String getLastName() {
		String lastNm = "";
		
		if (lastName != null) {
			lastNm = lastName;
		}
		
		return lastNm;
	}

	public String getFullName() {
		return lastName + ", " + firstName;
	}
	
	public String getDisplayName() {
		if (getLastName().length() == 0 && getFirstName().length() == 0) {
			return alias;
		} else if (getLastName().length() == 0) {
			return getFirstName() + " (" + alias + ")";
		} else if (getFirstName().length() == 0) {
			return getLastName() + " (" + alias + ")";
		} else {
			return lastName + ", " + firstName;
		}
	}

	@Override
	public String toString() {
		return getDisplayName();
	}
}
