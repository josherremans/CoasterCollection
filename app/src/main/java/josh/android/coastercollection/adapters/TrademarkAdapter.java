package josh.android.coastercollection.adapters;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import josh.android.coastercollection.R;
import josh.android.coastercollection.activities.GalleryActivity;
import josh.android.coastercollection.bo.Trademark;
import josh.android.coastercollection.enums.IIntentExtras;

/**
 * Created by Jos on 2/02/2017.
 */
public class TrademarkAdapter extends BaseAdapter {

    private ArrayList<Trademark> lstTrademarks;

    private LayoutInflater layoutInflater;

    private int listViewTypeId;
    private boolean showAllInfo = false;

    private Context cx;

    public TrademarkAdapter(Context context, String listViewType, ArrayList<Trademark> lstTrademarks) {
        cx = context;

        if (listViewType.equals(cx.getResources().getStringArray(R.array.pref_listview_type_values)[0])) { // "CardType"
            listViewTypeId = R.layout.item_trademark_list_card;
            showAllInfo = true;
        }

        if (listViewType.equals(cx.getResources().getStringArray(R.array.pref_listview_type_values)[1])) { // "FullWidthType"
            listViewTypeId = R.layout.item_trademark_list;
            showAllInfo = true;
        }

        if (listViewType.equals(cx.getResources().getStringArray(R.array.pref_listview_type_values)[2])) { // "FullWidthTypeSum"
            listViewTypeId = R.layout.item_trademark_list;
            showAllInfo = false;
        }

        this.lstTrademarks = lstTrademarks;

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
    public Object getItem(int position) {
        return lstTrademarks.get(position);
    }

    @Override
    public long getItemId(int position) {
        return lstTrademarks.get(position).getTrademarkID();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View rowView = convertView;

        final Trademark trademark = (Trademark) getItem(position);

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
                Intent galleryIntent = new Intent(cx, GalleryActivity.class);

                galleryIntent.putExtra(IIntentExtras.EXTRA_TRADEMARKID, trademark.getTrademarkID());
                galleryIntent.putExtra(IIntentExtras.EXTRA_GALLERY_SUBTITLE, trademark.getTrademark());

                cx.startActivity(galleryIntent);
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
