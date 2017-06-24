package josh.android.coastercollection.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;

import josh.android.coastercollection.R;
import josh.android.coastercollection.activities.ImageFullscreenActivity;
import josh.android.coastercollection.bl.ImageManager;
import josh.android.coastercollection.bo.GalleryItem;
import josh.android.coastercollection.enums.IIntentExtras;

import static josh.android.coastercollection.bl.ImageManager.DIR_DEF_IMAGES;

/**
 * Created by Jos on 23/04/2017.
 */

public class GalleryAdapter extends RecyclerView.Adapter<GalleryAdapter.ViewHolder> {
    private ArrayList<GalleryItem> galleryList;
    private Context context;

    public GalleryAdapter(Context context, ArrayList<GalleryItem> galleryList) {
        this.galleryList = galleryList;
        this.context = context;
    }

    @Override
    public GalleryAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_gallery, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(GalleryAdapter.ViewHolder viewHolder, int i) {
        viewHolder.img.setScaleType(ImageView.ScaleType.CENTER_CROP);

        String strImg = galleryList.get(i).getImageName();

        File imgFile = ImageManager.getImgFile(strImg);

        viewHolder.txtCoasterID.setVisibility(View.GONE);
        viewHolder.txtSeriesIndex.setVisibility(View.GONE);

        if (imgFile.exists()) {
            new ImageManager().load(strImg, DIR_DEF_IMAGES, viewHolder.img);

            viewHolder.img.setOnClickListener(new ImageOnClickListener(galleryList.get(i).getCoasterID(), strImg, true));
        } else {
            viewHolder.img.setImageResource(R.drawable.beer_bg_115);

            if (galleryList.get(i).getCoasterID() != -1) {
                viewHolder.txtCoasterID.setVisibility(View.VISIBLE);
                viewHolder.txtCoasterID.setText("" + galleryList.get(i).getCoasterID());
            }

            if (galleryList.get(i).getSeriesNbr() != -1) {
                viewHolder.txtSeriesIndex.setVisibility(View.VISIBLE);
                viewHolder.txtSeriesIndex.setText("#" + galleryList.get(i).getSeriesNbr());
            }

            viewHolder.img.setOnClickListener(null);
        }
    }

    @Override
    public int getItemCount() {
        return galleryList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        public ImageView img;
        public TextView txtCoasterID;
        public TextView txtSeriesIndex;

        public ViewHolder(View view) {
            super(view);

            img = (ImageView) view.findViewById(R.id.img);
            txtCoasterID = (TextView) view.findViewById(R.id.txtCoasterID);
            txtSeriesIndex = (TextView) view.findViewById(R.id.txtSeriesIndex);
        }
    }

    private class ImageOnClickListener implements View.OnClickListener {
        private String coasterImgName;
        private boolean isFrontImage;
        private long coasterID;

        public ImageOnClickListener(long coasterID, String coasterImgName, boolean isFrontImage) {
            this.coasterID = coasterID;
            this.coasterImgName = coasterImgName;
            this.isFrontImage = isFrontImage;
        }

        @Override
        public void onClick(View v) {
            Intent imgIntent = new Intent(context, ImageFullscreenActivity.class);

            imgIntent.putExtra(IIntentExtras.EXTRA_COASTERID, coasterID);
            imgIntent.putExtra(IIntentExtras.EXTRA_IMAGE_PATH, coasterImgName);

            context.startActivity(imgIntent);
        }
    }
}