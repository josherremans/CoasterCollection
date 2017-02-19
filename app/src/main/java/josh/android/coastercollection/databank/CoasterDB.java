package josh.android.coastercollection.databank;

import java.util.ArrayList;

import josh.android.coastercollection.bo.CoasterType;
import josh.android.coastercollection.bo.ImagePossibility;

/**
 * Created by Jos on 25/11/2016.
 */
public class CoasterDB {

//    public static ArrayList<Trademark> getTrademarks() {
//        ArrayList<Trademark> lstAllTrademarks = new ArrayList<Trademark>();
//
//        int i = 0;
//
//        lstAllTrademarks.add(new Trademark(i++, "Affligem"));
//        lstAllTrademarks.add(new Trademark(i++, "Brugge Tripel"));
//        lstAllTrademarks.add(new Trademark(i++, "Carlsberg"));
//        lstAllTrademarks.add(new Trademark(i++, "Dentergems"));
//        lstAllTrademarks.add(new Trademark(i++, "Duvel"));
//        lstAllTrademarks.add(new Trademark(i++, "Gust"));
//        lstAllTrademarks.add(new Trademark(i++, "Heineken"));
//        lstAllTrademarks.add(new Trademark(i++, "Hoegaarden"));
//        lstAllTrademarks.add(new Trademark(i++, "Jupiler"));
//        lstAllTrademarks.add(new Trademark(i++, "Kasteelbier van Ingelmunster"));
//        lstAllTrademarks.add(new Trademark(i++, "Maes"));
//        lstAllTrademarks.add(new Trademark(i++, "Omer"));
//        lstAllTrademarks.add(new Trademark(i++, "Primus"));
//        lstAllTrademarks.add(new Trademark(i++, "Rodenbach"));
//        lstAllTrademarks.add(new Trademark(i++, "Rodenbos"));
//        lstAllTrademarks.add(new Trademark(i++, "Rodler"));
//        lstAllTrademarks.add(new Trademark(i++, "Stella Artois"));
//        lstAllTrademarks.add(new Trademark(i++, "Wittekerke"));
//
//        return lstAllTrademarks;
//    }

//    public static ArrayList<Series> getSeries(String trademark) {
//        ArrayList<Series> lstAllSeries = new ArrayList<Series>();
//
//        int i =0;
//
//        if (trademark.equals("Jupiler")) {
//            lstAllSeries.add(new Series(i++, "Jupiler", "Mannen weten waarom."));
//        }
//
//        return lstAllSeries;
//    }

   public static ArrayList<CoasterType> getCoasterTypes() {
        ArrayList<CoasterType> lstAllCoasterTypes = new ArrayList<CoasterType>();

        lstAllCoasterTypes.add(new CoasterType(-1, -1, "(Select Coaster Type)"));
        lstAllCoasterTypes.add(new CoasterType( 1, -1, "Beer coasters"));
        lstAllCoasterTypes.add(new CoasterType( 2, -1, "Coffee coasters"));
        lstAllCoasterTypes.add(new CoasterType( 3, -1, "Commercial coasters"));
        lstAllCoasterTypes.add(new CoasterType( 4,  3, "Car sale coasters"));

        return lstAllCoasterTypes;
    }

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

//    public static ArrayList<Collector> getCollectors() {
//        ArrayList<Collector> lstAllCollectors = new ArrayList<Collector>();
//
//        int i=0;
//
//        lstAllCollectors.add(new Collector(i++, "Jos", "Herremans", "JH", ""));
//        lstAllCollectors.add(new Collector(i++, "Elly", "Van Acoleyen", "EVA", ""));
//        lstAllCollectors.add(new Collector(i++, "Eric", "Van Acoleyen", "EVA2", ""));
//        lstAllCollectors.add(new Collector(i++, "Mike", "Van Acoleyen", "MVA", ""));
//        lstAllCollectors.add(new Collector(i++, "Roger", "Van Acoleyen", "RVA", ""));
//        lstAllCollectors.add(new Collector(i++, "Greet", "Dolvelde", "GD", ""));
//        lstAllCollectors.add(new Collector(i++, "Koen", "Stockman", "KS", ""));
//        lstAllCollectors.add(new Collector(i++, "Lily", "Callens", "LC", ""));
//
//        return lstAllCollectors;
//    }

//    public static ArrayList<Coaster> getCoasters(String trademarkFilter) {
//        // TODO Temp data!!!
//        ArrayList<Coaster> lstAllCoasters = new ArrayList<Coaster>();
//
//        Coaster c1 = new Coaster(1001);
//        c1.setCoasterTrademarkID(50);
//        c1.setCoasterDescription("This is the coaster description of c1");
//        c1.setCoasterText("This is the coaster text of c1");
//
//        lstAllCoasters.add(c1);
//
//        Coaster c2 = new Coaster(1002);
//        c2.setCoasterTrademarkID(100);
//        c2.setCoasterDescription("This is the coaster description of c2");
//        c2.setCoasterText("This is the coaster text of c2");
//
//        lstAllCoasters.add(c2);
//
//        Coaster c3 = new Coaster(1003);
//        c3.setCoasterTrademarkID(150);
//        c3.setCoasterDescription("This is the coaster description of c3");
//        c3.setCoasterText("This is the coaster text of c3");
//
//        lstAllCoasters.add(c3);
//
//        Coaster c4 = new Coaster(1004);
//        c4.setCoasterTrademarkID(200);
//        c4.setCoasterDescription("This is the coaster description of c4");
//        c4.setCoasterText("This is the coaster text of c4");
//
//        lstAllCoasters.add(c4);
//
//        Coaster c5 = new Coaster(1005);
//        c5.setCoasterTrademarkID(210);
//        c5.setCoasterDescription("This is the coaster description of c5");
//        c5.setCoasterText("This is the coaster text of c5");
//
//        lstAllCoasters.add(c5);
//
//        // Filter:
//
//        ArrayList<Trademark> lstTrademarks = new ArrayList<>();
//
//        for (Trademark t : getTrademarks()) {
//            if ((trademarkFilter == null)
//                    || (trademarkFilter.length() == 0)
//                    || (t.getTrademark().regionMatches(true, 0, trademarkFilter, 0, trademarkFilter.length()))) {
//                    lstTrademarks.add(t);
//            }
//        }
//
//        ArrayList<Coaster> lstCoasters = new ArrayList<>();
//
//        for (Coaster c : lstAllCoasters)  {
//            if ((trademarkFilter == null)
//                    || (trademarkFilter.length() == 0)) {
//                lstCoasters.add(c);
//            } else {
//                for (Trademark t: lstTrademarks) {
//                    if (t.getTrademarkID() == c.getCoasterTrademarkID()) {
//                        lstCoasters.add(c);
//                        break;
//                    }
//                }
//            }
//        }
//
//        return lstCoasters;
//    }
}
