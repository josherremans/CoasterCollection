package josh.android.coastercollection.adapters;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

import josh.android.coastercollection.R;
import josh.android.coastercollection.activities.ImageFullscreenActivity;
import josh.android.coastercollection.application.CoasterApplication;
import josh.android.coastercollection.bl.ImageManager;
import josh.android.coastercollection.bo.Coaster;
import josh.android.coastercollection.bo.Series;
import josh.android.coastercollection.bo.Shape;
import josh.android.coastercollection.utils.Util;

import static josh.android.coastercollection.bl.ImageManager.DIR_DEF_IMAGES;

/**
 * Created by Jos on 13/11/2016.
 */
public class CoasterCollectionAdapter extends BaseAdapter {

    private LayoutInflater layoutInflater;

    private int listViewTypeId;
    private boolean showAllInfo = false;

    private Context cx;

    private ArrayList<Long> lstCoasterIds = new ArrayList<>();

    public CoasterCollectionAdapter(Context context, String listViewType) {
        cx = context;

        if (listViewType.equals(cx.getResources().getStringArray(R.array.pref_listview_type_values)[0])) { // "CardType"
            listViewTypeId = R.layout.item_coaster_list_card;
            showAllInfo = true;
        }

        if (listViewType.equals(cx.getResources().getStringArray(R.array.pref_listview_type_values)[1])) { // "FullWidthType"
            listViewTypeId = R.layout.item_coaster_list_more;
            showAllInfo = true;
        }

        if (listViewType.equals(cx.getResources().getStringArray(R.array.pref_listview_type_values)[2])) { // "FullWidthTypeSum"
            listViewTypeId = R.layout.item_coaster_list_less;
            showAllInfo = false;
        }

        layoutInflater = (LayoutInflater) context.getSystemService(Service.LAYOUT_INFLATER_SERVICE);
    }

    public void updateCoasterForList(ArrayList<Long> lstCoasterIds) {
        this.lstCoasterIds.clear();
        this.lstCoasterIds.addAll(lstCoasterIds);
    }

    @Override
    public int getCount() {
        if (lstCoasterIds == null) {
            return 0;
        } else {
            return lstCoasterIds.size();
        }
    }

    @Override
    public Object getItem(int position) {
        if (lstCoasterIds.size() > 0) {
            return lstCoasterIds.get(position);
        } else {
            return null;
        }
    }

    public int getPositionOfCoaster(long coasterID) {
        boolean asc = (lstCoasterIds.get(0)<lstCoasterIds.get(lstCoasterIds.size()-1) ? true : false);

        if (asc) {
            for (int i=0; i<lstCoasterIds.size(); i++) {
                if (lstCoasterIds.get(i) == coasterID) {
                    return i;
                } else {
                    if (lstCoasterIds.get(i) > coasterID) {
                        if (i>0) {
                            return --i;
                        } else {
                            return 0;
                        }
                    }
                }
            }
        } else {
            for (int i=0; i<lstCoasterIds.size(); i++) {
                if (lstCoasterIds.get(i) == coasterID) {
                    return i;
                } else {
                    if (lstCoasterIds.get(i) < coasterID) {
                        if (i>0) {
                            return --i;
                        } else {
                            return 0;
                        }
                    }
                }
            }
        }

        return 0;
    }

    public Coaster getRealItem(int position) {
        Long coasterId = (Long) getItem(position);

        Coaster coaster = CoasterApplication.collectionData.mapCoasters.get(coasterId);

        return coaster;
    }

