package josh.android.coastercollection.bo;

public class CoasterType {

	private long coasterTypeID = -1;
	private long coasterTypeIDParent = -1;
	private String coasterTypeName = "";

	public CoasterType(long ID, long parentID, String name) {
		this.coasterTypeID = ID;
		this.coasterTypeIDParent = parentID;
		this.coasterTypeName = name;
	}

	public String getCoasterTypeName() {
		return coasterTypeName;
	}

	public long getCoasterTypeID() {
		return coasterTypeID;
	}

	@Override
	public String toString() {
		return this.getCoasterTypeName();
	}
}
