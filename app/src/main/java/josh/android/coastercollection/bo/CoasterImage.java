package josh.android.coastercollection.bo;

public class CoasterImage {
	private String imageName;
	private String imageSubscript;
	
	public CoasterImage(String imageName, String imageSubscript) {
		this.imageName = imageName;
		this.imageSubscript = imageSubscript;
	}
	
	public String getImageName() {
		return imageName;
	}
	
	public String getImageSubscript() {
		return imageSubscript;
	}
}