    @Override
    public long getItemId(int position) {
        return lstCoasterIds.get(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View rowView = convertView;

        Coaster coaster = getRealItem(position);

        // reuse views
        if (rowView == null) {
            rowView = layoutInflater.inflate(listViewTypeId, null);

            // configure view holder
            ViewHolder viewHolder = new ViewHolder(rowView);

            rowView.setTag(viewHolder);
        }

        // Translate trademark id in trademark name:

        String trademark = CoasterApplication.collectionData.mapTrademarks.get(coaster.getCoasterTrademarkID()).getTrademark();

        // Translate collector id in collector name:

        String collectorName = CoasterApplication.collectionData.mapCollectors.get(coaster.getCollectorID()).getDisplayName();

        // Translate shape id in shape name:

        Shape foundShape = null;

        for (Shape sh : CoasterApplication.collectionData.lstShapes) {
            if (sh.getShapeID() == coaster.getCoasterMainShape()) {
                foundShape = sh;
                break;
            }
        }

        // Translate series id in series name:
        Series foundSeries = null;

        for (Series ser : CoasterApplication.collectionData.lstSeries) {
            if (ser.getSeriesID() == coaster.getCoasterSeriesID()) {
                foundSeries = ser;
                break;
            }
        }

        // fill data
        ViewHolder holder = (ViewHolder) rowView.getTag();

        holder.coasterID.setText("" + coaster.getCoasterID());

        String strImgFront = coaster.getCoasterImageFrontName();
        holder.txtImgCoasterFront.setVisibility(View.GONE);

        if ((strImgFront != null) && (strImgFront.length() > 0)) {
            if (strImgFront.equals("-")) {
                holder.imgCoasterFront.setImageResource(R.drawable.beer_bg_115);
                holder.txtImgCoasterFront.setVisibility(View.VISIBLE);
                holder.txtImgCoasterFront.setText(R.string.lblNameUndefined);
            } else {
                File imgFile = ImageManager.getImgFile(strImgFront);

                if (imgFile.exists()) {
                    new ImageManager().load(strImgFront, DIR_DEF_IMAGES, holder.imgCoasterFront);
                } else {
                    holder.imgCoasterFront.setImageResource(R.drawable.beer_bg_115);
                    holder.txtImgCoasterFront.setVisibility(View.VISIBLE);
                    holder.txtImgCoasterFront.setText(R.string.lblOops);
                }
            }
        } else {
            holder.imgCoasterFront.setImageResource(R.drawable.beer_bg_115);
            holder.txtImgCoasterFront.setVisibility(View.VISIBLE);
            holder.txtImgCoasterFront.setText(R.string.lblUnknown);
        }

        String strImgBack = coaster.getCoasterImageBackName();
        holder.txtImgCoasterBack.setVisibility(View.GONE);

        if ((strImgBack != null) && (strImgBack.length() > 0)) {
            holder.imgCoasterBack.setVisibility(View.VISIBLE);

            if (strImgBack.equals("-")) {
                holder.imgCoasterBack.setImageResource(R.drawable.beer_bg_115);
                holder.txtImgCoasterBack.setVisibility(View.VISIBLE);
                holder.txtImgCoasterBack.setText(R.string.lblNameUndefined);
            } else {
                File imgFile = ImageManager.getImgFile(strImgBack);

                if (imgFile.exists()) {
                    new ImageManager().load(strImgBack, DIR_DEF_IMAGES, holder.imgCoasterBack);
                } else {
                    holder.imgCoasterBack.setImageResource(R.drawable.beer_bg_115);
                    holder.txtImgCoasterBack.setVisibility(View.VISIBLE);
                    holder.txtImgCoasterBack.setText(R.string.lblOops);
                }
            }
        } else {
            holder.imgCoasterBack.setVisibility(View.GONE);
            holder.txtImgCoasterBack.setVisibility(View.GONE);
        }

        holder.imgCoasterFront.setOnClickListener(new ImageOnClickListener(coaster, true));
        holder.imgCoasterBack.setOnClickListener(new ImageOnClickListener(coaster, false));
        //holder.imgCoasterBack.setOnClickListener(new ImageSwitcherOnClickListener(coaster, holder.imgCoasterFront));

        holder.imgCoasterFront.setTag(R.id.TAG_SWITCHER, true);
        holder.imgCoasterBack.setTag(R.id.TAG_SWITCHER, true);

        String desc = coaster.getCoasterDescription();
        String text = coaster.getCoasterText();

        if ((desc == null) || (desc.length() == 0)) {
            holder.coasterDesc.setVisibility(View.GONE);
        } else {
            holder.coasterDesc.setVisibility(View.VISIBLE);
            holder.coasterDesc.setText(coaster.getCoasterDescription());
        }

        if ((text == null) || (text.length() == 0)) {
            holder.coasterText.setVisibility(View.GONE);
        } else {
            holder.coasterText.setVisibility(View.VISIBLE);
            holder.coasterText.setText(coaster.getCoasterText());
        }

        holder.trademark.setText(trademark);

        if (coaster.getCoasterSeriesID() == -1) {
            holder.layoutSeries.setVisibility(View.GONE);
        } else {
            holder.layoutSeries.setVisibility(View.VISIBLE);

            if ((coaster.getCoasterSeriesIndex() > 0) || (foundSeries.getMaxNumber() > 0)) {
                holder.seriesNbr.setVisibility(View.VISIBLE);

                holder.seriesNbr.setText("" + coaster.getCoasterSeriesIndex() + "/" + foundSeries.getMaxNumber() + ":");
            } else {
                holder.seriesNbr.setVisibility(View.GONE);
            }

            holder.seriesName.setText(foundSeries.getSeries());
        }

        if (showAllInfo) {
            final SimpleDateFormat dateFormatter = new SimpleDateFormat("dd/MM/yyyy", Locale.US);

            holder.donationDate.setText(dateFormatter.format(coaster.getCollectionDate()));
            holder.donor.setText(collectorName);

            if (coaster.getCoasterMainShape() != -1) {
                holder.shape.setText(foundShape.getName());
                holder.measurements.setText("(" + Util.getDisplayMeasurements(foundShape, coaster) + ")");
            } else {
                holder.shape.setVisibility(View.GONE);
                holder.measurements.setVisibility(View.GONE);
            }
        }

        return rowView;
    }

    private class ViewHolder {
        public TextView coasterID;
        public TextView trademark;
        public LinearLayout layoutCoasterDetails;
        public LinearLayout layoutImages;
        public LinearLayout layoutSeries;
        public TextView seriesNbr;
        public TextView seriesName;
        public TextView shape;
        public TextView measurements;
        public TextView donationDate;
        public TextView donor;
        public ImageView imgCoasterFront;
        public ImageView imgCoasterBack;
        public TextView txtImgCoasterFront;
        public TextView txtImgCoasterBack;
        public TextView coasterDesc;
        public TextView coasterText;

        public ViewHolder(View parent) {
            coasterID = (TextView) parent.findViewById(R.id.txtCoasterID);
            layoutCoasterDetails = (LinearLayout) parent.findViewById(R.id.layoutCoasterDetails);
            layoutImages = (LinearLayout) parent.findViewById(R.id.layoutImages);
            imgCoasterFront = (ImageView) parent.findViewById(R.id.imgCoasterFront);
            imgCoasterBack = (ImageView) parent.findViewById(R.id.imgCoasterBack);
            txtImgCoasterFront = (TextView) parent.findViewById(R.id.txtImgCoasterFront);
            txtImgCoasterBack = (TextView) parent.findViewById(R.id.txtImgCoasterBack);
            coasterDesc = (TextView) parent.findViewById(R.id.txtCoasterDescription);
            coasterText = (TextView) parent.findViewById(R.id.txtCoasterText);
            trademark = (TextView) parent.findViewById(R.id.txtTrademark);
            layoutSeries = (LinearLayout) parent.findViewById(R.id.layoutSeries);
            seriesNbr = (TextView) parent.findViewById(R.id.txtSeriesNbr);
            seriesName = (TextView) parent.findViewById(R.id.txtSeriesName);
            shape = (TextView) parent.findViewById(R.id.txtShape);
            measurements = (TextView) parent.findViewById(R.id.txtMeasurements);
            donationDate = (TextView) parent.findViewById(R.id.txtDonationDate);
            donor = (TextView) parent.findViewById(R.id.txtDonor);
        }
    }

    private class ImageOnClickListener implements View.OnClickListener {
        private Coaster coaster;
        private boolean isFrontImage;

        public ImageOnClickListener(Coaster coaster, boolean isFrontImage) {
            this.coaster = coaster;
            this.isFrontImage = isFrontImage;
        }

        @Override
        public void onClick(View v) {
            Intent imgIntent = new Intent(cx, ImageFullscreenActivity.class);

            if (((boolean) v.getTag(R.id.TAG_SWITCHER)) && (isFrontImage)) {
                imgIntent.putExtra("imgPath", coaster.getCoasterImageFrontName());
            } else {
                imgIntent.putExtra("imgPath", coaster.getCoasterImageBackName());
            }

            cx.startActivity(imgIntent);
        }
    }
}
