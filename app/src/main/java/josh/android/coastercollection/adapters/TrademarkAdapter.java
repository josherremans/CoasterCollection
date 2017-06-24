package josh.android.coastercollection.adapters;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import josh.android.coastercollection.R;
import josh.android.coastercollection.activities.AddTrademarkActivity;
import josh.android.coastercollection.activities.GalleryActivity;
import josh.android.coastercollection.bo.Trademark;
import josh.android.coastercollection.enums.IIntentExtras;

/**
 * Created by Jos on 2/02/2017.
 */
public class TrademarkAdapter extends ArrayAdapter<Trademark> {

    private ArrayList<Trademark> lstTrademarks;

    private LayoutInflater layoutInflater;

    private int listViewTypeId;
    private boolean showAllInfo = false;

    public TrademarkAdapter(Context context, ArrayList<Trademark> lstTrademarks) {
        super(context, R.layout.item_trademark_list, lstTrademarks);

        this.lstTrademarks = lstTrademarks;

        listViewTypeId = R.layout.item_trademark_list;

        layoutInflater = (LayoutInflater) context.getSystemService(Service.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        if (lstTrademarks == null) {
            return 0;
        } else {
            return lstTrademarks.size();
        }
    }

    @Override
    public Trademark getItem(int position) {
        return lstTrademarks.get(position);
    }

    @Override
    public long getItemId(int position) {
        return lstTrademarks.get(position).getTrademarkID();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View rowView = convertView;

        final Trademark trademark = getItem(position);

        // reuse views
        if (rowView == null) {
            rowView = layoutInflater.inflate(listViewTypeId, null);

            // configure view holder
            ViewHolder viewHolder = new ViewHolder(rowView);

            rowView.setTag(viewHolder);
        }

        // fill data
        ViewHolder holder = (ViewHolder) rowView.getTag();

        holder.trademark.setText(trademark.getTrademark());
        holder.brewery.setText(trademark.getBrewery());

        rowView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryIntent = new Intent(getContext(), GalleryActivity.class);

                galleryIntent.putExtra(IIntentExtras.EXTRA_TRADEMARKID, trademark.getTrademarkID());
                galleryIntent.putExtra(IIntentExtras.EXTRA_GALLERY_SUBTITLE, trademark.getTrademark());

                getContext().startActivity(galleryIntent);
            }
        });

        rowView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Intent alterTrademarkIntent = new Intent(getContext(), AddTrademarkActivity.class);

                alterTrademarkIntent.putExtra(IIntentExtras.EXTRA_TRADEMARKID, trademark.getTrademarkID());

                getContext().startActivity(alterTrademarkIntent);

                return true;
            }
        });
        return rowView;
    }

    private class ViewHolder {
        public TextView trademark;
        public TextView brewery;

        public ViewHolder(View parent) {
            trademark = (TextView) parent.findViewById(R.id.txtTrademark);
            brewery = (TextView) parent.findViewById(R.id.txtBrewery);
        }
    }
}
