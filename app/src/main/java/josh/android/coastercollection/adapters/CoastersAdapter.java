package josh.android.coastercollection.adapters;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

import josh.android.coastercollection.R;
import josh.android.coastercollection.activities.GalleryActivity;
import josh.android.coastercollection.activities.ImageFullscreenActivity;
import josh.android.coastercollection.application.CoasterApplication;
import josh.android.coastercollection.bl.ImageManager;
import josh.android.coastercollection.bo.Coaster;
import josh.android.coastercollection.bo.Series;
import josh.android.coastercollection.bo.Shape;
import josh.android.coastercollection.enums.IIntentExtras;
import josh.android.coastercollection.utils.Util;

import static josh.android.coastercollection.bl.ImageManager.DIR_DEF_IMAGES;

/**
 * Created by Jos on 13/11/2016.
 */
public class CoastersAdapter extends ArrayAdapter<Long> {

    private LayoutInflater layoutInflater;

    private Context cx;

    private ArrayList<Long> lstCoasterIds = new ArrayList<>();

    public CoastersAdapter(Context context, int itemLayoutType, ArrayList<Long> lstCoasterIds) {
        super(context, itemLayoutType, lstCoasterIds);

        cx = context;

        this.lstCoasterIds = lstCoasterIds;

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
    public Long getItem(int position) {
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

        final Coaster coaster = getRealItem(position);

        ViewHolder holder;

        // reuse views
        if (rowView == null) {
            rowView = layoutInflater.inflate(R.layout.item_coaster_list_more, null);

            // configure view holder
            holder = new ViewHolder(rowView);

            rowView.setTag(holder);
        }

        // Translate trademark id in trademark name:

        final String trademark = CoasterApplication.collectionData.mapTrademarks.get(coaster.getCoasterTrademarkID()).getTrademark();

        // Translate collector id in collector name:

        final String collectorName = CoasterApplication.collectionData.mapCollectors.get(coaster.getCollectorID()).getDisplayName();

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
        holder = (ViewHolder) rowView.getTag();

        holder.coasterID.setText("" + coaster.getCoasterID());

        if (coaster.getCoasterQuality() > 0) {
            if (coaster.getCoasterQuality() <= 5) {
                holder.coasterID.setTextColor(cx.getResources().getColor(R.color.colorQualityVeryBad));
            } else if (coaster.getCoasterQuality() <= 8) {
                holder.coasterID.setTextColor(cx.getResources().getColor(R.color.colorQualityBad));
            } else {
                holder.coasterID.setTextColor(cx.getResources().getColor(R.color.colorQualityGood));
            }
        } else {
            holder.coasterID.setTextColor(cx.getResources().getColor(R.color.colorQualityGood));
        }

        String strImgFront = coaster.getCoasterImageFrontName();
        holder.txtImgCoasterFront.setVisibility(View.GONE);

        if ((strImgFront != null) && (strImgFront.length() > 0)) {
            if (strImgFront.equals("-")) {
                int resID = ImageManager.getDummyPictureResourceID(coaster.getCoasterMainShape(), coaster.getMeasurement1(), coaster.getMeasurement2());

                holder.imgCoasterFront.setImageResource(resID);
                holder.txtImgCoasterFront.setVisibility(View.VISIBLE);
                holder.txtImgCoasterFront.setText(R.string.lblNameUndefined);
            } else {
                File imgFile = ImageManager.getImgFile(strImgFront);

                if (imgFile.exists()) {
                    new ImageManager().load(strImgFront, DIR_DEF_IMAGES, holder.imgCoasterFront);
                } else {
                    int resID = ImageManager.getDummyPictureResourceID(coaster.getCoasterMainShape(), coaster.getMeasurement1(), coaster.getMeasurement2());

                    holder.imgCoasterFront.setImageResource(resID);
                    holder.txtImgCoasterFront.setVisibility(View.VISIBLE);
                    holder.txtImgCoasterFront.setText(R.string.lblOops);
                }
            }
        } else {
            int resID = ImageManager.getDummyPictureResourceID(coaster.getCoasterMainShape(), coaster.getMeasurement1(), coaster.getMeasurement2());

            holder.imgCoasterFront.setImageResource(resID);
            holder.txtImgCoasterFront.setVisibility(View.VISIBLE);
            holder.txtImgCoasterFront.setText(R.string.lblUnknown);
        }

        holder.imgCoasterBack.setVisibility(View.GONE);
        holder.txtImgCoasterBack.setVisibility(View.GONE);

        if (CoasterApplication.showImageBackside) {
            String strImgBack = coaster.getCoasterImageBackName();

            if ((strImgBack != null) && (strImgBack.length() > 0)) {
                holder.imgCoasterBack.setVisibility(View.VISIBLE);

                if (strImgBack.equals("-")) {
                    int resID = ImageManager.getDummyPictureResourceID(coaster.getCoasterMainShape(), coaster.getMeasurement1(), coaster.getMeasurement2());

                    holder.imgCoasterBack.setImageResource(resID);
                    holder.txtImgCoasterBack.setVisibility(View.VISIBLE);
                    holder.txtImgCoasterBack.setText(R.string.lblNameUndefined);
                } else {
                    File imgFile = ImageManager.getImgFile(strImgBack);

                    if (imgFile.exists()) {
                        new ImageManager().load(strImgBack, DIR_DEF_IMAGES, holder.imgCoasterBack);
                    } else {
                        int resID = ImageManager.getDummyPictureResourceID(coaster.getCoasterMainShape(), coaster.getMeasurement1(), coaster.getMeasurement2());

                        holder.imgCoasterBack.setImageResource(resID);
                        holder.txtImgCoasterBack.setVisibility(View.VISIBLE);
                        holder.txtImgCoasterBack.setText(R.string.lblOops);
                    }
                }
            }
        }

        holder.imgCoasterFront.setOnClickListener(new ImageOnClickListener(coaster, true));
        holder.imgCoasterBack.setOnClickListener(new ImageOnClickListener(coaster, false));

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

        holder.trademark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryIntent = new Intent(cx, GalleryActivity.class);

                galleryIntent.putExtra(IIntentExtras.EXTRA_TRADEMARKID, coaster.getCoasterTrademarkID());
                galleryIntent.putExtra(IIntentExtras.EXTRA_GALLERY_SUBTITLE, trademark);

                cx.startActivity(galleryIntent);
            }
        });

        holder.layoutSeries.setVisibility(View.GONE);

        if (CoasterApplication.showSeries) {
            if (coaster.getCoasterSeriesID() != -1) {
                holder.layoutSeries.setVisibility(View.VISIBLE);

                if ((coaster.getCoasterSeriesIndex() > 0) && (foundSeries.getMaxNumber() > 0)) {
                    holder.seriesNbr.setVisibility(View.VISIBLE);

                    holder.seriesNbr.setText("" + coaster.getCoasterSeriesIndex() + "/" + foundSeries.getMaxNumber() + ":");
                } else {
                    holder.seriesNbr.setVisibility(View.GONE);
                }

                holder.seriesName.setText(foundSeries.getSeries());

                final Series fseries = foundSeries;

                holder.seriesName.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent galleryIntent = new Intent(cx, GalleryActivity.class);

                        String subTitle = fseries.getSeries();

                        if (fseries.getMaxNumber() > 0) {
                            subTitle += " (" + fseries.getMaxNumber() + (fseries.isOrdered() ? "s" : "") + ")";
                        }

                        galleryIntent.putExtra(IIntentExtras.EXTRA_SERIESID, coaster.getCoasterSeriesID());
                        galleryIntent.putExtra(IIntentExtras.EXTRA_GALLERY_SUBTITLE, subTitle);

                        cx.startActivity(galleryIntent);
                    }
                });
            }
        }

        holder.donationDate.setVisibility(View.GONE);

        if (CoasterApplication.showCollectDate) {
            holder.donationDate.setVisibility(View.VISIBLE);

            final SimpleDateFormat dateFormatter = new SimpleDateFormat("dd/MM/yyyy", Locale.US);

            holder.donationDate.setText(dateFormatter.format(coaster.getCollectionDate()));
        }

        holder.donor.setVisibility(View.GONE);

        if (CoasterApplication.showCollector) {
            holder.donor.setVisibility(View.VISIBLE);

            holder.donor.setText(collectorName);

            holder.donor.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent galleryIntent = new Intent(cx, GalleryActivity.class);

                    galleryIntent.putExtra(IIntentExtras.EXTRA_COLLECTORID, coaster.getCollectorID());
                    galleryIntent.putExtra(IIntentExtras.EXTRA_GALLERY_SUBTITLE, collectorName);

                    cx.startActivity(galleryIntent);
                }
            });
        }

        holder.shape.setVisibility(View.GONE);
        holder.measurements.setVisibility(View.GONE);

        if (CoasterApplication.showShape) {
            if (coaster.getCoasterMainShape() != -1) {
                holder.shape.setVisibility(View.VISIBLE);
                holder.measurements.setVisibility(View.VISIBLE);

                holder.shape.setText(foundShape.getName());
                holder.measurements.setText("(" + Util.getDisplayMeasurements(foundShape, coaster) + ")");
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

            imgIntent.putExtra(IIntentExtras.EXTRA_COASTERID, coaster.getCoasterID());

            if (((boolean) v.getTag(R.id.TAG_SWITCHER)) && (isFrontImage)) {
                imgIntent.putExtra(IIntentExtras.EXTRA_IMAGE_PATH, coaster.getCoasterImageFrontName());
            } else {
                imgIntent.putExtra(IIntentExtras.EXTRA_IMAGE_PATH, coaster.getCoasterImageBackName());
            }

            cx.startActivity(imgIntent);
        }
    }
}
