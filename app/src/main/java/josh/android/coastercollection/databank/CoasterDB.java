package josh.android.coastercollection.databank;

import java.util.ArrayList;

import josh.android.coastercollection.bo.ImagePossibility;

/**
 * Created by Jos on 25/11/2016.
 */
public class CoasterDB {

    public static ArrayList<ImagePossibility> getImagePossibilities(boolean front) {
        ArrayList<ImagePossibility> lstAllImagePossibilities = new ArrayList<ImagePossibility>();

        lstAllImagePossibilities.add(new ImagePossibility(-1, true, "(Select Image)"));
        lstAllImagePossibilities.add(new ImagePossibility(0, true, "New front image"));
        lstAllImagePossibilities.add(new ImagePossibility(1, true, "Existing front image"));

        lstAllImagePossibilities.add(new ImagePossibility(-1, false, "(Select Image)"));
        lstAllImagePossibilities.add(new ImagePossibility(0, false, "No back image"));
        lstAllImagePossibilities.add(new ImagePossibility(1, false, "New back image"));
        lstAllImagePossibilities.add(new ImagePossibility(2, false, "Existing back image"));
        lstAllImagePossibilities.add(new ImagePossibility(3, false, "Same as front image"));

        ArrayList<ImagePossibility> lstImagePossibilities = new ArrayList<ImagePossibility>();

        for (ImagePossibility imgPos : lstAllImagePossibilities) {
            if (imgPos.isFront() == front) {
                lstImagePossibilities.add(imgPos);
            }
        }

        return lstImagePossibilities;
    }
}
